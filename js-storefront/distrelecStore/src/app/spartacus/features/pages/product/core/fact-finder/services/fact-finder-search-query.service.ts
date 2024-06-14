import { Injectable } from '@angular/core';
import { Params, Router } from '@angular/router';
import { decode, encode } from '@helpers/encoding';
import { ViewConfig } from '@spartacus/storefront';
import { PageHelper } from '@helpers/page-helper';
import { SearchQueryService } from '@features/pages/product/core/services/abstract-product-search-query.service';
import { Feature } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class FactFinderSearchQueryService extends SearchQueryService {
  constructor(
    config: ViewConfig,
    private pageHelper: PageHelper,
    private router: Router,
  ) {
    super(config);
  }

  override buildProductSpecificationsQuery(feature: Feature) {
    const featureName = feature.name.replace(/±/g, '%26plusmn%3B');
    const featureValue = feature.featureValues[0].value.replace(/±/g, '%26plusmn%3B');

    let filterId = 'filter_' + encode(featureName);
    const filterValue = encode(featureValue);

    if (feature.featureUnit) {
      const featureUnitName = feature.featureUnit.name.replace(/±/g, '%26plusmn%3B');
      filterId += '~~' + encode(featureUnitName);
    }

    return { key: filterId, value: filterValue };
  }

  /**
   * Map individual parameters to query string
   * The format is: 'queryString:sort:filter1:value1:filter2:value2'
   * e.g. { q: 'test', filter_categoryCode: '123' } => 'test::categoryCode:123'
   **/
  protected override mapQuery(params: Params): { query: string } {
    const { q: queryString, ...rest } = params;
    const decodedQueryString = queryString ? decode(queryString) : queryString;
    let mappedQuery = decodedQueryString ? `${decodedQueryString}:` : ':';

    const mapQuery: string = Object.entries(rest)
      .map(
        ([key, value]) => <string>[]
            .concat(value) //map value(s) to array regardless of it's type
            .map((val: string) => `:${key}:${val}`)
            .join(''),
      )
      .join('');

    if (mapQuery) {
      mappedQuery += `${mapQuery}`;
    }
    return { query: mappedQuery };
  }

  protected override addPageSpecificSearchParameters(params: Params): Params {
    let firstIndex = this.router.url.lastIndexOf('/') + 1; // length of '/'
    const lastIndex = this.router.url.lastIndexOf('?');

    if (this.pageHelper.isCategoryPage()) {
      const categoryCode =
        lastIndex !== -1 ? this.router.url?.slice(firstIndex, lastIndex) : this.router.url?.slice(firstIndex);

      return {
        ...params,
        // eslint-disable-next-line @typescript-eslint/naming-convention
        mainCategory: categoryCode,
      };
    }

    if (this.pageHelper.isManufacturerDetailPage()) {
      let manufacturer =
        lastIndex !== -1 ? this.router.url?.slice(firstIndex, lastIndex) : this.router.url?.slice(firstIndex);

      if (manufacturer.includes('#')) {
        manufacturer = manufacturer.split('#')[0];
      }

      return {
        ...params,
        // eslint-disable-next-line @typescript-eslint/naming-convention
        filter_manufacturerCode: manufacturer,
      };
    }

    if (this.pageHelper.isProductFamilyPage()) {
      firstIndex = this.router.url.lastIndexOf('pf/') + 3; //length of 'pf/'
      const family =
        lastIndex !== -1 ? this.router.url?.slice(firstIndex, lastIndex) : this.router.url?.slice(firstIndex);

      return {
        ...params,
        // eslint-disable-next-line @typescript-eslint/naming-convention
        filter_productFamilyCode: family,
      };
    }

    if (this.pageHelper.isNewProductsPage()) {
      return {
        ...params,
        newSearch: true,
      };
    }

    if (this.pageHelper.isClearancePage()) {
      return {
        ...params,
        outletSearch: true,
      };
    }

    return params;
  }
}
