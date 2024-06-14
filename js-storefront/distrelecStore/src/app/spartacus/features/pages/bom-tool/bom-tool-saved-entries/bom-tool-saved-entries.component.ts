import { Component } from '@angular/core';
import { BomToolService } from '@features/pages/bom-tool/bom-tool.service';

@Component({
  selector: 'app-bom-tool-saved-entries',
  templateUrl: './bom-tool-saved-entries.component.html',
  styleUrls: ['./bom-tool-saved-entries.component.scss'],
})
export class BomToolSavedEntriesComponent {
  files$ = this.bomToolService.getList();
  maxFilesReached = false;

  constructor(private bomToolService: BomToolService) {}

  onError(type: string) {
    if (type === 'BomToolFileLimitExceededError') {
      this.maxFilesReached = true;
    }
  }
}
