import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { mapToCanActivate, RouterModule } from '@angular/router';
import { ShoppingListComponent } from './shopping-list.component';
import { BreadcrumbWrapperModule } from '@features/shared-modules/breadcrumb/breadcrumb-wrapper.module';
import { CmsPageGuard } from '@spartacus/storefront';
import { ShoppingListSidebarComponent } from './sidebar/sidebar.component';
import { QtyAddToCartModule } from '@features/shared-modules/header/main-nav/search/search-results/search-suggestion-add-to-cart/search-suggestion-add-to-cart.module';
import { ArticleNumberPipeModule } from 'src/app/spartacus/pipes/article-number-pipe.module';
import { PricesModule } from '@features/pages/product/pdp/price-and-stock/prices/prices.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { ShoppingListSortByComponent } from './sort-by/sort-by.component';
import { PriceAndStockModule } from '@features/pages/product/pdp/price-and-stock/price-and-stock.module';
import { ShoppingListPricingComponent } from './pricing/pricing.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { ShoppingListItemComponent } from './shopping-list-item/shopping-list-item.component';
import { MinOrderQuantityPopupModule } from '@features/shared-modules/components/min-order-quantity-popup/min-order-quantity-popup.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { ErrorPopupComponent } from '@features/shared-modules/popups/error-popup/error-popup.component';
import { EnergyEfficiencyLabelModule } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { AuthGuardWithParams } from '@features/guards/auth-with-params.guard';
import { PricesComponentModule } from '@design-system/prices/prices.module';
import { AlertBannerComponentModule } from '@design-system/alert-banner/alert-banner.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';
import { ShoppingListWarningPopupComponent } from '@features/shared-modules/popups/shopping-list-warning-popup/shopping-list-warning-popup';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        ShoppingListComponent: {
          component: ShoppingListComponent,
        },
      },
    } as CmsConfig),
    FormsModule,
    ReactiveFormsModule,
    I18nModule,
    FontAwesomeModule,
    RouterModule,
    BreadcrumbWrapperModule,
    QtyAddToCartModule,
    ArticleNumberPipeModule,
    PricesModule,
    RouterModule.forChild([
      {
        path: '',
        component: ShoppingListComponent,
        canActivate: mapToCanActivate([CmsPageGuard, AuthGuardWithParams]),
        pathMatch: 'prefix',
        data: {
          pageLabel: '/shopping',
        },
      },
      {
        path: ':id',
        component: ShoppingListComponent,
        canActivate: mapToCanActivate([CmsPageGuard, AuthGuardWithParams]),
        pathMatch: 'prefix',
        data: {
          pageLabel: '/shopping',
        },
      },
    ]),
    NgSelectModule,
    PriceAndStockModule,
    SharedModule,
    MinOrderQuantityPopupModule,
    PricePipeModule,
    EnergyEfficiencyLabelModule,
    VolumePricePipeModule,
    DecimalPlacesPipeModule,
    PricesComponentModule,
    AlertBannerComponentModule,
    AtcButtonModule,
    DistIconModule,
    NumericStepperComponentModule,
  ],
  declarations: [
    ShoppingListComponent,
    ShoppingListSidebarComponent,
    ShoppingListSortByComponent,
    ShoppingListPricingComponent,
    ShoppingListItemComponent,
    ErrorPopupComponent,
    ShoppingListWarningPopupComponent,
  ],
})
export class ShoppingListModule {}
