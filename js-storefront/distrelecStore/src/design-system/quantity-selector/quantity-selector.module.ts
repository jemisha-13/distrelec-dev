import { NgModule } from '@angular/core';
import { QuantitySelectorComponent } from './quantity-selector.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { I18nModule } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [FontAwesomeModule, CommonModule, ArticleNumberPipeModule, I18nModule, DistIconModule, ReactiveFormsModule],
  declarations: [QuantitySelectorComponent],
  exports: [QuantitySelectorComponent],
})
export class QuantitySelectorComponentModule {}
