import { Injectable } from '@angular/core';
import { Params, Router, UrlSerializer } from '@angular/router';
import { Converter, ConverterService, Facet, FacetValue, ProductSearchPage } from '@spartacus/core';
import { ViewConfig } from '@spartacus/storefront';
import {
  FusionCategoryFacetValue,
  FusionCustomerRestrictedData,
  FusionFacet,
  FusionFacetValue,
  FusionProductSearch,
} from '@model/fusion-product-search.model';
import {
  FUSION_FEEDBACK_CAMPAIGNS_NORMALIZER,
  FUSION_PRODUCT_NORMALIZER,
} from '@features/pages/product/core/fusion/converters/injection-tokens';
import { decode, encode } from '@helpers/encoding';
import { CategoryFilter, ProductSearchPagePunchout } from '@model/product-search.model';
import { SearchExperienceService } from '@features/pages/product/core/services/search-experience.service';
import { Breadcrumb } from '@model/breadcrumb.model';
import { PageHelper } from '@helpers/page-helper';
import { FUSION_PARAM_SEPARATOR, FUSION_RANGE_SEPARATOR, PAGE_QUERY_PARAM } from '@helpers/constants';
import { FusionSearchQueryService } from '../services/fusion-search-query.service';

@Injectable()
export class DistFusionProductSearchPageNormalizer implements Converter<FusionProductSearch, ProductSearchPage> {
  protected readonly CATEGORY_FILTER_KEY = this.experienceService.getCategoryKey();
  protected readonly CATEGORY_FILTER_PARAM = this.experienceService.getCategoryParam();

  // Cache some calculated values
  private selectedFacetQuery?: string;

  constructor(
    private converterService: ConverterService,
    private router: Router,
    private pageHelper: PageHelper,
    private urlSerializer: UrlSerializer,
    private viewConfig: ViewConfig,
    private experienceService: SearchExperienceService,
    private fusionSearchQueryService: FusionSearchQueryService,
  ) {}

  convert(source: FusionProductSearch, target: ProductSearchPage = {}): ProductSearchPage {
    this.clearCachedValues();
    this.prepareSourceFacets(source);

    target.url = this.router.url;

    target.pagination = this.mapPagination(source);
    target.currentQuery = this.mapCurrentQuery(source);
    target.freeTextSearch = this.mapFreeTextSearch(source);
    target.keywordRedirectUrl = this.mapRedirectUrl(source);
    target.facets = this.mapFacets(source);
    target.categoryFilters = this.mapCategoryFilters(source);
    target.categoryBreadcrumbs = this.mapCategoryDisplayData(source);

    target.noRelevantResultsBanner = source?.fusion?.removedCategoryCodeFilter ?? false;

    target.products =
      source?.response?.docs?.map((product) => this.converterService.convert(product, FUSION_PRODUCT_NORMALIZER)) ?? [];

    this.normalisePunchOutData(source as FusionCustomerRestrictedData, target);
    this.normaliseFeedbackData(source, target);

    return target;
  }

  private clearCachedValues(): void {
    this.selectedFacetQuery = undefined;
  }

  private prepareSourceFacets(source: FusionProductSearch): void {
    source.facets = source.facets ?? [];
    this.setSelectedFacets(source);
  }

