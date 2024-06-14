import { Injectable, OnDestroy } from '@angular/core';
import { Channel } from '@model/site-settings.model';
import { FusionProduct } from '@model/fusion-product-search.model';
import { Price, PriceType } from '@spartacus/core';
import { Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';

@Injectable({
  providedIn: 'root',
})
export class FusionProductPriceService implements OnDestroy {
  //CAN WE MAKE THIS A CONVERTER ?

  private activeChannel: Channel;
  private subscription: Subscription = new Subscription();

  constructor(private channelService: ChannelService) {
    this.subscription.add(
      this.channelService
        .getActive()
        .pipe(tap((channel) => (this.activeChannel = channel)))
        .subscribe(),
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  mapSinglePrice(source: FusionProduct): Price {
    const price: number = this.activeChannel === 'B2C' ? source.singleMinPriceGross : source.singleMinPriceNet;
    const promoPrice: number = this.activeChannel === 'B2C' ? source.standardPriceGross : source.standardPriceNet;

    return {
      currencyIso: source.currency,
      formattedValue: `${price} ${source.currency}`,
      maxQuantity: 2, // volume price max qty for each threshold, initial one gets set looking at response ?
      minQuantity: source.itemMin,
      value: price,
      priceType: PriceType.BUY,
      promoValue: promoPrice,
      saving: source.percentageDiscount,
    };
  }
  mapVolumePrice(scalePricesGross: string, scalePricesNet: string, currency: string) {
    const grossData = JSON.parse(scalePricesGross);
    const netData = JSON.parse(scalePricesNet);
    const keys = Object.keys(grossData);

    return keys.map((key, index) => {
      const minQuantity = parseInt(key, 10);
      const nextMinQuantity = index < keys.length - 1 ? parseInt(keys[index + 1], 10) : null;
      const maxQuantity = nextMinQuantity ? nextMinQuantity - 1 : undefined;

      return {
        key: minQuantity,
        value: [
          {
            key: 'custom',
            value: {
              currencyIso: currency,
              formattedValue: `${grossData[key].toFixed(2)} ${currency}`,
              ...(maxQuantity !== undefined ? { maxQuantity } : {}),
              minQuantity,
              priceType: PriceType.BUY,
              value: grossData[key],
            },
          },
          {
            key: 'list',
            value: {
              currencyIso: currency,
              formattedValue: `${netData[key].toFixed(2)} ${currency}`,
              ...(maxQuantity !== undefined ? { maxQuantity } : {}),
              minQuantity: 1,
              priceType: PriceType.BUY,
              value: netData[key],
            },
          },
        ],
      };
    });
  }

  normalizeVolumePrices(volumePriceMap: any): Price[] {
    if (!volumePriceMap) {
      return [];
    }

    return volumePriceMap
      .map((prices) => {
        const channelPrice = this.activeChannel === 'B2B' ? 'list' : 'custom';
        const { value } = prices.value.find((item) => item.key === channelPrice) || {};
        if (value) {
          value.minQuantity = prices.key;
          return value;
        }
      })
      .filter(Boolean);
  }
}
