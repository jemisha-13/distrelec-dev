import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { faPencilAlt } from '@fortawesome/free-solid-svg-icons';

import { BomToolService } from '@features/pages/bom-tool/bom-tool.service';
import { BomFile } from '@features/pages/bom-tool/model/bom-file';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-bom-file-title',
  templateUrl: './bom-file-title.component.html',
  styleUrls: ['./bom-file-title.component.scss'],
})
export class BomFileTitleComponent implements OnInit, OnDestroy {
  @Input() file: BomFile;
  @Input() isNewFile: boolean;

  inputValue: string;
  renaming = false;

  faPencilAlt = faPencilAlt;

  renameFileSubscription: Subscription = new Subscription();

  constructor(private bomToolService: BomToolService) {}

  ngOnInit(): void {
    this.inputValue = this.file.fileName;
  }

  fileRename() {
    this.renaming = true;
  }

  fileRenameSave() {
    if (!this.inputValue.length) {
      this.inputValue = this.file.fileName;
      this.renaming = false;
      return;
    }

    this.renameFileSubscription.add(
      this.bomToolService.renameFile(this.file.fileName, this.inputValue).subscribe(() => {
        this.file.fileName = this.inputValue;
        this.renaming = false;
      }),
    );
  }

  fileRenameClose() {
    this.renaming = false;
    this.inputValue = this.file.fileName;
  }

  ngOnDestroy(): void {
    this.renameFileSubscription.unsubscribe();
  }
}
