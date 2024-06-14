import { Component, ViewEncapsulation } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { CmsService, WindowRef } from '@spartacus/core';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { map } from 'rxjs/operators';
import { CarouselProductData } from '@model/product-search.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';

@Component({
  selector: 'app-feedback-campaign',
  templateUrl: './feedback-campaign.component.html',
  styleUrls: ['./feedback-campaign.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class FeedbackCampaignComponent {
  isPlpActive$: Observable<boolean> = this.productListService.isPlpActive$;

  currentChannel$ = this.siteSettingsService.getCurrentChannelData();
  banner$: Observable<{ noRelevantResultsBanner: boolean; searchTerm: string }> =
    this.productListService.searchResults$.pipe(
      map((data) => ({
        noRelevantResultsBanner: data.noRelevantResultsBanner,
        searchTerm: data.freeTextSearch,
      })),
    );

  feedbackCampaigns$: Observable<CarouselProductData[]> = this.productListService.searchResults$.pipe(
    map((data) => data?.feedbackCampaigns ?? []),
  );

  pageTemplate$: Observable<string> = this.pageService.getCurrentPage().pipe(map((page) => page?.template));

  displayFeedBackCampaigns$: Observable<CarouselProductData[]> = combineLatest([
    this.feedbackCampaigns$,
    this.pageTemplate$,
  ]).pipe(
    map(([feedbackCampaigns, pageTemplate]) => {
      if (pageTemplate === 'CategoryPageTemplate' || pageTemplate === 'SearchResultsListPageTemplate') {
        return feedbackCampaigns;
      }
      return [];
    }),
  );

  isBrowser: boolean = this.winRef.isBrowser();

  constructor(
    private productListService: DistProductListComponentService,
    private winRef: WindowRef,
    private pageService: CmsService,
    private siteSettingsService: AllsitesettingsService,
  ) {}
}
