import { Component, OnDestroy, OnInit } from '@angular/core';
import { GlobalMessageService, GlobalMessageType, RoutingService } from '@spartacus/core';
import { faChevronLeft, faChevronRight } from '@fortawesome/free-solid-svg-icons';

import { BomToolService } from '@features/pages/bom-tool/bom-tool.service';
import { ImportContent } from '@features/pages/bom-tool/model/import-content';
import { ImportFile } from '@features/pages/bom-tool/model/import-file';
import { ErrorResponse } from '@features/pages/bom-tool/model/error-response';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-bom-tool-matching-page',
  templateUrl: './bom-tool-matching-page.component.html',
  styleUrls: ['./bom-tool-matching-page.component.scss'],
})
export class BomToolMatchingPageComponent implements OnInit, OnDestroy {
  importFile: ImportFile;

  get importContent(): ImportContent {
    const { content } = this.importFile;

    // In the old storefront we only show the first three columns. Map it here to simplify the template.
    return (this.ignoreFirstRow ? content.slice(1) : content).map((row) => row.slice(0, 3));
  }

  firstRow: Array<string | null>;

  ignoreFirstRow = false;
  articleNumberPosition?: number;
  quantityPosition?: number;
  referencePosition?: number;
  filledColumns = [false, false, false];

  showValidation = false;
  isSubmitting = false;

  icons = {
    faChevronRight,
    faChevronLeft,
  };

  createFromImportFileSubscription: Subscription = new Subscription();

  constructor(
    private bomToolService: BomToolService,
    private router: RoutingService,
    private globalMessageService: GlobalMessageService,
  ) {}

  ngOnInit(): void {
    this.bomToolService
      .getImportContent()
      .subscribe((importFile) => {
        if (!importFile?.content.length) {
          return this.router.goByUrl('/bom-tool');
        }
        this.importFile = importFile;
        this.firstRow = this.importFile?.content[0].slice(0, 3);
      })
      .unsubscribe();
  }

  handleSelectChange(event, columnIndex) {
    const value: string = event.target.value;

    this.filledColumns[columnIndex] = Boolean(event.target?.value);

    switch (value) {
      case 'quant':
        this.quantityPosition = columnIndex;
        break;
      case 'dis-an':
        this.articleNumberPosition = columnIndex;
        break;
      case 'ref':
        this.referencePosition = columnIndex;
        break;
    }
  }

  isColumnFilled(columnIndex) {
    return this.filledColumns[columnIndex];
  }

  isValid() {
    if (this.articleNumberPosition === undefined || this.quantityPosition === undefined) {
      return false;
    }

    if (
      this.articleNumberPosition === this.quantityPosition ||
      this.quantityPosition === this.referencePosition ||
      this.articleNumberPosition === this.referencePosition
    ) {
      return false;
    }

    return true;
  }

  submit() {
    if (!this.isValid()) {
      this.showValidation = true;
      return;
    }

    this.isSubmitting = true;

    this.createFromImportFileSubscription.add(
      this.bomToolService
        .createFromImportFile(
          this.importFile.fileName,
          this.ignoreFirstRow,
          this.articleNumberPosition,
          this.quantityPosition,
          this.referencePosition,
        )
        .subscribe(
          () => this.router.goByUrl('/bom-tool/review-file'),
          (response: ErrorResponse) => {
            this.isSubmitting = false;
            if (response.error?.errors.length) {
              for (let i = 0; i < response.error?.errors.length; i++) {
                this.globalMessageService.add(response.error?.errors[i]?.message, GlobalMessageType.MSG_TYPE_ERROR);
              }
            }
          },
        ),
    );
  }

  ngOnDestroy(): void {
    this.createFromImportFileSubscription.unsubscribe();
  }
}
