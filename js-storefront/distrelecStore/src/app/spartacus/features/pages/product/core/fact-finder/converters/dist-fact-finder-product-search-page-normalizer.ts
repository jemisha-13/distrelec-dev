import { Injectable, OnDestroy } from '@angular/core';
import {
  Converter,
  ConverterService,
  Facet,
  FacetValue,
  Occ,
  PRODUCT_NORMALIZER,
  ProductSearchPage,
} from '@spartacus/core';
import { ChannelService } from '../../../../../../site-context/services/channel.service';
import { Channel } from '@model/site-settings.model';
import { EmptySearchPageType } from '../../../../../../model';
import { Params, Router, UrlSerializer } from '@angular/router';
import { PageHelper } from '@helpers/page-helper';

@Injectable({ providedIn: 'root' })
export class DistFactFinderProductSearchPageNormalizer
  implements Converter<Occ.ProductSearchPage, ProductSearchPage>, OnDestroy
{
  /**
   * Specifies the minimal number of top values in case
   * non have been setup by the business.
   */
  protected DEFAULT_TOP_VALUES = 6;
  protected readonly CATEGORY_FILTER_KEY = 'categoryCodePathROOT';

  private subscription;
  private activeChannel: Channel;

  constructor(
    private converterService: ConverterService,
    private channelService: ChannelService,
    private router: Router,
    private pageHelper: PageHelper,
    private urlSerializer: UrlSerializer,
  ) {
    this.subscription = this.channelService.getActive().subscribe((channel) => {
      this.activeChannel = channel;
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  convert(source: Occ.ProductSearchPage, target: ProductSearchPage = {}): ProductSearchPage {
    target = {
      ...target,
      ...(source as any),
    };

    target.url = this.router.url;

    //FactFinder takes care of this in business rules, Fusion cannot. Added for clarity
    target.noRelevantResultsBanner = false;

    this.normalizeFacets(target);
    this.normalizeCategoryFilters(target);
    this.normalizeCategoryBreadcrumbs(target);

    target.products =
      source.products?.map((product) => this.converterService.convert(product, PRODUCT_NORMALIZER)) ?? [];

    this.populateErrorType(target);

    if (!source.pagination) {
      target.pagination = {
        currentPage: 1,
        pageSize: 50,
        totalPages: 1,
        totalResults: 0,
      };
    }

    return target;
  }

  private normalizeFacets(target: ProductSearchPage): void {
    this.normalizeFacetValues(target);
    this.normalizeUselessFacets(target);
  }

  /**
   * The (current) backend returns facets with values that do not contribute
   * to the facet navigation much, as the number in the result list will not get
   * behavior, see https://jira.hybris.com/browse/CS-427.
   *
   * As long as this is not in place, we manually filter the facet from the list;
   * any facet that does not have a count < the total results will be dropped from
   * the facets.
   */
  private normalizeUselessFacets(target: ProductSearchPage): void {
    if (target.facets) {
      target.facets = target.facets.filter(
        (facet) =>
          !target.pagination ||
          !target.pagination.totalResults ||
          // Temporary customisation as it's not set to visible on backend
          (facet as any).code === this.CATEGORY_FILTER_KEY ||
          ((!facet.hasOwnProperty('visible') || facet.visible) &&
            facet.values &&
            facet.values.find((value) => value.selected || value.count < target.pagination.totalResults)),
      );
    }
  }

  /*
   * In case there are so-called `topValues` given for the facet values,
   * values are obsolete.
   *
   * `topValues` is a feature in Adaptive Search which can limit a large
   * amount of facet values to a small set (5 by default). As long as the backend
   * provides all facet values AND topValues, we normalize the data to not bother
   * the UI with this specific feature.
   */
  private normalizeFacetValues(target: ProductSearchPage): void {
    target.facets =
      target.facets?.map((facetSource: Facet) => {
        const { topValues, ...facetTarget } = facetSource;
        facetTarget.topValueCount = topValues?.length > 0 ? topValues.length : this.DEFAULT_TOP_VALUES;
        facetTarget.values = facetTarget.values?.map((facetValue) => {
          const code = this.transformFacetCodeType(facetSource, facetValue);
          return { ...facetValue, code };
        });

        return facetTarget;
      }) ?? [];
  }

  private transformFacetCodeType(facetSource: Facet, facetValue: FacetValue): string | number {
    const facetQueryFilter = facetValue.queryFilter.split('=')[1];
    const code = facetQueryFilter ?? facetValue.name;

    return facetSource.hasMinMaxFilters ? facetValue.name : code;
  }

  private normalizeCategoryFilters(target: ProductSearchPage): void {
    const categories = target.facets?.find((facet) => facet.code === this.CATEGORY_FILTER_KEY)?.values ?? null;
    const categoryDisplayData = target.categoryDisplayData ?? null;

    if (!categories || !categoryDisplayData) {
      target.categoryFilters = [];
      return;
    }

    const reducedDisplayData = categoryDisplayData?.reduce((acc, curr) => ({ ...acc, [curr.code]: curr }), {});
    const codes = Object.keys(reducedDisplayData);
    const queryParams = this.getQueryParams();

    target.categoryFilters = categories
      ?.filter((cat) => codes.includes(cat.name))
      ?.map((cat) => ({
        ...cat,
        displayName: reducedDisplayData[cat.name].name,
        url: this.adjustCategoryDisplayUrl(reducedDisplayData[cat.name].url, queryParams),
      }));
  }

  private normalizeCategoryBreadcrumbs(target: ProductSearchPage): void {
    const queryParams = this.getQueryParams();

    target.categoryBreadcrumbs = target.categoryBreadcrumbs?.map((category) => ({
      ...category,
      url: this.adjustCategoryDisplayUrl(category.url, queryParams),
    }));
  }

  private adjustCategoryDisplayUrl(categoryUrl: string, queryParams: Params): string {
    try {
      const urlTree = this.urlSerializer.parse(categoryUrl);

      if (queryParams.sort?.length) {
        urlTree.queryParams.sort = queryParams.sort;
      }

      if (queryParams.pageSize) {
        urlTree.queryParams.pageSize = queryParams.pageSize;
      }

      if (queryParams.useTechnicalView === 'true') {
        urlTree.queryParams.useTechnicalView = queryParams.useTechnicalView;
        return urlTree.toString();
      }

      if (this.pageHelper.isNewProductsPage()) {
        return '/cms' + urlTree.toString();
      }

      return urlTree.toString();
    } catch (e) {
      return categoryUrl;
    }
  }

  private populateErrorType(target: ProductSearchPage): void {
    if (target.products?.length === 1 && !target.products[0].availableToB2C && this.activeChannel === 'B2C') {
      target.products = [];
      target.emptyPageType = EmptySearchPageType.B2BOnlyProduct;
    }
  }

  private getQueryParams(): Params {
    const queryParameters = this.router.url.split('?')[1] ?? '';
    const urlSearchParams = new URLSearchParams(queryParameters);
    return Object.fromEntries(urlSearchParams) as Params;
  }
}
