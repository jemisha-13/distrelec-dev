import { Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { ProductAvailability } from '@model/product-availability.model';
import { catchError, debounceTime, distinctUntilChanged, first, switchMap } from 'rxjs/operators';
import { DistCartService } from '@services/cart.service';
import { DefaultImageService } from '@services/default-image.service';
import { AbstractControl, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { of, Subscription } from 'rxjs';
import { GlobalMessageService, GlobalMessageType, Translatable, WindowRef } from '@spartacus/core';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-cart-quotation-list',
  templateUrl: './cart-quotation-list.component.html',
  styleUrls: ['./cart-quotation-list.component.scss'],
})
export class CartQuotationListComponent implements OnInit, OnDestroy {
  @Input() cartData: Cart;
  @Input() items: OrderEntry[];
  @Input() quotationId: string;
  @Input() productAvailabilities: ProductAvailability[];
  @Output() removeClicked = new EventEmitter<string>();
  @Output() focusRefInput = new EventEmitter<ElementRef>();
  @ViewChild('quoteRefInput') quoteRefInput: ElementRef;
  quotationReference: string;
  customerReference: string;

  missingImgSrc = this.defaultImage.getDefaultImage();
  cartQuotationReferenceForm: UntypedFormGroup;

  cartReferenceCodeToggleState: { [refCode: string]: boolean } = {};

  invalidRefMessage: Translatable = {
    // using translation from checkout as we are using similar message for order reference validation.
    key: 'checkout.support_failed',
  };

  isQuoteRefPasted: boolean;

  private subscriptions = new Subscription();

  constructor(
    private cartService: DistCartService,
    private defaultImage: DefaultImageService,
    private winRef: WindowRef,
    private fb: UntypedFormBuilder,
    private globalMessageService: GlobalMessageService,
  ) {}

  ngOnInit(): void {
    this.quotationReference = this.items.find((entry) => entry.quotationReference)?.quotationReference;
    this.customerReference = this.items.find((entry) => entry.customerReference)?.customerReference;
    this.cartQuotationReferenceForm = this.fb.group({
      cartQuotationReference: new UntypedFormControl(this.customerReference ?? '', { updateOn: 'change' }),
    });
    this.listenAndSaveReference();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
  listenAndSaveReference(): void {
    this.subscriptions.add(
      this.getCartQuotationReference()
        .valueChanges.pipe(
          debounceTime(1500),
          distinctUntilChanged(),
          switchMap((value) => {
            this.updateReference(value);
            this.focusRefInput.emit(this.quoteRefInput);
            return of(value);
          }),
          catchError((error) => {
            this.updateReference('');
            return error;
          }),
        )
        .subscribe(() => {
          if (this.isQuoteRefPasted) {
            this.isQuoteRefPasted = false;
          }
        }),
    );
  }

  onPasteQuoteRef(event: ClipboardEvent): void {
    const clipboardData = event.clipboardData;
    const pastedText = clipboardData.getData('text').substring(0, 35);
    const lastEntry = this.items.slice(-1).pop();
    if (lastEntry) {
      const entryNumber = lastEntry.entryNumber;
      this.isQuoteRefPasted = true;
      this.subscriptions.add(this.cartService.updateEntryReference(entryNumber, pastedText).subscribe());
    }
  }

  getCartQuotationReference(): AbstractControl {
    return this.cartQuotationReferenceForm.get('cartQuotationReference');
  }

  removeQuotation() {
    this.removeClicked.emit(this.quotationId);
  }

  updateReference(reference: string) {
    const lastEntry = this.items.slice(-1).pop();
    if (lastEntry) {
      const entryNumber = lastEntry.entryNumber;
      this.cartService
        .updateEntryReference(entryNumber, reference)
        .pipe(first())
        .subscribe(
          (response) => response,
          (error) => {
            this.globalMessageService.add(this.invalidRefMessage, GlobalMessageType.MSG_TYPE_ERROR);
          },
        );
    }
  }

  toggleMobileReferenceCode(refCode: string, isVisible: boolean) {
    this.cartReferenceCodeToggleState[refCode] = isVisible;
  }

  getCartQuotationReferenceLength(): number {
    return this.getCartQuotationReference()?.value?.length ?? 0;
  }
}
