import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CmsComponentData } from '@spartacus/storefront';

@Component({
  selector: 'app-dist-product-carousel',
  templateUrl: './dist-product-carousel.component.html',
  styleUrls: ['./dist-product-carousel.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DistProductCarouselComponent {
  constructor(public component: CmsComponentData<any>) {}
}
