import { PageRobotsMeta } from '@spartacus/core';

declare module '@spartacus/core' {
  interface PageMeta {
    alternateLinks?: string;
  }
}

export interface SeoMetaData {
  alternateHreflangUrls?: string;
  canonicalUrl?: string;
  metaDescription?: string;
  metaImage?: string;
  metaTitle?: string;
  robots?: PageRobotsMeta[];
}
