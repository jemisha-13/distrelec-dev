import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlternativeComponent } from './alternative.component';
import { I18nModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@features/shared-modules/shared.module';
import { AlternativeItemComponent } from './alternative-item/alternative-item.component';
import { IfModule } from '@rx-angular/template/if';
import { ForModule } from '@rx-angular/template/for';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';

@NgModule({
  imports: [CommonModule, I18nModule, RouterModule, SharedModule, VolumePricePipeModule],
  declarations: [AlternativeComponent, AlternativeItemComponent],
  exports: [AlternativeComponent, AlternativeItemComponent, IfModule, ForModule],
})
export class AlternativeModule {}
