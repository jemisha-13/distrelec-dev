import { NgModule } from '@angular/core';
import {
  ConfigModule,
  FeaturesConfig,
  LoginEvent,
  LogoutEvent,
  OccConfig,
  provideConfig,
  RoutingConfig,
  StateConfig,
  StateTransferType,
} from '@spartacus/core';
import { defaultCmsContentProviders, mediaConfig, PRODUCT_LISTING_URL_MATCHER } from '@spartacus/storefront';
import { distGlobalMessageConfigFactory } from '@helpers/global-messages.config';
import { environment } from '@environment';
import { i18nConfig } from './i18n/i18n-config';
import { SmartEditConfig } from '@spartacus/smartedit/root';
import { SERVER_CONNECTION_TIMEOUT } from '../../server/constants';

@NgModule({
  declarations: [],
  imports: [ConfigModule.withConfigFactory(distGlobalMessageConfigFactory)],
  providers: [
    ...defaultCmsContentProviders,

    provideConfig(mediaConfig),

    provideConfig(<RoutingConfig>{
      routing: {
        routes: {
          category: {
            matchers: [PRODUCT_LISTING_URL_MATCHER],
          },
          productFamily: {
            paths: ['pf/:productFamilyCode', ':productFamilyName/pf/:productFamilyCode'],
          },
          manufacturer: {
            paths: ['manufacturer/:manufacturerName/:manufacturerCode', 'manufacturer/:manufacturerCode'],
          },
          search: {
            paths: ['search'],
          },
        },
      },
    }),

    provideConfig(<OccConfig>{
      backend: {
        occ: {
          useWithCredentials: true,
          prefix: 'rest/v2',
          baseUrl: environment.occBaseUrl,
          endpoints: {
            product: {
              default: 'products/${productCode}',
              details: 'products/${productCode}',
              list: 'products/${productCode}',
              variants: 'products/${productCode}',
            },
            orderDetail: 'users/${userId}/orders/${orderId}?fields=DEFAULT',
            productSearch: 'products/search',
            cart: 'users/${userId}/carts/${cartId}/mini', // Use mini-cart to avoid delays from calculating cart
            baseSites:
              'basesites?fields=baseSites(uid,name,bloomreachScriptToken,checkoutDeliveryMethodPricesShown,defaultLanguage(FULL),urlEncodingAttributes,urlPatterns,tracking,reevooActivated,stores(quotationsEnabled,backorderAllowed,deliveryCountries(isocode),currencies(isocode),defaultCurrency(isocode),languages(isocode,nameEN),defaultLanguage(isocode),searchExperienceMap),theme,defaultPreviewCatalogId,defaultPreviewCategoryCode,defaultPreviewProductCode,guestCheckoutEnabled,enableRegistration,enableAddToCart,enableNewsletter,enableMyAccountRedirect,enableProductReturn,enableSubUserManagement)', // eslint-disable-line max-len
          },
        },
        loadingScopes: {
          product: {
            default: {
              reloadOn: [LoginEvent, LogoutEvent],
            },
            list: {
              reloadOn: [LoginEvent, LogoutEvent],
            },
            variants: {
              reloadOn: [LoginEvent, LogoutEvent],
            },
          },
        },
        timeout: {
          server: SERVER_CONNECTION_TIMEOUT,
        },
      },
    }),

    provideConfig(<StateConfig>{
      state: {
        ssrTransfer: {
          keys: {
            cms: false as any as StateTransferType, // Force content to be refreshed on hydrate
          },
        },
      },
    }),

    provideConfig(i18nConfig),

    provideConfig(<FeaturesConfig>{
      features: {
        level: '4.3',
      },
    }),

    provideConfig(<SmartEditConfig>{
      smartEdit: {
        storefrontPreviewRoute: 'cx-preview',
        allowOrigin: 'localhost:9002, *.hybris.distrelec.com:443, hybris.distrelec.com:443',
      },
    }),
  ],
})
export class SpartacusConfigurationModule {}
