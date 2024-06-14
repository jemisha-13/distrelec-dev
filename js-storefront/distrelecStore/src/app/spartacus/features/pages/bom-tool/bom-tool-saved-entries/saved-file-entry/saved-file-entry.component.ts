import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';
import { BomToolService } from '@features/pages/bom-tool/bom-tool.service';
import { ErrorResponse } from '@features/pages/bom-tool/model/error-response';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-saved-file-entry',
  templateUrl: './saved-file-entry.component.html',
  styleUrls: ['./saved-file-entry.component.scss'],
})
export class SavedFileEntryComponent implements OnInit, OnDestroy {
  @Input() bomFile: string;
  @Input() index: number;
  @Output() error = new EventEmitter<string>();
  @Output() fileDeleted = new EventEmitter<void>();

  inputValue: string;

  renaming = false;
  showMenu = false;
  showDeleteConfirmation = false;

  faChevronDown = faChevronDown;
  faChevronUp = faChevronUp;

  private subscriptions: Subscription = new Subscription();

  constructor(private bomToolService: BomToolService) {}

  ngOnInit(): void {
    this.inputValue = this.bomFile;
  }

  fileRename() {
    this.renaming = true;
    this.showMenu = false;
  }

  fileDuplicate() {
    this.subscriptions.add(
      this.bomToolService.copyFile(this.bomFile).subscribe(
        () => this.bomToolService.fetchList(),
        (response: ErrorResponse) => this.error.next(response.error.errors[0].type),
        () => (this.showMenu = false),
      ),
    );
  }

  fileRenameSave() {
    this.bomFile = encodeURIComponent(this.bomFile);
    if (!this.inputValue.length) {
      this.inputValue = this.bomFile;
      this.renaming = false;
      return;
    }

    this.subscriptions.add(
      this.bomToolService.renameFile(this.bomFile, this.inputValue).subscribe(() => {
        this.bomFile = this.inputValue;
        this.bomToolService.fetchList();
      }),
    );
  }

  fileRenameClose() {
    this.renaming = false;
    this.inputValue = this.bomFile;
  }

  showFileDeleteModal() {
    this.showDeleteConfirmation = true;
    this.showMenu = false;
  }

  onModalClose(event: string) {
    if (event === 'confirmed') {
      this.subscriptions.add(
        this.bomToolService.deleteFile(this.bomFile).subscribe(() => {
          this.fileDeleted.next();
          this.bomToolService.fetchList();
          this.showDeleteConfirmation = false;
          this.showMenu = false;
        }),
      );
    } else {
      this.showDeleteConfirmation = false;
      this.showMenu = false;
    }
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}
