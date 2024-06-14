import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { getLongDateFormat } from '@helpers/date-helper';
import { NgbDate, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { AppendComponentService } from '@services/append-component.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { DistCartService } from '@services/cart.service';
import { DistrelecUserService } from '@services/user.service';
import { MultiCartStatePersistenceService } from '@spartacus/cart/base/core';
import { createFrom, EventService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { first, take } from 'rxjs/operators';
import { AllsitesettingsService } from 'src/app/spartacus/services/allsitesettings.service';
import { OptionSetListService } from 'src/app/spartacus/services/option-set-list.service';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { CartStoreService } from '@state/cartState.service';

@Component({
  selector: 'app-my-account-quote-history',
  templateUrl: './quote-history.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./quote-history.component.scss'],
})
export class QuoteHistoryComponent implements OnInit, OnDestroy {
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  responseMessage: string;
  responseType: string;
  buttonResetText = 'form.reset';
  buttonSearchText = 'form.searchText';
  faAngleRight = faAngleRight;
  sortType = 'DESC';
  selectedSorting = 'byRequestDate:desc';
  userDetails_ = this.distrelecUserService.userDetails_;
  userType: any;
  orderByList$: Observable<any> = this.optionSetListService.getContactsList();
  orderByList: any = [];
  minDate: NgbDateStruct;
  minExpiryDate: NgbDateStruct;
  maxDate: NgbDateStruct;
  maxExpiryDate: NgbDateStruct;
  currencyName = 'EUR';
  currentChannel_ = this.siteSettingsService.currentChannelData$;
  siteSettings: any;
  quotations_: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  clearEvent: Subject<void> = new Subject<void>();
  statuses: any;
  showQuotationDetailsFlag = false;
  selectedQuote: any;
  sortingEntity = 'byRequestDate';
  now = new Date();
  pagination: any;
  selectedPaginationPage = 1;
  statusList: any = [];
  dateFormat: string;

  loadingQuoteStates: { [quoteId: string]: boolean } = {};
  processQuotationStates: { [quoteId: string]: boolean } = {};

  sortingList = [
    { name: 'accountorder.history.sort.byPONumberasc', value: 'byPONumber:asc' },
    { name: 'accountorder.history.sort.byPONumberdesc', value: 'byPONumber:desc' },
    { name: 'accountorder.history.sort.byRequestDateasc', value: 'byRequestDate:asc' },
    { name: 'accountorder.history.sort.byRequestDatedesc', value: 'byRequestDate:desc' },
    { name: 'accountorder.history.sort.byExpiryDateasc', value: 'byExpiryDate:asc' },
    { name: 'accountorder.history.sort.byExpiryDatedesc', value: 'byExpiryDate:desc' },
    { name: 'accountorder.history.sort.byStatusasc', value: 'byStatus:asc' },
    { name: 'accountorder.history.sort.byStatusdesc', value: 'byStatus:desc' },
    { name: 'accountorder.history.sort.byTotalPriceasc', value: 'byTotalPrice:asc' },
    { name: 'accountorder.history.sort.byTotalPricedesc', value: 'byTotalPrice:desc' },
  ];

  pageSize = [{ label: 10 }, { label: 25 }, { label: 50 }];

  ordersPerPage = 10;

  // request object model for quotation history  request.
  qoutationHistoryRequestModel = {
    articleNumber: '',
    contactId: '',
    currencyCode: '',
    expiryFromDate: '',
    expiryToDate: '',
    fromDate: '',
    maxTotal: null,
    minTotal: null,
    page: 0,
    pageSize: 10,
    poNumber: '',
    quotationId: '',
    quotationReference: '',
    sort: '',
    sortType: '',
    status: 'ALL',
    toDate: '',
  };

  isAddToCartEnabled$: Observable<boolean> = this.distBaseSiteService.isAddToCartEnabledForActiveSite();
  quotationsEnabled$: Observable<boolean> = this.distBaseSiteService.isQuotationEnabledForActiveStore();

  private subscription: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private optionSetListService: OptionSetListService,
    private winRef: WindowRef,
    private distrelecUserService: DistrelecUserService,
    private siteSettingsService: AllsitesettingsService,
    private appendComponentService: AppendComponentService,
    private router: Router,
    private multiCartStatePersistenceService: MultiCartStatePersistenceService,
    private cartService: DistCartService,
    private breakpointService: DistBreakpointService,
    private cartStoreService: CartStoreService,
    private distBaseSiteService: DistrelecBasesitesService,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    // subscribing to user service for the user related info
    this.subscription.add(
      this.userDetails_.subscribe((data) => {
        this.userType = data?.customerType;
      }),
    );
    // get order by
    this.orderByList$.pipe(take(1)).subscribe((res) => {
      this.orderByList = res;
      this.orderByList.unshift({ uid: '', name: '' });
    });

    this.subscription.add(
      this.currentChannel_.subscribe((data) => {
        this.siteSettings = data;
        this.currencyName = this.siteSettings?.currency;
        const countryCode = this.siteSettings?.country;
        const languageCode = this.siteSettings?.language;
        this.dateFormat = getLongDateFormat(countryCode, languageCode);
      }),
    );

    this.search(1);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  resetForm() {
    this.qoutationHistoryRequestModel.articleNumber = '';
    this.qoutationHistoryRequestModel.quotationId = '';
    this.qoutationHistoryRequestModel.minTotal = '';
    this.qoutationHistoryRequestModel.maxTotal = '';
    this.minDate = null;
    this.maxDate = null;
    this.qoutationHistoryRequestModel.fromDate = '';
    this.qoutationHistoryRequestModel.toDate = '';
    this.minExpiryDate = null;
    this.maxExpiryDate = null;
    this.qoutationHistoryRequestModel.expiryFromDate = '';
    this.qoutationHistoryRequestModel.expiryToDate = '';
    this.qoutationHistoryRequestModel.contactId = '';
    this.qoutationHistoryRequestModel.status = 'ALL';
    this.ordersPerPage = 10;

    this.search(1); //reset search as well
    this.clearEvent.next();
  }

  //search invoice history
  search(selectedPage: number) {
    this.quotations_.next(null); //reset
    this.selectedPaginationPage = selectedPage;
    this.statusList = [];

    if (this.qoutationHistoryRequestModel.minTotal) {
      this.qoutationHistoryRequestModel.minTotal = this.qoutationHistoryRequestModel.minTotal.replace(',', '.');
    }

    if (this.qoutationHistoryRequestModel.maxTotal) {
      this.qoutationHistoryRequestModel.maxTotal = this.qoutationHistoryRequestModel.maxTotal.replace(',', '.');
    }

    //decimal places
    const minTotal = parseFloat(this.qoutationHistoryRequestModel.minTotal);
    const maxTotal = parseFloat(this.qoutationHistoryRequestModel.maxTotal);

    const searchRequest = {
      articleNumber: this.qoutationHistoryRequestModel.articleNumber,
      contactId: this.qoutationHistoryRequestModel.contactId ?? '',
      currencyCode: this.currencyName?.toUpperCase(),
      expiryFromDate:
        this.qoutationHistoryRequestModel.expiryFromDate !== ''
          ? this.qoutationHistoryRequestModel.expiryFromDate
          : null,
      expiryToDate:
        this.qoutationHistoryRequestModel.expiryToDate !== '' ? this.qoutationHistoryRequestModel.expiryToDate : null,
      fromDate: this.qoutationHistoryRequestModel.fromDate !== '' ? this.qoutationHistoryRequestModel.fromDate : null,
      toDate: this.qoutationHistoryRequestModel.toDate !== '' ? this.qoutationHistoryRequestModel.toDate : null,
      maxTotal: maxTotal > 0 ? maxTotal : null,
      minTotal: minTotal > 0 ? minTotal : null,
      pageSize: this.ordersPerPage,
      page: this.selectedPaginationPage,
      poNumber: '',
      quotationId: this.qoutationHistoryRequestModel.quotationId,
      quotationReference: '',
      sort: this.selectedSorting,
      status: this.qoutationHistoryRequestModel.status,
    };

    this.http
      .post<any>(this.occEndpoints.buildUrl(`/users/current/quotations`), searchRequest)
      .pipe(take(1))
      .subscribe((data: any) => {
        if (data) {
          this.quotations_.next(data?.quotations);
          this.pagination = data.pagination;
          if (data.quotationStatuses && data.quotationStatuses.length > 0) {
            for (const quoteStatus of data.quotationStatuses) {
              if (quoteStatus.code !== '05') {
                this.statusList.push(quoteStatus);
              }
            }
          }
        }
      });
  }
  resubmitQuote(quoteId: string) {
    this.loadingQuoteStates[quoteId] = true;

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

            this.search(1);
          }

          this.loadingQuoteStates[quoteId] = false;
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
  }

  onPagination() {
    setTimeout(() => {
      this.search(this.selectedPaginationPage);
    });
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow?.scrollTo(0, 0);
    }
  }

  onPageSizeSelect() {
    setTimeout(() => {
      this.search(1);
    });

    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow?.scrollTo(0, 0);
    }
  }

  scroll(element) {
    const el = document.getElementById(element);
    el.scrollIntoView({ behavior: 'smooth' });
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

  showQuoteDetails(quotationId) {
    this.selectedQuote = quotationId;
    this.router.navigate([`my-account/quote-history/quote-details/${this.selectedQuote}`]);
    this.showQuotationDetailsFlag = !this.showQuotationDetailsFlag;
    this.scrollToTop();
  }

  quoteDetailsReturnEvent() {
    this.showQuotationDetailsFlag = !this.showQuotationDetailsFlag;
  }

  onSortingHistory() {
    const sort = this.selectedSorting.split(':');
    this.sortType = sort[1].toUpperCase();
    this.sortingEntity = sort[0];
    this.search(1);
  }

  //expiry from date datepicker event handler method
  expiryFromDate(date: NgbDate) {
    this.minExpiryDate = date;
    this.qoutationHistoryRequestModel.expiryFromDate = date.year + '-' + date.month + '-' + date.day;
  }

  //expiry to date
  expiryToDate(date: NgbDate) {
    this.maxExpiryDate = date;
    this.qoutationHistoryRequestModel.expiryToDate = date.year + '-' + date.month + '-' + date.day;
  }

  //Due date from
  fromDate(date: NgbDate) {
    this.minDate = date;
    this.qoutationHistoryRequestModel.fromDate = date.year + '-' + date.month + '-' + date.day;
  }

  //Due to date
  toDate(date: NgbDate) {
    this.maxDate = date;
    this.qoutationHistoryRequestModel.toDate = date.year + '-' + date.month + '-' + date.day;
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

  print() {
    this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.QUOTE_HISTORY } }));
    this.winRef.nativeWindow.print();
  }

  addToCart(quoteId: string) {
    this.processQuotation(quoteId);
  }

  processQuotation(quoteId) {
    const request = {
      quotationId: quoteId,
    };

    this.processQuotationStates[quoteId] = true;

    this.cartService
      .addQuotationToCart(request)
      .pipe(take(1))
      .subscribe((data) => {
        this.cartStoreService.setCartState(data);
        this.multiCartStatePersistenceService.initSync();
        this.router.navigate(['/cart']);
        this.processQuotationStates[quoteId] = false;
      });
  }
}
