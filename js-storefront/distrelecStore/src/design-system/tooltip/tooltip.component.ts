import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-tooltip',
  templateUrl: './tooltip.component.html',
  styleUrls: ['./tooltip.component.scss'],
})
export class TooltipComponent {
  @Input() size: 'sm' | 'md' = 'md';
  @Input() position:
    | 'left'
    | 'top-left'
    | 'top'
    | 'top-right'
    | 'right'
    | 'none'
    | 'bottom-right'
    | 'bottom'
    | 'bottom-left' = 'bottom';
  @Input() textAlign: 'left' | 'center' | 'right' = 'left';
  @Input() tooltipID?: string;
}
