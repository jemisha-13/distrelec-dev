import { Injectable, OnDestroy } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OccEndpointsService, UserIdService } from '@spartacus/core';
import { BehaviorSubject, Observable, ReplaySubject, Subscription } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { BomFile } from '@features/pages/bom-tool/model/bom-file';
import { ImportFile } from '@features/pages/bom-tool/model/import-file';
import { ImportContent } from '@features/pages/bom-tool/model/import-content';
import { LocalStorageService } from '@services/local-storage.service';
import { BomFileRequest, BomFileRequestEntry } from '@features/pages/bom-tool/model/bom-file-request';
import { DistCartService } from '@services/cart.service';
import { BulkProducts } from '@model/cart.model';

const LOCALSTORAGE_KEY_BOMFILE = 'bom-tool-file';
const LOCALSTORAGE_KEY_IMPORTFILE = 'bom-tool-import-file';

@Injectable({
  providedIn: 'root',
})
export class BomToolService implements OnDestroy {
  private subscriptions = new Subscription();
  private userId = 'anonymous';

  private bomFile_ = new BehaviorSubject<BomFile>(null);
  private importFile_ = new BehaviorSubject<ImportFile>(null);

  private fileList$ = new ReplaySubject<string[]>(1);

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private userIdService: UserIdService,
    private localStorageService: LocalStorageService,
    private cartService: DistCartService,
  ) {
    this.subscriptions.add(
      this.userIdService.getUserId().subscribe((id) => {
        this.userId = id;
        if (id !== 'anonymous') {
          this.fetchList();
        } else {
          this.resetFileList();
        }
      }),
    );
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  fetchList(): Observable<string[]> {
    this.http
      .get<string[]>(this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/list-files`))
      .subscribe((list) => this.fileList$.next(list));

    return this.getList();
  }

  getList(): Observable<string[]> {
    return this.fileList$;
  }

  getListSize(): Observable<number> {
    return this.getList().pipe(map((list) => list?.length ?? 0));
  }

  getFile(fileName: string): Observable<BomFile> {
    const endpoint = this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/load-file`, { queryParams: { fileName } });
    return this.http.get<BomFile>(endpoint).pipe(
      tap((file: BomFile) => {
        this.cacheFile(file);
      }),
    );
  }

  createFromText(text: string): Observable<any> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json; charset=utf-8');
    const endpoint = this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/review`);
    const payload = text.replace(/\r?\n/g, '\r\n');

    return this.http.post(endpoint, payload, { headers }).pipe(
      tap((file: BomFile) => {
        this.cacheFile(file);
      }),
    );
  }

  createFromImportFile(
    fileName: string,
    ignoreFirstRow: boolean,
    articleNumberPosition?: number,
    quantityPosition?: number,
    referencePosition?: number,
  ) {
    const formData = new FormData();
    formData.append('fileName', fileName);
    formData.append('ignoreFirstRow', String(ignoreFirstRow));
    formData.append('articleNumberPosition', String(articleNumberPosition ?? ''));
    formData.append('quantityPosition', String(quantityPosition ?? ''));
    formData.append('referencePosition', String(referencePosition ?? ''));

    const endpoint = this.occEndpoints.buildUrl(`bom-tool/${this.userId}/review-file`);

    return this.http.post(endpoint, formData).pipe(
      tap((file: BomFile) => {
        this.bomFile_.next(file);
        this.localStorageService.setItem(LOCALSTORAGE_KEY_BOMFILE, file);
        this.localStorageService.removeItem(LOCALSTORAGE_KEY_IMPORTFILE);
      }),
    );
  }

  submitImportFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('uploadFile', file);

    const endpoint = this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/matching`);
    return this.http.post(endpoint, formData).pipe(
      map((content: ImportContent): ImportFile => ({ fileName: file.name, content })),
      tap((importFile) => {
        this.cacheImportFile(importFile);
      }),
    );
  }

  saveFile(file: BomFile): Observable<any> {
    const endpoint = this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/save-file`);
    const payload = this.convertToBomFileRequest(file);
    return this.http.post(endpoint, payload, { responseType: 'text' }).pipe(tap(() => this.fetchList()));
  }

  updateFile(file: BomFile): Observable<any> {
    const endpoint = this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/edit-file`);
    const payload = this.convertToBomFileRequest(file);
    return this.http.put(endpoint, payload, { responseType: 'text' }).pipe(tap(() => this.fetchList()));
  }

  getLastCreatedFile(): BehaviorSubject<BomFile | null> {
    if (!this.bomFile_.getValue() && this.localStorageService.getItem(LOCALSTORAGE_KEY_BOMFILE)) {
      this.bomFile_.next(this.localStorageService.getItem(LOCALSTORAGE_KEY_BOMFILE));
    }

    return this.bomFile_;
  }

  getImportContent(): BehaviorSubject<ImportFile> {
    if (!this.importFile_.getValue() && this.localStorageService.getItem(LOCALSTORAGE_KEY_IMPORTFILE)) {
      this.importFile_.next(this.localStorageService.getItem(LOCALSTORAGE_KEY_IMPORTFILE));
    }

    return this.importFile_;
  }

  convertToBomFileRequest(file: BomFile): BomFileRequest {
    const entries = [
      ...file.matchingProducts,
      ...file.notMatchingProductCodes,
      ...file.unavailableProducts.filter(
        (entry) =>
          !file.notMatchingProductCodes.some((notMatchingEntry) => entry.position === notMatchingEntry.position),
      ),
    ].sort((a, b) => a.position - b.position);

    return {
      fileName: file.fileName,
      entry: [
        ...entries.map(
          (entry): BomFileRequestEntry => ({
            code: entry.searchTerm,
            productCode: entry.productCode ?? '',
            customerReference: entry.reference,
            quantity: entry.quantity.toString(),
          }),
        ),
        ...file.duplicateMpnProducts.map((mpnEntry) => ({
          code: mpnEntry.searchTerm,
          productCode: '',
          customerReference: mpnEntry.reference,
          quantity: mpnEntry.quantity.toString(),
        })),
      ],
    };
  }

  formatFile(file: BomFile): BulkProducts {
    const entries = [...file.matchingProducts, ...file.duplicateMpnProducts, ...file.unavailableProducts];
    return entries
      .filter((e) => e.isSelected)
      .map((e, index) => ({
        itemNumber: (index + 1).toString(),
        quantity: e.quantity,
        reference: e.reference,
        productCode: (e.selectedAlternative ?? e.product).code,
      }));
  }

  renameFile(oldName: string, newName: string) {
    const endpoint = this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/rename-file/${oldName}`, {
      queryParams: { newName },
    });
    return this.http.post(endpoint, {}, { responseType: 'text' });
  }

  copyFile(fileName: string) {
    const endpoint = this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/copyFile`, { queryParams: { fileName } });
    return this.http.post(endpoint, {}, { responseType: 'text' });
  }

  deleteFile(fileName: string) {
    const endpoint = this.occEndpoints.buildUrl(`/bom-tool/${this.userId}/delete-file`, { queryParams: { fileName } });
    return this.http.post(endpoint, {});
  }

  private resetFileList(): void {
    this.fileList$.next([]);
  }

  private cacheFile(file: BomFile) {
    this.bomFile_.next(file);
    this.localStorageService.setItem(LOCALSTORAGE_KEY_BOMFILE, file);
  }

  private cacheImportFile(importFile: ImportFile) {
    this.importFile_.next(importFile);
    this.localStorageService.setItem(LOCALSTORAGE_KEY_IMPORTFILE, importFile);
  }
}
