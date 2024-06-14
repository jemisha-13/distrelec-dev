import { NgModule } from '@angular/core';
import { LegacyQuantitySelectorComponent } from './quantity-selector-legacy.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { I18nModule } from '@spartacus/core';
import { MinOrderQuantityPopupModule } from '@features/shared-modules/components/min-order-quantity-popup/min-order-quantity-popup.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [
    FontAwesomeModule,
    CommonModule,
    ArticleNumberPipeModule,
    I18nModule,
    DistIconModule,
    MinOrderQuantityPopupModule,
  ],
  declarations: [LegacyQuantitySelectorComponent],
  exports: [LegacyQuantitySelectorComponent],
})
export class LegacyQuantitySelectorComponentModule {}
