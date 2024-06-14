import { Injectable } from '@angular/core';
import { Converter } from '@spartacus/core';
import { SearchExperienceFactory } from '@features/pages/product/core/dynamic/search-experience.factory';

import { ICustomProduct } from '@model/product.model';

@Injectable({ providedIn: 'root' })
export class DynamicProductNormalizer implements Converter<any, ICustomProduct> {
  private normalizer: Converter<any, ICustomProduct>;

  constructor(private factory: SearchExperienceFactory) {}

  convert(source: any, target: ICustomProduct): ICustomProduct {
    if (!this.normalizer) {
      this.createNormalizer();
    }
    return this.normalizer.convert(source, target);
  }

  private createNormalizer(): void {
    this.normalizer = this.factory.createProductNormalizer();
  }
}
