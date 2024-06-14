import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConfigModule } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

const npsPageLayoutConfig: LayoutConfig = {
  layoutSlots: {
    netPromoterScorePageTemplate: {
      slots: ['netPromoterScorePage'],
    },
  },
};

@NgModule({
  imports: [
    ConfigModule.withConfig(npsPageLayoutConfig),
    RouterModule.forChild([
      {
        path: 'feedback/nps',
        loadChildren: () => import('./nps.module').then((m) => m.NpsModule),
      },
    ]),
  ],
})
export class NpsFeatureModule {}
