import { OnDestroy, Pipe, PipeTransform } from '@angular/core';
import { Subscription } from 'rxjs';
import { AllsitesettingsService } from '@services/allsitesettings.service';

@Pipe({
  name: 'distrelecLocaleFormatNumber',
})
export class DistrelecLocaleFormatNumberPipe implements PipeTransform, OnDestroy {
  private currentChannel = null;
  private readonly channelSubscription: Subscription = new Subscription();

  constructor(private siteSettingsService: AllsitesettingsService) {
    this.channelSubscription.add(
      this.siteSettingsService.currentChannelData$.subscribe((data) => (this.currentChannel = data)),
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

    const getLocaleForCurrencyFormat = (language: any, country: any) => {
      if (country === 'BE' || country === 'AT' || country === 'CH') {
        // dot as thousands delimiter
        return 'nl-BE';
      }
      if (country === 'EE' || country === 'LV' || country === 'NO' || country === 'PL') {
        //space as thousands delimiter
        return 'fr-FR';
      }
      if (country === 'LI' || country === 'LV') {
        //comma as thousands delimiter
        return 'en-GB';
      }
      return language + '-' + country;
    };

    try {
      const { language, country } = this.currentChannel;
      const locale = getLocaleForCurrencyFormat(language, country);
      const { minimumFractionDigits } = new Intl.NumberFormat(locale).resolvedOptions();
      return new Intl.NumberFormat(locale, { minimumFractionDigits, maximumFractionDigits: 0 }).format(valueNumeric);
    } catch (e) {
      return valueNumeric?.toFixed(0);
    }
  }
}
