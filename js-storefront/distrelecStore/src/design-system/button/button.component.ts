import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';

@Component({
  selector: 'app-dist-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss'],
})
export class DistButtonComponent {
  @ViewChild('button') buttonDOMElement: ElementRef;
  @Input() width: 'w-fill' | 'w-fixed' | 'w-large' | 'w-medium' | 'w-small' = 'w-medium';
  @Input() type: 'primary' | 'secondary' | 'outlined' = 'primary';
  @Input() height: 'h-small' | 'h-default' | 'h-large' = 'h-default';
  @Input() customClass: string;
  @Input() isDisabled: boolean;
  @Input() icon: IconDefinition | undefined = undefined;
  @Input() buttonId: string;
}
