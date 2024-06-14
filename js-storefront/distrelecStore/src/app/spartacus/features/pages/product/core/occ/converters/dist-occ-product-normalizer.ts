import { Injectable } from '@angular/core';
import { Converter, Occ } from '@spartacus/core';
import { PriceService } from '@services/price.service';
import { ICustomProduct } from '@model/product.model';

@Injectable({ providedIn: 'root' })
export class DistOccProductNormalizer implements Converter<Occ.Product, ICustomProduct> {
  constructor(private priceService: PriceService) {}

  convert(source: any, target: ICustomProduct): ICustomProduct {
    target.price = source.price;
    target.breadcrumbs = source.breadcrumbs;
    target.volumePricesMap = source.volumePricesMap;
    target.productInformation = source.productInformation;
    target.countryOfOrigin = source.countryOfOrigin;
    target.distManufacturer = source.distManufacturer;
    target.grossWeight = source.grossWeight;
    target.grossWeightUnit = source.grossWeightUnit;
    target.dimensions = source.dimensions;
    target.customsCode = source.customsCode;
    target.unspsc5 = source.unspsc5;
    target.ean = source.ean;
    target.enumber = source.enumber;
    target.isDangerousGoods = source.isDangerousGoods;
    target.isProductBatteryCompliant = source.isProductBatteryCompliant;
    target.isRNDProduct = source.isRNDProduct;
    target.isROHSComplaint = source.isROHSComplaint;
    target.hasSvhc = source.hasSvhc;
    target.rohs = source.rohs;
    target.rohsCode = source.rohsCode;
    target.salesStatus = source.salesStatus;
    target.svhc = source.svhc;
    target.svhcURL = source.svhcURL;
    target.svhcReviewDate = source.svhcReviewDate;
    target.scip = source.scip;
    target.transportGroupData = source.transportGroupData;
    target.ghsImages = source.ghsImages;
    target.signalWord = source.signalWord;
    target.images = this.htmlEncodeImages(source.images); // Override ProductImageNormalizer
    target.classifications = source.classifications;
    target.metaData = source.metaData;

    this.populateTopLabel(source, target);
    this.populateDiscountPrice(source, target);
    this.normalizeVolumePrices(source, target);

    return target as ICustomProduct;
  }

  private htmlEncodeImages(productImageList: Occ.Image[]): Occ.Image[] {
    const parsedImageList = JSON.stringify(productImageList) ? JSON.parse(JSON.stringify(productImageList)) : null;
    if (parsedImageList) {
      for (const [key] of Object.entries(parsedImageList)) {
        parsedImageList[key].url = encodeURI(parsedImageList[key].url);
      }
      return parsedImageList;
    } else {
      return [];
    }
  }

  private populateTopLabel(source: any, target: ICustomProduct) {
    if (source.activePromotionLabels?.length > 0) {
      if (source.activePromotionLabels[0].code !== 'calibrationService') {
        target.topLabel = {
          label: source.activePromotionLabels[0].label,
          code: source.activePromotionLabels[0].code,
        };
      } else if (source.activePromotionLabels?.length > 1) {
        target.topLabel = {
          label: source.activePromotionLabels[1].label,
          code: source.activePromotionLabels[1].code,
        };
      }
    }
  }

  private populateDiscountPrice(source, target: ICustomProduct) {
    if (!source.volumePricesMap) {
      return;
    }

    const volumePrices = source.volumePricesMap.find((volumePriceMap) => !!volumePriceMap?.value)?.value;
    const customPrice = this.priceService.getCustomPrice(volumePrices);
    const listPrice = this.priceService.getListPrice(volumePrices);

    if (customPrice?.value) {
      target.price.value = customPrice.value;

      if (this.priceService.isPriceDiscounted(customPrice, listPrice)) {
        target.price.saving = customPrice.saving;
        target.price.promoValue = listPrice.value;
      }
    }
  }

  private normalizeVolumePrices(source: any, target: ICustomProduct) {
    if (!source.volumePricesMap) {
      return;
    }

    target.volumePrices = [];

    source.volumePricesMap.forEach((prices) => {
      const mappedPrices = prices.value
        .filter((newItem) => newItem.key === 'custom')
        .map((setOfPrices) => {
          setOfPrices.value.minQuantity = prices.key;
          return setOfPrices.value;
        });

      target.volumePrices.push(...mappedPrices);
    });
  }
}
