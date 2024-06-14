import { Component, EventEmitter, Input, Output } from '@angular/core';
import { close, confirmation } from '@assets/icons/icon-index';

@Component({
  selector: 'app-chip',
  templateUrl: './chip.component.html',
  styleUrls: ['./chip.component.scss'],
})
export class ChipComponent {
  @Input() iconLeft? = false;
  @Input() iconRight? = true;
  @Input() type?: 'squared' | 'round' = 'squared';
  @Input() text: string;
  @Input() id?: string;
  @Input() tooltip?: string;

  @Output() iconClick = new EventEmitter<void>();

  iconClose = close;
  iconConfirmation = confirmation;

  onIconClick(): void {
    this.iconClick.emit();
  }
}
