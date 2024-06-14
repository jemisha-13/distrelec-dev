export const MOCK_SITE_SEARCH_NO_RESULT_DATA = {
  name: "Search Results Page",
  type: "ContentPage",
  label: "search",
  template: "SearchResultsListPageTemplate",
  pageId: "search",
  title: "Search",
  properties: {
    seo: {
      alternateHreflangUrls: "x-default~https://pretest.distrelec.ch/en/search/cms/search|cs~https://pretest.distrelec.cz/cs/search/cms/search|de~https://pretest.distrelec.de/de/search/cms/search|da~https://pretest.elfadistrelec.dk/da/search/cms/search|et~https://pretest.elfadistrelec.ee/et/search/cms/search|fi~https://pretest.elfadistrelec.fi/fi/search/cms/search|hu~https://pretest.distrelec.hu/hu/search/cms/search|it~https://pretest.distrelec.it/it/search/cms/search|lt~https://pretest.distrelec.lt/lt/search/cms/search|lv~https://pretest.elfadistrelec.lv/lv/search/cms/search|nl~https://pretest.distrelec.nl/nl/search/cms/search|no~https://pretest.elfadistrelec.no/no/search/cms/search|pl~https://pretest.elfadistrelec.pl/pl/search/cms/search|ro~https://pretest.distrelec.ro/ro/search/cms/search|sv~https://pretest.elfa.se/sv/search/cms/search|sk~https://pretest.distrelec.sk/sk/search/cms/search|en~https://pretest.distrelec.biz/en/search/cms/search|fr~https://pretest.distrelec.fr/fr/search/cms/search|nl-BE~https://pretest.distrelec.be/nl/search/cms/search|fr-BE~https://pretest.distrelec.be/fr/search/cms/search|en-BE~https://pretest.distrelec.be/en/search/cms/search|en-DK~https://pretest.elfadistrelec.dk/en/search/cms/search|sv-FI~https://pretest.elfadistrelec.fi/sv/search/cms/search|en-DE~https://pretest.distrelec.de/en/search/cms/search|en-NL~https://pretest.distrelec.nl/en/search/cms/search|en-NO~https://pretest.elfadistrelec.no/en/search/cms/search|de-AT~https://pretest.distrelec.at/de/search/cms/search|en-PL~https://pretest.elfadistrelec.pl/en/search/cms/search|de-CH~https://pretest.distrelec.ch/de/search/cms/search|fr-CH~https://pretest.distrelec.ch/fr/search/cms/search|en-SE~https://pretest.elfa.se/en/search/cms/search|en-CH~https://pretest.distrelec.ch/en/search/cms/search",
      canonicalUrl: "https://pretest.distrelec.ch/en/search/cms/search",
      metaTitle: "Search - Distrelec Switzerland",
      metaImage: "https://localhost:8088/medias/distrelec-rs-logo-en.svg?context=bWFzdGVyfHJvb3R8NjAzNXxpbWFnZS9zdmcreG1sfGhhMi9oNDgvOTY2ODQxMDU3MjgzMC9kaXN0cmVsZWNfcnNfbG9nb19lbi5zdmd8YzhlN2U5ZjExNmJhZjI4OWY0ODczMjZjOTY3NjA4MjQ3YWEyMTM0ZDI0N2Q5NGQxZTJiODIwODFlMTM0YzQ4Yw"
    },
    languageEN: "english"
  },
  robots: [
    "NOINDEX",
    "FOLLOW"
  ],
  slots: {
    TeaserContent: {},
    MainCategoryNav: {
      components: [
        {
          uid: "CategoryMainNavComponent",
          typeCode: "DistMainNavigationComponent",
          flexType: "DistMainNavigationComponent"
        }
      ]
    },
    TopHeader: {
      components: [
        {
          uid: "MainTopNavComponent",
          typeCode: "DistMainNavigationComponent",
          flexType: "DistMainNavigationComponent"
        },
        {
          uid: "SiteSwitcherComponent",
          typeCode: "CMSFlexComponent",
          flexType: "SiteSwitcherComponent"
        }
      ]
    },
    MainNav: {
      components: [
        {
          uid: "MainTopNavComponent",
          typeCode: "DistMainNavigationComponent",
          flexType: "DistMainNavigationComponent"
        }
      ]
    },
    HeaderLogin: {
      components: [
        {
          uid: "HeaderLoginComponent",
          typeCode: "CMSFlexComponent",
          flexType: "HeaderLoginComponent"
        }
      ]
    },
    ServiceNav: {
      components: [
        {
          uid: "ImportToolPageLink",
          typeCode: "CMSLinkComponent",
          flexType: "CMSLinkComponent"
        }
      ]
    },
    SearchBox: {
      components: [
        {
          uid: "SearchBoxComponent",
          typeCode: "CMSFlexComponent",
          flexType: "SearchBoxComponent"
        }
      ]
    },
    SiteSwitcher: {
      components: [
        {
          uid: "SiteSwitcherComponent",
          typeCode: "CMSFlexComponent",
          flexType: "SiteSwitcherComponent"
        }
      ]
    },
    MobileHeader: {
      components: [
        {
          uid: "LogoComponent",
          typeCode: "SimpleBannerComponent",
          flexType: "SimpleBannerComponent"
        },
        {
          uid: "HeaderLoginComponent",
          typeCode: "CMSFlexComponent",
          flexType: "HeaderLoginComponent"
        },
        {
          uid: "MiniCartComponent",
          typeCode: "CMSFlexComponent",
          flexType: "MiniCartComponent"
        },
        {
          uid: "CategoryMainNavComponent",
          typeCode: "DistMainNavigationComponent",
          flexType: "DistMainNavigationComponent"
        }
      ]
    },
    LeftSideContentPosition: {
      components: [
        {
          uid: "leftside_divider",
          typeCode: "CMSParagraphComponent",
          flexType: "CMSParagraphComponent"
        }
      ]
    },
    ProductListMain: {
      components: [
        {
          uid: "ProductListMainComponent",
          typeCode: "CMSFlexComponent",
          flexType: "ProductListMainComponent"
        }
      ]
    },
    ProductListTitle: {
      components: [
        {
          uid: "ProductListTitleComponent",
          typeCode: "CMSFlexComponent",
          flexType: "ProductListTitleComponent"
        }
      ]
    },
    MainManufacturerNav: {
      components: [
        {
          uid: "MainManufacturerNavComponent",
          typeCode: "DistMainNavigationComponent",
          flexType: "DistMainNavigationComponent"
        }
      ]
    },
    CertLabels: {
      components: [
        {
          uid: "comp_00000A2M",
          typeCode: "SimpleBannerComponent",
          flexType: "SimpleBannerComponent"
        }
      ]
    },
    ProductListFilters: {
      components: [
        {
          uid: "ProductListFiltersComponent",
          typeCode: "CMSFlexComponent",
          flexType: "ProductListFiltersComponent"
        }
      ]
    },
    ProductListPagination: {
      components: [
        {
          uid: "ProductListPaginationComponent",
          typeCode: "CMSFlexComponent",
          flexType: "ProductListPaginationComponent"
        }
      ]
    },
    Vivocha: {},
    WarningSlot: {
      components: [
        {
          uid: "cmsitem_00263000",
          typeCode: "DistWarningComponent",
          flexType: "DistWarningComponent"
        },
        {
          uid: "cmsitem_00261002",
          typeCode: "DistWarningComponent",
          flexType: "DistWarningComponent"
        },
        {
          uid: "cmsitem_00261001",
          typeCode: "DistWarningComponent",
          flexType: "DistWarningComponent"
        }
      ]
    },
    ProductListSideBar: {
      components: [
        {
          uid: "ProductListSideBarComponent",
          typeCode: "CMSFlexComponent",
          flexType: "ProductListSideBarComponent"
        }
      ]
    },
    FooterLinks: {
      components: [
        {
          uid: "DistFooterComponent",
          typeCode: "DistFooterComponent",
          flexType: "DistFooterComponent"
        }
      ]
    },
    Logo: {
      components: [
        {
          uid: "LogoComponent",
          typeCode: "SimpleBannerComponent",
          flexType: "SimpleBannerComponent"
        }
      ]
    },
    MiniCart: {
      components: [
        {
          uid: "MiniCartComponent",
          typeCode: "CMSFlexComponent",
          flexType: "MiniCartComponent"
        }
      ]
    },
    breadcrumb: {
      components: [
        {
          uid: "BreadcrumbComponent",
          typeCode: "BreadcrumbComponent",
          flexType: "BreadcrumbComponent"
        }
      ]
    },
    HeaderWrapper: {
      components: [
        {
          uid: "LogoComponent",
          typeCode: "SimpleBannerComponent",
          flexType: "SimpleBannerComponent"
        },
        {
          uid: "CategoryMainNavComponent",
          typeCode: "DistMainNavigationComponent",
          flexType: "DistMainNavigationComponent"
        },
        {
          uid: "HeaderLoginComponent",
          typeCode: "CMSFlexComponent",
          flexType: "HeaderLoginComponent"
        },
        {
          uid: "MiniCartComponent",
          typeCode: "CMSFlexComponent",
          flexType: "MiniCartComponent"
        },
        {
          uid: "SearchBoxComponent",
          typeCode: "CMSFlexComponent",
          flexType: "SearchBoxComponent"
        }
      ]
    },
    BottomHeader: {
      components: [
        {
          uid: "CategoryMainNavComponent",
          typeCode: "DistMainNavigationComponent",
          flexType: "DistMainNavigationComponent"
        },
        {
          uid: "MainManufacturerNavComponent",
          typeCode: "DistMainNavigationComponent",
          flexType: "DistMainNavigationComponent"
        },
        {
          uid: "SearchBoxComponent",
          typeCode: "CMSFlexComponent",
          flexType: "SearchBoxComponent"
        },
        {
          uid: "MyListComponent",
          typeCode: "CMSFlexComponent",
          flexType: "MyListComponent"
        },
        {
          uid: "HeaderLoginComponent",
          typeCode: "CMSFlexComponent",
          flexType: "HeaderLoginComponent"
        },
        {
          uid: "MiniCartComponent",
          typeCode: "CMSFlexComponent",
          flexType: "MiniCartComponent"
        }
      ]
    },
    Impressum: {
      components: [
        {
          uid: "comp_00000A35",
          typeCode: "CMSLinkComponent",
          flexType: "CMSLinkComponent"
        },
        {
          uid: "comp_00000A3L",
          typeCode: "CMSLinkComponent",
          flexType: "CMSLinkComponent"
        },
        {
          uid: "comp_00000A36",
          typeCode: "CMSLinkComponent",
          flexType: "CMSLinkComponent"
        }
      ]
    },
    Swyn: {
      components: [
        {
          uid: "FacebookLink",
          typeCode: "CMSLinkComponent",
          flexType: "CMSLinkComponent"
        },
        {
          uid: "GoogleLink",
          typeCode: "CMSLinkComponent",
          flexType: "CMSLinkComponent"
        },
        {
          uid: "YouTubeLink",
          typeCode: "CMSLinkComponent",
          flexType: "CMSLinkComponent"
        }
      ]
    },
    RSIntegrationMessage: {
      components: [
        {
          uid: "cmsitem_00243002",
          typeCode: "RSIntegrationMessageComponent",
          flexType: "RSIntegrationMessageComponent"
        }
      ]
    },
    ErrorPageContentSlot: {
      components: [
        {
          uid: "ErrorPageContent",
          flexType: "ErrorPageContent",
          typeCode: "ErrorPageContent"
        }
      ]
    },
    PreHeader: {
      components: [
        {
          uid: "HamburgerMenuComponent",
          flexType: "HamburgerMenuComponent",
          typeCode: "HamburgerMenuComponent"
        }
      ]
    },
    SiteLogin: {
      components: [
        {
          uid: "LoginComponent",
          flexType: "LoginComponent",
          typeCode: "LoginComponent"
        }
      ]
    }
  }
};
