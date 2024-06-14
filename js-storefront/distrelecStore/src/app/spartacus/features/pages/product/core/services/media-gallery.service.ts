import { Injectable } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Video } from '@model/product.model';
import { Occ } from '@spartacus/core';
import { UseWebpImage } from '@helpers/useWebpImage';
import { ProductImageFormat } from '../../pdp/product-image-format.enum';

export type PreviewMedia = { type: 'image'|'video', source: string, safeResourceUrl?: SafeResourceUrl };

@Injectable({
  providedIn: 'root'
})
export class MediaGalleryService {

constructor(
  private sanitizer: DomSanitizer
) { }

  returnMediaList(data: PreviewMedia[], videos: Video[], images: Occ.Image[],): PreviewMedia[] {
    return data.concat(
      videos ? videos?.map(videos => ({ type: "video", source: videos.youtubeUrl,  safeResourceUrl: this.sanitizeVideoUrl(videos.youtubeUrl) })) : [ ],
      images?.map(img => ({ type: "image", source: UseWebpImage(ProductImageFormat.landscapeLargeWebp, images, img) })) ?? [],
    );
  }

  prevImage(currentIndex: number, data: PreviewMedia[]): number {
    return (currentIndex - 1 + data.length) % data.length;
  }

  nextImage(currentIndex: number, data: PreviewMedia[]): number {
    return currentIndex = (currentIndex + 1) % data.length;
  }

  sanitizeVideoUrl(url: string): SafeResourceUrl {
    if (url.search('youtu') !== -1) {
      url ='https://www.youtube.com/embed/' + url.split('/')[3] + '?rel=0&modestbranding=1';
    }
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

}
