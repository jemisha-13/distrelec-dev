import { Injectable } from '@angular/core';
import { Converter, Occ, ProductSearchPage } from '@spartacus/core';
import { SearchExperienceFactory } from '@features/pages/product/core/dynamic/search-experience.factory';

@Injectable({ providedIn: 'root' })
export class DynamicProductSearchPageNormalizer implements Converter<Occ.ProductSearchPage, ProductSearchPage> {
  private normalizer: Converter<any, ProductSearchPage>;

  constructor(private factory: SearchExperienceFactory) {}

  convert(source: Occ.ProductSearchPage, target: ProductSearchPage = {}): ProductSearchPage {
    if (!this.normalizer) {
      this.createNormalizer();
    }
    return this.normalizer.convert(source, target);
  }

  private createNormalizer(): void {
    this.normalizer = this.factory.createSearchPageNormalizer();
  }
}
