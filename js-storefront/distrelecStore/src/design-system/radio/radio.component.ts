import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-radio',
  templateUrl: './radio.component.html',
  styleUrls: ['./radio.component.scss'],
})
export class RadioComponent {
  @Input() size: 'large' | 'small' = 'large';
  @Input() type: 'standard' | 'emphasised' = 'standard';
  @Input() disabled = false;
  @Input() checked = false;
  @Input() primaryLabelText?: string;
  @Input() secondaryLabelText?: string;
  @Input() radioID?: string;
  @Input() radioName: string;
  @Input() radioValue: string;
  @Input() primaryLabelID?: string;
  @Input() secondaryLabelID?: string;
}
