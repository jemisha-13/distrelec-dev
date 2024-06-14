import { Component, Input } from '@angular/core';
import { Order } from '@spartacus/order/root';
import { Router } from '@angular/router';

@Component({
  selector: 'app-checkout-manage-your-account',
  templateUrl: './checkout-manage-your-account.component.html',
  styleUrls: ['./checkout-manage-your-account.component.scss'],
})
export class CheckoutManageYourAccountComponent {
  @Input() order: Order;
  @Input() isCustomerB2C: Boolean;

  constructor(private router: Router) {}

  redirectToMyAccount(): void {
    this.router.navigate([
      this.isCustomerB2C ? '/my-account/my-account-information' : '/my-account/company/information',
    ]);
  }
}
