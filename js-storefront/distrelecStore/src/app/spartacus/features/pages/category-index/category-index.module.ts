import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, provideConfig } from '@spartacus/core';

import { CategoryIndexComponent } from '@features/pages/category-index/category-index.component';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [CategoryIndexComponent],
  imports: [CommonModule, RouterModule],
  exports: [CategoryIndexComponent],
  providers: [
    provideConfig({
      cmsComponents: {
        CategoryIndexComponent: {
          component: CategoryIndexComponent,
        },
      },
    } as CmsConfig),
  ],
})
export class CategoryIndexModule {}
