import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsletterSuccessLayoutModule } from './layout-config/newsletter-success-layout.module';
import { provideDefaultConfig, CmsConfig, I18nModule } from '@spartacus/core';
import { NewsletterSuccessComponent } from './newsletter-success.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [CommonModule, NewsletterSuccessLayoutModule, I18nModule, RouterModule],
  declarations: [NewsletterSuccessComponent],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        NewsletterSuccessComponent: {
          component: NewsletterSuccessComponent,
        },
      },
    }),
  ],
})
export class NewsletterSuccesModule {}
