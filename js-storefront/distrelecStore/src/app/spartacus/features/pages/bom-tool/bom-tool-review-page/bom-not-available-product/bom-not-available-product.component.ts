import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { BomFileEntry } from '@features/pages/bom-tool/model/bom-file';
import { ProductReferencesService } from '@features/pages/product/core/services/product-references.service';
import { ProductReference, ProductReferenceType } from '@model/product-reference.model';
import { Subscription } from 'rxjs';
import { ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-bom-not-available-product',
  templateUrl: './bom-not-available-product.component.html',
  styleUrls: ['./bom-not-available-product.component.scss'],
})
export class BomNotAvailableProductComponent implements OnInit, OnDestroy {
  @Input() entry: BomFileEntry;
  @Input() index: number;

  get product() {
    return this.entry.product;
  }

  requestedQuantity = 0;

  productReferences: ProductReference[] = [];

  showAlternatives = false;

  productReferenceSubscription: Subscription = new Subscription();

  constructor(
    private productReferencesService: ProductReferencesService,
    private changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.requestedQuantity = this.entry.quantity;

    this.productReferenceSubscription.add(
      this.productReferencesService
        .getReferencesByType(this.product.code, [
          ProductReferenceType.DIS_ALTERNATIVE_SIMILAR,
          ProductReferenceType.DIS_ALTERNATIVE_BETTERVALUE,
          ProductReferenceType.DIS_ALTERNATIVE_DE,
          ProductReferenceType.DIS_ALTERNATIVE_UPGRADE,
        ])
        .subscribe((productReferences) => (this.productReferences = productReferences)),
    );
  }

  toggleAlternatives() {
    this.showAlternatives = !this.showAlternatives;
    this.changeDetector.detectChanges();
  }

  onSelect(product: ICustomProduct) {
    this.entry.selectedAlternative = product;
    this.showAlternatives = false;
    this.changeDetector.detectChanges();
  }

  onRemove() {
    this.entry.selectedAlternative = undefined;
    this.entry.isSelected = false;
  }

  ngOnDestroy(): void {
    this.productReferenceSubscription.unsubscribe();
  }
}
