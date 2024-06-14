import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BackorderLayoutModule } from './layout-config/layout-category.module';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    BackorderLayoutModule,
    RouterModule.forChild([
      {
        path: 'checkout/backorderDetails',
        loadChildren: () => import('./back-order.module').then((m) => m.BackOrderModule),
      },
    ]),
  ],
})
export class BackOrderFeatureModule {}
