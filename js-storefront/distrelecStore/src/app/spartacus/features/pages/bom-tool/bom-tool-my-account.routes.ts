import { mapToCanActivate, Routes } from '@angular/router';
import { CmsPageGuard } from '@spartacus/storefront';
import { BomToolSavedEntriesComponent } from '@features/pages/bom-tool/bom-tool-saved-entries/bom-tool-saved-entries.component';
import { MyAccountAuthGuard } from '@features/guards/my-account-auth.guard';

export const bomToolMyAccountRoutes: Routes = [
  {
    path: 'savedBomEntries',
    component: BomToolSavedEntriesComponent,
    canActivate: mapToCanActivate([CmsPageGuard, MyAccountAuthGuard]),
    pathMatch: 'full',
    data: {
      pageLabel: '/my-account/savedBomEntries',
    },
  },
];
