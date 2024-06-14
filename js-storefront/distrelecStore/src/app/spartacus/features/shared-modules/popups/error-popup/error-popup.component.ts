import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-error-popup',
  templateUrl: './error-popup.component.html',
  styleUrls: ['./error-popup.component.scss'],
})
export class ErrorPopupComponent {
  @Input() data: {
    title: string;
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
