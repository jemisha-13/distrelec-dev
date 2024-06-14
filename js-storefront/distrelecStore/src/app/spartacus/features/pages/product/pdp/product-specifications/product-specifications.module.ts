import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ProductSpecificationsComponent } from './product-specifications.component';
import { I18nModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { IfModule } from '@rx-angular/template/if';
import { ForModule } from '@rx-angular/template/for';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { AccordionComponentModule } from '@design-system/accordion/accordion.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    FontAwesomeModule,
    I18nModule,
    RouterModule,
    IfModule,
    ForModule,
    ArticleNumberPipeModule,
    AccordionComponentModule,
    DistIconModule,
    ReactiveFormsModule,
  ],
  declarations: [ProductSpecificationsComponent],
  exports: [ProductSpecificationsComponent],
})
export class ProductSpecificationsModule {}
