import { OnDestroy, Pipe, PipeTransform } from '@angular/core';
import { Subscription } from 'rxjs';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { SalesOrg } from '@model/site-settings.model';

@Pipe({
  name: 'price',
})
export class PricePipe implements PipeTransform, OnDestroy {
  private currentChannel = null;
  private decimalCommaCountries = [];
  private currentSalesOrg: SalesOrg;
  private readonly channelSubscription: Subscription = new Subscription();

  constructor(private siteSettingsService: AllsitesettingsService) {
    this.channelSubscription.add(
      this.siteSettingsService.currentChannelData$.subscribe((data) => (this.currentChannel = data)),
    );
    this.channelSubscription.add(
      this.siteSettingsService.getDecimalCommaCountries().subscribe((data) => (this.decimalCommaCountries = data)),
    );
    this.channelSubscription.add(
      this.siteSettingsService.getCurrentSalesOrg().subscribe((data) => (this.currentSalesOrg = data)),
    );
  }

  ngOnDestroy(): void {
    this.channelSubscription.unsubscribe();
  }

  transform(value: number | string): string {
    let valueNumeric: number;

    if (typeof value === 'undefined' || typeof value === null) {
      value = 0;
    }

    if (typeof value === 'string') {
      valueNumeric = Number.parseFloat(value);
    } else {
      valueNumeric = value;
    }

    const getLocaleForCurrencyFormat = (language: any) => {
      if (!language || !this.currentSalesOrg) {
        return 'en-EN';
      }
      if (this.currentSalesOrg?.countryIsocode === 'CH') {
        return 'de-CH';
      }
      if (language === 'en' && this.decimalCommaCountries.includes(this.currentSalesOrg?.countryIsocode)) {
        return 'de-' + this.currentSalesOrg?.countryIsocode;
      }
      return language + '-' + this.currentSalesOrg?.countryIsocode;
    };

    const getScale = (val: number) => {
      const partsOfValue = val.toString().split('.');
      if (partsOfValue.length <= 1) {
        return 0;
      }
      return partsOfValue[1].length;
    };

    try {
      const { currency, language } = this.currentChannel;
      const locale = getLocaleForCurrencyFormat(language);

      const { minimumFractionDigits } = new Intl.NumberFormat(locale, {
        style: 'currency',
        currency,
      }).resolvedOptions();
      const scaleOfPrice = getScale(valueNumeric);
      const maxFractionDigits = scaleOfPrice > 2 && valueNumeric < 1 ? 6 : 2;
      return new Intl.NumberFormat(locale, { minimumFractionDigits, maximumFractionDigits: maxFractionDigits }).format(
        valueNumeric,
      );
    } catch (e) {
      return valueNumeric?.toFixed(2);
    }
  }
}
