import { Component, Input, OnInit } from '@angular/core';

import { faCheckCircle, faInfoCircle, faTimesCircle } from '@fortawesome/free-solid-svg-icons';

import { BomFile } from '@features/pages/bom-tool/model/bom-file';
import { GlobalMessageService, GlobalMessageType } from '@spartacus/core';

@Component({
  selector: 'app-bom-tool-review-summary',
  templateUrl: './bom-tool-review-summary.component.html',
  styleUrls: ['./bom-tool-review-summary.component.scss'],
})
export class BomToolReviewSummaryComponent implements OnInit {
  @Input() file: BomFile;

  icons = {
    faTimesCircle,
    faInfoCircle,
    faCheckCircle,
  };

  showNotMatchingProducts = false;
  showQuantityAdjustedProducts = false;

  get matchingProducts() {
    return this.file.matchingProducts;
  }

  get foundProducts() {
    return this.file.matchingProducts.length + this.mpnDuplicateProductCount;
  }

  get totalProducts() {
    return (
      this.file.matchingProducts.length +
      this.file.notMatchingProductCodes.length +
      this.unavailableProductsWithAlternative +
      this.mpnDuplicateProductCount
    );
  }

  get unavailableProductsWithAlternative() {
    return this.file.unavailableProducts.filter(
      (entry) =>
        !this.file.notMatchingProductCodes.some((notMatchingEntry) => notMatchingEntry.searchTerm === entry.searchTerm),
    ).length;
  }

  get unavailableProductsWithNoAlternative() {
    return this.file.unavailableProducts.filter((entry) =>
      this.file.notMatchingProductCodes.some((notMatchingEntry) => notMatchingEntry.searchTerm === entry.searchTerm),
    ).length;
  }

  get mpnDuplicateProductCount() {
    return this.file.duplicateMpnProducts.length;
  }

  constructor(private globalMessageService: GlobalMessageService) {}

  ngOnInit(): void {
    if (this.file.errorMessages.length) {
      // eslint-disable-next-line @typescript-eslint/prefer-for-of
      for (let i = 0; i < this.file.errorMessages.length; i++) {
        this.globalMessageService.add(this.file.errorMessages[i], GlobalMessageType.MSG_TYPE_ERROR);
      }
    }
  }
}
