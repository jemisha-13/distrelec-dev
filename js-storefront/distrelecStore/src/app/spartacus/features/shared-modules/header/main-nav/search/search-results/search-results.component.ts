import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { faAngleRight, faCopy } from '@fortawesome/free-solid-svg-icons';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { AllsitesettingsService } from 'src/app/spartacus/services/allsitesettings.service';
import { createFrom, EventService, RoutingService, Suggestion } from '@spartacus/core';
import { SiteConfigService } from '@services/siteConfig.service';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { filter, map } from 'rxjs/operators';
import { CopyProductNumberService } from '@services/copy-product-number.service';
import { DistSearchBoxService } from '@features/pages/product/core/services/dist-search-box.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { ProductSuggestion } from '@model/product-suggestions.model';
import { DefaultImageService } from '@services/default-image.service';

@Component({
  selector: 'app-search-results',
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.scss'],
})
export class SearchResultsComponent implements OnInit {
  @Input() categoryName: string;
  @Input() searchTerm: string;
  @ViewChild('result') input: any;

  searchSuggestionResults$: Observable<Suggestion> = this.distSearchBoxService
    .getSuggestResults()
    .pipe(filter((results) => results?.products?.length > 0));

  displayLeftSideSuggestions$: Observable<boolean> = this.searchSuggestionResults$.pipe(
    map(
      (results) =>
        results.termSuggestions.length > 0 ||
        results.categorySuggestions.length > 0 ||
        results.manufacturerSuggestions.length > 0,
    ),
  );

  suggestions$: Observable<{ results: Suggestion; displayLeftSideSuggestions: boolean }> = combineLatest([
    this.searchSuggestionResults$,
    this.displayLeftSideSuggestions$,
  ]).pipe(map(([results, displayLeftSideSuggestions]) => ({ results, displayLeftSideSuggestions })));

  currentChannel$: Observable<CurrentSiteSettings>;
  faAngleRight = faAngleRight;
  faCopy = faCopy;
  pageType = this.siteConfigService.getCurrentPageTemplate();
  popupCallbackArray: any[] = [];
  copiedState$ = this.copyProductNumberService.copiedState$;
  itemListEntity = ItemListEntity;

  constructor(
    private siteSettingsService: AllsitesettingsService,
    private siteConfigService: SiteConfigService,
    private energyEfficiencyLabelService: EnergyEfficiencyLabelService,
    private distSearchBoxService: DistSearchBoxService,
    private copyProductNumberService: CopyProductNumberService,
    private eventService: EventService,
    private defaultImage: DefaultImageService,
    private router: RoutingService,
  ) {}

  ngOnInit(): void {
    this.currentChannel$ = this.siteSettingsService.getCurrentChannelData();
  }

  eventFromChild(obj) {
    this.popupCallbackArray.push(obj);

    if (this.popupCallbackArray.length > 1) {
      if (this.popupCallbackArray[0].id !== this.popupCallbackArray[1].id) {
        //catch if the same popup has been opened twice
        const callback = this.popupCallbackArray[0].callback;
        callback();
      }

      this.popupCallbackArray.shift();
    }
  }

  onProductClick(product: ProductSuggestion, index: number, channel: CurrentSiteSettings): void {
    this.trackProductClick(product, index);

    let url = product.url;

    if (this.isProductUrlWithLocale(url, channel.language)) {
      url = url.replace(new RegExp(`^\\/${channel.language}`), '');
    }
    this.router.go(url);
  }

  isProductUrlWithLocale(url: string, language: string): boolean {
    return url.includes(`/${language}/`);
  }

  trackProductClick(product: ProductSuggestion, index: number): void {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        product,
        listType: ItemListEntity.SUGGESTED_SEARCH,
        index,
      } as ProductClickEvent),
    );
  }

  scroll() {
    const element = this.input.nativeElement;
    element.scrollTop = element.scrollHeight;
  }

  getDefaultImage(event) {
    event.target.src = this.defaultImage.getDefaultImage();
  }

  getEnergyEfficiencyRating(energyEfficiency: string) {
    return this.energyEfficiencyLabelService.getEnergyEfficiencyRating(energyEfficiency);
  }

  getEnergyEfficiencyLabelImageUrl(uri: string): string {
    return this.energyEfficiencyLabelService.getAbsoluteImageUrl(uri);
  }

  testEnergyLabel(energyLabel) {
    return this.energyEfficiencyLabelService.hasValidEnergyEfficiencyLabel(energyLabel);
  }

  copyText(content: string, key: string, origin?: string): void {
    this.copyProductNumberService.copyNumber(content, key, origin);
  }
}
