import { formatDate } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  faArrowCircleDown,
  faArrowCircleRight,
  faCheck,
  faShoppingCart,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
import { AppendComponentService } from '@services/append-component.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { ExcelService } from '@services/excel.service';
import { LocalStorageService } from '@services/local-storage.service';
import { createFrom, EventService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { AngularCsv } from 'angular-csv-ext/dist/Angular-csv';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { distinctUntilChanged, first, take } from 'rxjs/operators';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { OrderStatus } from '@model/order.model';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { BulkProducts } from '@model/cart.model';

@Component({
  selector: 'app-my-account-order-approval-details',
  templateUrl: './order-approval-details.component.html',
  styleUrls: ['./order-approval-details.component.scss'],
})
export class OrderApprovalDetailsComponent implements OnInit, OnDestroy {
  responseType = '';
  responseMessage = '';
  orderCancelledIcon = faXmark;
  checkIcon = faCheck;
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  isEditOrderReference = false;
  orderReferenceNumber = '';
  orderDetails: any;
  loadingExport = false;
  loadedOrderDetails = false;
  showEditOrderReferenceField: boolean;
  orderCode: string;
  workFlowCode: string;
  approvalData: any;
  faPending = faXmark;
  faArrowCircleDown = faArrowCircleDown;
  faArrowCircleRight = faArrowCircleRight;
  faShoppingCart = faShoppingCart;
  termsAndConditionCheck: boolean;
  approvalRejectionSubscription: Subscription;
  rejectionNote: string;
  userDetails$: BehaviorSubject<any> = this.distrelecUserService.userDetails_;
  userDetails: any;
  userRoles: any;
  orderStatus = OrderStatus;
  isAddToCartEnabled$: Observable<boolean> = this.distBaseSiteService.isAddToCartEnabledForActiveSite();
  itemListEntity = ItemListEntity;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private ngZone: NgZone,
    private winRef: WindowRef,
    private appendComponentService: AppendComponentService,
    private excelService: ExcelService,
    private windowRef: WindowRef,
    private router: ActivatedRoute,
    private route: Router,
    private localStorage: LocalStorageService,
    private distrelecUserService: DistrelecUserService,
    private breakpointService: DistBreakpointService,
    private distBaseSiteService: DistrelecBasesitesService,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    this.subscriptions.add(
      this.userDetails$.subscribe((data) => {
        if (data) {
          this.userDetails = data;
          this.userRoles = data?.roles;
        }
      }),
    );

    this.approvalRejectionSubscription = this.appendComponentService._handleEvent$
      .pipe(distinctUntilChanged())
      .subscribe((item) => this.saveApprovalRejectionNote(item));

    this.orderCode = this.router.snapshot.paramMap.get('orderCode');
    this.workFlowCode = this.router.snapshot.paramMap.get('workFlowCode');

    this.http
      .get<any>(this.occEndpoints.buildUrl(`/users/current/order-approval-details/workflow/${this.workFlowCode}`))
      .pipe(take(1))
      .subscribe(
        (data: any) => {
          if (data) {
            this.approvalData = data;
            this.orderDetails = data.order;
            this.orderReferenceNumber = this.orderDetails?.projectNumber;
            this.loadedOrderDetails = true;
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

  hasRole(role: string) {
    return this.userRoles.indexOf(role) > -1;
  }

  goBack() {
    if (this.userDetails?.customerType === 'B2B' || this.userDetails?.customerType === 'B2B_KEY_ACCOUNT') {
      if (this.hasRole('b2badmingroup')) {
        this.route.navigate(['my-account/order-approval']);
      } else if (!this.hasRole('b2badmingroup') && this.hasRole('b2bcustomergroup')) {
        this.route.navigate(['my-account/order-approval-requests']);
      }
    }
  }

  ngOnDestroy(): void {
    this.approvalRejectionSubscription.unsubscribe();
    this.subscriptions.unsubscribe();
  }

  updateOrderReferenceNumber() {
    const data = {
      orderCode: this.orderCode,
      orderReference: this.orderReferenceNumber,
      workflowCode: this.workFlowCode,
    };
    this.isEditOrderReference = false;
    this.http
      .post(this.occEndpoints.buildUrl(`/users/current/update/order-reference`), data)
      .pipe(first())
      .subscribe(
        () => {},
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
          });
        },
        () => {
          //completed
          this.showEditOrderReferenceField = false;
        },
      );
  }

  printOrders() {
    this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.ORDER_DETAILS } }));
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
        'Requested By',
        'Requested On',
        'Status',
        'Order value',
        'Yearly Budget',
        'order exceeds yearly budget by',
        'order budget',
        'order exceeds order budget by',
        'Reference Number',
        'Art Nr.',
        'MPN',
        'Manufacturer',
        'Quantity',
        'My Single Price',
        'Subtotal',
        'Billing Address',
        'Delivery Address',
        'Shipping Method',
        'Payment Method',
        'Shipping',
        'Tax',
        'Total payable',
      ],
      useHeader: false,
      nullToEmptyString: true,
    };

    const exportData = [];
    const data = this.orderDetails;
    if (data) {
      this.orderDetails.entries.forEach((entry, index) => {
        const order = {
          'Requested By':
            this.orderDetails?.b2bCustomerData?.firstName + ' ' + this.orderDetails?.b2bCustomerData?.lastName,
          'Requested On': data?.created ? formatDate(data?.created, 'dd/MM/yyyy', 'en-US') : '',
          Status: this.approvalData?.approvalDecisionRequired ? 'pending' : '',
          'Order value': `${this.orderDetails?.totalPrice?.currencyIso} ${this.orderDetails?.totalPrice?.formattedValue}`,
          'Yearly Budget': `${this.orderDetails?.totalPrice?.currencyIso} ${this.orderDetails?.totalPrice?.formattedValue}`,
          'order exceeds yearly budget by': `${this.orderDetails?.b2bCustomerData?.budget.currency.isocode} ${this.orderDetails?.b2bCustomerData?.budget.exceededYearlyBudget}`,
          'order budget': `${this.orderDetails?.b2bCustomerData?.budget.currency.isocode} ${this.orderDetails?.b2bCustomerData?.budget?.orderBudget}`,
          'order exceeds order budget by': `${this.orderDetails?.b2bCustomerData?.budget.currency.isocode} ${this.orderDetails?.b2bCustomerData?.budget.exceededOrderBudget}`,
          'Reference Number': this.orderDetails?.projectNumber,
          'Art Nr.': entry?.product.elfaArticleNumber,
          MPN: entry?.product.typeName,
          Manufacturer: entry?.product.distManufacturer?.name,
          Quantity: entry?.quantity,
          'My Single Price': `${entry?.basePrice?.currencyIso} ${entry?.basePrice.formattedValue}`,
          Subtotal: `${entry?.totalPrice?.currencyIso} ${entry?.totalPrice.formattedValue}`,
          'Billing Address': `${this.orderDetails?.b2bCustomerData?.billingAddress?.formattedAddress}`,
          'Delivery Address': `${this.orderDetails?.deliveryAddress?.formattedAddress}`,
          'Shipping Method': `${this.orderDetails?.deliveryMode?.name}`,
          'Payment Method': `${this.orderDetails?.paymentMode?.name}`,
          Shipping: `${this.orderDetails?.deliveryCost?.currencyIso} ${this.orderDetails?.deliveryCost?.formattedValue}`,
          Tax: `${this.orderDetails?.totalTax?.currencyIso} ${this.orderDetails?.totalTax?.formattedValue}`,
          'Total payable': `${this.orderDetails?.totalPrice?.currencyIso} ${this.orderDetails?.totalPrice?.formattedValue}`,
        };

        exportData.push(order);
      });

      if (type === 'csv') {
        new AngularCsv(exportData, `Order-` + orderNumber, options);
      } else {
        this.excelService.exportAsExcelFile(exportData, `Order-` + orderNumber);
      }
    }
    this.loadingExport = false;
  }

  addToShoppingList() {
    this.appendComponentService.appendBackdropModal();
    this.appendComponentService.appendShoppingListModal(
      this.orderDetails.entries.map((entry) => ({
        product: { code: entry.product.productCode ?? entry.product.code },
        desired: entry.quantity,
        comment: entry.customerReference,
      })),
      ItemListEntity.ORDER_APPROVAL,
    );
  }

  openRejectModal() {
    this.appendComponentService.appendBackdropModal();
    this.appendComponentService.appendApprovalRejectModal('order', this.orderDetails.entries);
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

  scrollToElement($element): void {
    $element.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' });
  }

  saveApprovalRejectionNote(event: any) {
    ////Note rejection handling here
    this.rejectionNote = event;
    if (event && event !== 0) {
      this.approveAndSubmit(true);
    }
  }

  approveAndSubmit(rejectionFlow?: boolean) {
    if (this.termsAndConditionCheck || rejectionFlow) {
      const data = {
        approverSelectedDecision: rejectionFlow ? 'reject' : 'approve',
        comments: rejectionFlow ? this.rejectionNote : '',
        termsAndConditions: this.termsAndConditionCheck,
        workFlowActionCode: this.workFlowCode,
      };
      this.isEditOrderReference = !this.isEditOrderReference;
      this.http
        .post(this.occEndpoints.buildUrl(`/users/current/order-approval/approval-decision`), data)
        .pipe(first())
        .subscribe(
          () => {},
          (response) => {
            this.responseType = 'danger'; //error
            response?.error?.errors.forEach((err) => {
              this.responseMessage = err.message;
            });
          },
          () => {
            //completed
            this.responseType = 'success';
            this.responseMessage = 'The update was successful.';
            this.handleRedirection(rejectionFlow);

            this.ngZone.run(() => {
              setTimeout(() => {
                this.responseType = '';
                this.responseMessage = '';
              }, 5000);
            });
          },
        );
    }
  }

  handleRedirection(rejectionFlow: boolean) {
    if (rejectionFlow) {
      this.localStorage.setItem('approval-rejected', true);
      this.route.navigate(['my-account/order-approval']);
    } else {
      this.localStorage.setItem('dispatchPurchaseEvent', true);
      this.route.navigate(['checkout/orderConfirmation/' + this.orderCode]);
    }
  }

  orderEntries(): BulkProducts | undefined {
    return this.orderDetails.entries;
  }
}
