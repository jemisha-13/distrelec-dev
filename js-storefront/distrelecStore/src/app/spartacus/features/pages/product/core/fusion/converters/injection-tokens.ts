import { InjectionToken } from '@angular/core';
import { Converter } from '@spartacus/core/src/util/converter.service';
import { Product } from '@spartacus/core/src/model/product.model';
import { FusionProduct, FusionProductSearch } from '@model/fusion-product-search.model';
import { CarouselProductData } from '@model/product-search.model';

export const FUSION_PRODUCT_NORMALIZER = new InjectionToken<Converter<FusionProduct, Product>>(
  'FusionProductNormalizer',
);

export const FUSION_FEEDBACK_CAMPAIGNS_NORMALIZER = new InjectionToken<
  Converter<FusionProductSearch, CarouselProductData>
>('FusionFeedbackCampaignsNormalizer');
