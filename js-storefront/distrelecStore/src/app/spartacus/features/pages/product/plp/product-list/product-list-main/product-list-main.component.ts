import { Component, OnDestroy, OnInit } from '@angular/core';
import { faAngleDown } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Observable, ReplaySubject, Subject } from 'rxjs';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { Channel, CurrentSiteSettings } from '@model/site-settings.model';
import { first, map, switchMap } from 'rxjs/operators';
import { Product, ProductSearchPage, UserIdService } from '@spartacus/core';
import { FloatingToolbarEvents } from '@features/pages/product/plp/product-list/product-list-main/floating-toolbar/floating-toolbar.component';
import { UseWebpImage } from '@helpers/useWebpImage';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { PageHelper } from '@helpers/page-helper';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { CompareService } from '@services/feature-services';

@Component({
  selector: 'app-product-list-main',
  templateUrl: './product-list-main.component.html',
  styleUrls: ['./product-list-main.component.scss'],
})
export class ProductListMainComponent implements OnInit, OnDestroy {
  faAngleDown = faAngleDown;
  useWebpImg = UseWebpImage;

  model$: Observable<ProductSearchPage> = this.productListService.model$;
  isPlpActive$: Observable<boolean> = this.productListService.isPlpActive$;
  isLoading$ = this.productListService.isLoading$;

  channel$: Observable<Channel> = this.channelService.getActive();

  toolbarEventHandler = new Subject<FloatingToolbarEvents>();
  showProductFamilyLink = !this.pageHelper.isProductFamilyPage();

  currentChannel_: BehaviorSubject<CurrentSiteSettings> = this.allSiteSettings.currentChannelData$;
  userId: string;

  private destroyed$ = new ReplaySubject<void>();

  constructor(
    private allSiteSettings: AllsitesettingsService,
    private productListService: DistProductListComponentService,
    private compareService: CompareService,
    private channelService: ChannelService,
    private userIdService: UserIdService,
    private pageHelper: PageHelper,
  ) {}

  get compareCount(): Observable<number> {
    return this.compareService.getCompareListState().pipe(map((compareList) => compareList.products.length));
  }

  ngOnInit(): void {
    this.userIdService.takeUserId().subscribe((userId: string) => {
      this.userId = userId;
    });
  }

  ngOnDestroy() {
    this.productListService.resetPage();
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  trackProducts(i: number, el: Product): string | undefined {
    return el ? el.code : undefined;
  }

  clearAllCompare(): void {
    this.compareService
      .getCompareListState()
      .pipe(
        first(),
        switchMap((compareList) =>
          this.compareService.removeCompareProduct(compareList.products.map((product) => product.code)),
        ),
      )
      .subscribe();
  }
}
