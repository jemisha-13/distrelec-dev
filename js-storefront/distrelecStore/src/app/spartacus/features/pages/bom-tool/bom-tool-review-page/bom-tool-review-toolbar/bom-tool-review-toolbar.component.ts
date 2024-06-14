import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { EventService, RoutingService } from '@spartacus/core';
import { CartAddEntrySuccessEvent } from '@spartacus/cart/base/root';
import { Observable, Subscription } from 'rxjs';
import { BomToolReviewService } from '@features/pages/bom-tool/bom-tool-review.service';
import { BomToolService } from '@features/pages/bom-tool/bom-tool.service';
import { BomFile } from '@features/pages/bom-tool/model/bom-file';
import { AddToShoppingListEvent } from '@features/tracking/events/add-to-shopping-list-event';
import { AppendComponentService } from '@services/append-component.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { BulkProducts } from '@model/cart.model';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'app-bom-tool-review-toolbar',
  templateUrl: './bom-tool-review-toolbar.component.html',
  styleUrls: ['./bom-tool-review-toolbar.component.scss'],
})
export class BomToolReviewToolbarComponent implements OnInit, OnDestroy {
  @Input() file: BomFile;
  @Input() isNewFile = false;
  @Input() isDirty = false;

  @Output() saveFile = new EventEmitter();

  isAnySelected$: Observable<boolean>;

  addedToCart = false;
  cartButtonClicked = false;

  addedToShoppingList = false;
  shoppingListButtonClicked = false;
  itemListEntity = ItemListEntity;

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(1),
  });

  private subscription = new Subscription();

  constructor(
    private bomToolService: BomToolService,
    private bomToolReviewService: BomToolReviewService,
    private componentService: AppendComponentService,
    private eventService: EventService,
    private router: RoutingService,
  ) {}

  ngOnInit(): void {
    this.isAnySelected$ = this.bomToolReviewService.isAnySelected();

    this.subscription.add(
      this.eventService.get(AddToShoppingListEvent).subscribe(() => {
        if (this.shoppingListButtonClicked) {
          this.addedToShoppingList = true;
        }
      }),
    );

    this.subscription.add(
      this.eventService.get(CartAddEntrySuccessEvent).subscribe(() => {
        if (this.cartButtonClicked) {
          this.addedToCart = true;
        }
      }),
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  onAddToShoppingList() {
    const selectedProducts = this.bomToolReviewService.getSelected();
    this.shoppingListButtonClicked = true;
    this.componentService.appendShoppingListModal(
      selectedProducts.map((entry) => {
        const product = entry.selectedAlternative ?? entry.product;
        return {
          product: { code: product.productCode ?? product.code },
          desired: entry.quantity,
          comment: entry.reference,
        };
      }),
      ItemListEntity.BOM,
    );
  }

  format(): BulkProducts {
    return this.bomToolService.formatFile(this.file);
  }

  onAddToCart() {
    this.addedToCart = true;
    this.bomToolReviewService.markSelectedAsAddedToCart();
    this.bomToolReviewService.setAllSelected(false);
  }

  onSave() {
    this.saveFile.emit();
  }

  goToCart() {
    this.router.goByUrl('/cart');
  }
}
