import { Injectable, Injector } from '@angular/core';
import {
  Converter,
  ConverterService,
  LanguageService,
  OccEndpointsService,
  ProductSearchPage,
  Suggestion,
} from '@spartacus/core';
import { DistProductSearchAdapter } from '@features/pages/product/core/adapters/dist-product-search.adapter';
import { DistFusionProductSearchAdapter } from '@features/pages/product/core/fusion/adapters/dist-fusion-product-search.adapter';
import { DistFactFinderProductSearchAdapter } from '@features/pages/product/core/fact-finder/adapters/dist-fact-finder-product-search.adapter';
import { DistFactFinderProductSearchPageNormalizer } from '@features/pages/product/core/fact-finder/converters/dist-fact-finder-product-search-page-normalizer';
import { DistFusionProductSearchPageNormalizer } from '@features/pages/product/core/fusion/converters/dist-fusion-product-search-page-normalizer';
import { DistOccProductNormalizer } from '@features/pages/product/core/occ/converters/dist-occ-product-normalizer';
import { DistFusionProductNormalizer } from '@features/pages/product/core/fusion/converters/dist-fusion-product-normalizer';

import { HttpClient } from '@angular/common/http';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { FactFinderService } from '@features/pages/product/core/fact-finder/services/fact-finder.service';
import { SearchExperienceService } from '@features/pages/product/core/services/search-experience.service';
import { ChannelService } from '@context-services/channel.service';
import { Router, UrlSerializer } from '@angular/router';
import { PageHelper } from '@helpers/page-helper';
import { PriceService } from '@services/price.service';
import { FusionSessionService } from '../fusion/services/fusion-session.service';
import { FactFinderSessionService } from '../fact-finder/services/fact-finder-session.service';
import { DistCookieService } from '@services/dist-cookie.service';
import { DistSearchBoxService } from '../services/dist-search-box.service';
import { DistProductSuggestionFusionNormalizer } from '../fusion/converters/dist-fusion-product-suggestion-normalizer';
import { DistProductSuggestionFactFinderNormalizer } from '../fact-finder/converters/dist-fact-finder-product-suggestion-normalizer';
import { DefaultImageService } from '@services/default-image.service';
import { FusionSuggestionQueryService } from '../fusion/services/fusion-suggestion-query.service';
import { ViewConfig } from '@spartacus/storefront';
import { FusionProductPriceService } from '../fusion/services/fusion-product-price.service';
import { SessionService } from '@features/pages/product/core/services/abstract-session.service';
import { SearchQueryService } from '@features/pages/product/core/services/abstract-product-search-query.service';
import { FactFinderSearchQueryService } from '@features/pages/product/core/fact-finder/services/fact-finder-search-query.service';
import { FusionSearchQueryService } from '@features/pages/product/core/fusion/services/fusion-search-query.service';
import { CountryService } from '@context-services/country.service';
import { CategoriesService } from '@services/categories.service';
import { DistrelecUserService } from '@services/user.service';
import { ICustomProduct } from '@model/product.model';

@Injectable({
  providedIn: 'root',
})
export class SearchExperienceFactory {
  constructor(
    private experienceService: SearchExperienceService,
    private injector: Injector,
  ) {}

  createSearchAdapter(): DistProductSearchAdapter {
    if (this.experienceService.isFusion()) {
      return new DistFusionProductSearchAdapter(
        this.injector.get(HttpClient),
        this.injector.get(OccEndpointsService),
        this.injector.get(ConverterService),
        this.injector.get(DistSearchBoxService),
        this.injector.get(FusionSuggestionQueryService),
        this.injector.get(AllsitesettingsService),
        this.injector.get(SessionService),
        this.injector.get(Router),
      );
    }

    return new DistFactFinderProductSearchAdapter(
      this.injector.get(HttpClient),
      this.injector.get(OccEndpointsService),
      this.injector.get(ConverterService),
      this.injector.get(FactFinderService),
      this.injector.get(DistSearchBoxService),
    );
  }

  createProductNormalizer(): Converter<any, ICustomProduct> {
    if (this.experienceService.isFusion()) {
      return new DistFusionProductNormalizer(
        this.injector.get(FusionProductPriceService),
        this.injector.get(AllsitesettingsService),
      );
    }

    return new DistOccProductNormalizer(this.injector.get(PriceService));
  }

  createProductSuggestionNormalizer(): Converter<any, Suggestion> {
    if (this.experienceService.isFusion()) {
      return new DistProductSuggestionFusionNormalizer(
        this.injector.get(ChannelService),
        this.injector.get(AllsitesettingsService),
        this.injector.get(DefaultImageService),
      );
    }

    return new DistProductSuggestionFactFinderNormalizer(
      this.injector.get(DefaultImageService),
      this.injector.get(ChannelService),
      this.injector.get(AllsitesettingsService),
    );
  }

  createSearchPageNormalizer(): Converter<any, ProductSearchPage> {
    if (this.experienceService.isFusion()) {
      return new DistFusionProductSearchPageNormalizer(
        this.injector.get(ConverterService),
        this.injector.get(Router),
        this.injector.get(PageHelper),
        this.injector.get(UrlSerializer),
        this.injector.get(ViewConfig),
        this.injector.get(SearchExperienceService),
        this.injector.get(FusionSearchQueryService),
      );
    }

    return new DistFactFinderProductSearchPageNormalizer(
      this.injector.get(ConverterService),
      this.injector.get(ChannelService),
      this.injector.get(Router),
      this.injector.get(PageHelper),
      this.injector.get(UrlSerializer),
    );
  }

  createSessionService(): SessionService {
    if (this.experienceService.isFusion()) {
      return new FusionSessionService();
    }

    return this.injector.get(FactFinderSessionService);
  }

  createSearchQueryService(): SearchQueryService {
    if (this.experienceService.isFusion()) {
      return new FusionSearchQueryService(
        this.injector.get(ViewConfig),
        this.injector.get(PageHelper),
        this.injector.get(Router),
        this.injector.get(LanguageService),
        this.injector.get(CountryService),
        this.injector.get(ChannelService),
        this.injector.get(CategoriesService),
        this.injector.get(DistrelecUserService),
        this.injector.get(SearchExperienceService),
        this.injector.get(DistCookieService),
      );
    }

    return new FactFinderSearchQueryService(
      this.injector.get(ViewConfig),
      this.injector.get(PageHelper),
      this.injector.get(Router),
    );
  }
}
