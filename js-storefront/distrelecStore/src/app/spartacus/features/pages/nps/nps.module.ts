import { NgModule } from '@angular/core';
import { mapToCanActivate, RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CmsPageGuard, OutletModule, PageLayoutModule, PageSlotModule } from '@spartacus/storefront';
import { I18nModule } from '@spartacus/core';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '@features/shared-modules/shared.module';
import { AccountInformationModule } from '@features/pages/my-account/account-information.module';
import { NpsComponent } from './nps.component';
import { NgSelectModule } from '@ng-select/ng-select';

const staticRoutes: Routes = [
  {
    path: '',
    component: NpsComponent,
    canActivate: mapToCanActivate([CmsPageGuard]),
    data: {
      pageLabel: '/feedback/nps',
    },
  },
];

@NgModule({
  declarations: [NpsComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(staticRoutes),
    SharedModule,
    PageSlotModule,
    PageLayoutModule,
    OutletModule,
    I18nModule,
    FormsModule,
    AccountInformationModule,
    NgSelectModule,
  ],
})
export class NpsModule {}
