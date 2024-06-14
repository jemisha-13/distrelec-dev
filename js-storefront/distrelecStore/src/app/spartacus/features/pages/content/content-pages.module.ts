import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { mapToCanActivate, RouterModule } from '@angular/router';
import { CmsPageGuard, PageLayoutComponent } from '@spartacus/storefront';
import { ContentPageRedirectGuard } from '@features/pages/content/content-page-redirect.guard';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: 'cms/:pageIdOrLabel',
        canActivate: mapToCanActivate([ContentPageRedirectGuard, CmsPageGuard]),
        component: PageLayoutComponent,
      },
      {
        path: ':pageTitle/cms/:pageIdOrLabel',
        canActivate: mapToCanActivate([ContentPageRedirectGuard, CmsPageGuard]),
        component: PageLayoutComponent,
      },
    ]),
  ],
})
export class ContentPagesModule {}
