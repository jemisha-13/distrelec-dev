/* eslint-disable @typescript-eslint/naming-convention */
export interface IFactFinderResults {
  suggestions: FactFinderSuggestion[];
}

export interface FactFinderSuggestion {
  attributes: Attributes;
  hitCount: number;
  image: string;
  name: string;
  searchParams: string;
  type: FactFinderSuggestionsType;
}

export interface Attributes {
  TypeName?: string;
  articleNr: string;
  ItemsMin: string;
  deeplink?: string;
  SalesUnit?: string;
  Title: string;
  ProductNumber?: string;
  AdditionalImageURLs: string;
  ProductURL: string;
  ItemsStep: string;
  Price?: string;
  categoryCodePathROOT?: string;
  id?: string;
  Buyable?: string;
  energyEfficiency?: string;
  ManufacturerUrl?: string;
  CategoryExtensions?: string;
  sourceField?: string;
  energyEfficiencyLabelImageUrl?: string;
}

export type FactFinderSuggestionsType = 'searchTerm' | 'category' | 'brand' | 'productName';

export interface QuickSearchPrice {
  currency: string;
  price: PriceType;
  priceType: string;
}

export interface PriceType {
  minQuantity: string;
  quantityPrice: string;
}

export interface IFactFinderRecommendations {
  resultRecords: IResultRectord[];
  timedOut: boolean;
}

export interface IResultRectord {
  id: string;
  record: IRecord;
}

export interface IRecord {
  Accessory: string;
  AdditionalImageURLs: string;
  AlternateAliasMPN: string;
  Buyable: string;
  Category1: string;
  Category2: string;
  Category3: string;
  Category4: string;
  CategoryExtensions: string;
  CategoryPath: string;
  DQAttributes: string;
  Description: string;
  EAN: string;
  FFAfterSearchReorder: string;
  FFAutomaticSearchOptimization: string;
  FFCheckoutCount: string;
  FFRanking: string;
  ImageURL: string;
  InStock: string;
  ItemsMin: string;
  ItemsStep: string;
  Manufacturer: string;
  ManufacturerImageUrl: string;
  ManufacturerUrl: string;
  NumericalSearchAttributes: string;
  Price: string;
  ProductFamilyName: string;
  ProductNumber: string;
  ProductNumberElfa: string;
  ProductNumberMovex: string;
  ProductURL: string;
  PromotionLabels: string;
  Replacement: string;
  SalesUnit: string;
  StandardPrice: string;
  TechnicalAttributes: string;
  Title: string;
  TitleShort: string;
  TypeName: string;
  TypeNameNormalized: string;
  WebUse: string;
  alternativesMPN: string;
  availableInPickup: string;
  category1Code: string;
  category2Code: string;
  category3Code: string;
  category4Code: string;
  categoryCodePath: string;
  curatedProductSelection: string;
  energyEfficiency: string;
  normalizedAlternateAliasMPN: string;
  normalizedAlternativesMPN: string;
  pickupStock: string;
  productFamilyCode: string;
  productStatus: string;
  salesStatus: string;
  sapMPN: string;
  sapPlantProfiles: string;
  singleMinPrice: string;
  singleUnitPrice: string;
  totalInStock: string;
}

export interface FactFinderProductParameters {
  [filter: string]: unknown;
  trackQuery: string;
  pos: number;
  origPos: number;
  page: number;
  pageSize: number;
  origPageSize: number;
  track: string;
  product?: string;
  prodprice?: number;
  simi?: number;
  quantity?: number;
  campaign?: number;
  pageType?: string;
}
export interface IFactFinderResultImages {
  landscape_medium: string;
  landscape_small: string;
}

export interface GroupedFactFinderSuggestions {
  displaySuggestions: boolean;
  displayLeftSideSuggestions: boolean;
  searchTerm: FactFinderSuggestion[];
  category: FactFinderSuggestion[];
  brand: FactFinderSuggestion[];
  productName: FactFinderSuggestion[];
}

export interface FactFinderEnv {
  ffsearchChannel: string;
  ffrecoUrl: string;
  ffsuggestUrl: string;
}
