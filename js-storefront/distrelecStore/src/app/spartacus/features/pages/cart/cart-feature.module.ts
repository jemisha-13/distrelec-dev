import { NgModule } from '@angular/core';
import { HttpErrorHandler, provideConfig } from '@spartacus/core';

import { CartHttpErrorHandler } from './cart-http-error.handler';
import { CartBaseCoreModule } from '@spartacus/cart/base/core';
import { CartBaseOccModule } from '@spartacus/cart/base/occ';

@NgModule({
  declarations: [],
  imports: [CartBaseCoreModule, CartBaseOccModule],
  providers: [
    provideConfig({
      featureModules: {
        cart: {
          module: () => import('@features/pages/cart/cart.module').then((m) => m.DistrelecCartModule),
          cmsComponents: ['CartComponent'],
        },
      },
    }),
    {
      provide: HttpErrorHandler,
      useExisting: CartHttpErrorHandler,
      multi: true,
    },
  ],
})
export class CartFeatureModule {}
