import { Injectable, OnDestroy } from '@angular/core';
import { Converter, Suggestion } from '@spartacus/core';
import { ProductSuggestion, Suggestions } from '@model/product-suggestions.model';
import { FusionProductSuggestion, FusionSuggestion, FusionTermSuggestions } from '@model/fusion-suggestions.model';
import { Channel } from '@model/site-settings.model';
import { Subscription } from 'rxjs';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { first, tap } from 'rxjs/operators';
import { formatPrice } from '@helpers/price-format';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { DefaultImageService } from '@services/default-image.service';

@Injectable({ providedIn: 'root' })
export class DistProductSuggestionFusionNormalizer implements Converter<FusionSuggestion, Suggestion>, OnDestroy {
  private activeChannel: Channel;
  private subscription: Subscription = new Subscription();
  private mediaDomain: string;

  constructor(
    private channelService: ChannelService,
    private allSiteSettings: AllsitesettingsService,
    private defaultImage: DefaultImageService,
  ) {
    this.subscription.add(
      this.channelService
        .getActive()
        .pipe(tap((channel) => (this.activeChannel = channel)))
        .subscribe(),
    );

    this.allSiteSettings
      .getMediaDomain()
      .pipe(first())
      .subscribe((mediaDomain) => (this.mediaDomain = mediaDomain));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  convert(source: FusionSuggestion, target: Suggestion): Suggestion {
    target = {};

    target.products = this.mapProducts(source.response.docs);
    target.categorySuggestions = this.mapSuggestions(source.response.categorySuggestions);
    target.manufacturerSuggestions = this.mapSuggestions(source.response.manufacturerSuggestions);
    target.termSuggestions = this.mapSuggestions(source.response.termSuggestions);

    return target;
  }

  private mapSuggestions(suggestions: FusionTermSuggestions[]): Suggestions[] {
    if (!suggestions) {
      return [];
    }

    return suggestions.map((item) => ({
      name: item.name,
      url: item.url,
    }));
  }

  private mapPrice(privatePrice: number, businessPrice: number, currency: string): { price: string; currency: string } {
    currency = currency ?? '';
    privatePrice = privatePrice ?? 0;
    businessPrice = businessPrice ?? 0;

    const channelPrice = (this.activeChannel === 'B2C' ? privatePrice : businessPrice).toString();
    const formattedPrice = formatPrice(channelPrice);

    return { price: formattedPrice, currency };
  }

  private mapImages(image: string): string {
    if (image) {
      return this.mediaDomain + image;
    }
    return this.defaultImage.getDefaultImage();
  }

  private mapProducts(docs: FusionProductSuggestion[]): ProductSuggestion[] {
    if (!docs) {
      return [];
    }

    return docs.map((item: FusionProductSuggestion) => ({
      code: item.productNumber,
      name: item.title,
      url: item.productUrl,
      image: this.mapImages(item.imageURL),
      articleNr: item.productNumber,
      typeName: item.typeName,
      itemMin: item.itemMin,
      itemStep: item.itemStep,
      priceData: this.mapPrice(item.singleMinPriceGross, item.singleMinPriceNet, item.currency),
      energyEfficiency: item.energyEffiency ?? '',
      energyEfficiencyLabelImageUrl: item.energyEfficiencyLabelImageUrl ?? '',
    }));
  }
}
