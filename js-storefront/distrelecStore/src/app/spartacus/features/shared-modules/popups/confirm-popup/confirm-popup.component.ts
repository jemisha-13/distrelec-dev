import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { faCircleExclamation, faExclamationTriangle, faInfoCircle, faTrash } from '@fortawesome/free-solid-svg-icons';

export enum typeScope {
  DEFAULT = 'BASIC',
  PRIMARY = 'DEFAULT',
  SUCCESS = 'FULL',
  INFO = 'INFO',
  WARNING = 'WARNING',
  DANGER = 'DANGER',
}

@Component({
  selector: 'app-confirm-popup',
  templateUrl: './confirm-popup.component.html',
  styleUrls: ['./confirm-popup.component.scss'],
})
export class ConfirmPopupComponent implements OnInit {
  faIcon: any;
  isDisabled = false;

  @Input() buttonText = '';
  @Input() data: {
    title: string;
    content: string;
    type?: string;
    link?: string;
  };
  @Input() noCancelButton = false;

  @Output() confirmedEvents = new EventEmitter<'confirmed' | 'cancelled'>();

  constructor() {}

  ngOnInit() {
    this.getFaIcon();
  }

  onCloseButton() {
    this.confirmedEvents.emit('cancelled');
  }

  onYesButton() {
    this.isDisabled = true;
    this.confirmedEvents.emit('confirmed');
  }

  getFaIcon() {
    switch (this.data.type) {
      case 'info':
        this.faIcon = faInfoCircle;
        break;
      case 'warning':
        this.faIcon = faCircleExclamation;
        break;
      case 'danger':
        this.faIcon = faTrash;
        break;
      default:
        this.faIcon = faExclamationTriangle;
        break;
    }
  }
}
