import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';

import { ProductsCarouselComponent } from './products-carousel.component';

import { AccessoriesModule } from './accessories/accessories.module';
import { AlternativeModule } from './alternative/alternative.module';
import { ReevooModule } from '@features/shared-modules/reevoo/reevoo.module';
import { SharedModule } from '@features/shared-modules/shared.module';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        DistProductFFCarouselComponent: {
          component: ProductsCarouselComponent,
        },
      },
    } as CmsConfig),
    I18nModule,
    RouterModule,
    AccessoriesModule,
    AlternativeModule,
    ReevooModule,
    SharedModule,
  ],
  declarations: [ProductsCarouselComponent],
})
export class ProductsCarouselModule {}
