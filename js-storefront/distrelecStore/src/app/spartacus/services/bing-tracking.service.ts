import { Injectable } from '@angular/core';
import { SiteContextInterceptor } from '@interceptors/site-context.interceptor';
import { ScriptService } from '@services/script.service';
import { microsoftUetTagId } from '@services/siteConfig.service';
import { BIZCountryCodesEnum } from '../site-context/services/country.service';

@Injectable({
  providedIn: 'root',
})
export class BingTrackingService {
  constructor(
    private scriptService: ScriptService,
    private siteContextInterceptor: SiteContextInterceptor,
  ) {}

  executeBingConversionTracking(orderData) {
    if (this.getMicrosoftTagID()) {
      this.scriptService.appendScript({
        innerHTML:
          `
        window.uetq = [];
        window.uetq.push('event', 'purchase',
          {
            "page_path": "/checkout/orderConfirmation/` +
          orderData.code +
          `",
            "revenue_value":` +
          orderData.totalPrice?.value +
          `,
            "currency": "` +
          orderData.totalPrice?.currencyIso +
          `"
          }
        );`,
      });
    }
  }

  getCountryIsoCode(): string {
    const isoCode = this.siteContextInterceptor.activeCountry as BIZCountryCodesEnum;
    const isCountryEX = Object.values(BIZCountryCodesEnum).includes(isoCode);
    return isCountryEX ? 'EX' : isoCode;
  }

  getMicrosoftTagID(): string | boolean {
    return microsoftUetTagId.find((item) => item.isoCode === this.getCountryIsoCode()).tagId ?? false;
  }
}
