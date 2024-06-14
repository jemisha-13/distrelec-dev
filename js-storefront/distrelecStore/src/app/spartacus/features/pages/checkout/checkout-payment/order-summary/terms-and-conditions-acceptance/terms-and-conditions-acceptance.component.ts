import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import { faSquareCheck } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-terms-and-conditions-acceptance',
  templateUrl: './terms-and-conditions-acceptance.component.html',
  styleUrls: ['./terms-and-conditions-acceptance.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class TermsAndConditionsAcceptanceComponent {
  // Value which we receive from parent, since summary checkbox is hidden once iframe takes place on screen
  // Once user changes payment method, checkbox is shown again and we need to remember previous state of it
  @Input() summaryTermsTickChecked: boolean;

  @Input() highlightTermsAndConditions: boolean;

  @Output() summaryTermsChanged = new EventEmitter<boolean>();

  faSquare = faSquare;
  faSquareCheck = faSquareCheck;

  constructor() {}

  // As user is changing checkbox state, we are sending value to parent, so we can check if submit btn should be enabled or disabled
  summaryTermsChange(checked: boolean) {
    // Send value to parent
    this.summaryTermsChanged.emit(checked);
  }
}
