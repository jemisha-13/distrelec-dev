import { NgModule } from '@angular/core';
import { StripHTMLTagsPipe } from './strip-html-tags-pipe';

@NgModule({
  declarations: [StripHTMLTagsPipe],
  exports: [StripHTMLTagsPipe],
})
export class StripHTMLTagsPipesModule {}