  private setSelectedFacets(source: FusionProductSearch): void {
    const filterQueries: string[] = source.request.fq ?? [];
    const queryValueMap = new Map<string, Set<string>>();

    const addValueToMap = (code: string, value: string | number) => {
      if (typeof value === 'string') {
        if (value.startsWith('"') && value.endsWith('"')) {
          value = value.slice(1, -1);
        }
      } else {
        value = value.toString();
      }

      if (queryValueMap.has(code)) {
        queryValueMap.get(code).add(value);
      } else {
        queryValueMap.set(code, new Set<string>([value]));
      }
    };

    const getValuesInRange = (facet: FusionFacet, min: string, max: string): (string | number)[] =>
      facet?.values
        .filter((facetValue) => {
          const val = Number(facetValue.value);
          return val >= Number(min) && val <= Number(max);
        })
        .map((facetValue) => facetValue.value) ?? [];

    filterQueries.forEach((filterQuery) => {
      const firstColonIndex = filterQuery.indexOf(':');
      const code = filterQuery.substring(0, firstColonIndex);
      const value = filterQuery.substring(firstColonIndex + 1);

      if (value.startsWith('(') && value.endsWith(')')) {
        // Multi-select values
        const values = value.slice(1, -1).split(FUSION_PARAM_SEPARATOR);
        values.forEach((val) => addValueToMap(code, val));
      } else if (value.startsWith('[') && value.endsWith(']')) {
        // Range values
        const [min, max] = value.slice(1, -1).split(FUSION_RANGE_SEPARATOR);
        const facet = source.facets.find((fusionFacet) => fusionFacet.code === code);
        if (facet) {
          facet.minValue = Number(min);
          facet.maxValue = Number(max);

          getValuesInRange(facet, min, max).forEach((val) => addValueToMap(code, val));
        }
      } else {
        addValueToMap(code, value);
      }
    });

    const isFacetSelected = (facet: FusionFacet, facetValue: FusionFacetValue) => {
      const values = queryValueMap.get(facet.code);
      const queryParams = this.getQueryParams();

      const factFinderCategoryCmsLink = this.fusionSearchQueryService.findDeepestCategoryFactFinder(queryParams);
      if (factFinderCategoryCmsLink) {
        return facet.values.some((facetVal) => facetVal.value === factFinderCategoryCmsLink.value);
      }

      if (facet.code === this.CATEGORY_FILTER_KEY && !queryParams.hasOwnProperty(this.CATEGORY_FILTER_PARAM)) {
        return false;
      }

      if (values) {
        values.forEach((value) => {
          const hasBeenConverted = this.convertScientificNotation(value) !== value;
          if (hasBeenConverted) {
            values.delete(value);
            values.add(this.convertScientificNotation(value));
          }
        });
      }

      return values && values.has(decode(facetValue.value.toString()));
    };

    source.facets.forEach((facet) => {
      facet.values.forEach((value) => {
        value.selected = isFacetSelected(facet, value);
      });
    });
  }

  private convertScientificNotation(value: string): string {
    const scientificNotationRegex = /^-?\d+(\.\d+)?(e[-+]?\d+)?$/i;
    if (value.match(scientificNotationRegex)) {
      return Number(value).toString();
    }

    return value;
  }

  private mapCategoryFilters(source: FusionProductSearch): CategoryFilter[] {
    const categories = source.facets?.find((facet) => facet.code.includes(this.CATEGORY_FILTER_KEY))?.values ?? null;

    if (!categories) {
      return [];
    }

    const filterLevel: number = this.getNextFilterLevel(source, categories as FusionCategoryFacetValue[]);
    const url = this.router.url;
    const [path, queryParams] = url.split('?');
    const params = new URLSearchParams(queryParams ?? '');
    params.delete(PAGE_QUERY_PARAM);

    const maxLevel = Math.max(...categories.map((category) => category.level));
    let targetLevel = filterLevel;

    while (targetLevel < maxLevel) {
      const currentLevelCategoriesCount = categories.filter((category) => category.level === targetLevel).length;
      if (currentLevelCategoriesCount > 1) {
        break;
      }
      targetLevel++;
    }

    return categories
      .filter((value: FusionCategoryFacetValue) => value.level === targetLevel)
      .map((cat: FusionCategoryFacetValue) => {
        params.set(this.CATEGORY_FILTER_PARAM, cat.value);
        return {
          count: cat.count,
          name: cat.value,
          displayName: cat.name,
          selected: false,
          url: `/${path}?${params.toString()}`,
          code: cat.value,
        };
      })
      .sort((a, b) => b.count - a.count);
  }

  private getNextFilterLevel(source: FusionProductSearch, categories: FusionCategoryFacetValue[]): number {
    const selectedCategory = categories.find((cat) => cat.selected);

    if (selectedCategory) {
      return selectedCategory.level + 1;
    }

    if (source?.request?.fq) {
      const regex = /category(\d+)Code(?:\S+)?/;
      let match: string[] | null;
      source.request.fq.find((fq: string) => {
        match = fq.match(regex);
        return match;
      });
      if (match) {
        return Number(match[1]) + 1;
      }
    }

    return 1;
  }

