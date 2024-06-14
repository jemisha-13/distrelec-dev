import { Component, Input, OnDestroy, OnInit, WritableSignal, signal } from '@angular/core';
import { Occ, Product, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable, Subscription, filter } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { SourceType } from '../image-gallery/image-gallery.component';
import { chevronBtnNext, chevronBtnPrev } from '@assets/icons/icon-index';
import { EventHelper } from '@features/tracking/event-helper.service';
import { MediaGalleryService, PreviewMedia } from '@features/pages/product/core/services/media-gallery.service';
import {
  SALES_STATUS_PURCHASING_INACTIVE_IDENTIFIER,
  SALES_STATUS_PURCHASING_PHASEOUT_IDENTIFIER,
} from '@helpers/constants';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { DistBreakpointService } from '@services/breakpoint.service';

@Component({
  selector: 'app-image-holder',
  templateUrl: './image-holder.component.html',
  styleUrls: ['./image-holder.component.scss'],
})
export class ImageHolderComponent implements OnInit, OnDestroy {
  @Input() code: string;
  @Input() productImageData: Occ.Image[];
  @Input() isCalibrated: boolean;
  @Input() activePromotionLabels;
  @Input() statusCode: string;
  @Input() sourceType: SourceType = 'image';
  @Input() energyEfficiency: string;
  @Input() energyPower: string;
  @Input() productTitle: string;
  @Input() energyEfficiencyLabelImageUrl: string;
  @Input() productData: Product;

  selectedMedia$ = toObservable(this.productDataService.sharedMediaSignal);

  currentChannel$: BehaviorSubject<CurrentSiteSettings> = this.siteSettingsService.currentChannelData$;
  isMobileBreakpoint: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  imageLabels: boolean;

  displayPreview: WritableSignal<string> = signal(null);

  data: PreviewMedia[] = [];
  currentIndex = 0;

  next = chevronBtnNext;
  prev = chevronBtnPrev;

  subscriptions = new Subscription();

  constructor(
    private productDataService: ProductDataService,
    private siteSettingsService: AllsitesettingsService,
    private eventHelper: EventHelper,
    private mediaGalleryService: MediaGalleryService,
    private breakpointService: DistBreakpointService,
    private winRef: WindowRef,
  ) {}

  ngOnInit() {
    this.subscriptions.add(
      this.selectedMedia$
        .pipe(filter(Boolean))
        .subscribe(
          (mediaSource: string) => (this.currentIndex = this.data.findIndex((media) => media.source === mediaSource)),
        ),
    );

    this.data = this.mediaGalleryService.returnMediaList(
      this.data,
      this.productData.videos,
      this.productImageData?.filter((img) => img?.format === 'landscape_large'),
    );

    this.assignMissingImageIfNoMedia();

    this.currentIndex = this.data.findIndex((media) => media.source === this.productDataService.sharedMediaSignal());
    this.imageLabels =
      this.productData?.availableInSnapEda ||
      this.isCalibrated ||
      this.activePromotionLabels?.length > 0 ||
      !!this.energyEfficiency ||
      this.productData?.audios?.length > 0 ||
      this.productData?.isBetterWorld;
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  onImageClick(media: any) {
    if (!media.source.includes('missing_landscape_medium')) {
      this.displayPreview.set(media.source);
      this.eventHelper.trackImageClick(this.code);
    }
  }

  prevImage(): void {
    this.currentIndex = this.mediaGalleryService.prevImage(this.currentIndex, this.data);
    this.productDataService.updateMediaSignal(this.data[this.currentIndex].source);
  }

  nextImage(): void {
    this.currentIndex = this.mediaGalleryService.nextImage(this.currentIndex, this.data);
    this.productDataService.updateMediaSignal(this.data[this.currentIndex].source);
  }

  isInactiveProduct(): boolean {
    return (
      this.statusCode.startsWith(SALES_STATUS_PURCHASING_PHASEOUT_IDENTIFIER) ||
      this.statusCode.startsWith(SALES_STATUS_PURCHASING_INACTIVE_IDENTIFIER)
    );
  }

  private assignMissingImageIfNoMedia(): void {
    if (this.data.length === 0) {
      this.data.push({ type: 'image', source: this.productDataService.sharedMediaSignal() });
    }
  }

  scrollToBetterWorldContent(): void {
    const el: HTMLElement = this.winRef.document.getElementById('better-world-text');

    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'end' });
    }
  }
}
