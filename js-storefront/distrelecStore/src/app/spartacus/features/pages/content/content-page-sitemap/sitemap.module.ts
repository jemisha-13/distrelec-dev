import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SitemapComponent } from './sitemap.component';
import { SitemapLayoutModule } from './sitemap-layout';
import { I18nModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { PageSlotModule } from '@spartacus/storefront';
import { ExternalRouterLinkModule } from '@features/shared-modules/directives/external-router-link.module';

@NgModule({
  imports: [CommonModule, SitemapLayoutModule, I18nModule, RouterModule, PageSlotModule, ExternalRouterLinkModule],
  exports: [SitemapComponent, SitemapLayoutModule],
  declarations: [SitemapComponent],
})
export class SitemapModule {}
