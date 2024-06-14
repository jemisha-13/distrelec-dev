import { NgModule } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FeaturedProductsComponent } from './featured-products.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { StripHTMLTagsPipesModule } from '@features/shared-modules/pipes/strip-sup-pipes.module';
import { ForModule } from '@rx-angular/template/for';
import { IfModule } from '@rx-angular/template/if';
import { LetModule } from '@rx-angular/template/let';
import { SharedModule } from '@features/shared-modules/shared.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { PromoLabelModule } from '@features/shared-modules/components/promo-label/promo-label.module';

@NgModule({
  declarations: [FeaturedProductsComponent],
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        DistFeaturedProductsComponent: {
          component: () => import('./featured-products.component').then((m) => m.FeaturedProductsComponent),
        },
      },
    } as CmsConfig),
    I18nModule,
    FontAwesomeModule,
    RouterModule,
    StripHTMLTagsPipesModule,
    ForModule,
    IfModule,
    LetModule,
    SharedModule,
    VolumePricePipeModule,
    PromoLabelModule,
    NgOptimizedImage,
  ],
  exports: [FeaturedProductsComponent],
})
export class FeaturedProductsModule {}
