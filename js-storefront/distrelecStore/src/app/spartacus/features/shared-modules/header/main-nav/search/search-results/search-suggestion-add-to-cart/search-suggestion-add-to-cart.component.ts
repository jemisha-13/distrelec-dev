import { Component, Input, OnInit } from '@angular/core';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { faShoppingCart } from '@fortawesome/free-solid-svg-icons';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { Observable } from 'rxjs';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';
import { ProductAvailability } from '@model/product-availability.model';

@Component({
  selector: 'app-search-suggestion-add-to-cart',
  templateUrl: './search-suggestion-add-to-cart.component.html',
  styleUrls: ['./search-suggestion-add-to-cart.component.scss'],
})
export class AddToCartFFComponent implements OnInit {
  @Input() moq: number;
  @Input() artNr: string;
  @Input() index: number;
  @Input() qtyStep = 1;
  @Input() availability: ProductAvailability;

  faShoppingCart = faShoppingCart;
  qty = 0;
  suggestedSearch = ItemListEntity.SUGGESTED_SEARCH;
  numericStepperID: NumericStepperIds;
  isAddToCartDisabled$: Observable<boolean> = this.distBaseSiteService.isAddToCartDisabledForActiveSite();

  constructor(private distBaseSiteService: DistrelecBasesitesService) {}

  onQuantityChange(qty: number): void {
    this.qty = qty < this.moq ? this.moq : qty;
  }

  ngOnInit(): void {
    //setting qty to moq we dont want to override moq value
    this.qty = this.moq;
    this.numericStepperID = this.assignNumericStepperID();
  }

  assignNumericStepperID(): NumericStepperIds {
    return {
      inputId: 'search-suggest-quantity-selector' + this.index,
      minusButtonId: 'search-suggest-quantity-selector-minus' + this.index,
      plusButtonId: 'search-suggest-quantity-selector-plus' + this.index,
    };
  }
}
