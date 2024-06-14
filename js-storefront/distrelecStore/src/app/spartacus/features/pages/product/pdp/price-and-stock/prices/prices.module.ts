import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { ReactiveFormsModule } from '@angular/forms';
import { PricesComponent } from './prices.component';
import { ReplaceStringModule } from '@pipes/replace-string.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import { BulkDiscountLabelComponent } from './bulk-discount-label/bulk-discount-label.component';
import { MainPriceComponent } from './main-price/main-price.component';
import { PricesXUOMComponent } from './prices-xuom/prices-xuom.component';
import { DiscountPricesModule } from './discount-prices/discount-prices.module';
import { VolumePricesComponent } from './volume-prices/volume-prices.component';
import { StructuredDataModule } from '@spartacus/storefront';
import { DistJsonLdModule } from '@features/shared-modules/directives/dist-json-ld.module';
import { AlertBannerComponentModule } from '@design-system/alert-banner/alert-banner.module';

@NgModule({
  imports: [
    CommonModule,
    FontAwesomeModule,
    I18nModule,
    ReactiveFormsModule,
    ReplaceStringModule,
    SharedModule,
    DistJsonLdModule,
    VolumePricePipeModule,
    DecimalPlacesPipeModule,
    DiscountPricesModule,
    StructuredDataModule,
    AlertBannerComponentModule,
  ],
  declarations: [
    PricesComponent,
    BulkDiscountLabelComponent,
    MainPriceComponent,
    PricesXUOMComponent,
    VolumePricesComponent,
  ],
  exports: [PricesComponent],
})
export class PricesModule {}
