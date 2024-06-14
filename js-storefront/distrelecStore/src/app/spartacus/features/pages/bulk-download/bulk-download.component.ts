import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { LanguageService, OccEndpointsService, TranslationService, User } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import { faCloudUploadAlt, faDownload, faExclamationTriangle, faInfoCircle } from '@fortawesome/free-solid-svg-icons';
import { SiteConfigService } from '@services/siteConfig.service';
import { ExcelService } from 'src/app/spartacus/services/excel.service';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { saveAs } from 'file-saver';
import { formatDate } from '@angular/common';
import { catchError, tap } from 'rxjs/operators';
import { DistrelecUserService } from '@services/user.service';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { AppendComponentService } from '@services/append-component.service';

const MAX_FILE_SIZE = 1048576;
const ACCEPTED_FILE_TYPES = ['xls', 'xlsx', 'csv', 'txt'];

@Component({
  selector: 'app-bulk-download',
  templateUrl: './bulk-download.component.html',
  styleUrls: ['./bulk-download.component.scss'],
})
export class BulkDownloadComponent implements OnInit, OnDestroy {
  currentSiteId = this.siteConfigService.getCurrentSiteId();
  faCloudUploadAlt = faCloudUploadAlt;
  faDownload = faDownload;
  faExclamationTriangle = faExclamationTriangle;
  faInfoCircle = faInfoCircle;

  customerName?: string;
  selectedFile?: File;
  validationErrors: string[] = [];
  validCodes: string[] = [];
  invalidCodes: string[] = [];
  validationWarnings: string[] = [];
  isDragOver = false;

  date = new Date();
  fileDate = `${formatDate(this.date, 'ddMMyyyy', 'en-US')}${this.date.getHours()}${this.date.getMinutes()}`;
  private subscriptions = new Subscription();
  private currentLanguage = 'en';

  customerData$: Observable<User>;
  customerNameForm: UntypedFormGroup;

  constructor(
    private appendComponentService: AppendComponentService,
    private changeDetector: ChangeDetectorRef,
    private siteConfigService: SiteConfigService,
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private excelService: ExcelService,
    private i18n: TranslationService,
    private userService: DistrelecUserService,
    private languageService: LanguageService,
    private fb: UntypedFormBuilder,
  ) {}

