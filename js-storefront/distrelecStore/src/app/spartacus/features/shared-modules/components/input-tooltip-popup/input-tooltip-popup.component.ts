import { Component, Input } from '@angular/core';

export type PopupAlignment = 'left' | 'center' | 'right';
export type PopupPosition = 'right' | 'top';

@Component({
  selector: 'app-input-tooltip-popup',
  templateUrl: './input-tooltip-popup.component.html',
  styleUrls: ['./input-tooltip-popup.component.scss'],
})
export class InputTooltipPopupComponent {
  constructor() {}
  @Input() alignment?: PopupAlignment = 'center';
  @Input() id?: string;
  @Input() content?: string;
  @Input() position?: PopupPosition = 'top';
}
