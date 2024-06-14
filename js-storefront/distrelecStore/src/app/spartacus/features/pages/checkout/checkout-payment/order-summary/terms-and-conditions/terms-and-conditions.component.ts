import { Component, Input, ViewEncapsulation } from '@angular/core';
import { CustomerType } from '@model/site-settings.model';

@Component({
  selector: 'app-terms-and-conditions',
  templateUrl: './terms-and-conditions.component.html',
  styleUrls: ['./terms-and-conditions.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class TermsAndConditionsComponent {
  @Input() currentSiteId: string;
  @Input() customerType: CustomerType;

  constructor() {}
}
