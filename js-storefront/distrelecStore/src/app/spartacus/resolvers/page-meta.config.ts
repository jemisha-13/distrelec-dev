import { PageMetaConfig } from '@spartacus/core';

export const pageMetaConfig: PageMetaConfig = {
  pageMeta: {
    enableInDevMode: true,
    resolvers: [
      {
        property: 'title',
        method: 'resolveTitle',
      },
      {
        property: 'heading',
        method: 'resolveHeading',
      },
      {
        property: 'breadcrumbs',
        method: 'resolveBreadcrumbs',
      },
      {
        property: 'description',
        method: 'resolveDescription',
        disabledInCsr: false,
      },
      {
        property: 'image',
        method: 'resolveImage',
        disabledInCsr: false,
      },
      {
        property: 'robots',
        method: 'resolveRobots',
        disabledInCsr: false,
      },
      {
        property: 'canonicalUrl',
        method: 'resolveCanonicalUrl',
        disabledInCsr: false,
      },
      {
        property: 'alternateLinks',
        method: 'resolveAlternateLinks',
      },
    ],
  },
};
