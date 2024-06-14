import { Component, ViewEncapsulation } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { map } from 'rxjs/operators';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { SingleWordData } from '@model/product-search.model';

@Component({
  selector: 'app-product-list-empty-products',
  templateUrl: './product-list-empty-products.component.html',
  styleUrls: ['./product-list-empty-products.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ProductListEmptyProductsComponent {
  singlSearchResults$: Observable<SingleWordData[]> = this.productListService.searchResults$.pipe(
    map((model) => model.singleWordSearchItems),
  );
  currentChannel$: BehaviorSubject<CurrentSiteSettings> = this.siteSettingsService.currentChannelData$;
  faChevronRight = faChevronRight;

  constructor(
    private productListService: DistProductListComponentService,
    private siteSettingsService: AllsitesettingsService,
  ) {}
}
