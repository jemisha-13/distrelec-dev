import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule } from '@spartacus/core';
import { PageComponentModule } from '@spartacus/storefront';

import { SharedModule } from '@features/shared-modules/shared.module';

import { ContentComponent } from './content.component';
import { CategoryThumbsComponent } from './category-thumbs/category-thumbs.component';
import { CategoryDescriptionComponent } from './category-description/category-description.component';
import { DistComponentGroupModule } from '@features/shared-modules/dist-component-group/dist-component-group.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        DistCategoryThumbsComponent: {
          component: ContentComponent,
        },
      },
    } as CmsConfig),
    RouterModule,
    SharedModule,
    DistComponentGroupModule,
    PageComponentModule,
    DistIconModule,
  ],
  declarations: [ContentComponent, CategoryThumbsComponent],
  exports: [DistIconModule],
})
export class CategoryPageContentModule {}
