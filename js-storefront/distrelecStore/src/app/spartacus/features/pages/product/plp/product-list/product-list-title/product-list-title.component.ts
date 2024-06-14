import { Component, ViewEncapsulation } from '@angular/core';
import { PaginationModel, WindowRef } from '@spartacus/core';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { Observable } from 'rxjs';
import { CountryService } from '../../../../../../site-context/services/country.service';
import { navigateBack } from '@helpers/navigation-helper';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { PageHelper } from '@helpers/page-helper';

@Component({
  selector: 'app-product-list-title',
  templateUrl: './product-list-title.component.html',
  styleUrls: ['./product-list-title.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ProductListTitleComponent {
  title$: Observable<string>;
  isPlpActive$: Observable<boolean>;
  isProductFamilyPage: boolean = this.pageHelper.isProductFamilyPage();
  results$: Observable<PaginationModel | null>;
  faArrowLeft = faArrowLeft;

  constructor(
    private productListService: DistProductListComponentService,
    public countryService: CountryService,
    private winRef: WindowRef,
    private pageHelper: PageHelper,
  ) {
    this.title$ = this.productListService.title$;
    this.isPlpActive$ = this.productListService.isPlpActive$;
    this.results$ = this.productListService.pagination$;
  }

  navigateBack(event) {
    navigateBack(this.winRef, event);
  }
}
