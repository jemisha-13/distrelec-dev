import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UnsubscribeComponent } from './unsubscribe.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { SharedModule } from '@features/shared-modules/shared.module';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { mapToCanActivate, RouterModule } from '@angular/router';
import { CmsPageGuard } from '@spartacus/storefront';

@NgModule({
  imports: [
    CommonModule,
    FontAwesomeModule,
    SharedModule,
    I18nModule,
    ReactiveFormsModule,
    ConfigModule.forRoot({
      cmsComponents: {
        UnsubscribeFeedbackComponent: {
          component: UnsubscribeComponent,
        },
      },
    } as CmsConfig),
    FormsModule,
    ComponentLoadingSpinnerModule,
    RouterModule.forChild([
      {
        path: '',
        component: UnsubscribeComponent,
        canActivate: mapToCanActivate([CmsPageGuard]),
        data: {
          pageLabel: '/newsletter/unsubscribe-feedback',
        },
      },
    ]),
  ],
  declarations: [UnsubscribeComponent],
  exports: [UnsubscribeComponent],
})
export class UnsubscribeModule {}
