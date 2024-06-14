import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FUSION_PRODUCT_NORMALIZER } from '@features/pages/product/core/fusion/converters/injection-tokens';
import { DistFusionProductNormalizer } from '@features/pages/product/core/fusion/converters/dist-fusion-product-normalizer';

@NgModule({
  imports: [CommonModule],
  providers: [
    {
      provide: FUSION_PRODUCT_NORMALIZER,
      useExisting: DistFusionProductNormalizer,
      multi: true,
    },
  ],
})
export class DistProductFusionModule {}
