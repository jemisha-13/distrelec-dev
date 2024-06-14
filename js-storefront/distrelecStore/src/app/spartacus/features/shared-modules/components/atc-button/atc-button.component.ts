import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { DistButtonComponent } from '@design-system/button/button.component';
import { DistCartService } from '@services/cart.service';
import { SiteConfigService } from '@services/siteConfig.service';
import { BehaviorSubject, combineLatest, EMPTY, Observable, Subject, Subscription } from 'rxjs';
import { catchError, map, startWith, takeUntil, tap } from 'rxjs/operators';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { AddBulkResponse, BulkProducts } from '@model/cart.model';
import { Cart } from '@spartacus/cart/base/root';
import { DistIcon } from '@model/icon.model';
import { addToCart } from '@assets/icons/icon-index';
import { ProductQuantityService } from '@services/product-quantity.service';
import { ProductAvailability } from '@model/product-availability.model';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { WindowRef } from '@spartacus/core';

@Component({
  selector: 'app-atc-button',
  templateUrl: './atc-button.component.html',
  styleUrls: ['./atc-button.component.scss'],
})
export class AtcButtonComponent implements OnDestroy, AfterViewInit, OnInit {
  @Input() shouldOpenFlyout = false;
  @Input() id = '';
  @Input() pageTrackingType: ItemListEntity;
  @Input() isIconOnly = false;
  @Input() productCode: string;
  @Input() availabilityData: ProductAvailability;
  @Input() salesStatus: string;
  @Input() minimumQuantity?: number;
  @Input() quantityStep: number;

  @Input() set disabled(isDisabled: boolean) {
    this.isDisabled$.next(isDisabled);
  }

  @Input() showAnimation = true;
  @Input() translationKey = 'product.product_info.add_to_cart_btn';

  // Used for pages like bom tool or shopping list that only allow selected products to be added to cart
  // Remember to use bind(this) when passing the method in the template
  @Input() getBulkItems?: () => BulkProducts = undefined;

  // Used when product can be added to cart individually, e.g. PLP or PDP pages
  @Input() control?: UntypedFormControl;

  // Do not user this input, it will be deprecated once quick search is enhanced with availability data
  // Use control for passing product quantity
  @Input() quantity?: number;

  @Output() added: EventEmitter<boolean> = new EventEmitter(false);

  @HostBinding('class.isDisabled') get disabledButtonTooltip() {
    return this.isAddToCartDisabledForActiveSite;
  }

  @ViewChild('atcButton') atcButton: DistButtonComponent;

  public addToCartIcon: DistIcon = addToCart;
  public isAdding = false;
  public isAdded = false;
  public isCart = this.siteConfigService.getCurrentPageTemplate() === 'CartPageTemplate';
  public forceIcon = false;
  public isAddToCartDisabledForActiveSite = false;
  public addToCartTooltipId = 'add_to_cart_tooltip';

  isBTR: boolean;
  maximumQuantity: number;
  hasStock: boolean;
  isQuantityInvalid: boolean;

  isDisabled$ = new BehaviorSubject<boolean>(false);

