import { Breadcrumb } from '@model/breadcrumb.model';
import { FactFinderProductParameters } from '@model/factfinder.model';
import { VolumePriceMap } from '@model/price.model';
import { ManufacturerData } from '@model/manufacturer.model';
import { Occ, Price, Product } from '@spartacus/core';
import { DownloadData } from '@model/downloads.model';

declare module '@spartacus/core' {
  interface Product {
    activePromotionLabels?: PromotionLabel[];
    allowBulk?: boolean;
    audios?: AudioData[];
    alternativeAliasMPN?: string;
    availableInSnapEda?: boolean;
    availableToB2B?: boolean;
    availableToB2C?: boolean;
    batteryComplianceCode?: string;
    breadcrumbs?: Breadcrumb[];
    businessOnlyProduct?: boolean;
    buyable?: boolean;
    buyablePhaseoutProduct?: unknown;
    buyableReplacementProduct?: unknown;
    calibrated?: boolean;
    calibrationItemArtNo?: string;
    catPlusItem?: boolean;
    categoryCodePath?: string;
    codeErpRelevant?: string;
    commonAttrs?: Attribute[];
    countryOfOrigin?: CountryOfOrigin;
    configurable?: boolean;
    customsCode?: string;
    detailsVisible?: boolean;
    dimensions?: string;
    distManufacturer?: ManufacturerData;
    downloads?: DownloadData[];
    ean?: string;
    elfaArticleNumber?: string;
    eligibleForReevoo?: boolean;
    endOfLifeDate?: Date;
    energyEfficiency?: string; // TODO: Not on ProductData response -- did it change name?
    energyEfficiencyLabelImage?: { key: string; value: { format: string; url: string } }[];
    energyPower?: string;
    enumber?: string;
    exclusionReason?: string;
    factFinderTrackingParameters?: FactFinderProductParameters;
    formattedSvhcReviewDate?: string;
    ghsImages?: []; // Needs checking from BE
    grossWeight?: number;
    grossWeightUnit?: string;
    hasSvhc?: boolean;
    hazardStatements?: any[];
    images360?: any[];
    isBetterWorld?: boolean;
    isDangerousGoods?: boolean;
    isProductBatteryCompliant?: boolean;
    isRNDProduct?: boolean;
    isROHSComplaint?: boolean;
    isROHSConform?: boolean;
    isROHSValidForCountry?: boolean;
    itemCategoryGroup?: string;
    movexArticleNumber?: string;
    nameEN?: string;
    navisionArticleNumber?: string;
    orderQuantityMinimum?: number;
    orderQuantityStep?: number;
    origPosition?: string;
    otherAttributes?: { key: string; value: Attribute }[];
    possibleOtherAttributes?: { entry: { key: string; value: string }[] };
    productCode?: string;
    productFamilyUrl?: string;
    productInformation?: ProductInformation;
    productImages?: Occ.Image[];
    promotionEndDate?: Date;
    replacementReason?: string;
    rohs?: string; // Needs checking from BE
    rohsCode?: string;
    salesStatus?: string;
    salesUnit?: string;
    scip?: string; // Needs checking from BE
    signalWord?: string;
    supplementalHazardInfos?: any[];
    svhc?: string;
    svhcReviewDate?: string;
    svhcURL?: string;
    technicalAttributes?: { key: string; value: string }[];
    topLabel?: { label: string; code: string };
    transportGroupData?: TransportGroupData;
    typeName?: string;
    unspsc5?: string;
    videos?: Video[];
    volumePricesMap?: VolumePriceMap[];
    price?: Price;
    id?: string;
    metaData?: ProductMetaData;
  }

  interface Feature {
    position?: number;
    featurePosition?: number;
    searchable?: boolean;
    visibility?: string;
  }

  interface FeatureValue {
    baseUnitValue?: string;
  }
}

export interface ProductEnhancement {
  productCode: string;
  inShoppingList: boolean;
  exclusionReason?: string;
}

export interface VolumePricesValue extends Price {
  basePrice?: number;
  currencyIso?: string;
  formattedValue?: string;
  minQuantity?: number;
  value?: number;
  pricePerX?: number;
  pricePerXBaseQty?: number;
  pricePerXUOM?: string;
  pricePerXUOMDesc?: string;
  pricePerXUOMQty?: number;
  priceWithVat?: number;
  vatPercentage?: number;
  vatValue?: number;
}

export type VolumePricesMapList = VolumePriceMap;

export interface ProductInformation {
  articleDescription: string[];
  articleDescriptionBullets: string[];
  familyDescription: string[];
  familyDescriptionBullets: string[];
  seriesDescription: string[];
  seriesDescriptionBullets: string[];
  usageNote: string[];

  // Found on details modal implementation but not confirmed on data
  deliveryNote?: string;
  deliveryNoteArticle?: string;
  paperCatalogPageNumber?: number;
  // eslint-disable-next-line @typescript-eslint/naming-convention
  paperCatalogPageNumber_16_17?: number;
}

export interface CountryOfOrigin {
  name?: string;
  nameEN?: string;
  isocode?: string;
  european?: boolean;
}

export interface TransportGroupData {
  bulky?: boolean;
  code?: string;
  dangerous?: boolean;
  nameErp?: string;
  relevantName?: string;
}

export interface Attribute {
  code: string;
  name: string;
  comparable: boolean;
  featureUnit: { name: string; symbol: string; unitType: string };
  featureValues: { value: number }[];
  range: boolean;
}

export interface ProductMetaData {
  alternateHreflangUrls?: string;
  canonicalUrl?: string;
  metaDescription?: string;
  metaImage?: string;
  metaTitle?: string;
  robots?: string;
}

export interface Video {
  brightcovePlayerId?: string;
  brightcoveVideoId?: string;
  languages?: string[];
  youtubeUrl?: string;
}

export interface PromotionLabel {
  code?: string;
  label?: string;
  nameEN?: string;
  priority?: number;
  rank?: number;
}

export interface ICustomProduct extends Omit<Product, 'images'> {
  // Our product normalizer/populator override is using the OCC model for Images
  // We could use the correct model but we would need to migrate all the components
  images: Occ.Image[];
}

export interface AudioData {
  audioUrl: string;
  mimeType: string;
}
