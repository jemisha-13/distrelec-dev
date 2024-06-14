import { AfterViewInit, Component, Input } from '@angular/core';
@Component({
  selector: 'app-payment-form',
  templateUrl: './payment-form.component.html',
})
export class PaymentFormComponent implements AfterViewInit {
  @Input() data: any;

  constructor() {}

  ngAfterViewInit(): void {}
}
