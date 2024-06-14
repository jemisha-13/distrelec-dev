import { Component } from '@angular/core';
import { CmsProductCardGroupComponent } from '@model/cms.model';
import { CmsComponentData } from '@spartacus/storefront';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-product-card-group',
  templateUrl: './product-card-group.component.html',
  styleUrls: ['./product-card-group.component.scss'],
})
export class ProductCardGroupComponent {
  productCardUids$ = this.component.data$.pipe(map((data) => data.productCardItems?.split(' ')));
  landscapeOrientation: string;

  constructor(private component: CmsComponentData<CmsProductCardGroupComponent>) {}
}
