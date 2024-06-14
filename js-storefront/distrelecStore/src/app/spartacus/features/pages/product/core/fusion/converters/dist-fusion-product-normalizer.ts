import { Injectable } from '@angular/core';
import { FusionProduct } from '@model/fusion-product-search.model';
import { Channel } from '@model/site-settings.model';
import { ImageList } from '@model/misc.model';
import { FusionProductPriceService } from '../services/fusion-product-price.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { first } from 'rxjs/operators';
import { ICustomProduct } from '@model/product.model';

@Injectable({ providedIn: 'root' })
export class DistFusionProductNormalizer {
  private mediaDomain: string;

  constructor(
    private productPriceService: FusionProductPriceService,
    private allSiteSettings: AllsitesettingsService,
  ) {
    this.allSiteSettings
      .getMediaDomain()
      .pipe(first())
      .subscribe((mediaDomain) => (this.mediaDomain = mediaDomain));
  }

  convert(source: FusionProduct, target: ICustomProduct): ICustomProduct {
    target = {
      ...target,
    };

    target.url = source.url;
    target.buyable = source.buyable;
    target.code = source.productNumber;
    target.codeErpRelevant = source.codeErpRelevant;
    target.orderQuantityMinimum = source.orderQuantityMinimum;
    target.orderQuantityStep = source.itemStep;
    target.description = source.description;
    target.salesUnit = source.salesUnit;
    target.availableToB2B = this.mapChannel(source.visibleInChannels, 'B2B');
    target.availableToB2C = this.mapChannel(source.visibleInChannels, 'B2C');
    target.typeName = source.typeName;
    target.salesStatus = source.salesStatus?.toString() ?? '';
    target.ean = source.ean;
    target.distManufacturer = this.mapDistManufacturer(
      source.distManufacturer,
      source.manufacturerImage as unknown as ImageList,
      source.manufacturerUrl,
    );
    target.technicalAttributes = this.mapTechnicalAttributes(source.displayFields);
    target.categoryCodePath = source.categoryLastCode;
    target.name = source.title;
    target.images = this.mapImages(source);
    target.availableInSnapEda = false; //not in fusion response
    target.elfaArticleNumber = source.elfaArticleNumber;
    target.eligibleForReevoo = source.eligibleForReevoo;
    target.energyEfficiencyLabelImage = this.mapEnergyEffiencyImage(source);
    target.energyEfficiency = this.mapEnergyEffiencyRating(source);
    target.energyPower = '1.3'; // not in fusion response
    target.manufacturer = source.distManufacturer;
    target.movexArticleNumber = source.movexArticleNumber;
    target.origPosition = '5'; //TODO make this dynamic based on position in array or ask omar ?
    target.productFamilyUrl = source.productFamilyUrl;
    target.salesUnit = source.salesUnit;
    target.price = this.productPriceService.mapSinglePrice(source);
    target.volumePricesMap = this.productPriceService.mapVolumePrice(
      source.scalePricesGross,
      source.scalePricesNet,
      source.currency,
    ) as any;
    target.volumePrices = this.productPriceService.normalizeVolumePrices(target.volumePricesMap);

    this.populateTopLabel(source, target);

    return target;
  }

  private mapChannel(visibleInChannels: Channel[], channelType): boolean {
    return visibleInChannels.some((channel) => channel === channelType);
  }

  private mapDistManufacturer(name: string, image: ImageList, url: string) {
    return { name, image, url };
  }

  private mapTechnicalAttributes(attributes?: string): { key: string; value: string }[] {
    if (!attributes) {
      return [];
    }
    const parsedDisplayFields = JSON.parse(attributes);

    return parsedDisplayFields.map((item) => ({
      key: item.attributeName,
      value: item.unit ? item.value + ' ' + item.unit : item.value,
    }));
  }

  private mapImages(source: FusionProduct) {
    const images = [];

    if (source.imageURL_landscape_medium) {
      images.push({
        format: 'landscape_medium',
        url: source.imageURL_landscape_medium,
      });
    }

    if (source.imageURL_landscape_small) {
      images.push({
        format: 'landscape_small',
        url: source.imageURL_landscape_small,
      });
    }

    return images;
  }

  private mapEnergyEffiencyRating(source): string {
    try {
      return JSON.parse(source.energyEfficiency).Energyclasses_LOV ?? '';
    } catch (e) {
      return '';
    }
  }

  private mapEnergyEffiencyImage(source) {
    if (!source.energyEfficiencyLabelImageUrl) {
      return [];
    }

    return [
      {
        key: 'portrait_medium',
        value: {
          format: 'portrait_medium',
          url: this.mediaDomain + source.energyEfficiencyLabelImageUrl,
        },
      },
    ];
  }

  private populateTopLabel(source: FusionProduct, target: ICustomProduct) {
    if (!source.activePromotionLabels?.length) {
      return;
    }

    const labels = source.activePromotionLabels.split('/');
    const selectedLabel = labels
      .map((labelString) => labelString.split('|'))
      .filter(([code]) => code !== 'calibrationService')
      .find(() => true);

    if (selectedLabel) {
      const [code, label] = selectedLabel;
      target.topLabel = {
        code,
        label,
      };
    }
  }
}
