import { NgModule } from '@angular/core';
import { MetaTagConfigModule } from '@spartacus/core';
import { BaseStorefrontModule } from '@spartacus/storefront';
import { SpartacusConfigurationModule } from './spartacus-configuration.module';
import { SpartacusFeaturesModule } from './spartacus-features.module';

@NgModule({
  declarations: [],
  imports: [BaseStorefrontModule, SpartacusFeaturesModule, SpartacusConfigurationModule, MetaTagConfigModule.forRoot()],
  exports: [BaseStorefrontModule, SpartacusFeaturesModule],
})
export class SpartacusModule {}
