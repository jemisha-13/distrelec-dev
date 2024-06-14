import { Pipe, PipeTransform } from '@angular/core';
import { IFactFinderResultImages } from '@model/factfinder.model';
import { DefaultImageService } from '@services/default-image.service';

@Pipe({
  name: 'ProductImageFactFinder',
})
export class ProductImageFactFinderPipe implements PipeTransform {
  constructor(private defaultImage: DefaultImageService) {}

  transform(value: string | IFactFinderResultImages, mediaDomain: string): string {
    if (value) {
      const parsedImages = this.getParsedImages(value);
      if (parsedImages.landscape_small) {
        return mediaDomain + parsedImages.landscape_small;
      }
    }
    return this.defaultImage.getDefaultImage();
  }

  getParsedImages(value): IFactFinderResultImages {
    try {
      return JSON.parse(value);
    } catch {
      return value;
    }
  }
}
