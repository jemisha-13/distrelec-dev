import { APP_INITIALIZER, Provider } from '@angular/core';
import { LanguageInitializer } from '@spartacus/core';
import { DistrelecLanguageInitializer } from '../initializers/language-initializer';
import { CountryInitializer } from '../initializers/country-initializer';
import { ChannelInitializer } from '../initializers/channel-initializer';

export function initializeCountry(countryInitializer: CountryInitializer): () => void {
  const result = () => {
    countryInitializer.initialize();
  };
  return result;
}

export function initializeChannel(channelInitializer: ChannelInitializer): () => void {
  const result = () => {
    channelInitializer.initialize();
  };
  return result;
}

export const customContextInitializerProviders: Provider[] = [
  {
    provide: LanguageInitializer,
    useClass: DistrelecLanguageInitializer,
  },
  {
    provide: APP_INITIALIZER,
    useFactory: initializeCountry,
    deps: [CountryInitializer],
    multi: true,
  },
  {
    provide: APP_INITIALIZER,
    useFactory: initializeChannel,
    deps: [ChannelInitializer],
    multi: true,
  },
];
