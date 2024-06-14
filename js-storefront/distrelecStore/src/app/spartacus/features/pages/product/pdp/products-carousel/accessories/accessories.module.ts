import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccessoriesComponent } from './accessories.component';
import { I18nModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@features/shared-modules/shared.module';
import { ProductCardModule } from '@features/shared-modules/components/accessories-card-holder/accessories-card-holder.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';

@NgModule({
  imports: [CommonModule, I18nModule, RouterModule, SharedModule, ProductCardModule, DistButtonComponentModule],
  declarations: [AccessoriesComponent],
  exports: [AccessoriesComponent],
})
export class AccessoriesModule {}
