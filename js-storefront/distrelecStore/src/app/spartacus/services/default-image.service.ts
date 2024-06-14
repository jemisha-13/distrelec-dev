import { Injectable } from '@angular/core';
import { SiteContextConfig } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class DefaultImageService {
  missingImageRegex = /(elfa|LV|NO|PL|FI|LT|EE|SE|DK)/i;

  private assetsPath = '/app/spartacus/assets/media/img';

  constructor(private contextConfig: SiteContextConfig) {}

  getDefaultImage(): string {
    const missingImageRegex = /(elfa|LV|NO|PL|FI|LT|EE|SE|DK)/i;

    if (!this.contextConfig.context || this.contextConfig.context.baseSite.length === null) {
      return `${this.assetsPath}/missing_landscape_small.png`;
    }

    const missingImage = missingImageRegex.test(this.contextConfig.context.baseSite[0])
      ? `${this.assetsPath}/missing_landscape_small-elfa.png`
      : `${this.assetsPath}/missing_landscape_small.png`;

    return missingImage;
  }

  getDefaultImageMedium(): string {
    const missingImage = this.missingImageRegex.test(this.contextConfig.context.baseSite[0])
      ? `${this.assetsPath}/missing_landscape_medium-elfa.png`
      : `${this.assetsPath}/missing_landscape_medium.png`;

    return missingImage;
  }
}
