import { mapToCanActivate, Routes } from '@angular/router';
import { BomToolUploadPageComponent } from '@features/pages/bom-tool/bom-tool-upload-page/bom-tool-upload-page.component';
import { CmsPageGuard } from '@spartacus/storefront';
import { BomToolReviewPageComponent } from '@features/pages/bom-tool/bom-tool-review-page/bom-tool-review-page.component';
import { BomToolMatchingPageComponent } from '@features/pages/bom-tool/bom-tool-matching-page/bom-tool-matching-page.component';

export const bomToolRoutes: Routes = [
  {
    path: '',
    component: BomToolUploadPageComponent,
    canActivate: mapToCanActivate([CmsPageGuard]),
    data: {
      pageLabel: '/bom-tool',
    },
  },
  {
    path: 'review',
    component: BomToolReviewPageComponent,
    canActivate: mapToCanActivate([CmsPageGuard]),
    pathMatch: 'full',
    data: {
      pageLabel: '/bom-tool/review',
    },
  },
  {
    path: 'review-file',
    component: BomToolReviewPageComponent,
    canActivate: mapToCanActivate([CmsPageGuard]),
    pathMatch: 'full',
    data: {
      pageLabel: '/bom-tool/review-file',
    },
  },
  {
    path: 'load-file',
    component: BomToolReviewPageComponent,
    canActivate: mapToCanActivate([CmsPageGuard]),
    pathMatch: 'full',
    data: {
      pageLabel: '/bom-tool/load-file',
    },
  },
  {
    path: 'matching',
    component: BomToolMatchingPageComponent,
    canActivate: mapToCanActivate([CmsPageGuard]),
    pathMatch: 'full',
    data: {
      pageLabel: '/bom-tool/matching',
    },
  },
];
