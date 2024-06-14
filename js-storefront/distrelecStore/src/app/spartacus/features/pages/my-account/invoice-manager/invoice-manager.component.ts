import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { faAngleLeft, faAngleRight, faPlus, faCalendar } from '@fortawesome/free-solid-svg-icons';
import { NgbDate, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { createFrom, EventService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { Observable, Subject, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';
import { OptionSetListService } from 'src/app/spartacus/services/option-set-list.service';
import { DistrelecUserService } from '@services/user.service';
import { AllsitesettingsService } from 'src/app/spartacus/services/allsitesettings.service';
import { getLongDateFormat } from '@helpers/date-helper';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';

@Component({
  selector: 'app-my-account-invoice-manager',
  templateUrl: './invoice-manager.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./invoice-manager.component.scss'],
})
export class InvoiceManagerComponent implements OnInit, OnDestroy {
  @ViewChild('dp2')
  dp2: ElementRef;

  @ViewChild('dp1')
  dp1: ElementRef;

  @ViewChild('dp3')
  dp3: ElementRef;

  @ViewChild('dp4')
  dp4: ElementRef;

  responseMessage: string;
  responseType: string;
  buttonResetText = 'form.reset';
  buttonSearchText = 'form.searchText';
  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;
  faPlus = faPlus;
  faCalendar = faCalendar;
  sortType = 'DESC';
  orderNumber = '';
  articleNumber = '';
  invoiceNumber = '';
  invoiceTotalTo = '';
  invoiceTotalFrom = '';
  invoiceDateFrom = '';
  invoiceDateTo = '';
  dueDateFrom = '';
  dueDateTo = '';
  selectedOrderdBy = '';
  selectedInvoiceSorting = 'byDate:desc';
  status = 'ALL';
  pagination: any;
  selectedPaginationPage = 1;
  orderByList$: Observable<any> = this.optionSetListService.getContactsList();
  orderByList: any = [];
  userDetails_ = this.distrelecUserService.userDetails_;
  userType: any;
  timeSuffix = 'Z';
  directionLinks = false;
  sortingList = [
    { name: 'accountorder.history.sort.byDateasc', value: 'byDate:asc' },
    { name: 'accountorder.history.sort.byDatedesc', value: 'byDate:desc' },
    { name: 'accountorder.history.sort.byDueDateasc', value: 'byDueDate:asc' },
    { name: 'accountorder.history.sort.byDueDatedesc', value: 'byDueDate:desc' },
    { name: 'accountorder.history.sort.byStatusasc', value: 'byStatus:asc' },
    { name: 'accountorder.history.sort.byStatusdesc', value: 'byStatus:desc' },
    { name: 'accountorder.history.sort.byTotalPriceasc', value: 'byTotalPrice:asc' },
    { name: 'accountorder.history.sort.byTotalPricedesc', value: 'byTotalPrice:desc' },
  ];

  statusList = [
    { name: 'listfilter.invoiceStatus.ALL', value: 'ALL' },
    { name: 'listfilter.invoiceStatus.OPEN', value: 'OPEN' },
    { name: 'listfilter.invoiceStatus.PAID', value: 'PAID' },
  ];

  pageSize = [{ label: 10 }, { label: 25 }, { label: 50 }];

  invoicesPerPage = 10;
  invoices: Array<any> = [];
  invoiceMinDate: NgbDateStruct;
  dueMinDate: NgbDateStruct;
  invoiceMaxDate: NgbDateStruct;
  dueMaxDate: NgbDateStruct;
  clearEvent: Subject<void> = new Subject<void>();
  currentChannel_ = this.siteSettingsService.currentChannelData$;
  siteSettings: any;
  dateFormat: string;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private optionSetListService: OptionSetListService,
    private winRef: WindowRef,
    private distrelecUserService: DistrelecUserService,
    private siteSettingsService: AllsitesettingsService,
    private ngZone: NgZone,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    this.subscriptions.add(
      this.userDetails_.subscribe((data) => {
        this.userType = data?.customerType;
      }),
    );

    this.subscriptions.add(
      this.currentChannel_.subscribe((data) => {
        this.siteSettings = data;
        const countryCode = this.siteSettings?.country;
        const languageCode = this.siteSettings?.language;
        this.dateFormat = getLongDateFormat(countryCode, languageCode);
      }),
    );

    this.orderByList$.pipe(take(1)).subscribe((res) => {
      this.orderByList = res;
      this.orderByList.unshift({ uid: '', name: '' });
    });

    this.search(1);
  }

  printInvoices() {
    this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.INVOICE } }));
    this.winRef.nativeWindow.print();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  onSortingInvoices() {
    const sort = this.selectedInvoiceSorting.split(':');
    this.sortType = sort[1].toUpperCase();
    this.search(1);
  }

  resetForm() {
    this.orderNumber = '';
    this.articleNumber = '';
    this.invoiceNumber = '';
    this.invoiceTotalTo = '';
    this.invoiceTotalFrom = '';
    this.invoiceMinDate = null;
    this.invoiceMaxDate = null;
    this.invoiceDateFrom = '';
    this.invoiceDateTo = '';
    this.dueDateFrom = '';
    this.dueDateTo = '';
    this.dueMinDate = null;
    this.dueMaxDate = null;
    this.selectedOrderdBy = '';
    this.selectedInvoiceSorting = 'byDate:desc';
    this.status = 'ALL';
    this.responseMessage = '';
    this.responseType = '';
    this.clearEvent.next();
  }

  //search invoice history
  search(selectedPage: number) {
    let fromDateInvoiceStr = '';
    let toDateInvoiceStr = '';

    if (this.invoiceMaxDate) {
      fromDateInvoiceStr = this.dateToString(this.invoiceMaxDate);
    } else {
      fromDateInvoiceStr = this.invoiceDateFrom;
    }
    if (this.invoiceMinDate) {
      toDateInvoiceStr = this.dateToString(this.invoiceMinDate);
    } else {
      toDateInvoiceStr = this.invoiceDateTo;
    }

    this.invoices = null; //reset
    this.responseMessage = '';
    this.responseType = '';
    this.selectedPaginationPage = selectedPage;

    //decimal places
    const minTotal = parseFloat(parseFloat(this.invoiceTotalFrom).toFixed(2));
    const maxTotal = parseFloat(parseFloat(this.invoiceTotalTo).toFixed(2));

    const searchRequest = {
      articleNumber: this.articleNumber,
      contactId: this.selectedOrderdBy ? this.selectedOrderdBy : '',
      fromDate: fromDateInvoiceStr !== '' ? fromDateInvoiceStr : null,
      toDate: toDateInvoiceStr !== '' ? toDateInvoiceStr : null,
      invoiceNumber: this.invoiceNumber,
      maxTotal: maxTotal > 0 ? maxTotal : null,
      minTotal: minTotal > 0 ? minTotal : null,
      orderNumber: this.orderNumber,
      ordernf: '',
      page: this.selectedPaginationPage,
      pageSize: this.invoicesPerPage,
      show: 'PAGE',
      sort: this.selectedInvoiceSorting,
      status: this.status,
      fromDueDate: this.dueDateFrom !== '' ? this.dueDateFrom : null,
      toDueDate: this.dueDateTo !== '' ? this.dueDateTo : null,
    };

    this.http
      .post<any>(this.occEndpoints.buildUrl(`/users/current/invoice-history?fields=FULL`), searchRequest)
      .pipe(take(1))
      .subscribe(
        (data: any) => {
          if (data) {
            this.pagination = data.pagination;
            this.invoices = data.invoices;
            this.directionLinks = this.pagination?.totalPages > 1 ? true : false;
            if (data.invoiceFromDate || data.invoiceToDate) {
              this.invoiceMaxDate = this.toDpDate(data.invoiceFromDate);
              this.invoiceMinDate = this.toDpDate(data.invoiceToDate);
            }
            if (data.dueFromDate || data.dueToDate) {
              this.dueMaxDate = this.toDpDate(data.dueFromDate);
              this.dueMinDate = this.toDpDate(data.dueToDate);
            }
          }
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
          });
          this.invoices = [];
        },
      );
  }

  toDpDate(date: string): NgbDateStruct {
    const year = +date.substring(0, 4);
    const month = +date.substring(5, 7);
    const day = +date.substring(8);
    return { year, month, day };
  }

  dateToString(date: NgbDateStruct): string {
    return `${date.year}-${date.month}-${date.day}`;
  }

  //invoice date from
  invoiceFromDate(date: NgbDate) {
    const currentVal = this.dp2.nativeElement.value;
    this.invoiceMaxDate = date;
    this.invoiceDateFrom = date.year + '-' + date.month + '-' + date.day;
    this.dateValueReset(this.dp2, currentVal);
  }

  //Invoice to date
  invoiceToDate(date: NgbDate) {
    const currentVal = this.dp1.nativeElement.value;
    this.invoiceMinDate = date;
    this.invoiceDateTo = date.year + '-' + date.month + '-' + date.day;
    this.dateValueReset(this.dp1, currentVal);
  }

  //Due date from
  dueFromDate(date: NgbDate) {
    const currentVal = this.dp4.nativeElement.value;
    this.dueMinDate = date;
    this.dueDateFrom = date.year + '-' + date.month + '-' + date.day;
    this.dateValueReset(this.dp4, currentVal);
  }

  //Due to date
  dueToDate(date: NgbDate) {
    const currentVal = this.dp3.nativeElement.value;
    this.dueMaxDate = date;
    this.dueDateTo = date.year + '-' + date.month + '-' + date.day;
    this.dateValueReset(this.dp3, currentVal);
  }
  // Function to counteract side effect behavior of using ngModelChange where date field is wrongly repopulated
  dateValueReset(el: ElementRef, val: string) {
    this.ngZone.run(() => {
      setTimeout(() => {
        el.nativeElement.value = val;
      }, 10);
    });
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

  onPagination() {
    setTimeout(() => {
      this.search(this.selectedPaginationPage);
    });
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scrollTo(0, 0);
    }
  }

  onPageSizeSelect() {
    setTimeout(() => {
      this.search(1);
    });

    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scrollTo(0, 0);
    }
  }

  scroll(element) {
    const el = document.getElementById(element);
    el.scrollIntoView({ behavior: 'smooth' });
  }
}
