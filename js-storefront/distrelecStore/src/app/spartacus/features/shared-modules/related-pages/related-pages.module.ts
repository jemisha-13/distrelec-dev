import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RelatedPagesComponent } from './related-pages.component';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        RelatedDataComponent: {
          component: RelatedPagesComponent,
        },
      },
    } as CmsConfig),
    I18nModule,
    RouterModule,
  ],
  declarations: [RelatedPagesComponent],
  exports: [RelatedPagesComponent],
})
export class RelatedPagesModule {}