  ngOnInit(): void {
    this.loadCurrentLanguage();

    this.customerNameForm = this.fb.group({
      name: [''],
    });

    this.customerData$ = this.userService
      .getUserInformation()
      .pipe(tap((data) => this.customerNameForm.get('name').setValue(data.name)));
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  get isFileFormValid(): boolean {
    return this.selectedFile && this.validationErrors.length === 0;
  }

  onFileChange(event): void {
    this.selectedFile = event.target.files[0];
    this.validateFile();

    this.submitImportFile(this.selectedFile);
  }

  validateFile(): void {
    const file = this.selectedFile;
    const fileExtension = file?.name.split('.').pop().toLowerCase();
    this.validationErrors = [];
    this.validationWarnings = [];

    if (!ACCEPTED_FILE_TYPES.includes(fileExtension)) {
      combineLatest([this.i18n.translate('product.information.environmental.documentation_failure_file_error')])
        .subscribe(([fileTypeDeniedMessage]) => {
          this.validationErrors.push(`${fileTypeDeniedMessage}`);
        })
        .unsubscribe();
    } else if (file.size > MAX_FILE_SIZE) {
      combineLatest([
        this.i18n.translate('bomdataimport.fileSizeDeniedMessage'),
        this.i18n.translate('bomdataimport.fileSizeMessage'),
      ])
        .subscribe(([fileSizeDeniedMessage, fileSizeMessage]) => {
          this.validationErrors.push(
            `${fileSizeDeniedMessage} ${Math.round((file.size / (1024 * 1024)) * 100) / 100}  MB. ${fileSizeMessage}`,
          );
        })
        .unsubscribe();
    }
  }

  submitImportFile(file: File): void {
    if (!this.isFileFormValid) {
      return;
    }
    this.validationErrors = [];
    this.validationWarnings = [];
    const formData = new FormData();
    const uploadFileEndpoint = this.occEndpoints.buildUrl('/quality-and-legal/upload-file');

    formData.append('file', file);

    this.http
      .post(uploadFileEndpoint, formData)
      .pipe(
        tap((data: { productCodes: []; invalidProductsCodes: [] }) => {
          this.validCodes = data.productCodes;
          this.invalidCodes = data.invalidProductsCodes;

          if (this.validCodes.length === 0) {
            combineLatest([
              this.i18n.translate('product.information.environmental.documentation_failure_file_error_empty'),
            ])
              .subscribe(([fileTypeError]) => {
                this.validationErrors.push(`${fileTypeError}`);
              })
              .unsubscribe();
          } else {
            if (this.invalidCodes.length > 0) {
              const invalidCodesStr = this.invalidCodes.join(', ');

              combineLatest([this.i18n.translate('product.information.environmental.documentation_invalid_codes')])
                .subscribe(([invalidCodesMessage]) => {
                  this.validationWarnings.push(`${invalidCodesMessage} ${invalidCodesStr}`);
                })
                .unsubscribe();
            }
          }
          this.changeDetector.detectChanges();
        }),
        catchError((err) => {
          this.validationErrors.push(err.error.errorMessage);
          this.changeDetector.detectChanges();
          return err;
        }),
      )
      .subscribe();
  }

  downloadExcelReport(): void {
    this.displayLoadingScreen();
    const downloadExcelUrl = this.occEndpoints.buildUrl('/quality-and-legal/excel');
    const excelObject = {
      productCodes: this.validCodes,
      customerName: this.customerNameForm.get('name').value,
    };

    this.http
      .post(downloadExcelUrl, excelObject, { responseType: 'blob' })
      .pipe(
        tap((data) => {
          if (data) {
            this.excelService.saveAsExcelFile(data, `environmental-information-${this.fileDate}`);
          }
          this.removeLoadingScreen();
        }),
        catchError((err) => {
          this.handleError();
          return err;
        }),
      )
      .subscribe();
  }

  downloadPDFReport(): void {
    this.displayLoadingScreen();

    const downloadPdfUrl = this.occEndpoints.buildUrl('/quality-and-legal/pdf');
    const pdfObject = {
      productCodes: this.validCodes,
      customerName: this.customerNameForm.get('name').value,
    };

    this.http
      .post(downloadPdfUrl, pdfObject, { responseType: 'blob' })
      .pipe(
        tap((data) => {
          const fileURL = new Blob([data], { type: 'application/pdf' });
          saveAs(fileURL, `environmental-information-${this.fileDate}`);
          this.removeLoadingScreen();
        }),
        catchError((err) => {
          this.handleError();
          return err;
        }),
      )
      .subscribe();
  }

  downloadBulkStatements(): void {
    this.displayLoadingScreen();

    const downloadBulkUrl = this.occEndpoints.buildUrl('/quality-and-legal/bulk-certificates');
    const bulkObject = { productCodes: this.validCodes };

    this.http
      .post(downloadBulkUrl, bulkObject, { responseType: 'blob' })
      .pipe(
        tap((data) => {
          const fileURL = new Blob([data], { type: 'application/pdf' });
          saveAs(fileURL, `environmental-information-bulk${this.fileDate}`);
          this.removeLoadingScreen();
        }),
        catchError((err) => {
          this.handleError();
          return err;
        }),
      )
      .subscribe();
  }

  onDrop(event): void {
    const file = event.dataTransfer.files[0];
    if (file) {
      this.selectedFile = file;
      this.validateFile();

      this.submitImportFile(file);
    }
    event.stopPropagation();
    event.preventDefault();
  }

  downloadFileUploadTemplate(event: MouseEvent): void {
    event.preventDefault();
    const downloadTemplateUrl = this.occEndpoints.buildUrl('/quality-and-legal/template');
    this.http
      .get(downloadTemplateUrl, { responseType: 'blob' })
      .pipe(
        tap((data) => {
          if (data) {
            this.excelService.saveAsExcelFile(data, `DistrelecImportFileTemplate-${this.currentLanguage}.xlsx`);
          }
        }),
        catchError((err) => {
          this.handleError();
          return err;
        }),
      )
      .subscribe();
  }

  handleError(): void {
    combineLatest([this.i18n.translate('product.information.environmental.documentation_failure_error')])
      .subscribe(([failureMessage]) => {
        this.validationErrors.push(`${failureMessage}`);
        this.removeLoadingScreen();
      })
      .unsubscribe();
  }

  onDragOver(event): void {
    this.isDragOver = true;
    event.stopPropagation();
    event.preventDefault();
  }

  onDragLeave(event): void {
    this.isDragOver = false;
    event.stopPropagation();
    event.preventDefault();
  }

  displayLoadingScreen(): void {
    this.appendComponentService.appendBackdropModal();
    this.appendComponentService.appendLoadingLogo();
  }

  removeLoadingScreen(): void {
    this.appendComponentService.removeLoadingLogoFromBody();
    this.appendComponentService.removeBackdropComponentFromBody();
  }

  private loadCurrentLanguage(): void {
    this.subscriptions.add(this.languageService.getActive().subscribe((language) => (this.currentLanguage = language)));
  }
}
