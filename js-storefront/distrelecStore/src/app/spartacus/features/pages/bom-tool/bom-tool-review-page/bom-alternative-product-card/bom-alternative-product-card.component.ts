import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-bom-alternative-product-card',
  templateUrl: './bom-alternative-product-card.component.html',
  styleUrls: ['./bom-alternative-product-card.component.scss'],
})
export class BomAlternativeProductCardComponent implements OnInit {
  @Input() product: ICustomProduct;
  @Input() referenceType?: string;
  @Input() index: number;
  @Input() isSelected: boolean;

  @Output() selectAlternative = new EventEmitter<ICustomProduct>();

  isModalOpen = false;

  get thumbnailSrc() {
    const image = this.product.images[0];
    if (!image) {
      return '/app/spartacus/assets/media/img/missing_portrait_small.png';
    }

    return image.url;
  }

  constructor() {}

  ngOnInit(): void {}

  openModal() {
    this.isModalOpen = true;
  }

  onClose() {
    this.isModalOpen = false;
  }

  onSelect() {
    this.selectAlternative.emit(this.product);
  }
}
