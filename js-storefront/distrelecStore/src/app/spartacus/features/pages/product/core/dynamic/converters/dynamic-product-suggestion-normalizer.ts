import { Injectable } from '@angular/core';
import { Converter, Suggestion } from '@spartacus/core';
import { SearchExperienceFactory } from '@features/pages/product/core/dynamic/search-experience.factory';

@Injectable({ providedIn: 'root' })
export class DynamicProductSuggestionNormalizer implements Converter<unknown, Suggestion> {
  private normalizer: Converter<any, Suggestion>;

  constructor(private factory: SearchExperienceFactory) {}

  convert(source: unknown, target: Suggestion): Suggestion {
    if (!this.normalizer) {
      this.createNormalizer();
    }
    return this.normalizer.convert(source, target);
  }

  private createNormalizer(): void {
    this.normalizer = this.factory.createProductSuggestionNormalizer();
  }
}
