import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ImageType, Occ, TranslationService, WindowRef } from '@spartacus/core';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { bug, letter } from '@assets/icons/icon-index';
import { ProductReferencesService } from '@features/pages/product/core/services/product-references.service';
import { ProductReference } from '@model/product-reference.model';
import { HeaderService } from '@features/shared-modules/header/header.service';
import { ICustomProduct, Video } from '@model/product.model';
import { ProductAvailability } from '@model/product-availability.model';
import { ProductAvailabilityService } from '../../core/services/product-availability.service';
import { DefaultImageService } from '@services/default-image.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.scss'],
})
export class SummaryComponent implements OnInit, OnDestroy {
  productData$: Observable<ICustomProduct>;
  altProducts$: Observable<ProductReference[]>;
  displayNotifyModal$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isDisplayReportErrorForm: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  missingImgSrc: string = this.defaultImage.getDefaultImageMedium();
  currentChannelData$ = this.siteSettingsService.currentChannelData$;
  alertBannerMessage: string;

  iconLetter = letter;
  iconBug = bug;

  isStickyAddToCartShown = false;

  isBTR: boolean;
  isWaldom: boolean;
  availableQty: number;
  hasStock: boolean;

  addToCartForm = this.productDataService.addToCartForm;
  productAvailability$: Observable<ProductAvailability>;

  private subscriptions = new Subscription();

  constructor(
    private productDataService: ProductDataService,
    private productReferencesService: ProductReferencesService,
    private activatedRoute: ActivatedRoute,
    private siteSettingsService: AllsitesettingsService,
    private energyEfficiencyService: EnergyEfficiencyLabelService,
    private winref: WindowRef,
    private headerService: HeaderService,
    private productAvailabilityService: ProductAvailabilityService,
    private defaultImage: DefaultImageService,
    private translationService: TranslationService,
  ) {}

  @HostListener('window:scroll', ['$event'])
  checkScroll(): void {
    const footerPos = this.winref.document.getElementById('footer')?.getBoundingClientRect();
    const breakpointPos = this.winref.document.getElementById('pdp_atc_btn')?.getBoundingClientRect();

    if (breakpointPos?.top < 0 && footerPos?.top > this.winref.nativeWindow.innerHeight) {
      this.isStickyAddToCartShown = true;
    } else {
      this.isStickyAddToCartShown = false;
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  ngOnInit() {
    const { productCode } = this.activatedRoute.snapshot.params;
    this.productData$ = this.productDataService.getProductData(productCode).pipe(
      tap(productData => this.productDataService.updateMediaSignal(this.getPrimaryImage(productData?.images)?.url ?? this.missingImgSrc))    
    );
    this.altProducts$ = this.productReferencesService.getAlternatives(productCode);
    this.productAvailability$ = this.productAvailabilityService.getAvailability(productCode);
  }

  getEnergyEfficiencyLabelImageUrl(eelImageMap): string {
    return this.energyEfficiencyService.getEnergyEfficiencyLabelImageUrl(eelImageMap);
  }

  getPrimaryImage(images: Occ.Image[]): Occ.Image {
    if (!images) {
      return {
        url: this.missingImgSrc,
        format: 'landscape_large',
        imageType: ImageType.PRIMARY,
      };
    }

    // Get primary image if exists, if not, return the first gallery image found
    const primaryImage: Occ.Image =
      images.find((image) => image.format === 'landscape_large' && image.imageType === 'PRIMARY') ?? null;

    if (primaryImage) {
      return primaryImage;
    } else {
      return images.find((image) => image.format === 'landscape_large' && image.imageType === 'GALLERY') ?? null;
    }
  }

  openDisplayForm() {
    this.isDisplayReportErrorForm.next(true);
  }

  isAlertBannerShown(productData: ICustomProduct): Observable<boolean> {
    return this.productAvailability$.pipe(
      map((productAvailability) => {
        if (!productAvailability) {
          return false;
        }

        const stockLevelTotal = productAvailability.stockLevelTotal;
        const statuses = [20, 21, 40, 41, 60, 61, 62, 90, 91];
        const parsedStatus = parseInt(productData.salesStatus, 10);

        if (parsedStatus === 20) {
          this.alertBannerMessage = 'product.message.status.20';
          return statuses.indexOf(parsedStatus) !== -1;
        } else if (parsedStatus === 21) {
          this.alertBannerMessage = 'product.message.status.21';
          return statuses.indexOf(parsedStatus) !== -1;
        } else if ((parsedStatus === 40 || parsedStatus === 41) && stockLevelTotal) {
          this.alertBannerMessage = 'product.status.no.longer.available.purchase';
          return statuses.indexOf(parsedStatus) !== -1;
        } else if ((parsedStatus === 40 || parsedStatus === 41) && !stockLevelTotal) {
          this.alertBannerMessage = 'salesStatus.no_stock.headline_no_longer_available';
          return statuses.indexOf(parsedStatus) !== -1;
        } else if (parsedStatus === 60 || parsedStatus === 61) {
          this.alertBannerMessage = 'product.status.nolongeravailable';
          return statuses.indexOf(parsedStatus) !== -1;
        } else if (parsedStatus === 90 || parsedStatus === 91) {
          this.alertBannerMessage = 'product.message.status.90';
          return statuses.indexOf(parsedStatus) !== -1;
        } else {
          return false;
        }
      }),
    );
  }

  scrollToAlternative(elementId: string): void {
    if (this.winref.isBrowser()) {
      const window: Window = this.winref.nativeWindow;
      const elementPosition: number = window.document.getElementById(elementId).getBoundingClientRect().top;
      const windowPosition: number = window.scrollY;
      const headerHeight: number = this.headerService.headerHeight;
      const offset: number = headerHeight + 100;

      const amount: number = elementPosition + windowPosition - offset;

      window.scrollTo({ top: amount, behavior: 'smooth' });
    }
  }

  getShareUrl(productData: ICustomProduct): Observable<string> {
    const title = `${productData?.typeName} - ${productData?.name}, ${productData?.distManufacturer?.name}`;

    return combineLatest([
      this.currentChannelData$,
      this.translationService.translate('product.share_email_subject'),
      this.translationService.translate('product.share_email_description'),
    ]).pipe(
      filter(
        ([currentChannelData, subject, description]) =>
          !!productData && !!currentChannelData && !!subject && !!description,
      ),
      map(([currentChannelData, subject, description]) => {
        return `mailto:?subject=${subject}&amp;body=${description}&nbsp;${title}%0D%0A${currentChannelData.domain}${productData?.url}`;
      }),
    );
  }

  isOnlyImage(images: Occ.Image[], videos: Video[]): boolean {
    return videos?.length === 0 || (!videos && images.filter((img) => img.format === 'landscape_small').length === 1);
  }
}
