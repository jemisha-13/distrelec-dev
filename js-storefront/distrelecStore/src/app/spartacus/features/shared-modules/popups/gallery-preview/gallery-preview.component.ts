import { Component, Input, OnInit, ViewEncapsulation, WritableSignal } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { MediaGalleryService, PreviewMedia } from '@features/pages/product/core/services/media-gallery.service';
import { faTimes, faAngleRight, faAngleLeft } from '@fortawesome/free-solid-svg-icons';
import { WindowRef } from '@spartacus/core';
import { BREAKPOINT, BreakpointService } from '@spartacus/storefront';


@Component({
  selector: 'app-gallery-preview',
  templateUrl: './gallery-preview.component.html',
  styleUrls: ['./gallery-preview.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GalleryPreviewComponent implements OnInit {
  @Input() data: PreviewMedia[];
  @Input() showCount = false;
  @Input() displayPreview: WritableSignal<string>;

  faTimes = faTimes;
  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;

  currentIndex: number = 0;
  controls: boolean = true;

  breakpoint$ = this.breakpointService.breakpoint$;

  isZoomed = false;

  constructor( 
    private winRef: WindowRef, 
    private breakpointService: BreakpointService,
    private mediaGalleryService: MediaGalleryService
  ) {}

  ngOnInit(): void {
    this.onPreview(this.displayPreview());
  }

  onPreview(source: string | SafeResourceUrl): void {
    this.currentIndex = this.data.findIndex(el => el.source === source);
  }

  onClosePreview(): void {
    this.winRef.document.body.classList.remove('is-locked');
    this.displayPreview.set(null);
  }

  getDisplayedItems(breakpoint: BREAKPOINT): PreviewMedia[] {
    if (breakpoint === BREAKPOINT.xs || breakpoint === BREAKPOINT.sm) {
      const total = this.data.length;
      return [
        this.data[this.currentIndex],
        this.data[(this.currentIndex + 1) % total],
        this.data[(this.currentIndex + 2) % total],
        this.data[(this.currentIndex + 3) % total],
  
      ];
    }
    
    return this.data;
  }

  prevImage(): void {
    this.currentIndex = (this.currentIndex - 1 + this.data.length) % this.data.length;
  }

  nextImage(): void {
    this.currentIndex = (this.currentIndex + 1) % this.data.length;
  }

}
