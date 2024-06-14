import { Page, PageRobotsMeta } from '@spartacus/core';

export const MOCK_CMS_PAGE: Page = {
  name: 'Search Results Page',
  type: 'ContentPage',
  label: 'search',
  template: 'SearchResultsListPageTemplate',
  pageId: 'search',
  title: 'Search',
  properties: {
    languageEN: 'english',
    seo: {
      alternateHreflangUrls:
        'x-default~https://test.distrelec.ch/search/cms/search|en~https://test.distrelec.ch/en/search/cms/search|de~https://test.distrelec.ch/de/search/cms/search|fr~https://test.distrelec.ch/fr/search/cms/search|nl-BE~https://test.distrelec.be/nl/search/cms/search|fr-BE~https://test.distrelec.be/fr/search/cms/search|en-BE~https://test.distrelec.be/en/search/cms/search|cs-CZ~https://test.distrelec.cz/cs/search/cms/search|da-DK~https://test.elfadistrelec.dk/da/search/cms/search|en-DK~https://test.elfadistrelec.dk/en/search/cms/search|de-DE~https://test.distrelec.de/de/search/cms/search|et-EE~https://test.elfadistrelec.ee/et/search/cms/search|sv-FI~https://test.elfadistrelec.fi/sv/search/cms/search|fr-FR~https://test.distrelec.fr/search/cms/search|en-DE~https://test.distrelec.de/en/search/cms/search|en~https://test.distrelec.biz/en/search/cms/search|it-IT~https://test.distrelec.it/it/search/cms/search|lv-LV~https://test.elfadistrelec.lv/lv/search/cms/search|lt-LT~https://test.distrelec.lt/lt/search/cms/search|hu-HU~https://test.distrelec.hu/hu/search/cms/search|nl-NL~https://test.distrelec.nl/nl/search/cms/search|en-NL~https://test.distrelec.nl/en/search/cms/search|no-NO~https://test.elfadistrelec.no/no/search/cms/search|en-NO~https://test.elfadistrelec.no/en/search/cms/search|de-AT~https://test.distrelec.at/de/search/cms/search|en-PL~https://test.elfadistrelec.pl/en/search/cms/search|pl-PL~https://test.elfadistrelec.pl/pl/search/cms/search|ro-RO~https://test.distrelec.ro/ro/search/cms/search|de-CH~https://test.distrelec.ch/de/search/cms/search|sk-SK~https://test.distrelec.sk/sk/search/cms/search|fr-CH~https://test.distrelec.ch/fr/search/cms/search|fi-FI~https://test.elfadistrelec.fi/fi/search/cms/search|sv-SE~https://test.elfa.se/sv/search/cms/search|en-SE~https://test.elfa.se/en/search/cms/search|en-CH~https://test.distrelec.ch/en/search/cms/search',
      canonicalUrl: 'https://test.distrelec.ch/en/search/cms/search',
      metaTitle: 'Search - Distrelec Switzerland',
      metaImage:
        'https://localhost:8088/medias/distrelec-rs-logo-en.svg?context=bWFzdGVyfHJvb3R8NjAzNXxpbWFnZS9zdmcreG1sfGgzYS9oYzEvOTY0NjI2MDAyNzQyMi9kaXN0cmVsZWNfcnNfbG9nb19lbi5zdmd8NGUzZTNlMjQ2OWRkOWNjOTY4N2YwMTEwZTMxMTA2NzI4MzFiNTNlMDAxMTBmMzBkYjZhN2UzOWIzMjMzMzExYQ',
    },
  },
  robots: [PageRobotsMeta.NOINDEX, PageRobotsMeta.FOLLOW],
  slots: {
    TeaserContent: {},
    Logo: {
      components: [
        {
          uid: 'LogoComponent',
          typeCode: 'SimpleBannerComponent',
          flexType: 'SimpleBannerComponent',
        },
      ],
    },
    ServiceNav: {
      components: [
        {
          uid: 'ImportToolPageLink',
          typeCode: 'CMSLinkComponent',
          flexType: 'CMSLinkComponent',
        },
      ],
    },
    MainNav: {
      components: [
        {
          uid: 'MainTopNavComponent',
          typeCode: 'DistMainNavigationComponent',
          flexType: 'DistMainNavigationComponent',
        },
      ],
    },
    Swyn: {
      components: [
        {
          uid: 'FacebookLink',
          typeCode: 'CMSLinkComponent',
          flexType: 'CMSLinkComponent',
        },
        {
          uid: 'GoogleLink',
          typeCode: 'CMSLinkComponent',
          flexType: 'CMSLinkComponent',
        },
        {
          uid: 'YouTubeLink',
          typeCode: 'CMSLinkComponent',
          flexType: 'CMSLinkComponent',
        },
      ],
    },
    FooterLinks: {
      components: [
        {
          uid: 'DistFooterComponent',
          typeCode: 'DistFooterComponent',
          flexType: 'DistFooterComponent',
        },
      ],
    },
    CertLabels: {
      components: [
        {
          uid: 'comp_00000A2M',
          typeCode: 'SimpleBannerComponent',
          flexType: 'SimpleBannerComponent',
        },
      ],
    },
    Impressum: {
      components: [
        {
          uid: 'comp_00000A35',
          typeCode: 'CMSLinkComponent',
          flexType: 'CMSLinkComponent',
        },
        {
          uid: 'comp_00000A3L',
          typeCode: 'CMSLinkComponent',
          flexType: 'CMSLinkComponent',
        },
        {
          uid: 'comp_00000A36',
          typeCode: 'CMSLinkComponent',
          flexType: 'CMSLinkComponent',
        },
      ],
    },
    Vivocha: {},
    LeftSideContentPosition: {
      components: [
        {
          uid: 'leftside_divider',
          typeCode: 'CMSParagraphComponent',
          flexType: 'CMSParagraphComponent',
        },
      ],
    },
    MainCategoryNav: {
      components: [
        {
          uid: 'CategoryMainNavComponent',
          typeCode: 'DistMainNavigationComponent',
          flexType: 'DistMainNavigationComponent',
        },
      ],
    },
    WarningSlot: {},
    HeaderWrapper: {
      components: [
        {
          uid: 'LogoComponent',
          typeCode: 'SimpleBannerComponent',
          flexType: 'SimpleBannerComponent',
        },
        {
          uid: 'CategoryMainNavComponent',
          typeCode: 'DistMainNavigationComponent',
          flexType: 'DistMainNavigationComponent',
        },
        {
          uid: 'HeaderLoginComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'HeaderLoginComponent',
        },
        {
          uid: 'MiniCartComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'MiniCartComponent',
        },
        {
          uid: 'SearchBoxComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'SearchBoxComponent',
        },
      ],
    },
    breadcrumb: {
      components: [
        {
          uid: 'BreadcrumbComponent',
          typeCode: 'BreadcrumbComponent',
          flexType: 'BreadcrumbComponent',
        },
      ],
    },
    SearchBox: {
      components: [
        {
          uid: 'SearchBoxComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'SearchBoxComponent',
        },
      ],
    },
    HeaderLogin: {
      components: [
        {
          uid: 'HeaderLoginComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'HeaderLoginComponent',
        },
      ],
    },
    MiniCart: {
      components: [
        {
          uid: 'MiniCartComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'MiniCartComponent',
        },
      ],
    },
    SiteSwitcher: {
      components: [
        {
          uid: 'SiteSwitcherComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'SiteSwitcherComponent',
        },
      ],
    },
    ProductListMain: {
      components: [
        {
          uid: 'ProductListMainComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'ProductListMainComponent',
        },
      ],
    },
    ProductListSideBar: {
      components: [
        {
          uid: 'ProductListSideBarComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'ProductListSideBarComponent',
        },
      ],
    },
    ProductListFilters: {
      components: [
        {
          uid: 'ProductListFiltersComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'ProductListFiltersComponent',
        },
      ],
    },
    ProductListTitle: {
      components: [
        {
          uid: 'ProductListTitleComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'ProductListTitleComponent',
        },
      ],
    },
    ProductListPagination: {
      components: [
        {
          uid: 'ProductListPaginationComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'ProductListPaginationComponent',
        },
      ],
    },
    MainManufacturerNav: {
      components: [
        {
          uid: 'MainManufacturerNavComponent',
          typeCode: 'DistMainNavigationComponent',
          flexType: 'DistMainNavigationComponent',
        },
      ],
    },
    TopHeader: {
      components: [
        {
          uid: 'MainTopNavComponent',
          typeCode: 'DistMainNavigationComponent',
          flexType: 'DistMainNavigationComponent',
        },
        {
          uid: 'SiteSwitcherComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'SiteSwitcherComponent',
        },
      ],
    },
    BottomHeader: {
      components: [
        {
          uid: 'CategoryMainNavComponent',
          typeCode: 'DistMainNavigationComponent',
          flexType: 'DistMainNavigationComponent',
        },
        {
          uid: 'MainManufacturerNavComponent',
          typeCode: 'DistMainNavigationComponent',
          flexType: 'DistMainNavigationComponent',
        },
        {
          uid: 'SearchBoxComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'SearchBoxComponent',
        },
        {
          uid: 'MyListComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'MyListComponent',
        },
        {
          uid: 'HeaderLoginComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'HeaderLoginComponent',
        },
        {
          uid: 'MiniCartComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'MiniCartComponent',
        },
      ],
    },
    MobileHeader: {
      components: [
        {
          uid: 'LogoComponent',
          typeCode: 'SimpleBannerComponent',
          flexType: 'SimpleBannerComponent',
        },
        {
          uid: 'HeaderLoginComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'HeaderLoginComponent',
        },
        {
          uid: 'MiniCartComponent',
          typeCode: 'CMSFlexComponent',
          flexType: 'MiniCartComponent',
        },
        {
          uid: 'CategoryMainNavComponent',
          typeCode: 'DistMainNavigationComponent',
          flexType: 'DistMainNavigationComponent',
        },
      ],
    },
    ErrorPageContentSlot: {
      components: [
        {
          uid: 'ErrorPageContent',
          flexType: 'ErrorPageContent',
          typeCode: 'ErrorPageContent',
        },
      ],
    },
    PreHeader: {
      components: [
        {
          uid: 'HamburgerMenuComponent',
          flexType: 'HamburgerMenuComponent',
          typeCode: 'HamburgerMenuComponent',
        },
      ],
    },
    SiteLogin: {
      components: [
        {
          uid: 'LoginComponent',
          flexType: 'LoginComponent',
          typeCode: 'LoginComponent',
        },
      ],
    },
  },
};
