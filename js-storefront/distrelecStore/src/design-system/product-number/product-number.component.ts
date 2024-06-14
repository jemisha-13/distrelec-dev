import { Component, EventEmitter, Input, Output } from '@angular/core';
import { copy } from '@assets/icons/icon-index';

export interface ProductNumberType {
  artNr?: string;
  MPN?: string;
}

@Component({
  selector: 'app-dist-product-number',
  templateUrl: './product-number.component.html',
  styleUrls: ['./product-number.component.scss'],
})
export class ProductNumberComponent {
  @Input() artNrId: string;
  @Input() mpnId: string;
  @Input() title: string;
  @Input() type: ProductNumberType;

  @Output() copyClick = new EventEmitter<string>();

  copyIcon = copy;

  handleClick(): void {
    const outputString = this.type.MPN ? this.type.MPN : this.type.artNr;
    this.copyClick.emit(outputString);
  }
}
