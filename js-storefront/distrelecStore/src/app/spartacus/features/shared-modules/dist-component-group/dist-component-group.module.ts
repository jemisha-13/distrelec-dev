import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CmsConfig, provideConfig } from '@spartacus/core';

import { DistComponentGroupComponent } from './dist-component-group.component';
import { ParseHtmlPipeModule } from '@pipes/parse-html-pipe.module';
import { FeaturedProductsModule } from '@features/pages/homepage/featured-products/featured-products.module';
import { GuestReturnsFormModule } from '@features/pages/guest-returns-form/guest-returns-form.module';
import { PageComponentModule, SupplementHashAnchorsModule } from '@spartacus/storefront';
import { SharedRxModule } from '@features/shared-modules/shared-rx.module';
import { ExternalRouterLinkModule } from '@features/shared-modules/directives/external-router-link.module';

@NgModule({
  declarations: [DistComponentGroupComponent],
  imports: [
    CommonModule,
    RouterModule,
    SharedRxModule,
    ParseHtmlPipeModule,
    FeaturedProductsModule,
    GuestReturnsFormModule,
    SupplementHashAnchorsModule,
    ExternalRouterLinkModule,
    PageComponentModule,
  ],
  exports: [DistComponentGroupComponent],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        DistComponentGroup: {
          component: DistComponentGroupComponent,
        },
      },
    }),
  ],
})
export class DistComponentGroupModule {}
