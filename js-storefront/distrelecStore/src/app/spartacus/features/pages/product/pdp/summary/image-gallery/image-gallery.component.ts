import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { Video } from '@model/product.model';
import { Image, WindowRef } from '@spartacus/core';
import { BREAKPOINT, BreakpointService } from '@spartacus/storefront';

export type SourceType = 'image' | 'video';
export type ImageObject = { small: Image; landscape: Image };

@Component({
  selector: 'app-image-gallery',
  templateUrl: './image-gallery.component.html',
  styleUrls: ['./image-gallery.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ImageGalleryComponent implements OnInit {
  @Input() productImages: Image[];
  @Input() productVideos?: Video[];

  @Output() sendImageSrcToParent = new EventEmitter<string>();

  data: ImageObject[] = [];
  _selectedMedia = this.productDataService.sharedMediaSignal;
  isBrowser: boolean = this.winRef.isBrowser();
  breakpoint$ = this.breakPointService.breakpoint$;

  constructor(
    private productDataService: ProductDataService,
    private winRef: WindowRef,
    private breakPointService: BreakpointService,
  ) {}

  ngOnInit(): void {
    this.assignCarouselData();
  }

  assignCarouselData(): void {
    const smallImages = this.productImages.filter((img) => img.format === 'portrait_small');
    const landscapeImages = this.productImages.filter((img) => img.format === 'landscape_large');

    smallImages.forEach((img, index) => {
      this.data.push({ small: img, landscape: landscapeImages[index] });
    });
  }

  getImages(breakpoint: BREAKPOINT): ImageObject[] {
    const maximumImages =
      breakpoint === BREAKPOINT.xl ? 6 - (this.productVideos?.length ?? 0) : 4 - (this.productVideos?.length ?? 0);
    return this.data.slice(0, maximumImages);
  }

  switchMedia(src: string): void {
    this.productDataService.updateMediaSignal(src);
  }
}
