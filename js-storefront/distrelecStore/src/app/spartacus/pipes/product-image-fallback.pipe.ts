import { Pipe, PipeTransform } from '@angular/core';
import { DefaultImageService } from '@services/default-image.service';
import { ImageFormat } from '@model/misc.model';

@Pipe({
  name: 'ProductImageFallback',
})
export class ProductImageFallbackPipe implements PipeTransform {
  constructor(private defaultImage: DefaultImageService) {}

  transform(value: any, mediaDomain: string = '', imageFormat: ImageFormat = 'landscape_small'): any {
    if (value && value.length > 0) {
      const imageInSelectedFormatFormat = value?.find((img) => img.format === imageFormat);
      if (imageInSelectedFormatFormat) {
        return mediaDomain + imageInSelectedFormatFormat.url;
      }
      const imageInSmallFormat = value?.find((img) => img.format === 'landscape_small');
      return mediaDomain + (imageInSmallFormat ? imageInSmallFormat.url : value[0].url);
    }
    return this.defaultImage.getDefaultImage();
  }
}
