import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccessoriesCardHolderComponent } from './accessories-card-holder.component';
import { DistAccessoriesCardModule } from '@design-system/accessories-card/accessories-card.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, DistAccessoriesCardModule, VolumePricePipeModule, I18nModule],
  declarations: [AccessoriesCardHolderComponent],
  exports: [AccessoriesCardHolderComponent],
})
export class ProductCardModule {}
