// Whist we do not use this model on the UI we still use it in populators and adapters as the source

export interface FusionSuggestion {
  response: {
    docs?: FusionProductSuggestion[];
    categorySuggestions: FusionTermSuggestions[];
    manufacturerSuggestions: FusionTermSuggestions[];
    termSuggestions: FusionTermSuggestions[];
    fusionQueryId?: string;
  };
}

export interface FusionProductSuggestion {
  id: string;
  mpnSearch: string;
  scalePricesNet: string;
  scalePricesGross: string;
  singleMinPriceNet: number;
  singleMinPriceGross: number;
  energyEffiency: string;
  itemStep: number;
  itemMin: number;
  titleShort: string;
  manufacturer: string;
  visibleInShop: boolean;
  productUrl: string;
  imageURL: string;
  title: string;
  typeName: string;
  productNumber: string;
  currency: string;
  energyEfficiencyLabelImageUrl: string;
}

export interface FusionTermSuggestions {
  url: string;
  name: string;
  score: number;
}
