import { Component, Input, EventEmitter, Output } from '@angular/core';
import { faExclamationCircle } from '@fortawesome/free-solid-svg-icons';
import { BackorderService } from '@services/backorder.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-back-order-item',
  templateUrl: './back-order-item.component.html',
  styleUrls: ['./back-order-item.component.scss'],
})
export class BackOrderItemComponent {
  @Input() entry;
  @Input() entryNumber;

  @Output() sendEventToParent = new EventEmitter<any>();

  faExclamationCircle = faExclamationCircle;
  alternativeDisplay: boolean = false;
  alternativeEntries$: Observable<any>;

  constructor(private backorderService: BackorderService) {}

  displayAlternatives() {
    this.alternativeDisplay = !this.alternativeDisplay;

    if (this.alternativeDisplay === true) {
      this.alternativeEntries$ = this.backorderService.getAlternativeItems(
        this.entry.product.code,
        this.entry.quantity,
      );
    }
  }

  sendCodeToParent(productCode: any): void {
    this.sendEventToParent.emit(productCode);
  }
}
