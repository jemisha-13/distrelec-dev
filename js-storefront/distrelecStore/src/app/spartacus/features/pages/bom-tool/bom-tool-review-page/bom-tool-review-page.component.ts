import { Component, OnDestroy, OnInit } from '@angular/core';
import { RoutingService } from '@spartacus/core';
import { BomFile, BomFileEntry } from '@features/pages/bom-tool/model/bom-file';
import { BomToolService } from '@features/pages/bom-tool/bom-tool.service';
import { ActivatedRoute } from '@angular/router';
import { BomToolReviewService } from '@features/pages/bom-tool/bom-tool-review.service';
import { Subscription } from 'rxjs';
import { faCircleCheck } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-bom-tool-review-page',
  templateUrl: './bom-tool-review-page.component.html',
  styleUrls: ['./bom-tool-review-page.component.scss'],
})
export class BomToolReviewPageComponent implements OnInit, OnDestroy {
  file: BomFile;
  isNewFile = false;
  isDirty = false;
  showSaveSuccessMessage = false;
  productsWithAlternatives = [];
  productsBelowMinOrderQuantity = [];
  faCircleCheck = faCircleCheck;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private activatedRoute: ActivatedRoute,
    private bomToolService: BomToolService,
    private bomToolReviewService: BomToolReviewService,
    private router: RoutingService,
  ) {}

  ngOnInit() {
    this.bomToolReviewService.reset();
    const fileName = this.activatedRoute.snapshot.queryParamMap.get('fileName');
    if (fileName) {
      this.subscriptions.add(
        this.bomToolService.getFile(fileName).subscribe(
          (file) => this.setFile(file),
          () => this.router.goByUrl('/bom-tool'),
        ),
      );
    } else {
      this.isNewFile = true;
      this.bomToolService
        .getLastCreatedFile()
        .subscribe((file) => {
          if (!file) {
            this.router.goByUrl('/bom-tool');
          }
          this.setFile(file);
        })
        .unsubscribe();
    }

    this.subscriptions.add(
      this.bomToolReviewService.getChangeSignal().subscribe(() => {
        this.isDirty = true;
        this.setProductsBelowMinOrderQuantity();
      }),
    );
  }

  onSave() {
    if (this.isNewFile) {
      this.subscriptions.add(
        this.bomToolService.saveFile(this.file).subscribe(() => {
          this.isNewFile = false;
          this.isDirty = false;
          this.showSaveSuccessMessage = true;
        }),
      );
    } else {
      this.subscriptions.add(
        this.bomToolService.updateFile(this.file).subscribe(() => {
          this.isDirty = false;
          this.showSaveSuccessMessage = true;
        }),
      );
    }
  }

  onRemoveEntry(removedEntry: BomFileEntry) {
    this.file.matchingProducts = this.file.matchingProducts.filter((entry) => entry.position !== removedEntry.position);
    if (this.file.matchingProducts.length === 0) {
      this.bomToolReviewService.setAllSelected(false);
    }
    this.setProductsBelowMinOrderQuantity();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private setFile(file) {
    this.file = file;

    const notMatchingPositions = file.notMatchingProductCodes.map((e) => e.position);
    this.productsWithAlternatives = [
      ...this.file.duplicateMpnProducts,
      ...this.file.unavailableProducts.filter(({ position }) => !notMatchingPositions.includes(position)),
    ];
    this.setProductsBelowMinOrderQuantity();
  }

  private setProductsBelowMinOrderQuantity() {
    this.productsBelowMinOrderQuantity = this.file.matchingProducts.filter(
      (entry) => entry.quantity < entry.product.orderQuantityMinimum,
    );
  }
}
