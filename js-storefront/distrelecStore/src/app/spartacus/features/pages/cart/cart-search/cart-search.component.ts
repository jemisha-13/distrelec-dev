import { Component, ElementRef, HostListener, Input, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '@services/login.service';
import { Suggestion, UserIdService } from '@spartacus/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { filter, take, tap } from 'rxjs/operators';
import { QuickSearchPrice } from '@model/factfinder.model';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { ArticleNumberPipe } from '@pipes/article-number.pipe';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { ProductSuggestion } from 'src/app/spartacus/model';
import { DistSearchBoxService } from '@features/pages/product/core/services/dist-search-box.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';

@Component({
  selector: 'app-cart-search',
  templateUrl: './cart-search.component.html',
  styleUrls: ['./cart-search.component.scss'],
})
export class CartSearchComponent implements OnInit {
  @Input() cartForm: UntypedFormGroup;
  @Input() currentChannel: any;

  isRecalculateInProgress_: BehaviorSubject<boolean> = this.cartService.isRecalculateInProgress_;

  selectedTab = 1;
  productQuantity = 1;

  isSearchListVisible = true;

  suggestionResults$: Observable<Suggestion | null>;

  userId$: Observable<string> = this.userIdService.getUserId();

  thumbnail: any;
  prices: [QuickSearchPrice[]] = [[]];
  searchResults: ProductSuggestion[] = [];
  selectedItem: any;
  displayPopup: boolean;
  numericStepperID: NumericStepperIds;

  product: ProductSuggestion;

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(null),
  });

  isAddToCartDisabled$: Observable<boolean> = this.distBaseSiteService.isAddToCartDisabledForActiveSite().pipe(
    take(1),
    tap((isDisabled: boolean) => {
      if (isDisabled) {
        this.disableAddProductToCartMethod();
      }
    }),
  );

  private articleNumberPipe: ArticleNumberPipe = new ArticleNumberPipe();

  constructor(
    private cartService: DistCartService,
    private eRef: ElementRef,
    private loginService: LoginService,
    private userIdService: UserIdService,
    private router: Router,
    private energyEfficiencyLabelService: EnergyEfficiencyLabelService,
    private distSearchBoxService: DistSearchBoxService,
    private availabilityService: ProductAvailabilityService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {}

  ngOnInit(): void {
    this.numericStepperID = this.assignNumericStepperID();
    this.addToCartForm.get('quantity').setValue(1);
  }

  @HostListener('document:click', ['$event'])
  clickout(event) {
    if (this.eRef.nativeElement.contains(event.target)) {
      this.isSearchListVisible = true;
    } else {
      this.hideProductList();
      this.distSearchBoxService.clearResults();
    }
  }

  onSearch($event): void {
    this.distSearchBoxService.clearResults();

    if ($event.target.value.length >= 3) {
      this.searchResults = [];
      this.addToCartForm.get('quantity').setValue(1);
      this.product = null;
      this.distSearchBoxService.searchSuggestions($event.target.value, {});

      this.suggestionResults$ = this.distSearchBoxService.getSuggestResults().pipe(
        filter((results) => results?.products?.length > 0),
        tap((results) => {
          this.formatResultsImages(results);
          if (results?.products.length === 1) {
            this.product = results?.products[0];
            this.addToCartForm.get('quantity').setValue(+results.products[0].itemMin);
          }
        }),
      );
    }
  }

  formatResultsImages(data: Suggestion): void {
    data.products.forEach((element) => {
      let searchEntry;
      if (element?.image) {
        this.thumbnail = element?.image;

        searchEntry = element;
        searchEntry.image = this.thumbnail;

        searchEntry.price = element?.priceData;

        this.searchResults.push(searchEntry);
      }
    });
  }

  onChange({ qty }): void {
    if (Number(qty)) {
      this.productQuantity = qty;
    }
  }

  onAddToCart() {
    if (this.searchResults.length === 1 && !this.product?.code) {
      this.addToCartForm.get('quantity').setValue(+this.searchResults[0]?.itemMin);

      this.setProductForCart(this.searchResults[0]?.code);
      this.hideProductList();

      return;
    }

    if (this.product?.code) {
      this.setProductForCart(this.product?.code);
      this.product = null;
    } else {
      this.displayPopup = true;
    }
  }

  hidePopup() {
    if (this.displayPopup) {
      this.displayPopup = false;
    }
  }

  setProductForCart(productCode: string): void {
    this.cartService
      .addProductToCart(productCode, this.addToCartForm.get('quantity').value, true, ItemListEntity.CART_SEARCH)
      .subscribe();
    this.cartForm.reset();
    this.addToCartForm.get('quantity').setValue(1);
  }

  selectProduct(productData: any): void {
    this.selectedItem = productData;
    this.cartForm.get('orderInput').patchValue(this.articleNumberPipe.transform(productData?.articleNr));

    this.product = productData;
    this.addToCartForm.get('quantity').setValue(+productData.itemMin);
    this.suggestionResults$ = null;
    this.distSearchBoxService.clearResults();
  }

  hideProductList(): void {
    this.isSearchListVisible = false;
    this.searchResults = [];
    this.suggestionResults$ = null;
    this.distSearchBoxService.clearResults();
  }

  redirectToLogin(): void {
    this.loginService.assignRedirectUrlAfterLogin('/cart');
    this.router.navigate(['/login']);
  }

  getEnergyEfficiencyRating(energyEfficiency: string) {
    return this.energyEfficiencyLabelService.getEnergyEfficiencyRating(energyEfficiency);
  }

  getEnergyEfficiencyLabelImageUrl(uri: string): string {
    return this.energyEfficiencyLabelService.getAbsoluteImageUrl(uri);
  }

  disableAddProductToCartMethod(): void {
    this.onAddToCart = function () {};
  }

  assignNumericStepperID(): NumericStepperIds {
    return {
      minusButtonId: 'cartSearchNumericStepperMinusButton',
      plusButtonId: 'cartSearchNumericStepperPlusButton',
      inputId: 'cartSearchNumericStepperInput',
      popupId: 'cartSearchNumericStepperPopup',
    };
  }
}
