import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { OutletModule, PageLayoutModule, PageSlotModule } from '@spartacus/storefront';
import { I18nModule } from '@spartacus/core';
import { FormsModule } from '@angular/forms';

import { SharedModule } from '@features/shared-modules/shared.module';
import { ReevooModule } from '@features/shared-modules/reevoo/reevoo.module';
import { AccountInformationModule } from '@features/pages/my-account/account-information.module';
import { PriceAndStockModule } from '@features/pages/product/pdp/price-and-stock/price-and-stock.module';

import { BomToolUploadPageComponent } from './bom-tool-upload-page/bom-tool-upload-page.component';
import { BomDataImportComponent } from './bom-tool-upload-page/bom-data-import/bom-data-import.component';
import { BomToolReviewPageComponent } from './bom-tool-review-page/bom-tool-review-page.component';
import { BomToolMatchingPageComponent } from './bom-tool-matching-page/bom-tool-matching-page.component';
import { BomProductComponent } from './bom-tool-review-page/bom-product/bom-product.component';
import { ErpSalesStatusComponent } from './bom-tool-review-page/erp-sales-status/erp-sales-status.component';
import { BomToolReviewControlBarComponent } from './bom-tool-review-page/bom-tool-review-controlbar/bom-tool-review-control-bar.component';
import { BomToolReviewSummaryComponent } from './bom-tool-review-page/bom-tool-review-summary/bom-tool-review-summary.component';
import { BomNotAvailableProductComponent } from './bom-tool-review-page/bom-not-available-product/bom-not-available-product.component';
import { BomMpnDuplicateProductComponent } from './bom-tool-review-page/bom-mpn-duplicate-product/bom-mpn-duplicate-product.component';
import { BomToolReviewToolbarComponent } from './bom-tool-review-page/bom-tool-review-toolbar/bom-tool-review-toolbar.component';
import { BomAlternativeProductCardComponent } from './bom-tool-review-page/bom-alternative-product-card/bom-alternative-product-card.component';
import { BomAlternativeDetailsModalComponent } from './bom-tool-review-page/bom-alternative-details-modal/bom-alternative-details-modal.component';
import { BomFileTitleComponent } from './bom-tool-review-page/bom-file-title/bom-file-title.component';
import { bomToolRoutes } from '@features/pages/bom-tool/bom-tool.routes';
import { BomToolSavedEntriesModule } from '@features/pages/bom-tool/bom-tool-saved-entries/bom-tool-saved-entries.module';
import { ButtonComponentModule } from '@features/shared-modules/components/button/button.module';
import { BrToNewlinePipeModule } from '@features/shared-modules/pipes/br-to-newline-pipe.module';
import { ToolbarItemComponentModule } from '@features/shared-modules/components/toolbar-item/toolbar-item.module';
import { ShoppingListToolbarItemComponentModule } from '@features/shared-modules/components/shopping-list-toolbar-item/shopping-list-toolbar-item.module';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { PageTitleModule } from '@features/shared-modules/components/page-title/page-title.module';
import { ScaledPricesComponentModule } from '@features/shared-modules/components/scaled-prices/scaled-prices.module';
import { ModalComponentModule } from '@features/shared-modules/popups/modal/modal.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { EnergyEfficiencyLabelModule } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';

@NgModule({
  declarations: [
    BomToolUploadPageComponent,
    BomToolReviewPageComponent,
    BomToolMatchingPageComponent,
    BomDataImportComponent,
    BomProductComponent,
    ErpSalesStatusComponent,
    BomToolReviewControlBarComponent,
    BomToolReviewSummaryComponent,
    BomNotAvailableProductComponent,
    BomMpnDuplicateProductComponent,
    BomToolReviewToolbarComponent,
    BomAlternativeProductCardComponent,
    BomAlternativeDetailsModalComponent,
    BomFileTitleComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(bomToolRoutes),
    SharedModule,
    PageSlotModule,
    PageLayoutModule,
    OutletModule,
    I18nModule,
    FormsModule,
    ReevooModule,
    AccountInformationModule,
    PriceAndStockModule,
    BomToolSavedEntriesModule,
    ButtonComponentModule,
    BrToNewlinePipeModule,
    ToolbarItemComponentModule,
    ShoppingListToolbarItemComponentModule,
    NumericStepperComponentModule,
    PageTitleModule,
    ScaledPricesComponentModule,
    ModalComponentModule,
    ArticleNumberPipeModule,
    EnergyEfficiencyLabelModule,
    PricePipeModule,
    AtcButtonModule,
  ],
})
export class BomToolModule {}
