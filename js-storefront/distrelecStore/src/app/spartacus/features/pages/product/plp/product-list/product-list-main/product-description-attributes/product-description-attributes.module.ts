import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductDescriptionAttributesComponent } from '@features/pages/product/plp/product-list/product-list-main/product-description-attributes/product-description-attributes.component';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { StripHTMLTagsPipesModule } from '@features/shared-modules/pipes/strip-sup-pipes.module';
import { ArticleTooltipModule } from '@features/shared-modules/components/article-tooltip/article-tooltip.module';

@NgModule({
  declarations: [ProductDescriptionAttributesComponent],
  imports: [
    CommonModule,
    ArticleNumberPipeModule,
    FontAwesomeModule,
    I18nModule,
    StripHTMLTagsPipesModule,
    ArticleTooltipModule,
  ],
  exports: [ProductDescriptionAttributesComponent],
})
export class ProductDescriptionAttributesModule {}
