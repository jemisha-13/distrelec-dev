/* eslint-disable @typescript-eslint/naming-convention */
// Whist we do not use this model on the UI we still use it in populators and adapters as the source

import { Channel } from '@model/site-settings.model';
import { SearchCriteria } from '@spartacus/storefront';
import { Params } from '@angular/router';

export interface FusionProductSearch {
  fusion?: {
    redirect?: string[];
    facet_labels?: string[];
    banner?: string[];
    removedCategoryCodeFilter?: boolean;
  };

  request: Params;

  response?: {
    docs?: FusionProduct[];
    numFound?: number;
    rulesTriggered?: boolean;
    query?: {
      q?: string;
      originalQuery?: string;
      autocorrected?: boolean;
    };
    start?: number;
    totalPages?: number;
    maxScore?: number;
    carousel?: FusionProduct[];
    numFoundExact?: boolean;
    fusionQueryId?: string;
    punchOut?: boolean;
    punchOutFilter?: {
      productCode?: string;
      productHierarchy?: string;
      manufacturerCode?: string;
    };
    ['search.keyword']?: string;
    ['search.keyword.query']?: string;
    ['search.spelling.corrected']?: boolean;
  };

  responseHeader?: {
    zkConnected?: boolean;
    QTime?: number;
    totalTime?: number;
    params?: {
      fusionQueryId?: string;
    };
    status?: number;
  };

  facets: FusionFacet[];
}

export interface FusionCustomerRestrictedData {
  customerRestricted?: {
    isRestricted: boolean;
    response?: {
      docs?: { productNumber: string }[];
    };
  };
}

export interface FusionFacet {
  code: string;
  name: string;
  hasMinMaxFilters: boolean;
  minValue?: number;
  maxValue?: number;
  values: FusionFacetValue[];
  type?: 'string' | 'number';
  unit?: string;
  baseUnit?: string;
  multiselect?: boolean;
  field?: string;
}

export interface FusionFacetValue {
  level?: number;
  count: number;
  name: string; // value for the UI
  value: string | number; // field to be sent to Fusion.
  selected?: boolean;
  path?: string;
  filter?: string;
}

export interface FusionCategoryFacetValue extends FusionFacetValue {
  value: string;
  level: number;
  path: string;
  filter: string;
}

export interface FusionProduct {
  inStock?: boolean;
  id?: string;
  description?: string;
  thumbnail?: string;
  displayFields?: string;
  energyEfficiency?: string;
  alternativeAliasMPN?: string;
  ean?: string;
  normalizedAlternativesMPN?: string;
  normalizedAlternativeAliasMPN?: string;
  sapMPN?: string;
  typeNameNormalized?: string;
  typeName?: string;
  category1Name?: string;
  category2Name?: string;
  category3Name?: string;
  category4Name?: string;
  category1Code?: string;
  category2Code?: string;
  category3Code?: string;
  category4Code?: string;
  category1?: string;
  category2?: string;
  category3?: string;
  category4?: string;
  categoryCodePath?: string;
  scalePricesGross?: string;
  scalePricesNet?: string;
  itemMin?: number;
  itemStep?: number;
  salesUnit?: string;
  orderQuantityMinimum?: number;
  singleMinPriceNet?: number;
  standardPriceNet?: number;
  singleMinPriceGross?: number;
  standardPriceGross?: number;
  sapPlantProfile?: string;
  isRndBrand?: boolean;
  productNumber?: string;
  productFamilyCode?: string;
  imageURL?: string;
  imageURL_landscape_medium?: string;
  imageURL_landscape_small?: string;
  codeErpRelevant?: string;
  visibleInChannels?: Channel[];
  visibleInShop?: boolean;
  buyable?: boolean;
  productFamilyUrl?: string;
  productFamily?: string;
  distManufacturer?: string;
  manufacturerUrl?: string;
  manufacturerImage?: string;
  manufacturerImageUrl?: string;
  url?: string;
  title?: string;
  code?: string;
  score?: number;
  salesStatus?: number;
  categoryLastCode?: string;
  currency?: string;
  movexArticleNumber?: string;
  elfaArticleNumber?: string;
  eligibleForReevoo?: boolean;
  activePromotionLabels?: string;
  energyEfficiencyLabelImageUrl?: string;
  percentageDiscount?: number;
}

export interface FusionSearchCriteria extends SearchCriteria {
  rows?: number;
  sort?: string;
  start?: number;
  country?: string;
  language?: string;
  channel?: string;
  customerId?: string;
  mode?: string;
  q?: string;
  userId?: string;
  sessionId?: string;
}

export interface FusionErrorResponse {
  timestamp: string;
  service: string;
  error: string;
  path: string;
  message: string;
  messages: string[];
  trace: unknown[];
}
