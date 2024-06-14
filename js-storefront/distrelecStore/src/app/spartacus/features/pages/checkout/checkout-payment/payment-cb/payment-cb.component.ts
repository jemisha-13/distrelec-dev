import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LocalStorageService } from '@services/local-storage.service';
import { PaymentService } from '@services/payment.service';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-payment-cb',
  templateUrl: './payment-cb.component.html',
})
export class PaymentCbComponent implements OnInit, OnDestroy {
  isPaypal: string | undefined;

  private subscriptions: Subscription = new Subscription();

  constructor(
    protected activatedRoute: ActivatedRoute,
    private paymentService: PaymentService,
    private localStorage: LocalStorageService,
  ) {}

  ngOnInit(): void {
    if (this.localStorage.getItem('paypal')) {
      this.isPaypal = this.localStorage.getItem('paypal');
    }
    const url = this.activatedRoute.snapshot.url.join().split(',');
    this.subscriptions.add(
      this.activatedRoute.queryParams
        .pipe(
          switchMap((params) =>
            this.paymentService.postPaymentRequest(params, url.includes('success') ? 'success' : 'failure'),
          ),
        )
        .subscribe(),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
