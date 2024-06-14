import { CommonModule } from '@angular/common';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { ProductListComponentService } from '@spartacus/storefront';

import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { SearchExperienceService } from '@features/pages/product/core/services/search-experience.service';
import { DynamicSearchExperienceModule } from '@features/pages/product/core/dynamic/dynamic-search-experience.module';
import { DistProductOccModule } from '@features/pages/product/core/occ/dist-product-occ.module';
import { DistProductFusionModule } from '@features/pages/product/core/fusion/dist-product-fusion.module';
import { DistFusionFeedbackCampaignsModule } from './fusion/dist-feedback-campaigns.module';

@NgModule({
  imports: [
    CommonModule,
    DistProductOccModule,
    DistProductFusionModule,
    DistFusionFeedbackCampaignsModule,
    DynamicSearchExperienceModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: (searchExperienceService: SearchExperienceService) => () => searchExperienceService.init(),
      deps: [SearchExperienceService],
      multi: true,
    },
    {
      provide: ProductListComponentService,
      useExisting: DistProductListComponentService,
    },
  ],
})
export class ProductCoreModule {}
