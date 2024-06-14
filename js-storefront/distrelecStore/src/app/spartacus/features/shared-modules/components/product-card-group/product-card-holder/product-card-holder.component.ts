import { Component, HostBinding, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { CmsProductCardComponent } from '@model/cms.model';
import { Prices } from '@model/price.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { PriceService } from '@services/price.service';
import { CmsService } from '@spartacus/core';
import { Observable, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-product-card-holder',
  templateUrl: './product-card-holder.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class ProductCardHolderComponent implements OnInit, OnDestroy {
  @Input() uid: string;

  @HostBinding('class.product-card-group__item') productCardItemClass = true;
  @HostBinding('class.mw-50') mw50 = true;
  @HostBinding('class.col') col = true;
  @HostBinding('class.landscape') landscape = false;

  productCardData$: Observable<[CmsProductCardComponent, ICustomProduct]>;
  productPriceData: Prices;
  channelData$ = this.siteSettingsService.currentChannelData$;

  quantitySelected = 0;
  productCode: string;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private pageService: CmsService,
    private productDataService: ProductDataService,
    private priceService: PriceService,
    private siteSettingsService: AllsitesettingsService,
  ) {}

  ngOnInit(): void {
    this.productCardData$ = this.pageService.getComponentData(this.uid).pipe(
      switchMap((cmsData: CmsProductCardComponent) =>
        this.productDataService.getProductData(cmsData?.articleNumber).pipe(
          map((productData) => {
            if (cmsData.orientation === 'LANDSCAPE') {
              this.landscape = true;
            }
            this.productCode = productData.code;
            this.quantitySelected = productData.orderQuantityMinimum;
            this.populatePriceData(productData.code);
            return [cmsData, productData] as [CmsProductCardComponent, ICustomProduct];
          }),
        ),
      ),
    );
  }

  quantityChange(qty: number): void {
    this.quantitySelected = qty;
  }

  populatePriceData(code: string): void {
    this.subscriptions.add(this.priceService.fetchPrices(code).subscribe((data) => (this.productPriceData = data)));
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
