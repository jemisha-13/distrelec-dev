import { HttpClient } from '@angular/common/http';
import { Component, NgZone, OnInit } from '@angular/core';
import { faBoxOpen, faCheck, faCheckCircle, faFileLines } from '@fortawesome/free-solid-svg-icons';
import { OccEndpointsService, TranslationService, WindowRef } from '@spartacus/core';
import { Observable } from 'rxjs';
import { first, shareReplay, take } from 'rxjs/operators';
import { SiteConfigService } from '@services/siteConfig.service';

@Component({
  selector: 'app-my-account-payment-and-delivery',
  templateUrl: './payment-and-delivery-options.component.html',
  styleUrls: ['./payment-and-delivery-options.component.scss'],
})
export class PaymentAndDeliveryOptionsComponent implements OnInit {
  uri = 'payment-and-delivery-options';
  isDefaultPaymentOption = false;
  responseType = '';
  responseMessage = '';
  responseTypePayment = '';
  responseMessagePayment = '';
  boxOpenIcon = faBoxOpen;
  fileIcon = faFileLines;
  checkIcon = faCheck;
  checkCircleIcon = faCheckCircle;
  paymentOptions$: Observable<any>;
  deliveryOptions$: Observable<any>;
  showConfirm = false;
  selectedPaymentMethodId = '';
  currentSiteId: string = this.siteConfigService.getCurrentSiteId();
  openRequestInvoicePayment: false;

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private ngZone: NgZone,
    private windowRef: WindowRef,
    private translation: TranslationService,
    private siteConfigService: SiteConfigService,
  ) {}

  setDefaultDeliveryOption(deliveryCode: string) {
    this.responseMessage = '';
    this.responseType = '';

    //let's update the details now
    this.http
      .put<any>(this.occEndpoints.buildUrl(`/users/current/set-delivery-option?shippingOption=${deliveryCode}`), {})
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
          this.translation
            .translate('form.selected_delivery_method_set_as_default')
            .pipe(first())
            .subscribe((val) => (this.responseMessage = val));
          this.getDeliveryOptions();
          this.ngZone.run(() => {
            setTimeout(() => {
              this.responseType = '';
              this.responseMessage = '';
            }, 5000);
          });
        },
      );
  }

  setDefaultPaymentOption(paymentCode: string) {
    this.responseMessagePayment = '';
    this.responseTypePayment = '';

    //let's update the details now
    this.http
      .put<any>(this.occEndpoints.buildUrl(`/users/current/set-payment-option?paymentOption=${paymentCode}`), {})
      .pipe(first())
      .subscribe(
        () => {},
        (response) => {
          this.responseTypePayment = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessagePayment = err.message;
          });
        },
        () => {
          //completed
          this.responseTypePayment = 'success';
          this.translation
            .translate('form.selected_payment_method_set_to_default')
            .pipe(first())
            .subscribe((val) => (this.responseMessagePayment = val));
          this.getPaymentOptions();
          this.ngZone.run(() => {
            setTimeout(() => {
              this.responseTypePayment = '';
              this.responseMessagePayment = '';
            }, 5000);
          });
        },
      );
  }

  ngOnInit() {
    this.getDeliveryOptions();
    this.getPaymentOptions();
  }

  getDeliveryOptions() {
    this.deliveryOptions$ = this.http
      .get<any>(this.occEndpoints.buildUrl('/users/current/deliverymodes'))
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));
  }

  getPaymentOptions() {
    this.paymentOptions$ = this.http
      .get<any>(this.occEndpoints.buildUrl('/users/current/paymentmodes?fields=FULL'))
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));
  }

  removeCC(paymentId) {
    this.selectedPaymentMethodId = paymentId;
    this.showConfirm = true;
  }

  confirmedRemoveCreditCard(returnedEvent: any) {
    this.responseType = '';
    this.responseMessage = '';

    if (returnedEvent === 'cancelled') {
      this.showConfirm = false;
    } else {
      //let's set default shipping address
      this.http
        .delete(this.occEndpoints.buildUrl(`/users/current/remove-payment-info/${this.selectedPaymentMethodId}`))
        .pipe(take(1))
        .subscribe(
          () => {
            //save as default success
            this.showConfirm = false;
            this.responseType = 'success';
            this.translation
              .translate('form.credit_card_removed')
              .pipe(first())
              .subscribe((val) => (this.responseMessage = val));
            this.scrollToTop();
            this.reset();
            this.getPaymentOptions();
          },
          (response) => {
            this.responseType = 'danger'; //error
            response?.error?.errors.forEach((err) => {
              this.responseMessage = err.message;
              this.showConfirm = false;
              this.scrollToTop();
            });
          },
          () => {
            //completed
          },
        );
    }
  }

  seDefaultCreditCard(paymentId) {
    this.responseType = '';
    this.responseMessage = '';

    //let's set default credit card as payment method
    this.http
      .post<any>(
        this.occEndpoints.buildUrl(`/users/current/set-payment-info/${paymentId}?paymentOption=CreditCard`),
        {},
      )
      .pipe(take(1))
      .subscribe(
        () => {
          //save as default success
          this.showConfirm = false;
          this.responseType = 'form.success';
          this.responseMessage = 'form.credit_card_set_default';
          this.scrollToTop();
          this.reset();
          this.getPaymentOptions();
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
            this.showConfirm = false;
            this.scrollToTop();
          });
        },
        () => {
          //completed
        },
      );
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

  reset() {
    this.ngZone.run(() => {
      setTimeout(() => {
        this.responseType = '';
        this.responseMessage = '';
      }, 5000);
    });
  }

  callRequestInvoicePayment() {
    this.http
      .post<any>(this.occEndpoints.buildUrl(`/users/current/request-invoice-payment-mode`), {})
      .pipe(take(1))
      .subscribe(
        () => {
          //save as default success
          this.showConfirm = false;
          this.responseType = 'success';
          this.responseMessage = 'Your request is successful.';
          this.scrollToTop();
          this.reset();
          this.getPaymentOptions();
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
            this.showConfirm = false;
            this.scrollToTop();
          });
        },
      );
  }
}
