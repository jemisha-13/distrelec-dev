import { Component, Input } from '@angular/core';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Product } from '@spartacus/core';
import { ProductAccessoryType } from '@model/product-reference.model';

@Component({
  selector: 'app-accessories-card-holder',
  templateUrl: './accessories-card-holder.component.html',
})
export class AccessoriesCardHolderComponent {
  @Input() product: Product;
  @Input() index = 0;
  @Input() itemListEntity: ItemListEntity;
  @Input() accessoryType: ProductAccessoryType;

  currentChannel$ = this.siteSettingsService.getCurrentChannelData();

  constructor(private siteSettingsService: AllsitesettingsService) {}
}
