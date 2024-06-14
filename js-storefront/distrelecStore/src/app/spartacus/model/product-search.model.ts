import { Breadcrumb } from '@model/breadcrumb.model';
import { FacetValue, Price, Product } from '@spartacus/core';

export enum EmptySearchPageType {
  B2BOnlyProduct = 'B2B_ONLY_PRODUCT',
}

export type CategoryFilter = FacetValue & {
  displayName?: string;
  url?: string;
};

export interface ProductSearchPagePunchout {
  punchedOut?: boolean;
  punchedOutProducts?: { code: string; codeErpRelevant: string }[];
}

export interface CarouselProductData {
  pushedProducts?: Product[];
  feedbackTexts?: FeedbackCampaignTexts<string>;
}

interface FeedbackCampaignTexts<T> {
  entry: { key: T; value: string }[];
}

declare module '@spartacus/core' {
  interface ProductSearchPage {
    keywordRedirectUrl?: string;
    categoryDisplayData?: CategoryDisplayData[];
    feedbackCampaigns?: CarouselProductData[];
    categoryBreadcrumbs?: Breadcrumb[];
    categoryFilters?: CategoryFilter[];
    punchedOut?: boolean;
    punchedOutProducts?: { code: string; codeErpRelevant: string }[];
    productFamilyCategoryCode?: string;
    notFound?: boolean;
    filtersRemovedGeneralSearch?: boolean;
    singleWordSearchItems?: SingleWordData[];
    emptyPageType?: EmptySearchPageType;
    url?: string;
    noRelevantResultsBanner?: boolean;
  }

  interface Facet {
    code: string;
    hasMinMaxFilters?: boolean;
    hasSelectedElements: boolean;
    type: string;
    unit: string;
    open?: boolean; // TODO refactor this to existing `expanded` property
    minValue?: number | string;
    maxValue?: number | string;
    values?: FacetValue[];
  }

  interface FacetValue {
    code: string | number;
    checked?: boolean;
    absoluteMaxValue?: number;
    absoluteMinValue?: number;
    propertyName?: string;
    propertyNameArgumentSeparator?: string;
    queryFilter?: string;
    selectedMaxValue?: number;
    selectedMinValue?: number;
    name?: string;
  }

  interface SearchConfig {
    sid?: string;
  }
}

declare module '@spartacus/storefront' {
  interface SearchCriteria {
    page?: number;
    sort?: string;
    sid?: string;
  }
}

export interface CategoryDisplayData {
  absoluteMaxValue: number;
  absoluteMinValue: number;
  code: string;
  count: number;
  image: {
    format: string;
    url: string;
  }[];
  name: string;
  selected: false;
  selectedMaxValue: number;
  selectedMinValue: number;
  url: string;
}

export interface SearchQuery {
  query: string;
  queryFromSuggest?: boolean;
}

export interface SingleWordData {
  count: number;
  singleTerm: string;
  items: {
    name: string;
    images: {
      format: string;
      url: string;
    }[];
    price: Price;
  }[];
}
