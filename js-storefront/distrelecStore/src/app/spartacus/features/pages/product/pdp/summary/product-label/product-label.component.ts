import { Component, Input } from '@angular/core';
import { LabelConfigService } from '@services/label-config.service';

@Component({
  selector: 'app-product-label',
  templateUrl: './product-label.component.html',
  styleUrls: ['./product-label.component.scss'],
})
export class ProductLabelComponent {
  @Input() activePromotionLabel;

  constructor(private labelConfigService: LabelConfigService) {}

  getPromoLabelColor(code: string) {
    return this.labelConfigService.getColorByLabel(code);
  }
}
