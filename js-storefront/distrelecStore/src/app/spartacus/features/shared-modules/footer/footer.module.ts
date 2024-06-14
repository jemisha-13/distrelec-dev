import { NgModule } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FooterLayoutModule } from './layout-config/footer-layout.module';
import { FooterComponent } from './footer.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { NewsletterModule } from './newsletter/newsletter.module';
import { CheckoutFooterComponent } from './checkout-footer/checkout-footer.component';
import { FooterLinkComponent } from './footer-link/footer-link.component';
import { SharedModule } from '../shared.module';
import { IfModule } from '@rx-angular/template/if';
import { SiteSettingsModule } from '../header/main-nav/sitesettings/sitesettings.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  declarations: [FooterComponent, CheckoutFooterComponent, FooterLinkComponent],
  imports: [
    CommonModule,
    I18nModule,
    ConfigModule.forRoot({
      cmsComponents: {
        DistFooterComponent: {
          component: FooterComponent,
        },
      },
    } as CmsConfig),
    RouterModule,
    FooterLayoutModule,
    NewsletterModule,
    FontAwesomeModule,
    RouterModule,
    SiteSettingsModule,
    SharedModule,
    IfModule,
    NgOptimizedImage,
    DistIconModule,
  ],
})
export class FooterModule {}
