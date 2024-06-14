import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BreadcrumbWrapperComponent } from './breadcrumb-wrapper.component';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { SlashRemoverPipeModule } from 'src/app/spartacus/pipes/slash-remover.pipe.module';
import { MyAccountBreadCrumbComponent } from './breadcrumb-my-account/my-account-breadcrumb.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { DistJsonLdModule } from '@features/shared-modules/directives/dist-json-ld.module';
import { DistIconModule } from '../icon/icon.module';
import { BreadcrumbsModule } from '@design-system/breadcrumbs/breadcrumbs.module';

@NgModule({
  declarations: [BreadcrumbWrapperComponent, MyAccountBreadCrumbComponent],
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        BreadcrumbComponent: {
          component: BreadcrumbWrapperComponent,
        },
      },
    } as CmsConfig),
    FontAwesomeModule,
    RouterModule,
    I18nModule,
    SlashRemoverPipeModule,
    SharedModule,
    DistJsonLdModule,
    DistIconModule,
    BreadcrumbsModule,
  ],

  exports: [MyAccountBreadCrumbComponent],
})
export class BreadcrumbWrapperModule {}