  private mapPagination(source: FusionProductSearch) {
    const { numFound, totalPages, start } = source.response;
    const pageSize = this.activatedPageSize(this.viewConfig.view.defaultPageSize);
    const currentPage = start > 0 ? Math.floor(start / pageSize) + 1 : 1;

    return {
      pageSize,
      currentPage,
      totalPages,
      totalResults: numFound,
    };
  }

  private activatedPageSize(defaultPageSize: number): number {
    const url = this.router.url;
    const queryParameters = url.split('?')[1];

    const params = new URLSearchParams(queryParameters);
    const pageSizeParam = params.get('pageSize') ? parseInt(params.get('pageSize'), 10) : null;

    return pageSizeParam ?? defaultPageSize;
  }

  private getSelectedFacetQuery(source: FusionProductSearch): string {
    if (this.selectedFacetQuery) {
      return this.selectedFacetQuery;
    }
    const selectedFacets = this.getSelectedFacets(source.facets);

    const query = selectedFacets.reduce((acc, facet) => {
      acc += `&${encode(facet.facetCode)}=${encode(facet.value)}`;
      return acc;
    }, source.response.query.q);

    this.selectedFacetQuery = query;

    return query;
  }

  private mapCurrentQuery(source: FusionProductSearch) {
    const query = this.getSelectedFacetQuery(source);
    const url: string = this.router.url;

    const path = url.split('?')[0].substring(url.indexOf('/'));

    return {
      query: {
        value: query,
      },
      url: `${path}?q=${query}`,
    };
  }

  private getSelectedFacets(facets: FusionFacet[]) {
    return facets.reduce((acc, facet) => {
      facet.values.forEach((value) => {
        if (value.selected) {
          acc.push({
            ...value,
            facetName: facet.name,
            facetCode: this.mapFacetCode(facet.code),
          });
        }
      });

      return acc;
    }, []);
  }

  private mapFacets(source: FusionProductSearch): Facet[] {
    const currentQuery = this.getSelectedFacetQuery(source);

    return source.facets.map((facet): Facet => {
      const code = this.mapFacetCode(facet.code);
      const values = this.mapFacetValues(code, facet.values, currentQuery);

      if (facet.type === 'number') {
        values.sort((a, b) => (a.code as number) - (b.code as number));
      } else {
        values.sort((a, b) => a.name.localeCompare(b.name));
      }

      return {
        code,
        values,
        name: facet.name,
        type: 'CHECKBOX',
        unit: facet.unit,
        hasSelectedElements: facet.values.some((value) => value.selected),
        hasMinMaxFilters: facet.type === 'number',
        minValue: facet.minValue,
        maxValue: facet.maxValue,
      };
    });
  }

  private mapFacetValues(facetCode: string, values: FusionFacetValue[], currentQuery: string): FacetValue[] {
    return values.map((value) => {
      const queryFilter = `${facetCode}=${value.value.toString()}`;
      const facetValueQuery = value.selected ? currentQuery : `${currentQuery}&${queryFilter}`;

      return {
        code: value.value,
        name: (value.name ?? '').toString(),
        count: value.count,
        selected: value.selected,

        propertyNameArgumentSeparator: '^',
        query: {
          query: {
            value: facetValueQuery,
          },
          url: `/search?q=${facetValueQuery}`,
        },
        queryFilter,
      };
    });
  }

  private mapRedirectUrl(source: FusionProductSearch): string | null {
    if (source.fusion?.redirect?.length > 0) {
      return source.fusion.redirect[0];
    }
    return null;
  }

  private mapFreeTextSearch(source: FusionProductSearch): string {
    return source.response.query.originalQuery ?? source.response['search.keyword'] ?? source.response.query.q ?? '';
  }

  private mapFacetCode(facetCode: string): string {
    return `filter_${facetCode.replace('pimWebUse_', '')}`;
  }

