import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, NgZone, OnDestroy, OnInit, Output } from '@angular/core';
import { createFrom, EventService, OccEndpointsService, WindowRef } from '@spartacus/core';
import {
  faAngleLeft,
  faAngleRight,
  faCheck,
  faEllipsisH,
  faMinus,
  faReply,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
import { first, take } from 'rxjs/operators';
import { AngularCsv } from 'angular-csv-ext/dist/Angular-csv';
import { formatDate } from '@angular/common';
import { ExcelService } from '@services/excel.service';
import { AppendComponentService } from '@services/append-component.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AllsitesettingsService } from 'src/app/spartacus/services/allsitesettings.service';
import { Observable, Subscription } from 'rxjs';
import { getLongDateFormat } from '@helpers/date-helper';
import { DistBreakpointService } from '@services/breakpoint.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { OrderStatus } from '@model/order.model';
import { BulkProducts } from '@model/cart.model';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Component({
  selector: 'app-my-account-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.scss'],
})
export class OrderDetailsComponent implements OnInit, OnDestroy {
  @Output() orderDetailsEvents = new EventEmitter<string>();
  @Input() order;

  replyIcon = faReply;
  uri = 'order-history/order-details';
  responseType = '';
  responseMessage = '';
  orderInProgressIcon = faEllipsisH;
  orderCancelledIcon = faXmark;
  checkIcon = faCheck;
  minusIcon = faMinus;
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  faAngleLeft = faAngleLeft;
  faAngleRight = faAngleRight;
  isEditOrderReference = false;
  orderReferenceNumber = '';
  orderDetails: any = [];

  loadingExport = false;
  loadedOrderDetails = false;
  showOrderReturnsFlag = false;
  supportFailed = false;
  isPendingLabel = false;
  isQuantityLabel = false;
  orderCode: string;
  currentChannel_ = this.allSiteSettingsService.currentChannelData$;
  siteSettings: any;
  dateFormat: string;
  orderStatus = OrderStatus;
  itemListEntity = ItemListEntity;
  isProductReturnEnabled$: Observable<boolean> = this.distBaseSiteService.isProductReturnEnabled();

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private ngZone: NgZone,
    private winRef: WindowRef,
    private appendComponentService: AppendComponentService,
    private excelService: ExcelService,
    private windowRef: WindowRef,
    private route: ActivatedRoute,
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
        const countryCode = this.siteSettings?.country;
        const languageCode = this.siteSettings?.language;
        this.dateFormat = getLongDateFormat(countryCode, languageCode);
      }),
    );

    this.orderCode = this.route.snapshot.paramMap.get('orderCode');
    this.http
      .get<any>(this.occEndpoints.buildUrl(`/users/current/order-details/${this.orderCode}`))
      .pipe(take(1))
      .subscribe(
        (data: any) => {
          if (data) {
            this.orderDetails = data;
            this.orderReferenceNumber = data?.projectNumber;
            this.loadedOrderDetails = true;
            this.calculateQuantityPendingLabel();
          }
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
          });
          this.loadedOrderDetails = true;
        },
      );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  goBack() {
    this.router.navigateByUrl('/my-account/order-history');
  }

  getOrderStatusOverviewBox(status: string = null): string {
    if (status) {
      return 'orderOverviewBox.state.' + status;
    } else {
      return 'orderOverviewBox.state.';
    }
  }

  orderStatusColor(status: string = null) {
    switch (status) {
      case OrderStatus.ERP_RECIEVED:
        return 'text-success';
      case OrderStatus.ERP_SHIPPED:
        return 'text-success';
      case OrderStatus.ERP_PARTIALLY_SHIPPED:
        return 'text-warning';
      case OrderStatus.ERP_CANCELLED:
        return 'text-danger';
      default:
        return 'text-dark';
    }
  }

  orderStatusIcon(status: string = null) {
    switch (status) {
      case OrderStatus.ERP_RECIEVED:
        return this.checkIcon;
      case OrderStatus.ERP_SHIPPED:
        return this.checkIcon;
      case OrderStatus.ERP_PARTIALLY_SHIPPED:
        return this.minusIcon;
      case OrderStatus.ERP_CANCELLED:
        return this.orderCancelledIcon;
      default:
        return this.orderInProgressIcon;
    }
  }

  orderReferenceUpdate() {
    this.isEditOrderReference = !this.isEditOrderReference;
  }

  updateOrderRefernceNumber(code: string) {
    const data = {
      orderCode: code,
      orderReference: this.orderReferenceNumber,
      workflowCode: '',
    };
    this.isEditOrderReference = !this.isEditOrderReference;
    this.http
      .post(this.occEndpoints.buildUrl(`/users/current/update/order-reference`), data)
      .pipe(first())
      .subscribe(
        () => {},
        (response) => {
          this.responseType = 'danger'; //error
          if (response?.error?.errors && response?.error?.errors.length > 0) {
            this.supportFailed = true;
          }
        },
        () => {
          //completed
          this.responseType = 'success';
          this.responseMessage = 'The update was successful.';

          this.ngZone.run(() => {
            setTimeout(() => {
              this.responseType = '';
              this.responseMessage = '';
            }, 5000);
          });
        },
      );
  }

  printOrders() {
    this.eventService.dispatch(
      createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.ORDER_HISTORY_DETAILS } }),
    );
    this.winRef.nativeWindow.print();
  }

  download(orderNumber: string, type: string = 'xlsx') {
    this.loadingExport = true;

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
          data.entries.forEach((entry, index) => {
            const order = {
              'Order Date': formatDate(data?.orderDate, 'dd/MM/yyyy', 'en-US'),
              'Order Number': orderNumber,
              'Order Received Via': data?.salesApplication,
              State: this.getOrderStatusOverviewBox(data?.status),
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
        }
        this.loadingExport = false;
      });
  }

  addToShoppingList() {
    this.appendComponentService.appendBackdropModal();
    this.appendComponentService.appendShoppingListModal(
      this.orderDetails.entries.map((entry) => ({
        product: { code: entry.product.productCode ?? entry.product.code },
        desired: entry.quantity,
        comment: entry.customerReference,
      })),
      ItemListEntity.ORDER_DETAILS,
    );
  }

  showDeliveryDetails(deliveryDetails) {
    this.appendComponentService.appendBackdropModal();
    this.appendComponentService.appendShippingTrackingModal(deliveryDetails);
  }

  scrollToTop() {
    if (this.windowRef.isBrowser()) {
      this.windowRef.nativeWindow.scroll({
        top: 0,
        left: 0,
        behavior: 'smooth',
      });
    }
  }

  showOrderReturns(orderCode: string) {
    this.subscriptions.add(
      this.allSiteSettingsService.getCurrentChannelData().subscribe((allSiteData) => {
        this.showOrderReturnsFlag = !this.showOrderReturnsFlag;
        if (allSiteData.domain.indexOf('biz') !== -1) {
          this.router.navigate([`return-or-repair/cms/returnorrepair`]);
        } else {
          this.router.navigate([`/my-account/order-history/order-return/${orderCode}/return-items`]);
        }
        this.scrollToTop();
      }),
    );
  }

  calculateQuantityPendingLabel() {
    this.orderDetails.entries.forEach((entry) => {
      if (entry?.availabilities) {
        this.isPendingLabel = true;
      }
      if (entry?.deliveryDate) {
        this.isQuantityLabel = true;
      }
    });
  }

  orderEntries(): BulkProducts | undefined {
    return this.orderDetails.entries;
  }
}
