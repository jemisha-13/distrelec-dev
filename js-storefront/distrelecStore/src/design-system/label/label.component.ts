import { Component, Input } from '@angular/core';
import { bullet } from '@assets/icons/icon-index';

@Component({
  selector: 'app-label',
  templateUrl: './label.component.html',
  styleUrls: ['./label.component.scss'],
})
export class LabelComponent {
  @Input() color: string | 'dark' | 'neutral' | 'error' | 'success' | 'warning' = 'neutral';
  @Input() size: 'small' | 'medium' | 'large' = 'large';
  @Input() style: 'fill' | 'line' | 'bullet' = 'fill';

  @Input() text = 'Label';
  @Input() ids?: { labelId: string };

  bulletIcon = bullet;
}
