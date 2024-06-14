import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { SearchComponent } from './search.component';
import { SearchResultsComponent } from './search-results/search-results.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { QtyAddToCartModule } from './search-results/search-suggestion-add-to-cart/search-suggestion-add-to-cart.module';
import { ParseJsonPipeModule } from '@pipes/parse-json-pipe.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { EnergyEfficiencyLabelModule } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.module';
import { RouterModule } from '@angular/router';
import { ProductImageFactFinderPipeModule } from '@pipes/product-image-factfinder-pipe.module';
import { IfModule } from '@rx-angular/template/if';
import { ForModule } from '@rx-angular/template/for';
import { LetModule } from '@rx-angular/template/let';
import { SharedModule } from '@features/shared-modules/shared.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { ArticleTooltipModule } from '@features/shared-modules/components/article-tooltip/article-tooltip.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistJsonLdModule } from '@features/shared-modules/directives/dist-json-ld.module';

@NgModule({
  declarations: [SearchComponent, SearchResultsComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    I18nModule,
    ReactiveFormsModule,
    QtyAddToCartModule,
    ParseJsonPipeModule,
    EnergyEfficiencyLabelModule,
    RouterModule,
    ArticleNumberPipeModule,
    FormsModule,
    ConfigModule.forRoot({
      cmsComponents: {
        SearchBoxComponent: {
          component: SearchComponent,
        },
      },
    } as CmsConfig),
    ProductImageFactFinderPipeModule,
    IfModule,
    ForModule,
    LetModule,
    SharedModule,
    PricePipeModule,
    SlideDrawerModule,
    ArticleTooltipModule,
    DistIconModule,
    DistJsonLdModule,
  ],
  exports: [SearchComponent],
})
export class HeaderSearchModule {}
