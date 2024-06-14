import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PRODUCT_SEARCH_PAGE_NORMALIZER, PRODUCT_SUGGESTION_NORMALIZER, ProductSearchAdapter } from '@spartacus/core';

import { DynamicProductSearchPageNormalizer } from '@features/pages/product/core/dynamic/converters/dynamic-product-search-page-normalizer';
import { DynamicProductSearchAdapter } from '@features/pages/product/core/dynamic/adapters/dynamic-product-search.adapter';
import { SessionService } from '../services/abstract-session.service';
import { DynamicSessionService } from './services/dynamic-session.service';
import { DynamicProductSuggestionNormalizer } from './converters/dynamic-product-suggestion-normalizer';
import { SearchQueryService } from '@features/pages/product/core/services/abstract-product-search-query.service';
import { DynamicSearchQueryService } from '@features/pages/product/core/dynamic/services/dynamic-search-query.service';

/***
 * The dynamic providers in this module will lazily create the normalizers and adapters for the configured search engine.
 * We can't use a factory provider here, because the factory would be created before the configuration is loaded during
 * the APP_INITIALIZER.
 */
@NgModule({
  imports: [CommonModule],
  providers: [
    // Customisations
    {
      provide: ProductSearchAdapter,
      useClass: DynamicProductSearchAdapter,
    },
    {
      provide: PRODUCT_SEARCH_PAGE_NORMALIZER,
      useExisting: DynamicProductSearchPageNormalizer,
      multi: true,
    },
    {
      provide: PRODUCT_SUGGESTION_NORMALIZER,
      useExisting: DynamicProductSuggestionNormalizer,
      multi: true,
    },
    {
      provide: SessionService,
      useClass: DynamicSessionService,
    },
    {
      provide: SearchQueryService,
      useClass: DynamicSearchQueryService,
    },
  ],
})
export class DynamicSearchExperienceModule {}
