import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-shopping-list-popup',
  templateUrl: './shopping-list-warning-popup.component.html',
  styleUrls: ['./shopping-list-warning-popup.component.scss'],
})
export class ShoppingListWarningPopupComponent {
  @Input() data: {
    subtitle: string;
    subtitleArgument?: string[];
    type: string;
  };
  @Output()
  popupClosed = new EventEmitter();

  constructor() {}
  onCloseButton() {
    this.popupClosed.emit();
  }
}
