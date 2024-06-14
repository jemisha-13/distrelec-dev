import { Component, Input } from '@angular/core';
import { faCopy } from '@fortawesome/free-regular-svg-icons';
import { CopyProductNumberService } from '@services/copy-product-number.service';
import { Product } from '@spartacus/core';

@Component({
  selector: 'app-product-description-attributes',
  templateUrl: './product-description-attributes.component.html',
  styleUrls: ['./product-description-attributes.component.scss'],
})
export class ProductDescriptionAttributesComponent {
  @Input() product: Product;
  @Input() index: number;

  readonly copiedState$ = this.copyProductNumberService.copiedState$;
  readonly faCopy = faCopy;

  constructor(private copyProductNumberService: CopyProductNumberService) {}

  copyToClipboard(text: string, key: string, origin?: string): void {
    this.copyProductNumberService.copyNumber(text, key, origin);
  }
}
