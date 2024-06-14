import { Category } from '@spartacus/core';
import { ImageList } from '@model/misc.model';

declare module '@spartacus/core' {
  interface Category {
    children?: Category[];
    images?: ImageList;
    introText?: string;
    level?: number;
    nameEN?: string;
    productCount?: number;
    selected?: boolean;
    seoMetaDescription?: string;
    seoMetaTitle?: string;
    seoSections?: { header?: string; text?: string }[];
  }
}

export interface CategoryPageData {
  breadcrumbs: Category[];
  showCategoriesOnly: boolean;
  sourceCategory: SourceCategory;
  subCategories: Category[];
}

export interface SourceCategory extends Category {
  relatedData: RelatedData[];
}

export interface DistCategoryIndexResponse {
  categories: Category[];
}

export interface TopCategories {
  categories?: Array<Category>;
  searchbarCategories?: Array<Category>;
}

export enum RelatedDataType {
  TopSeller = 'TOP_SELLER_PRODUCT',
  RelatedCategory = 'RELATED_CATEGORY',
  RelatedManufacturer = 'RELATED_MANUFACTURER',
  RelatedProduct = 'RELATED_PRODUCT',
  NewProducts = 'NEW_ARRIVAL_PRODUCT',
}

export interface RelatedDataValues {
  name: string;
  uid: string;
  url: string;
}

export interface CRelatedData {
  type: string;
  values: RelatedDataValues[];
}

export interface RelatedData {
  type: RelatedDataType;
  CRelatedData: CRelatedData[]; // eslint-disable-line @typescript-eslint/naming-convention
}
