import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  GlobalMessageService,
  GlobalMessageType,
  LanguageService,
  TranslationService,
  UserIdService,
} from '@spartacus/core';
import { combineLatest, Subscription } from 'rxjs';
import { faChevronRight, faCloudUploadAlt, faDownload } from '@fortawesome/free-solid-svg-icons';

import { BomToolService } from '@features/pages/bom-tool/bom-tool.service';
import { ErrorResponse } from '@features/pages/bom-tool/model/error-response';
import { Router } from '@angular/router';
import { AppendComponentService } from '@services/append-component.service';

const MAX_FILE_SIZE = 1048576;
const ACCEPTED_FILE_TYPES = ['xls', 'xlsx', 'csv', 'txt'];

@Component({
  selector: 'app-bom-data-import',
  templateUrl: './bom-data-import.component.html',
  styleUrls: ['./bom-data-import.component.scss'],
})
export class BomDataImportComponent implements OnInit, OnDestroy {
  icons = {
    faChevronRight,
    faCloudUploadAlt,
    faDownload,
  };

  textContent = '';
  selectedFile?: File;
  validationErrors: string[] = [];
  isDragOver = false;

  private subscriptions = new Subscription();
  private currentLanguage = 'en';
  private currentUser: string;

  get isTextFormValid(): boolean {
    return this.textContent.length > 0;
  }

  get isFileFormValid(): boolean {
    return this.selectedFile && this.validationErrors.length === 0;
  }

  get bomTemplateDownloadUrl() {
    return `/app/spartacus/assets/data/DistrelecImportFileTemplate-${this.currentLanguage}.xls`;
  }

  get isLoggedIn(): boolean {
    return this.currentUser !== 'anonymous';
  }

  constructor(
    private bomToolService: BomToolService,
    private languageService: LanguageService,
    private router: Router,
    private i18n: TranslationService,
    private globalMessageService: GlobalMessageService,
    private appendComponentService: AppendComponentService,
    private userService: UserIdService,
  ) {}

  ngOnInit(): void {
    this.subscriptions.add(this.languageService.getActive().subscribe((language) => (this.currentLanguage = language)));

    this.subscriptions.add(this.userService.getUserId().subscribe((id) => (this.currentUser = id)));
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  showLoginIfRequired(event?) {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    if (!this.isLoggedIn) {
      this.appendComponentService.appendBackdropModal();
      this.appendComponentService.appendLoginModal();
    }
  }

  onFileChange(event) {
    this.selectedFile = event.target.files[0];
    this.validateFile();
  }

  validateFile() {
    const file = this.selectedFile;
    this.validationErrors = [];

    const fileExtension = file.name.split('.').pop().toLowerCase();
    if (!ACCEPTED_FILE_TYPES.includes(fileExtension)) {
      combineLatest([
        this.i18n.translate('bomdataimport.fileTypeDeniedMessage'),
        this.i18n.translate('bomdataimport.fileTypeMessage'),
      ])
        .subscribe(([fileTypeDeniedMessage, fileTypeMessage]) => {
          this.validationErrors.push(
            `${fileTypeDeniedMessage} ${fileExtension} ${fileTypeMessage} ${ACCEPTED_FILE_TYPES.join(', ')}`,
          );
        })
        .unsubscribe();
    }

    if (file.size > MAX_FILE_SIZE) {
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

  submitText() {
    if (!this.isTextFormValid) {
      return;
    }

    this.subscriptions.add(
      this.bomToolService
        .createFromText(this.textContent)
        .subscribe(() => this.router.navigateByUrl('/bom-tool/review')),
    );
  }

  submitFile() {
    if (!this.isFileFormValid) {
      return;
    }

    this.subscriptions.add(
      this.bomToolService.submitImportFile(this.selectedFile).subscribe(
        () => this.router.navigateByUrl('/bom-tool/matching'),
        (response: ErrorResponse) => {
          this.globalMessageService.add(response.error?.errors[0]?.message, GlobalMessageType.MSG_TYPE_ERROR);
        },
      ),
    );
  }

  onDrop(event) {
    const file = event.dataTransfer.files[0];
    if (file) {
      this.selectedFile = file;
      this.validateFile();
    }
    event.stopPropagation();
    event.preventDefault();
  }

  onDragOver(event) {
    this.isDragOver = true;
    event.stopPropagation();
    event.preventDefault();
  }

  onDragLeave(event) {
    this.isDragOver = false;
    event.stopPropagation();
    event.preventDefault();
  }
}
