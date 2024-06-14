import { HttpClient } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewEncapsulation,
} from '@angular/core';
import { faAngleLeft, faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { createFrom, EventService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { take, first } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { DistCartService } from '@services/cart.service';
import { AppendComponentService } from '@services/append-component.service';
import { AllsitesettingsService } from 'src/app/spartacus/services/allsitesettings.service';
import { Observable, Subscription } from 'rxjs';
import { getLongDateFormat } from '@helpers/date-helper';
import { DistBreakpointService } from '@services/breakpoint.service';
import { CartStoreService } from '@state/cartState.service';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';

@Component({
  selector: 'app-my-account-quote-details',
  templateUrl: './quote-details.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./quote-details.component.scss'],
})
export class QuoteDetailsComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() quotationId: any;
  @Output() quoteDetailsEvents = new EventEmitter<string>();

  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  responseMessage: string;
  responseType: string;
  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;
  quoteDetails: any;
  loadedQuoteDetails = false;
  addToCartEnabled = true;
  loadingResubmit = false;
  resubmitted = false;
  loadingAddToCart = false;
  currentChannel_ = this.siteSettingsService.currentChannelData$;
  siteSettings: any;
  dateFormat: string;

  quantityValidaitonErrorState: { [index: string]: boolean } = {};

  isAddToCartEnabled$: Observable<boolean> = this.distBaseSiteService.isAddToCartEnabledForActiveSite();

  private subscription: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private winRef: WindowRef,
    private router: Router,
    private activatedRouter: ActivatedRoute,
    private cartService: DistCartService,
    private appendComponentService: AppendComponentService,
    private siteSettingsService: AllsitesettingsService,
    private breakpointService: DistBreakpointService,
    private cartStoreService: CartStoreService,
    private distBaseSiteService: DistrelecBasesitesService,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    this.quotationId = this.activatedRouter.snapshot.paramMap.get('quotationId');
    this.getQuoteDetails(this.quotationId);

    this.subscription.add(
      this.currentChannel_.subscribe((data) => {
        this.siteSettings = data;
        const countryCode = this.siteSettings?.country;
        const languageCode = this.siteSettings?.language;
        this.dateFormat = getLongDateFormat(countryCode, languageCode);
      }),
    );
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getQuoteDetails(quoteId) {
    this.loadedQuoteDetails = false;
    this.quoteDetails = null;
    this.http
      .get<any>(this.occEndpoints.buildUrl(`/users/current/quotations/${quoteId}`))
      .pipe(take(1))
      .subscribe(
        (data: any) => {
          if (data) {
            this.quoteDetails = data;
            if (this.quoteDetails?.quotationEntries) {
              const entries = this.quoteDetails.quotationEntries.map((entry) => ({
                ...entry,
                minQuantity: this.getMinQuantity(entry),
                maxQuantity: this.getMaxQuantity(entry),
              }));
              this.quoteDetails.quotationEntries = entries;
            }
            this.loadedQuoteDetails = true;
          }
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
          });
          this.loadedQuoteDetails = true;
        },
      );
  }

  getMaxQuantity(entry: any): number {
    return entry?.quantityModificationType === 'DECREASE' && entry?.quantity ? entry.quantity : null;
  }

  getMinQuantity(entry: any): number {
    return entry?.quantityModificationType === 'INCREASE' && entry?.quantity ? entry.quantity : 1;
  }

  isEditAllowed(purchasable: boolean = false, quantityModificationType: string): boolean {
    return (
      purchasable &&
      (quantityModificationType === 'ALL' ||
        quantityModificationType === 'INCREASE' ||
        quantityModificationType === 'DECREASE')
    );
  }

  isRemovable(purchasable: boolean = false, entry: any): boolean {
    if (entry) {
      return purchasable && !entry.mandatory;
    }
    return false;
  }

  removeEntry(entryId) {
    this.quoteDetails.quotationEntries.forEach((item, index) => {
      if (item.itemNumber === entryId) {
        this.quoteDetails.quotationEntries.splice(index, 1);
      }
    });

    if (this.quoteDetails?.quotationEntries.length === 0) {
      this.addToCartEnabled = false;
    }
  }

  goBack() {
    this.router.navigate(['my-account/quote-history']);
  }

  printOrders() {
    this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.QUOTE_DETAILS } }));
    this.winRef.nativeWindow.print();
  }

  getStatusColor(statusCode) {
    switch (statusCode) {
      case '01':
        return 'text-success';
      case '02':
        return 'text-success';
      case '03':
        return 'text-success';
      case '04':
        return 'text-danger';
      case '05':
        return 'text-danger';
      case '06':
        return 'text-danger';
      default:
        return 'text-dark';
    }
  }
  scrollToTop() {
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scroll({
        top: 0,
        left: 0,
        behavior: 'smooth',
      });
    }
  }

  resubmitQuote(quoteId: string) {
    this.loadingResubmit = true;

    //resubmit quotation request.
    this.http
      .post(this.occEndpoints.buildUrl(`/users/current/quotations/resubmit-quotation?previousQuoteId=${quoteId}`), null)
      .pipe(take(1))
      .subscribe(
        (res: any) => {
          if (res.code === 'FAILED' && res.status === 'LIMIT_EXCEEDED') {
            this.appendComponentService.appendContentPopup(
              'form.quote.resubmit.error.title',
              'form.quote.limit.message',
            );
          } else {
            this.appendComponentService.appendContentPopup('form.thank_you', 'form.quotation_request_received');
            this.resubmitted = true;
          }

          this.loadingResubmit = false;
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
          });
        },
        () => {
          //completed
        },
      );

    this.scrollToTop();
  }

  addToCart(quoteId: string) {
    this.processQuotation(quoteId);
  }

  processQuotation(quoteId) {
    const productsJson = [];
    let entry;

    this.quoteDetails.quotationEntries.forEach((item, index) => {
      entry = {
        productCode: item.product.code,
        quantity: item.quantity,
        itemNumber: item.itemNumber,
        reference: this.quoteDetails?.poNumber,
      };

      productsJson.push(entry);
    });

    const request = {
      quotationId: quoteId,
      products: productsJson,
    };

    this.loadingAddToCart = true;

    this.cartService
      .addQuotationToCart(request)
      .pipe(first())
      .subscribe((data) => {
        this.cartStoreService.setCartState(data);
        this.router.navigate(['/cart']);
        this.loadingAddToCart = false;
      });
  }
  showTooltip(event, id, maxQuantity) {
    if (event.target.value < maxQuantity) {
      this.quantityValidaitonErrorState[id] = true;
    } else {
      this.quantityValidaitonErrorState[id] = false;
    }
  }
}
