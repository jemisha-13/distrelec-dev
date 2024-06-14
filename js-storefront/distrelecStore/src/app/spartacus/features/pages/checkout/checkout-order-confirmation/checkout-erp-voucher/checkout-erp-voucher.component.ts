import { Component, Input } from '@angular/core';
import { GeneratedVoucher } from '@model/checkout.model';

@Component({
  selector: 'app-checkout-erp-voucher',
  templateUrl: './checkout-erp-voucher.component.html',
  styleUrls: ['./checkout-erp-voucher.component.scss'],
})
export class CheckoutErpVoucherComponent {
  @Input() erpVoucher: GeneratedVoucher;

  constructor() {}
}
