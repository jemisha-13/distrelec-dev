import { Component, OnInit } from '@angular/core';
import { CheckoutService } from 'src/app/spartacus/services/checkout.service';

@Component({
  selector: 'app-checkout-register',
  templateUrl: './checkout-register.component.html',
})
export class CheckoutRegisterComponent implements OnInit {
  constructor(private checkoutService: CheckoutService) {}

  ngOnInit() {
    this.checkoutService.checkoutPageSteps_.next({ loginRegisterStep: 'current' });
  }
}
