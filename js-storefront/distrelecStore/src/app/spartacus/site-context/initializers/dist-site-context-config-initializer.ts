import { BaseSite, ConfigInitializer, SiteContextConfig, SiteContextConfigInitializer } from '@spartacus/core';
import { Injectable } from '@angular/core';
import { CHANNEL_CONTEXT_ID, COUNTRY_CONTEXT_ID } from '../providers/custom-context-ids';
import { validCountriesForInternationalShop } from '../services/country.service';
import { validChannels } from '../services/channel.service';
import { isInternationalShop } from '../utils';

@Injectable({ providedIn: 'root' })
export class DistSiteContextConfigInitializer extends SiteContextConfigInitializer implements ConfigInitializer {
  protected getConfig(source: BaseSite): SiteContextConfig {
    const config = super.getConfig(source);

    // We can hard-code the URL context parameters here instead of having it dynamically set from backoffice WCMS settings
    // https://sap.github.io/spartacus-docs/automatic-context-configuration/#automatic-context-configuration
    config.context.urlParameters = ['language'];

    config.context[COUNTRY_CONTEXT_ID] = this.getValidDeliveryCountryCodes(source);
    config.context[CHANNEL_CONTEXT_ID] = validChannels;

    return config;
  }

  private getValidDeliveryCountryCodes(baseSite: BaseSite): string[] {
    if (isInternationalShop(baseSite.uid)) {
      return validCountriesForInternationalShop;
    }

    return baseSite.baseStore.deliveryCountries.map((country) => country.isocode);
  }
}
