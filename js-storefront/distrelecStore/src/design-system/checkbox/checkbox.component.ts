import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-checkbox',
  templateUrl: './checkbox.component.html',
  styleUrls: ['./checkbox.component.scss'],
})
export class CheckboxComponent {
  @Input() size: 'large' | 'small' = 'large';
  @Input() disabled = false;
  @Input() checked = false;
  @Input() primaryLabel?: string;
  @Input() primaryLabelID?: string;
  @Input() secondaryLabel?: string;
  @Input() secondaryLabelID?: string;
  @Input() checkboxID: string;
  @Input() checkboxValue?: string;
  @Input() checkboxName?: string;
}
