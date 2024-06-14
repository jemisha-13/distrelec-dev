import { NgModule } from '@angular/core';
import {
  OccProductAdapter,
  OccProductReferencesAdapter,
  OccProductReferencesListNormalizer,
  OccProductReviewsAdapter,
  PRODUCT_NORMALIZER,
  PRODUCT_REFERENCES_NORMALIZER,
  ProductAdapter,
  ProductImageNormalizer,
  ProductNameNormalizer,
  ProductReferencesAdapter,
  ProductReviewsAdapter,
  provideDefaultConfig,
} from '@spartacus/core';
import { defaultOccProductConfig } from '@features/pages/product/core/occ/default-occ-product-config';
import { CommonModule } from '@angular/common';
import { DistOccProductNormalizer } from '@features/pages/product/core/occ/converters/dist-occ-product-normalizer';

/**
 * Custom ProductOccModule to disable some providers which are handled by DynamicSearchExperienceModule (Fusion & FactFinder)
 */
@NgModule({
  imports: [CommonModule],
  providers: [
    provideDefaultConfig(defaultOccProductConfig),
    {
      provide: ProductAdapter,
      useClass: OccProductAdapter,
    },
    {
      provide: PRODUCT_NORMALIZER,
      useExisting: DistOccProductNormalizer,
      multi: true,
    },
    {
      provide: PRODUCT_NORMALIZER,
      useExisting: ProductNameNormalizer,
      multi: true,
    },
    {
      provide: ProductReferencesAdapter,
      useClass: OccProductReferencesAdapter,
    },
    {
      provide: PRODUCT_REFERENCES_NORMALIZER,
      useExisting: OccProductReferencesListNormalizer,
      multi: true,
    },
    {
      provide: ProductReviewsAdapter,
      useClass: OccProductReviewsAdapter,
    },
  ],
})
export class DistProductOccModule {}
