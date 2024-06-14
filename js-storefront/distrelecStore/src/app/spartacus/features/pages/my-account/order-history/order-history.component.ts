import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import {
  faAngleLeft,
  faAngleRight,
  faCheck,
  faEllipsisH,
  faMinus,
  faReply,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
import { NgbDate, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { createFrom, EventService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';
import { OptionSetListService } from 'src/app/spartacus/services/option-set-list.service';
import { ExcelService } from 'src/app/spartacus/services/excel.service';
import { AngularCsv } from 'angular-csv-ext/dist/Angular-csv';
import { formatDate } from '@angular/common';
import { DistrelecUserService } from '@services/user.service';
import { Router } from '@angular/router';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { getLongDateFormat } from '@helpers/date-helper';
import { DistBreakpointService } from '@services/breakpoint.service';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';

@Component({
  selector: 'app-my-account-order-history',
  templateUrl: './order-history.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./order-history.component.scss'],
})
export class OrderHistoryComponent implements OnInit, OnDestroy {
  orderInProgressIcon = faEllipsisH;
  orderCancelledIcon = faXmark;
  minusIcon = faMinus;
  replyIcon = faReply;
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  isMobile: boolean;
  uri = 'order-history';
  responseType = '';
  responseMessage = '';
  buttonResetText = 'form.reset';
  buttonSearchText = 'form.searchText';
  faAngleLeft = faAngleLeft;
  faAngleRight = faAngleRight;
  articleNumber = '';
  purchaseOrder = '';
  orderTotalTo = '';
  orderTotalFrom = '';
  invoiceNumber = '';
  dateFrom: string;
  dateTo: string;
  orderNumber = '';
  showOrderDetailsFlag = false;
  showOrderReturnsFlag = false;
  selectedOrder: string;
  orderByList$: Observable<any> = this.optionSetListService.getContactsList();
  orderByList: any = [];
  selectedOrderBy = '';
  checkIcon = faCheck;
  minDate: NgbDateStruct;
  maxDate: NgbDateStruct;
  pagination: any;
  selectedPaginationPage = 1;
  sortType = 'DESC';
  userDetails_ = this.distrelecUserService.userDetails_;
  userType: any;
  isProductReturnEnabled$: Observable<boolean> = this.distBaseSiteService.isProductReturnEnabled();

  sortingList = [
    { name: 'accountorder.history.sort.byDateasc', value: 'byDate:asc' },
    { name: 'accountorder.history.sort.byDatedesc', value: 'byDate:desc' },
    { name: 'accountorder.history.sort.byStatusasc', value: 'byStatus:asc' },
    { name: 'accountorder.history.sort.byStatusdesc', value: 'byStatus:desc' },
    { name: 'accountorder.history.sort.byTotalPriceasc', value: 'byTotalPrice:asc' },
    { name: 'accountorder.history.sort.byTotalPricedesc', value: 'byTotalPrice:desc' },
  ];

  statusList = [
    { value: 'ALL', name: 'listfilter.orderStatus.ALL' },
    { value: 'ERP_STATUS_RECIEVED', name: 'listfilter.orderStatus.ERP_STATUS_RECIEVED' },
    { value: 'ERP_STATUS_IN_PROGRESS', name: 'listfilter.orderStatus.ERP_STATUS_IN_PROGRESS' },
    { value: 'ERP_STATUS_PARTIALLY_SHIPPED', name: 'listfilter.orderStatus.ERP_STATUS_PARTIALLY_SHIPPED' },
    { value: 'ERP_STATUS_SHIPPED', name: 'listfilter.orderStatus.ERP_STATUS_SHIPPED' },
    { value: 'ERP_STATUS_CANCELLED', name: 'listfilter.orderStatus.ERP_STATUS_CANCELLED' },
  ];

  pageSize = [{ label: 10 }, { label: 25 }, { label: 50 }];

  ordersPerPage = 10;
  selectedStatusList = 'ALL';
  selectedOrderSorting = 'byDate:desc';
  userOrders_: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  clearEvent: Subject<void> = new Subject<void>();
  currentChannel_ = this.allSiteSettingsService.currentChannelData$;
  siteSettings: any;
  dateformat = '';
  countryCode = '';
  languageCode = '';

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private optionSetListService: OptionSetListService,
    private winRef: WindowRef,
    private excelService: ExcelService,
    private distrelecUserService: DistrelecUserService,
    private router: Router,
    private allSiteSettingsService: AllsitesettingsService,
    private breakpointService: DistBreakpointService,
    private distBaseSiteService: DistrelecBasesitesService,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    this.subscriptions.add(
      this.currentChannel_.subscribe((data) => {
        this.siteSettings = data;
        this.countryCode = this.siteSettings?.country;
        this.languageCode = this.siteSettings?.language;
        this.dateformat = getLongDateFormat(this.countryCode, this.languageCode);
      }),
    );

    this.orderByList$.pipe(take(1)).subscribe((res) => {
      this.orderByList = res;
      this.orderByList.unshift({ uid: '', name: '' });
    });

    //customer type
    this.subscriptions.add(
      this.userDetails_.subscribe((data) => {
        this.userType = data?.customerType;
      }),
    );

    this.subscriptions.add(
      this.isMobileBreakpoint$.subscribe((data) => {
        this.isMobile = data;
      }),
    );

    this.getAllOrders();
  }

  //get all orders on inital call
  getAllOrders() {
    this.http
      .get<any>(this.occEndpoints.buildUrl(`/users/current/orders?fields=FULL&pageSize=10`))
      .pipe(take(1))
      .subscribe((data: any) => {
        if (data) {
          this.userOrders_.next(data.orders);
          this.pagination = data.pagination;
          this.selectedPaginationPage = this.pagination.currentPage;
        }
      });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  showOrderDetails(order) {
    this.showOrderDetailsFlag = !this.showOrderDetailsFlag;
    this.router.navigate([`/my-account/order-history/order-details/${order.code}`]);
    this.scrollToTop();
    this.selectedOrder = order;
  }

  showOrderReturns(order) {
    this.subscriptions.add(
      this.allSiteSettingsService.getCurrentChannelData().subscribe((allSiteData) => {
        this.showOrderReturnsFlag = !this.showOrderReturnsFlag;
        if (allSiteData.domain.indexOf('biz') !== -1) {
          this.router.navigate([`return-or-repair/cms/returnorrepair`]);
        } else {
          this.router.navigate([`/my-account/order-history/order-return/${order.code}/return-items`]);
        }
        this.scrollToTop();
        this.selectedOrder = order;
      }),
    );
  }

  orderStatus(status: string = null) {
    if (status) {
      return 'orderOverviewBox.state.' + status;
    } else {
      return 'orderOverviewBox.state.';
    }
  }

  orderStatusColor(status: string = null) {
    switch (status) {
      case 'ERP_STATUS_RECIEVED':
        return 'text-success';
      case 'ERP_STATUS_SHIPPED':
        return 'text-success';
      case 'ERP_STATUS_PARTIALLY_SHIPPED':
        return 'text-warning';
      case 'ERP_STATUS_CANCELLED':
        return 'text-danger';
      default:
        return 'text-dark';
    }
  }

  orderStatusIcon(status: string = null) {
    switch (status) {
      case 'ERP_STATUS_RECIEVED':
        return this.checkIcon;
      case 'ERP_STATUS_SHIPPED':
        return this.checkIcon;
      case 'ERP_STATUS_PARTIALLY_SHIPPED':
        return this.minusIcon;
      case 'ERP_STATUS_CANCELLED':
        return this.orderCancelledIcon;
      default:
        return this.orderInProgressIcon;
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

  resetForm() {
    this.articleNumber = '';
    this.purchaseOrder = '';
    this.orderTotalTo = '';
    this.orderTotalFrom = '';
    this.invoiceNumber = '';
    this.orderNumber = '';
    this.minDate = null;
    this.maxDate = null;
    this.dateFrom = '';
    this.dateTo = '';
    this.selectedStatusList = 'ALL';
    this.selectedOrderSorting = 'byDate:desc';
    this.selectedOrderBy = '';
    this.responseMessage = '';
    this.responseType = '';

    this.search(1); //reset search as well
    this.clearEvent.next();
  }

  //search orders
  search(selectedPage: number) {
    this.userOrders_.next(null);
    this.responseMessage = '';
    this.responseType = '';
    this.selectedPaginationPage = selectedPage;

    //decimal places
    const orderMinTotal = parseFloat(parseFloat(this.orderTotalFrom).toFixed(2));
    const orderMaxTotal = parseFloat(parseFloat(this.orderTotalTo).toFixed(2));

    const searchRequest = {
      contactId: this.selectedOrderBy,
      filterContactId: this.selectedOrderBy,
      fromDate: this.dateFrom ? this.dateFrom : '',
      toDate: this.dateTo ? this.dateTo : '',
      invoiceNumber: this.invoiceNumber,
      maxTotal: orderMaxTotal ? orderMaxTotal : 0.0,
      minTotal: orderMinTotal ? orderMinTotal : 0.0,
      orderNumber: this.orderNumber,
      page: this.selectedPaginationPage,
      pageSize: this.ordersPerPage,
      productNumber: this.articleNumber,
      reference: this.purchaseOrder,
      show: 'PAGE',
      sort: this.selectedOrderSorting,
      sortType: this.sortType,
      status: this.selectedStatusList,
    };

    this.http
      .post<any>(this.occEndpoints.buildUrl(`/users/current/fetchorders?fields=DEFAULT`), searchRequest)
      .pipe(take(1))
      .subscribe(
        (data: any) => {
          if (data) {
            this.userOrders_.next(data.orders);
            this.pagination = data.pagination;

            if (this.isMobile) {
              this.scroll('searchResults');
            }
          }
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
          });
          this.userOrders_.next(null);
        },
      );
  }

  scroll(element) {
    const el = document.getElementById(element);
    el.scrollIntoView({ behavior: 'smooth' });
  }

  //date from
  receivedFromDate(date: NgbDate) {
    this.languageCode = this.languageCode === 'no' ? 'nb' : this.languageCode;
    this.minDate = date;
    this.dateFrom = date.year + '/' + date.month + '/' + date.day;
    this.dateFrom = formatDate(this.dateFrom, this.dateformat, this.languageCode + '-' + this.countryCode);
  }

  //to date
  receivedToDate(date: NgbDate) {
    this.languageCode = this.languageCode === 'no' ? 'nb' : this.languageCode;
    this.maxDate = date;
    this.dateTo = date.year + '/' + date.month + '/' + date.day;
    this.dateTo = formatDate(this.dateTo, this.dateformat, this.languageCode + '-' + this.countryCode);
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

  onSortingOrders() {
    const sort = this.selectedOrderSorting.split(':');
    this.sortType = sort[1].toUpperCase();
    this.search(1);
  }

  printOrders() {
    if (this.winRef.isBrowser()) {
      this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.ORDER_HISTORY } }));
      this.winRef.nativeWindow.print();
    }
  }

  download(orderNumber: string, type: string = 'xlsx') {
    this.winRef.document.getElementById('downloadButton-' + orderNumber).classList.add('d-none');
    this.winRef.document.getElementById('loading-' + orderNumber).classList.remove('d-none');

    const options = {
      fieldSeparator: ',',
      quoteStrings: '"',
      decimalseparator: '.',
      showLabels: true,
      showTitle: false,
      title: 'Order',
      headers: [
        'Order Date',
        'Order Number',
        'Order Received Via',
        'State',
        'Project number',
        'Comment',
        'Distrelec Article Number',
        'Reference number',
        'Your Reference',
        'Manufacturer',
        'Type',
        'Quantity',
        'Manufacturer Article Number',
        'Name',
        'My Single Price',
        'My Subtotal',
        'List Single Price',
        'List Subtotal',
      ],
      useHeader: false,
      nullToEmptyString: true,
    };

    const exportData = [];

    this.http
      .get<any>(this.occEndpoints.buildUrl(`/users/current/order-details/${orderNumber}`))
      .pipe(take(1))
      .subscribe((data: any) => {
        if (data) {
          data.entries.forEach((entry) => {
            const order = {
              'Order Date': formatDate(data?.orderDate, 'dd/MM/yyyy', 'en-US'),
              'Order Number': orderNumber,
              'Order Received Via': data?.salesApplication,
              State: this.orderStatus(data?.status),
              'Project number': data?.projectNumber,
              Comment: '',
              'Distrelec Article Number': entry?.product?.elfaArticleNumber,
              'Reference number': '',
              'Your Reference': '',
              Manufacturer: entry.product.distManufacturer.name,
              Type: entry.product.typeName,
              Quantity: entry.quantity,
              'Manufacturer Article Number': entry?.product?.typeName,
              Name: entry.product.name,
              'My Single Price': entry.totalPrice.currencyIso + entry.totalPrice.formattedValue,
              'My Subtotal': entry.totalListPrice.currencyIso + entry.totalListPrice.formattedValue,
              'List Single Price': data.subTotal.currencyIso + data.subTotal.formattedValue,
              'List Subtotal': data.totalPrice.currencyIso + data.totalPrice.formattedValue,
            };

            exportData.push(order);
          });

          if (type === 'csv') {
            new AngularCsv(exportData, `Order_export_` + orderNumber, options);
          } else {
            this.excelService.exportAsExcelFile(exportData, `Order`);
          }

          this.winRef.document.getElementById('downloadButton-' + orderNumber).classList.remove('d-none');
          this.winRef.document.getElementById('loading-' + orderNumber).classList.add('d-none');
        }
      });
  }
}
