import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { OccEndpointsService, TranslationService, WindowRef } from '@spartacus/core';
import { faAngleLeft, faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { first, take } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { LocalStorageService } from '@services/local-storage.service';
import { OrderHistoryService } from '@services/order-history.service';

@Component({
  selector: 'app-my-account-order-returns',
  templateUrl: './order-returns.component.html',
  styleUrls: ['./order-returns.component.scss'],
})
export class OrderReturnsComponent implements OnInit {
  @Output() orderReturnsEvents = new EventEmitter<string>();
  @Input() order;

  responseType = '';
  responseMessage = '';
  faAngleLeft = faAngleLeft;
  faAngleRight = faAngleRight;
  orderDetails: any = [];
  loadedOrderDetails = false;
  returnRequest = [];
  contactSupport = '/en/contact/cms/contact';
  orderCode: string;
  submitting = false;
  enablePostButton = false;

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private winRef: WindowRef,
    private route: ActivatedRoute,
    private router: Router,
    private localStorage: LocalStorageService,
    private translation: TranslationService,
    private orderHistoryService: OrderHistoryService,
  ) {}

  ngOnInit() {
    this.orderCode = this.route.snapshot.paramMap.get('orderCode');

    this.orderHistoryService
      .getOrderDetails(this.orderCode)
      .pipe(take(1))
      .subscribe(
        (data: any) => {
          if (data) {
            this.orderDetails = data;
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

  hasAnyRmaAllowedEntry(): boolean {
    return this.orderDetails.entries.some((entry) => entry.rmaData.notAllowed === false);
  }

  goBack() {
    this.router.navigateByUrl('/my-account/order-history/order-details/' + this.orderCode);
  }

  onMainReasonChange(event: any, i: number, entry: any) {
    if (event === '0') {
      delete this.returnRequest[i];
    } else {
      const req = {
        mainReason: event,
        subReason: '',
        quantity: 1,
        entryNumber: entry.itemPosition,
        articleNumber: entry.product.elfaArticleNumber,
        customerComment: '',
      };
      //if main reason is order not received
      if (event.mainReasonId === '006') {
        req.quantity = entry?.rmaData?.remainingReturnQty;
      }
      this.returnRequest[i] = req;
    }
    this.validatePostBtn();
  }

  onSubReasonChange(event: any, i: number, entry: any) {
    const subReasonEvent = event.split('|');
    this.returnRequest[i].subReasonId = subReasonEvent[0];
    this.returnRequest[i].subReason = subReasonEvent[1];
  }

  postOrderReturns() {
    let validQuantity = true;
    const returnRequestEntryInputs = this.returnRequest.map((x) => ({
      orderEntryNumber: x.entryNumber,
      articleNumber: x.articleNumber,
      quantity: x.quantity,
      mainReasonId: x.mainReason.mainReasonId !== '006' ? x.subReasonId : x.mainReason.mainReasonId,
      subReasonId: x.subReason,
      customerComment: x.customerComment,
    }));

    //remove empty objects
    const refinedEntries = returnRequestEntryInputs.filter((element) => {
      if (
        Object.keys(element).length !== 0 &&
        element.mainReasonId !== 0 &&
        (element.subReasonId || element.mainReasonId === '6' || element.mainReasonId === '006') &&
        element.quantity > 0
      ) {
        return true;
      }
    });

    //check if any qty is 0
    refinedEntries.map((x) => {
      if (!x.quantity || x.quantity === 0) {
        validQuantity = false;
      }
    });

    if (!validQuantity) {
      this.responseMessage = 'Please select at least one product and/or check the errors.';
      this.responseType = 'danger';
    } else {
      this.responseMessage = '';
      this.responseType = '';
      this.submitting = true;
      const request = {
        orderCode: this.orderCode,
        returnRequestEntryInputs: refinedEntries,
      };

      this.http
        .post<any>(this.occEndpoints.buildUrl(`/users/current/order-returns/create-return`), request)
        .pipe(take(1))
        .subscribe(
          (data: any) => {
            if (data) {
              if (data.errorMsgKey) {
                this.responseType = 'danger'; //error
                this.translation
                  .translate(data.errorMsgKey)
                  .pipe(first())
                  .subscribe((val) => (this.responseMessage = val));
                this.submitting = false;
                this.scrollToTop();
              } else {
                this.localStorage.setItem('rmaNumber', data.createRMAResponseData.rmaNumber);
                this.router.navigate([`/my-account/order-history/order-return/${this.orderCode}/confirmation`]);
              }
            }
          },
          (response) => {
            this.responseType = 'danger'; //error
            this.translation
              .translate(response?.errorMsgKey)
              .pipe(first())
              .subscribe((val) => (this.responseMessage = val));
          },
          () => {
            this.submitting = false;
          },
        );
    }
  }
  setQuantity(event: any, index: number, remainingReturnQty: number) {
    let qty = event.target.value;

    if (qty > remainingReturnQty) {
      event.target.value = remainingReturnQty;
      qty = remainingReturnQty;
    }

    this.returnRequest[index].quantity = qty;
    this.validatePostBtn();
  }

  validatePostBtn(): void {
    this.enablePostButton =
      this.returnRequest.some((item) => item.mainReason && item.subReason && item.quantity > 0) ||
      this.returnRequest.some((item) => item.mainReason && item.mainReason.mainReasonId === '006');
  }

  setMaxQuantity(index: number, remainingReturnQty: number) {
    this.returnRequest[index].quantity = remainingReturnQty;
    this.validatePostBtn();
  }

  setComment(index: number, comment: string) {
    this.returnRequest[index].customerComment = comment;
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
}
