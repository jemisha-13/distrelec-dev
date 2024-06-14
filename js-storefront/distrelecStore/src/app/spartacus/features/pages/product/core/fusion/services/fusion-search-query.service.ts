import { Injectable, OnDestroy } from '@angular/core';
import { Params, Router } from '@angular/router';
import { SearchCriteria, ViewConfig } from '@spartacus/storefront';
import { PageHelper } from '@helpers/page-helper';
import { SearchQueryService } from '@features/pages/product/core/services/abstract-product-search-query.service';
import { Feature, LanguageService } from '@spartacus/core';
import { CountryService } from '@context-services/country.service';
import { ChannelService } from '@context-services/channel.service';
import { combineLatest, ReplaySubject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {
  FusionPaginationQueryKey,
  SORT_MAPPING,
} from '@features/pages/product/core/fusion/model/fusion-query-params.model';
import { FusionSearchCriteria } from '@model/fusion-product-search.model';
import { CategoriesService } from '@services/categories.service';
import { CategoryPageData } from '@model/category.model';
import { DistrelecUserService } from '@services/user.service';
import { decode, encode } from '@helpers/encoding';
import { SearchExperienceService } from '../../services/search-experience.service';
import {
  FACT_FINDER_CATEGORY_ROOT,
  FILTER_PARAM_PREFIX,
  FILTER_RANGE_PARAM_SUFFIX,
  FILTER_RANGE_SEPARATOR,
  FUSION_PARAM_PREFIX,
  FUSION_PARAM_SEPARATOR,
  FUSION_RANGE_SEPARATOR,
} from '@helpers/constants';
import { DistCookieService } from '@services/dist-cookie.service';

const PRIMARY_FACET_LABELS = ['manufacturername', 'productstatus', 'categorycodes'];
const PAGE_SPECIFIC_FACET_LABELS = [
  'categorycode',
  'manufacturercode',
  'productfamilycode',
  'productstatus',
  'categorycodes',
];

@Injectable({
  providedIn: 'root',
})
export class FusionSearchQueryService extends SearchQueryService implements OnDestroy {
  private language: string;
  private country: string;
  private channel: string;
  private category: CategoryPageData;
  private customerId: string;
  private encryptedUserId: string;

  private readonly destroyed$ = new ReplaySubject<boolean>(1);

  constructor(
    config: ViewConfig,
    protected pageHelper: PageHelper,
    protected router: Router,
    protected languageService: LanguageService,
    protected countryService: CountryService,
    protected channelService: ChannelService,
    protected categoryService: CategoriesService,
    protected distrelecUserService: DistrelecUserService,
    private searchExperience: SearchExperienceService,
    private cookieService: DistCookieService,
  ) {
    super(config);

    combineLatest([
      this.languageService.getActive(),
      this.countryService.getActive(),
      this.channelService.getActive(),
      this.categoryService.getCurrentCategoryData(),
      this.distrelecUserService.getUserDetails(),
    ])
      .pipe(takeUntil(this.destroyed$))
      .subscribe(([language, country, channel, category, userDetails]) => {
        this.language = language;
        this.country = country;
        this.channel = channel;
        this.category = category;
        this.customerId = userDetails?.orgUnit?.erpCustomerId ?? '';
        this.encryptedUserId = userDetails?.encryptedUserID ?? '';
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  findDeepestCategoryFactFinder(params): { value: string; deepestLevel: number } {
    const filters = Object.entries(params)
      .filter(([key]) => key.startsWith(`${FILTER_PARAM_PREFIX}${FACT_FINDER_CATEGORY_ROOT}`))
      .map(([key, value]) => {
        const deepestLevel = key.split('/').length - 1;
        const formattedValue = Array.isArray(value) ? value[0] : value;
        return { value: formattedValue, deepestLevel };
      });

    return filters.sort((a, b) => b.deepestLevel - a.deepestLevel)[0];
  }

  override buildSearchCriteria(params: Params | SearchCriteria): FusionSearchCriteria {
    const { currentPage, pageSize, sort, ...otherParams } = super.buildSearchCriteria(params);
    const mapSortingLabels = this.mapSortingLabels(sort);
    const startIndex = ((currentPage || 1) - 1) * pageSize;

    const criteria: FusionSearchCriteria = {
      ...otherParams,
      // Map hybris param names to fusion param names
      [FusionPaginationQueryKey.PAGE]: startIndex,
      [FusionPaginationQueryKey.PAGE_SIZE]: pageSize,
      [FusionPaginationQueryKey.SORT]: mapSortingLabels,
      // Site context params
      country: this.country,
      language: this.language,
      channel: this.channel,
    };

    if (this.extractGaAnonymousSid()) {
      criteria.sessionId = this.extractGaAnonymousSid();
    }

    if (this.customerId) {
      criteria.customerId = this.customerId;
    }

    if (this.encryptedUserId) {
      criteria.userId = this.encryptedUserId;
    }

    return criteria;
  }

  override buildProductSpecificationsQuery(feature: Feature): { key: string; value: string } {
    const fusionFormattedCode = this.transformFeatureCode(feature.code);
    const typePrefix = this.featureType(feature.type);
    const filterValue = feature?.featureValues[0]?.baseUnitValue ?? feature?.featureValues[0]?.value;
    const filterKey = `filter_${fusionFormattedCode}_${this.language}_${typePrefix}`;

    return { key: filterKey, value: encode(this.convertScientificNotation(filterValue)) };
  }

  /**
   * Map params to query and fq parameters
   */
  protected mapQuery(params: Params) {
    const { q, mode, useTechnicalView, ...rest } = params;
    const decodedQuery = q ? decode(q) : q;
    const filteredCategories = this.setDeepestCategory(rest);
    const groupedParams = this.groupParams(filteredCategories);

    const mappedParams: Params = Object.entries(groupedParams).reduce((acc, [key, value]) => {
      let filterName = '';

      key = this.mapFactFinderCmsFilters(key);

      if (key === '') {
        return acc;
      }

      if (key === 'categoryCode') {
        filterName = `category${this.category?.sourceCategory?.level ?? 1}Code`;
        key = 'fq';
      } else if (this.isPageSpecificFacet(key)) {
        filterName = key;
        key = 'fq';
      } else if (key.startsWith(FILTER_PARAM_PREFIX)) {
        filterName = this.isPrimaryFacet(key)
          ? key.replace(FILTER_PARAM_PREFIX, '')
          : key.replace(FILTER_PARAM_PREFIX, FUSION_PARAM_PREFIX);
        key = 'fq';
      }

      if (!acc[key]) {
        acc[key] = [];
      }
      if (filterName.endsWith(FILTER_RANGE_PARAM_SUFFIX)) {
        const [min, max] = value.split(FILTER_RANGE_SEPARATOR);
        acc[key].push(`${filterName.replace(FILTER_RANGE_PARAM_SUFFIX, '')}:[${min}${FUSION_RANGE_SEPARATOR}${max}]`);
      } else {
        value = this.addParenthesis(value);
        acc[key].push(`${filterName ?? key}:${value}`);
      }

      return acc;
    }, {} as Params);

    // Fusion search query is separated and filters are under `fq` parameter
    return {
      query: decodedQuery ?? '*',
      otherParams: {
        mode,
        ...mappedParams,
      },
    };
  }

  protected addPageSpecificSearchParameters(params: Params): Params {
    let firstIndex = this.router.url.lastIndexOf('/') + 1; // length of '/'
    const lastIndex = this.router.url.lastIndexOf('?');

    if (this.pageHelper.isCategoryPage()) {
      const categoryCode =
        lastIndex !== -1 ? this.router.url?.slice(firstIndex, lastIndex) : this.router.url?.slice(firstIndex);

      if (!this.router.url.includes(this.searchExperience.getCategoryParam())) {
        params.categoryCodes = categoryCode;
      }

      return {
        ...params,
        categoryCode,
        mode: 'category',
      };
    }

    if (this.pageHelper.isManufacturerDetailPage()) {
      let manufacturerCode =
        lastIndex !== -1 ? this.router.url?.slice(firstIndex, lastIndex) : this.router.url?.slice(firstIndex);

      if (manufacturerCode.includes('#')) {
        manufacturerCode = manufacturerCode.split('#')[0];
      }

      return {
        ...params,
        manufacturerCode,
        mode: 'manufacturer',
      };
    }

    if (this.pageHelper.isProductFamilyPage()) {
      firstIndex = this.router.url.lastIndexOf('pf/') + 3; //length of 'pf/'
      const productFamilyCode =
        lastIndex !== -1 ? this.router.url?.slice(firstIndex, lastIndex) : this.router.url?.slice(firstIndex);

      return {
        ...params,
        // eslint-disable-next-line @typescript-eslint/naming-convention
        productFamilyCode,
        mode: 'family',
      };
    }

    if (this.pageHelper.isNewProductsPage()) {
      return {
        ...params,
        productStatus: '"NEW"',
        mode: 'new',
      };
    }

    if (this.pageHelper.isClearancePage()) {
      return {
        ...params,
        productStatus: '"OFFER"',
        mode: 'offer',
      };
    }

    return params;
  }

  private addParenthesis(value: string): string {
    if (value.startsWith('(') && value.endsWith(')')) {
      return decode(value);
    }

    return `("${decode(value)}")`;
  }

  private groupParams(params: Params): Params {
    return Object.entries(params).reduce((acc, [key, value]) => {
      if (Array.isArray(value)) {
        value = value.map((item) => `"${item}"`);
        acc[key] = `(${value.join(FUSION_PARAM_SEPARATOR)})`;
      } else {
        acc[key] = value;
      }
      return acc;
    }, {} as Params);
  }

  private mapSortingLabels(sortByLabel: string): string {
    return SORT_MAPPING[sortByLabel] ?? '';
  }

  private isPageSpecificFacet(facetName: string): boolean {
    return PAGE_SPECIFIC_FACET_LABELS.includes(facetName.toLowerCase());
  }

  private isPrimaryFacet(facetName: string): boolean {
    const normalizedName = facetName.replace(FILTER_PARAM_PREFIX, '').toLowerCase();
    return PRIMARY_FACET_LABELS.includes(normalizedName);
  }

  private featureType(type: string): string {
    return type === 'number' ? 'ds' : 'ss';
  }

  private transformFeatureCode(code: string): string {
    if (!code) {
      return;
    }

    const splitCode: string[] = code.split('.');
    const lastPart = splitCode[splitCode.length - 1];
    return lastPart.replace(/[^a-zA-Z0-9]/g, '');
  }

  private extractGaAnonymousSid(): string {
    const gaPsuedoID = this.cookieService.get('_ga');
    const extractedID = gaPsuedoID.match(/GA1\.1\.(\d+\.\d+)/);

    if (extractedID && extractedID[1]) {
      return extractedID[1];
    }

    return '';
  }

  private mapFactFinderCmsFilters(filterKey): string {
    if (filterKey === `${FILTER_PARAM_PREFIX}Buyable`) {
      return '';
    }

    return filterKey
      .replace(`${FILTER_PARAM_PREFIX}Manufacturer`, 'manufacturerName')
      .replace(`${FILTER_PARAM_PREFIX}productFamilyCode`, 'productFamilyCode');
  }

  private filterFactFinderParamsFromQuery(params: Params): Params {
    return Object.keys(params).filter((key) => key.startsWith(`${FILTER_PARAM_PREFIX}category`));
  }

  private setDeepestCategory(params: Params): Params {
    const deepestCategory = this.findDeepestCategoryFactFinder(params);

    if (!deepestCategory) {
      return params;
    }

    const filteredParams = this.filterFactFinderParamsFromQuery(params);
    filteredParams.categoryCodes = deepestCategory.value;

    return filteredParams;
  }

  private convertScientificNotation(value: string): string {
    const scientificNotationRegex = /^-?\d+(\.\d+)?(e[-+]?\d+)?$/i;
    if (value.match(scientificNotationRegex)) {
      return Number(value).toString();
    }

    return value;
  }
}
