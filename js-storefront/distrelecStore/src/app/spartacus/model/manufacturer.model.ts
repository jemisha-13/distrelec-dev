import { ImageList } from '@model/misc.model';

export interface ManufacturerListData {
  code?: string;
  name?: string;
  nameSeo?: string;
  url?: string;
  urlId?: string;
}

export interface ManufacturerListResponse {
  response?: {
    key?: string;
    manufacturerList?: ManufacturerListData[];
  }[];
}

export interface ManufacturerData {
  code?: string;
  emailAddresses?: string[];
  image: ImageList;
  name: string;
  nameSeo?: string;
  phoneNumbers?: string[];
  productGroups?: string[];
  promotionText?: string;
  seoMetaDescription?: string;
  seoMetaTitle?: string;
  url?: string;
  urlId?: string;
  webDescription?: string;
  websites?: string[];
}
