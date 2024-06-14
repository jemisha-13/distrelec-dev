import { Injectable } from '@angular/core';
import { SearchExperienceFactory } from '@features/pages/product/core/dynamic/search-experience.factory';
import { SearchQueryService } from '@features/pages/product/core/services/abstract-product-search-query.service';
import { SearchCriteria } from '@spartacus/storefront';
import { Params } from '@angular/router';
import { Feature } from '@spartacus/core';

@Injectable()
export class DynamicSearchQueryService {
  private serviceInstance: SearchQueryService;

  constructor(private factory: SearchExperienceFactory) {}

  private get service(): SearchQueryService {
    if (!this.serviceInstance) {
      this.serviceInstance = this.factory.createSearchQueryService();
    }
    return this.serviceInstance;
  }

  buildSearchCriteria(params: Params | SearchCriteria): SearchCriteria {
    return this.service.buildSearchCriteria(params);
  }

  buildProductSpecificationsQuery(feature: Feature): { key: string; value: string } {
    return this.service.buildProductSpecificationsQuery(feature);
  }
}