  private fallbackAddToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(1),
  });

  private subscriptions = new Subscription();

  private readonly cartAddingAnimationDelay = 2000;
  private readonly cartAddedAnimationDelay = 1400;
  private destroy$ = new Subject<void>();

  public isAddToCartDisabled$: Observable<boolean>;

  constructor(
    private cartService: DistCartService,
    private changeDetectorRef: ChangeDetectorRef,
    private siteConfigService: SiteConfigService,
    private slideDrawerService: SlideDrawerService,
    private winRef: WindowRef,
    private productQuantityService: ProductQuantityService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {}

  ngOnInit() {
    this.assignAvailability(this.availabilityData);

    this.isAddToCartDisabled$ = this.isAddToCartDisabled();

    this.control?.setValue(this.minimumQuantity);

    if (this.productCode) {
      this.addToCartTooltipId = 'add_to_cart_tooltip_' + this.productCode;
    }
  }

  public ngAfterViewInit(): void {
    setTimeout(() => {
      if (this.winRef.isBrowser()) {
        const element = this.atcButton.buttonDOMElement.nativeElement;
        if (element) {
          this.forceIcon = element.offsetWidth < element.scrollWidth || this.isIconOnly;
          this.changeDetectorRef.detectChanges();
        }
      }
    });
  }

  public ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.subscriptions.unsubscribe();
  }

  public addProductToCart(event: Event): void {
    if (this.isAdded || this.isAdding) {
      return;
    }

    this.isAdding = true;
    const action$: Observable<Cart | AddBulkResponse> =
      this.getBulkItems === undefined
        ? this.cartService.addProductToCart(
            this.productCode,
            this.control?.value ?? this.quantity,
            this.isCart,
            this.pageTrackingType,
          )
        : this.cartService.addBulkProductsToCart(this.getBulkItems(), this.pageTrackingType);

    action$
      .pipe(
        takeUntil(this.destroy$),
        catchError(() => {
          this.resetCartAddingAnimation();
          return EMPTY;
        }),
      )
      .subscribe((res: Cart) => {
        this.retriggerQuantityCheck();
        if (res?.statusCode === 'noStock' || res?.error) {
          this.resetCartAddingAnimation();
        } else {
          this.animateAddingToCart(event);
        }
      });
  }

  private retriggerQuantityCheck(): void {
    this.subscriptions.add(this.productQuantityService.getCartState().subscribe());
    const { quantitySelected } = this.productQuantityService.onNumericStepperChange(
      this.control?.value,
      this.maximumQuantity,
      this.minimumQuantity,
      this.isMaximumReachable,
      this.hasStock,
      this.productCode,
      this.isCart,
    );

    this.control?.setValue(quantitySelected);
  }

  private animateAddingToCart(event: Event): void {
    setTimeout(() => {
      this.isAdding = false;
      this.isAdded = true;
      this.added.emit(true);
      if (this.shouldOpenFlyout) {
        this.slideDrawerService.openPanelForLargeScreens(event);
      }
      this.changeDetectorRef.detectChanges();

      setTimeout(() => {
        this.isAdded = false;
        this.added.emit(false);
        this.blurButton();
        this.changeDetectorRef.detectChanges();
      }, this.cartAddedAnimationDelay);
    }, this.cartAddingAnimationDelay);
  }

  private resetCartAddingAnimation(): void {
    setTimeout(() => {
      this.isAdding = false;
      this.isAdded = false;
      this.blurButton();
      this.changeDetectorRef.detectChanges();
    }, this.cartAddingAnimationDelay);
  }

  private blurButton(): void {
    this.atcButton.buttonDOMElement.nativeElement.blur();
  }

  private isMaximumQuantityReached(): boolean {
    return this.productQuantityService.isMaximumQuantityReached(
      this.isMaximumReachable,
      this.productCode,
      this.maximumQuantity,
    );
  }

  private assignAvailability(data: ProductAvailability): void {
    const result = this.productQuantityService.assignAvailability(data);

    this.isBTR = result.isBTR;
    this.hasStock = result.hasStock;
    this.maximumQuantity = result.maximumQuantity;
  }

  get isMaximumReachable(): boolean {
    return this.productQuantityService.isMaximumReachable(this.isBTR, this.salesStatus);
  }

  private isAddToCartDisabled(): Observable<boolean> {
    return combineLatest([
      this.distBaseSiteService.isAddToCartDisabledForActiveSite(),
      this.isDisabled$,
      this.cartService.getCartDataFromStore(),
      this.control?.statusChanges.pipe(startWith('VALID')) ??
        this.fallbackAddToCartForm.get('quantity').statusChanges.pipe(startWith('VALID')),
    ]).pipe(
      tap(
        ([isAddToCartDisabledForActiveSite]) =>
          (this.isAddToCartDisabledForActiveSite = isAddToCartDisabledForActiveSite),
      ),
      map(
        ([isAddToCartDisabledForActiveSite, disabledFromParent]) =>
          isAddToCartDisabledForActiveSite ||
          disabledFromParent ||
          this.checkForButtonDisabled(this.control?.value) ||
          this.control?.disabled ||
          this.isMaximumQuantityReached() ||
          this.productQuantityService.isSelectorsDisabled(this.salesStatus, this.hasStock, this.isMaximumReachable),
      ),
    );
  }

  private checkForButtonDisabled(value: number): boolean {
    // Ignore checks if product is already blocked by parent component
    if (!this.disabled) {
      return (
        (value < this.minimumQuantity && !this.isMaximumReachable) ||
        this.productQuantityService.isSelectorsDisabled(this.salesStatus, this.hasStock, this.isMaximumReachable) ||
        this.isQuantityInputInProgress()
      );
    }
  }

  // This is performed when user inputs a value and we force a delay before running checks
  private isQuantityInputInProgress(): boolean {
    return (
      this.control?.errors?.length > 0 &&
      this.control?.errors?.find((error) => error.quantityInputInProgress)?.quantityInputInProgress
    );
  }
}
