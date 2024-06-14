import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { CompareComponent } from './compare.component';
import { CompareListComponent } from './components/compare-list/compare-list.component';

import { CompareLayoutModule } from './layout-config/layout-category.module';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { BreadcrumbWrapperModule } from '@features/shared-modules/breadcrumb/breadcrumb-wrapper.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { EnergyEfficiencyLabelModule } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.module';
import { PrintCompareComponent } from './components/print-compare/print-compare.component';
import { CompareListItemComponent } from './components/compare-list-item/compare-list-item.component';
import { AttributeListComponent } from './components/attribute-list/attribute-list.component';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { IfModule } from '@rx-angular/template/if';

@NgModule({
  imports: [
    ArticleNumberPipeModule,
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        CompareComponent: {
          component: CompareComponent,
        },
      },
    } as CmsConfig),
    CompareLayoutModule,
    BreadcrumbWrapperModule,
    FontAwesomeModule,
    I18nModule,
    RouterModule,
    EnergyEfficiencyLabelModule,
    AtcButtonModule,
    IfModule,
    DecimalPlacesPipeModule,
    VolumePricePipeModule,
  ],
  declarations: [
    CompareComponent,
    CompareListComponent,
    PrintCompareComponent,
    CompareListItemComponent,
    AttributeListComponent,
  ],
})
export class CompareModule {}
