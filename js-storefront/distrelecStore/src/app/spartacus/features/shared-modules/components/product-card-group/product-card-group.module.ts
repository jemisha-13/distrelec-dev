import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { DistProductCardModule } from '@design-system/product-card/product-card.module';
import { ProductCardGroupComponent } from './product-card-group.component';
import { ProductCardHolderComponent } from './product-card-holder/product-card-holder.component';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, DistProductCardModule, I18nModule],
  declarations: [ProductCardGroupComponent, ProductCardHolderComponent],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        DistProductCardGroupComponent: {
          component: () => import('./product-card-group.component').then((m) => m.ProductCardGroupComponent),
        },
      },
    }),
  ],
})
export class DistProductCardGroupModule {}
