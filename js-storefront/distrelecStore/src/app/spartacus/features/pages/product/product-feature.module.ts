import { NgModule } from '@angular/core';
import { ProductModule, ProductOccModule } from '@spartacus/core';
import { ProductDetailsPageModule, ProductListModule, ProductPageEventModule } from '@spartacus/storefront';

import { ProductCoreModule } from '@features/pages/product/core/product-core.module';
import { PdpFeatureModule } from '@features/pages/product/pdp/pdp-feature.module';
import { PlpFeatureModule } from '@features/pages/product/plp/plp-feature.module';

@NgModule({
  imports: [
    // Spartacus
    ProductModule.forRoot(),
    ProductOccModule,
    // Product UI,
    ProductDetailsPageModule,
    ProductListModule,
    ProductPageEventModule,

    // Custom
    ProductCoreModule,
    PdpFeatureModule,
    PlpFeatureModule,
  ],
})
export class ProductFeatureModule {}
