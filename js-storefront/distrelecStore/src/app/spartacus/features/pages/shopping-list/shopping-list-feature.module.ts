import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ShoppingListLayoutModule } from '@features/pages/shopping-list/layout-config/layout-config.module';

@NgModule({
  imports: [
    ShoppingListLayoutModule,
    RouterModule.forChild([
      {
        path: 'shopping',
        loadChildren: () => import('./shopping-list.module').then((m) => m.ShoppingListModule),
      },
    ]),
  ],
})
export class ShoppingListFeatureModule {}
