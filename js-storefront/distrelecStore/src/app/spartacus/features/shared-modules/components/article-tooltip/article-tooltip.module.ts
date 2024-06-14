import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { ArticleTooltipComponent } from './article-tooltip.component';

@NgModule({
  declarations: [ArticleTooltipComponent],
  exports: [ArticleTooltipComponent],
  imports: [I18nModule],
})
export class ArticleTooltipModule {}
