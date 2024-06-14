import { Injectable } from '@angular/core';
import { Params } from '@angular/router';
import { Feature } from '@spartacus/core';
import { SearchCriteria, ViewConfig } from '@spartacus/storefront';
import { isNil, omitBy } from 'lodash-es';
@Injectable()
export abstract class SearchQueryService {
  protected constructor(protected config: ViewConfig) {}

  buildSearchCriteria(params: Params | SearchCriteria): SearchCriteria {
    const { pageSize, currentPage, sort, sid, ...rest } = params;
    const searchParameters = this.addPageSpecificSearchParameters(rest);
    const { query, otherParams } = this.mapQuery(searchParameters);

    return omitBy(
      {
        query,
        currentPage,
        sort,
        sid,
        pageSize: pageSize || this.config.view.defaultPageSize,
        ...otherParams,
      },
      isNil,
    );
  }

  abstract buildProductSpecificationsQuery(feature: Feature): { key: string; value: string };

  protected abstract mapQuery(params: Params): { query: string; otherParams?: Params };

  protected abstract addPageSpecificSearchParameters(params: Params): Params;
}
