import { CmsBannerComponentMedia, CmsComponent, Image } from '@spartacus/core';

declare module '@spartacus/core' {
  export interface CmsNavigationComponent extends CmsComponent {
    navigationType?: string;
  }

  export interface CmsNavigationEntry {
    localizedUrl?: string;
  }

  export interface CmsLinkComponent {
    localizedUrl: string;
  }

  export interface CmsBannerComponent {
    modifiedtime?: Date;
    mode?: string;
    localizedUrlLink?: string;
    priority?: string;
    dataAttributes?: DataAttributes;
    backgroundImage?: CmsBannerComponentMedia;
    localizedUrlText?: string;
  }
}

export interface DataAttributes {
  aaSectionTitle?: string;
  aaSectionPos?: string;
  aaButtonPos?: string;
  aaLinkText?: string;
  aaType?: string;
}

export interface CmsWarningComponent extends CmsComponent {
  headline: string;
  body: string;
  warningType: CmsWarningType;
  componentWidth: CmsComponentWidth;
  displayIcon: boolean;
  visibleToDate?: string;
  modifiedTime?: Date;
}

export enum CmsComponentWidth {
  TwoThird = 'twoThird',
  FullWidth = 'fullWidth',
  OneThird = 'oneThird',
  ThreeQuarters = 'threeQuarters',
}

export enum CmsWarningType {
  Information = 'information',
  Promotion = 'promotion',
  Error = 'error',
  Warning = 'warning',
  Success = 'success',
}

export interface CmsHeadlineComponent extends CmsComponent {
  container?: string;
  headline?: string;
  modifiedtime?: Date;
  name?: string;
  typeCode?: string;
  uid?: string;
  uuid?: string;
}

export interface CmsProductCardComponent extends CmsComponent {
  articleNumber?: string;
  buttonType?: string;
  customTitle?: string;
  brandLogo?: string;
  topDisplay?: string;
  labelDisplay?: string;
  brandAlternateText?: string;
  image?: boolean;
  name?: string;
  orientation?: string;
  snippet?: string;
  title?: boolean;
  uid?: string;
  promotionParameter?: string;
  uuid?: string;
  typeCode?: string;
  modifiedtime?: Date;
}

export interface CmsProductCardGroupComponent extends CmsComponent {
  productCardItems?: string;
}

export interface CmsWelcomeMatComponent extends CmsComponent {
  uid?: string;
  uuid?: string;
  typeCode?: string;
  modifiedtime?: Date;
  name?: string;
  container?: string;
  image?: CmsBannerComponentMedia;
  useforMobile?: string;
  text?: string;
}

export interface CmsDistRestrictionComponentGroup extends CmsComponent {
  components?: string;
}
