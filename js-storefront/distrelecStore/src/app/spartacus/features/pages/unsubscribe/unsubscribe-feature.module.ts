import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'newsletter/unsubscribe-feedback',
        loadChildren: () => import('./unsubscribe.module').then((m) => m.UnsubscribeModule),
      },
    ]),
  ],
})
export class UnsubscribeFeatureModule {}
