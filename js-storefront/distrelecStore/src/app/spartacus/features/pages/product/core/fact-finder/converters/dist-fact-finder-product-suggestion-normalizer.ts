import { Injectable, OnDestroy } from '@angular/core';
import {
  Attributes,
  FactFinderSuggestion,
  FactFinderSuggestionsType,
  IFactFinderResultImages,
  IFactFinderResults,
} from '@model/factfinder.model';
import { Converter, Suggestion } from '@spartacus/core';
import { ProductSuggestion, Suggestions } from '@model/product-suggestions.model';
import { DefaultImageService } from '@services/default-image.service';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { first, tap } from 'rxjs/operators';
import { Channel } from '@model/site-settings.model';
import { Subscription } from 'rxjs';
import { formatPrice } from '@helpers/price-format';
import { AllsitesettingsService } from '@services/allsitesettings.service';

@Injectable({ providedIn: 'root' })
export class DistProductSuggestionFactFinderNormalizer implements Converter<IFactFinderResults, Suggestion>, OnDestroy {
  private activeChannel: Channel;
  private subscription: Subscription = new Subscription();
  private mediaDomain: string;

  constructor(
    private defaultImage: DefaultImageService,
    private channelService: ChannelService,
    private allSiteSettings: AllsitesettingsService,
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

  convert(source: IFactFinderResults, target: Suggestion): Suggestion {
    target = {};

    if (source.suggestions) {
      target.products = this.mapProducts(source.suggestions);
      target.termSuggestions = this.mapSuggestions(source.suggestions, 'searchTerm');
      target.categorySuggestions = this.mapSuggestions(source.suggestions, 'category');
      target.manufacturerSuggestions = this.mapSuggestions(source.suggestions, 'brand');
    }

    return target;
  }

  private mapImages(value: string): string {
    if (value) {
      const parsedImages = this.getParsedImages(value);
      if (parsedImages.landscape_small) {
        return this.mediaDomain + parsedImages.landscape_small;
      }
    }
    return this.defaultImage.getDefaultImage();
  }

  private getParsedImages(value): IFactFinderResultImages {
    try {
      return JSON.parse(value);
    } catch {
      return value;
    }
  }

  private mapSuggestions(suggestions: FactFinderSuggestion[], type: FactFinderSuggestionsType): Suggestions[] {
    return (
      suggestions
        .filter((item) => item.type === type)
        .map((item) => ({
          name: item.name,
          url: this.mapFactFinderTypesUrl(item, type),
        })) ?? []
    );
  }

  private mapFactFinderTypesUrl(item: FactFinderSuggestion, type: FactFinderSuggestionsType): string {
    if (type === 'brand') {
      return item.attributes.ManufacturerUrl;
    }

    if (type === 'category') {
      return this.getCategoryUrl(item.attributes);
    }

    return '';
  }

  private getCategoryUrl(attributes: Attributes): string {
    const data = JSON.parse(attributes.CategoryExtensions);
    const sourceField = attributes.sourceField;
    if (data !== undefined && sourceField !== undefined) {
      const indexOfCategory = Number(sourceField.charAt(sourceField.length - 1));

      if (indexOfCategory !== undefined) {
        return data[indexOfCategory - 1]?.url;
      }
    }

    return '';
  }

  private mapPrice(price: string): { price: string; currency: string } {
    const splitPrice = price.split('|');

    //TODO overlap in this converter and search both getting active channel and doing this logic
    const channelPrice = this.activeChannel === 'B2C' ? splitPrice[1] : splitPrice[2];

    const currencyIso = channelPrice.split(';')[0];
    const rawPrice = channelPrice.split('=')[1];

    const formattedPrice = formatPrice(rawPrice);

    return { price: formattedPrice, currency: currencyIso };
  }

  private mapProducts(suggestions: FactFinderSuggestion[]): ProductSuggestion[] {
    return suggestions
      .filter((item: FactFinderSuggestion) => item.type === 'productName')
      .map((item: FactFinderSuggestion) => ({
        code: item.attributes.ProductNumber,
        name: item.attributes.Title,
        url: item.attributes.ProductURL,
        image: this.mapImages(item.attributes.AdditionalImageURLs),
        articleNr: item.attributes.articleNr,
        typeName: item.attributes.TypeName,
        itemMin: item.attributes.ItemsMin,
        itemStep: item.attributes.ItemsStep,
        priceData: this.mapPrice(item.attributes.Price),
        energyEfficiency: item.attributes.energyEfficiency ?? '',
        energyEfficiencyLabelImageUrl: item.attributes.energyEfficiencyLabelImageUrl ?? '',
      }));
  }
}
