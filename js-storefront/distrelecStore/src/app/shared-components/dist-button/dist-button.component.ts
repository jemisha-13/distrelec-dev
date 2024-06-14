import { Component, Input } from '@angular/core';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';

@Component({
  selector: 'app-dist-button',
  templateUrl: './dist-button.component.html',
  styleUrls: ['./dist-button.component.scss'],
})
export class DistButtonComponent {
  @Input() width: 'w-fill' | 'w-fixed' | 'w-large' | 'w-medium' | 'w-small' = 'w-medium';
  @Input() type: 'primary' | 'secondary' | 'outlined' = 'primary';
  @Input() height: 'h-small' | 'h-default' | 'h-large' = 'h-default';
  @Input() isDisabled = false;
  @Input() text = 'Button';
  @Input() icon: IconDefinition | undefined = undefined;
}
