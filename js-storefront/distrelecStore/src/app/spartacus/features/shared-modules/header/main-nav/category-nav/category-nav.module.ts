import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryNavComponent } from './category-nav.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { SharedModule } from '@features/shared-modules/shared.module';
import { RemoveHostDirectiveModule } from '@features/shared-modules/directives/remove-host-directive.module';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistScrollBarModule } from '@design-system/scroll-bar/scroll-bar.module';

@NgModule({
  declarations: [CategoryNavComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    RouterModule,
    I18nModule,
    ConfigModule.forRoot({
      cmsComponents: {
        DistMainNavigationComponent: {
          component: CategoryNavComponent,
        },
      },
    } as CmsConfig),
    SharedModule,
    RemoveHostDirectiveModule,
    SlideDrawerModule,
    DistIconModule,
    DistScrollBarModule,
  ],
  exports: [CategoryNavComponent],
})
export class CategoryNavModule {}
