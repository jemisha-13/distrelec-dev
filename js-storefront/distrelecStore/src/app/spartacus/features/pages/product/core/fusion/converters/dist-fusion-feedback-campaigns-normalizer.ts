import { Injectable } from '@angular/core';
import { FusionProductSearch } from '@model/fusion-product-search.model';
import { Converter, ConverterService } from '@spartacus/core';
import { FUSION_PRODUCT_NORMALIZER } from '@features/pages/product/core/fusion/converters/injection-tokens';
import { CarouselProductData } from '@model/product-search.model';

@Injectable({
  providedIn: 'root',
})
export class DistFusionFeedbackCampaigns implements Converter<FusionProductSearch, CarouselProductData> {
  constructor(private converterService: ConverterService) {}

  convert(source: FusionProductSearch, target: CarouselProductData): CarouselProductData {
    target = {};

    this.mapPushedProducts(source, target);
    this.mapFeedbackTexts(source, target);

    return target;
  }

  private mapPushedProducts(source: FusionProductSearch, target: CarouselProductData): void {
    target.pushedProducts =
      source?.response?.carousel?.map((product) => this.converterService.convert(product, FUSION_PRODUCT_NORMALIZER)) ??
      [];
  }

  private parseFeedback(feed: string): { key: string; value: string } {
    try {
      const parsedFeedback = JSON.parse(feed);
      return {
        key: 'SearchResult_top',
        value: parsedFeedback.zone,
      };
    } catch {
      return {
        key: 'SearchResult_top',
        value: '',
      };
    }
  }

  private mapFeedbackTexts(source: FusionProductSearch, target: CarouselProductData): void {
    target.feedbackTexts = {
      entry: source?.fusion?.banner?.map((feed) => this.parseFeedback(feed)) ?? [],
    };
  }
}