  private normaliseFeedbackData(source: FusionProductSearch, target: ProductSearchPage): void {
    const feedBackResults = this.converterService.convert(source, FUSION_FEEDBACK_CAMPAIGNS_NORMALIZER);
    const hasFeedBackData = feedBackResults?.pushedProducts?.length || feedBackResults?.feedbackTexts?.entry?.length;

    if (hasFeedBackData) {
      if (feedBackResults.pushedProducts.length) {
        target.feedbackCampaigns = [
          {
            pushedProducts: feedBackResults.pushedProducts,
            feedbackTexts: feedBackResults.feedbackTexts,
          },
        ];
      } else {
        target.feedbackCampaigns = [
          {
            feedbackTexts: feedBackResults.feedbackTexts,
          },
        ];
      }
    }
  }

  private normalisePunchOutData(source: FusionCustomerRestrictedData, target: ProductSearchPagePunchout): void {
    target.punchedOut = source?.customerRestricted?.isRestricted ?? false;

    const punchedOutProducts = source?.customerRestricted?.response?.docs;

    if (!punchedOutProducts || punchedOutProducts.length === 0) {
      return;
    }

    const normalisedPunchoutProducts = punchedOutProducts.map((item) => ({
      code: item.productNumber,
      codeErpRelevant: item.productNumber,
    }));

    target.punchedOutProducts = normalisedPunchoutProducts;
  }

  private mapCategoryDisplayData(source: FusionProductSearch): Breadcrumb[] {
    const categories = source.facets?.find((facet) => facet.code === this.CATEGORY_FILTER_KEY)?.values ?? [];
    const selectedCategory = categories.find(
      (category: FusionCategoryFacetValue) => category.value === this.getCurrentCategoryCode(source),
    ) as FusionCategoryFacetValue;

    const path = selectedCategory?.path ?? '';
    const categoryPath = this.parseCategoryPath(path);
    const queryParams = this.getQueryParams();

    return categoryPath.map((category) => ({
      name: category.name,
      url: this.adjustCategoryDisplayUrl(category.code, queryParams),
    }));
  }

  private getCurrentCategoryCode(source: FusionProductSearch): string {
    const fq = source.request.fq ?? [];
    let filterParam = fq.find((query) => query.startsWith('categoryCodes'));
    if (!filterParam) {
      // category1Code, category2Code etc. have lower priority than `categoryCode`
      filterParam = fq.find((query) => query.startsWith('category'));
    }

    return filterParam?.replace(/[\(\)\"]/g, '').split(':')[1] ?? '';
  }

  private parseCategoryPath(path: string): { code: string; name: string }[] {
    // example path: "2||cat-L2-3D_525341|Optoelectronics||cat-L3D_525297|LEDs||cat-DNAV_PL_020206|Light Pipes"
    return path
      .slice(3)
      .split('||')
      .map((tuple) => {
        const [code, name] = tuple.split('|');
        return { code, name };
      });
  }

  private adjustCategoryDisplayUrl(categoryUrl: string, queryParams: Params): string {
    let path = `c/${categoryUrl}`;

    if (!this.pageHelper.isCategoryPage()) {
      const url = new URL('https://' + this.router.url);
      const query = url.searchParams.get('q');
      url.searchParams.set(this.CATEGORY_FILTER_PARAM, categoryUrl);
      if (this.pageHelper.isSearchPage()) {
        url.searchParams.set('q', query);
      }

      path = url.pathname + url.search;
    }

    const urlTree = this.urlSerializer.parse(path);

    if (queryParams.sort?.length) {
      urlTree.queryParams.sort = queryParams.sort;
    }

    if (queryParams.pageSize) {
      urlTree.queryParams.pageSize = queryParams.pageSize;
    }

    if (queryParams.useTechnicalView === 'true') {
      urlTree.queryParams.useTechnicalView = queryParams.useTechnicalView;
    }

    if (queryParams.sid) {
      urlTree.queryParams.sid = queryParams.sid;
    }

    return urlTree.toString();
  }

  private getQueryParams(): Params {
    const queryParameters = this.router.url.split('?')[1] ?? '';
    const urlSearchParams = new URLSearchParams(queryParameters);
    return Object.fromEntries(urlSearchParams) as Params;
  }
}
