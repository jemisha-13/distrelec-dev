import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { faCartPlus, faPlus } from '@fortawesome/free-solid-svg-icons';
import { DistCartService } from '@services/cart.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { DistBreakpointService } from '@services/breakpoint.service';
import { finalize } from 'rxjs/operators';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { ProductAvailability } from '@model/product-availability.model';
import { UntypedFormControl } from '@angular/forms';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';

@Component({
  selector: 'app-add-to-cart',
  templateUrl: './add-to-cart.component.html',
  styleUrls: ['./add-to-cart.component.scss'],
})
export class AddToCartComponent implements OnInit {
  @Input() minQuantity = 1;
  @Input() quantityStep: number;
  @Input() productCode: string;
  @Input() salesStatus: string;
  @Input() sideBarOpen: boolean;
  @Input() customButtonClass: string;
  @Output() addToCart = new EventEmitter<number>();

  @Input() minusButtonId?: string;
  @Input() plusButtonId?: string;
  @Input() inputId?: string;
  @Input() popupId?: string;
  @Input() cartButtonId?: string;

  @Input() pageType?: string;
  @Input() itemListEntity?: ItemListEntity;

  @Input() availabilityData: ProductAvailability;

  @Input() control?: UntypedFormControl;

  faCartPlus = faCartPlus;
  faPlus = faPlus;

  isBTR: boolean;
  maximumQuantity: number;
  hasStock: boolean;

  isPlpActive$: Observable<boolean>;
  isMobile$: Observable<boolean> = this.distBreakPointService.isMobileBreakpoint();
  numericStepperID: NumericStepperIds;

  inputDelay = 1000;

  constructor(
    private cartService: DistCartService,
    private productListService: DistProductListComponentService,
    private distBreakPointService: DistBreakpointService,
    private slideDrawerService: SlideDrawerService,
  ) {
    this.isPlpActive$ = this.productListService.isPlpActive$;
  }

  ngOnInit(): void {
    if (this.availabilityData?.stockLevels[0].mview === 'BTR') {
      this.isBTR = true;
      if (this.availabilityData?.stockLevels.filter((stock) => stock.available > 0)) {
        this.hasStock = true;
        this.maximumQuantity = this.availabilityData?.stockLevels[0].available;
      }
    }

    this.numericStepperID = this.assignNumericStepperID();
  }

  addProductToCart(productCode: string, event): void {
    this.cartService
      .addProductToCart(productCode, this.control?.value, false, this.itemListEntity)
      .pipe(finalize(() => this.openPanel(event)))
      .subscribe();
  }

  openPanel(event): void {
    this.slideDrawerService.openPanelForLargeScreens(event);
  }

  assignNumericStepperID(): NumericStepperIds {
    return {
      minusButtonId: this.minusButtonId,
      plusButtonId: this.plusButtonId,
      inputId: this.inputId,
      popupId: this.popupId,
    };
  }
}
