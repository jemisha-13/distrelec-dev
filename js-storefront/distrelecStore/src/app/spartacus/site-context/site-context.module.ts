import { APP_INITIALIZER, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  BASE_SITE_CONTEXT_ID,
  BaseSiteService,
  ContextServiceMap,
  CURRENCY_CONTEXT_ID,
  CurrencyService,
  LANGUAGE_CONTEXT_ID,
  LanguageService,
  SiteContextConfigInitializer,
} from '@spartacus/core';

import { DistSiteContextConfigInitializer } from './initializers/dist-site-context-config-initializer';

import { ContextCookiePersistenceService } from './services/context-cookie-persistence.service';
import { CountryService } from './services/country.service';
import { ChannelService } from './services/channel.service';
import { customContextInitializerProviders } from './providers/custom-context-initializer-providers';
import { CHANNEL_CONTEXT_ID, COUNTRY_CONTEXT_ID } from './providers/custom-context-ids';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { SiteContextInterceptor } from '@interceptors/site-context.interceptor';

export function serviceMapFactory() {
  return {
    [LANGUAGE_CONTEXT_ID]: LanguageService,
    [CURRENCY_CONTEXT_ID]: CurrencyService,
    [BASE_SITE_CONTEXT_ID]: BaseSiteService,
    [COUNTRY_CONTEXT_ID]: CountryService,
    [CHANNEL_CONTEXT_ID]: ChannelService,
  };
}

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: (contextService: ContextCookiePersistenceService) => () => contextService.init(),
      deps: [ContextCookiePersistenceService],
      multi: true,
    },
    {
      provide: ContextServiceMap,
      useFactory: serviceMapFactory,
    },
    {
      provide: SiteContextConfigInitializer,
      useClass: DistSiteContextConfigInitializer,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SiteContextInterceptor,
      multi: true,
    },
    ...customContextInitializerProviders,
  ],
})
export class SiteContextModule {}
