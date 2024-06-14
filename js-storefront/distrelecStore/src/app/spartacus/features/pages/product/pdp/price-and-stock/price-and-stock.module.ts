import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PriceAndStockComponent } from './price-and-stock.component';
import { I18nModule } from '@spartacus/core';
import { StockComponent } from './stock/stock.component';
import { ReactiveFormsModule } from '@angular/forms';
import { PricesModule } from './prices/prices.module';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { LetModule } from '@rx-angular/template/let';
import { IfModule } from '@rx-angular/template/if';
import { ForModule } from '@rx-angular/template/for';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { AddToCartModule } from '@features/pages/product/common/add-to-cart/add-to-cart.module';
import { NgOptimizedImage } from '@angular/common';
import { DividerLineModule } from '@design-system/divider-line/divider-line.module';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    ReactiveFormsModule,
    PricesModule,
    ComponentLoadingSpinnerModule,
    SharedModule,
    NumericStepperComponentModule,
    DistIconModule,
    AddToCartModule,
    NgOptimizedImage,
    DividerLineModule,
  ],
  declarations: [PriceAndStockComponent, StockComponent],
  exports: [PriceAndStockComponent, StockComponent, PricesModule, LetModule, IfModule, ForModule],
})
export class PriceAndStockModule {}
