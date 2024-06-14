import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';
import { Observable, Subscription } from 'rxjs';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { CountryService } from '../../../../../site-context/services/country.service';
import { AppendComponentService } from '@services/append-component.service';
import { take } from 'rxjs/operators';
import { LearnMorePopupComponent } from './learn-more-popup/learn-more-popup.component';
import { createFrom, EventService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { DownloadPDFEvent } from '@features/tracking/events/download-pdf-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PDFType } from '@features/tracking/model/pdf-types';
import { VolumePricesMapList } from '@model/product.model';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.scss'],
})
export class ProductDetailsComponent implements OnInit, AfterViewInit {
  @Input() productData;

  schema = {};

  assetPath = '/app/spartacus/assets/media/recycling/';
  faExclamationTriangle = faExclamationTriangle;

  currentChannel$: Observable<CurrentSiteSettings> = this.siteSettingsService.currentChannelData$;
  activeSubscription: Subscription;

  siteCountryCode$: Observable<string> = this.countryService.getActive();
  baseUrl = this.occEndpointsService.getBaseUrl();

  countriesWithMovexArticle: Array<string> = ['AT', 'BE', 'CH', 'CZ', 'HU', 'IT', 'RO', 'SK'];
  countriesWithElfaArticle: Array<string> = ['DK', 'FI', 'NO', 'SE', 'LT', 'LV', 'EE', 'NL', 'PL', 'EX'];

  weeeCategoryArray: [] = [];

  constructor(
    private countryService: CountryService,
    private siteSettingsService: AllsitesettingsService,
    private appendComponentService: AppendComponentService,
    private occEndpointsService: OccEndpointsService,
    private winRef: WindowRef,
    private eventService: EventService,
  ) {}

  ngOnInit(): void {
    this.weeeCategoryArray = this.productData.weeeCategory?.split('/');
  }

  ngAfterViewInit() {
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scrollTo(0, 0);
    }
  }

  getVolumePricesMap(volumePricesMap: VolumePricesMapList[]) {
    /* eslint-disable @typescript-eslint/naming-convention */
    return volumePricesMap?.map((list) => ({
      '@type': 'Offer',
      availability: this.productData.stock.stockLevelStatus,
      url: this.productData.url,
      priceCurrency: this.productData.price.currencyIso,
      priceSpecification: {
        '@type': 'CompoundPriceSpecification',
        name: 'Price',
        price: list.value[0].value.priceWithVat,
        priceCurrency: this.productData.price.currencyIso,
        valueAddedTaxIncluded: true,
      },
      eligibleQuantity: {
        '@type': 'QuantitativeValue',
        minValue: list.key,
      },
    }));
    /* eslint-enable @typescript-eslint/naming-convention */
  }

  openLearnMorePopup(rohsCode: string) {
    const learnMorePopupRef = this.appendComponentService.appendComponent(LearnMorePopupComponent, { rohsCode });

    learnMorePopupRef.instance.close
      .pipe(take(1))
      .subscribe(() => this.appendComponentService.destroyComponent(learnMorePopupRef));
  }

  isProductDataSectionPresent(productData): boolean {
    return this.isNotesInfoPresent(productData) || this.isFamilyInfoPresent(productData);
  }

  isEnvironmentSectionPresent(productData): boolean {
    return productData?.rohs || productData?.svhc || productData?.svhcReviewDate || productData?.isDangerousGoods;
  }

  isArticleInfoPresent(siteCountryCode: string): boolean {
    return (
      this.isElfaArticlePresent(siteCountryCode) ||
      this.isMovexArticlePresent(siteCountryCode) ||
      this.isDEAndPreviousArticlePresent(siteCountryCode) ||
      this.isProductCatalog16or17() ||
      this.isProductCatalogOther()
    );
  }

  isElfaArticlePresent(siteCountryCode: string): boolean {
    return this.countriesWithElfaArticle.includes(siteCountryCode) && this.productData?.elfaArticleNumber !== '';
  }

  isMovexArticlePresent(siteCountryCode: string) {
    return this.countriesWithMovexArticle.includes(siteCountryCode) && this.productData?.movexArticleNumber !== '';
  }

  isDEAndPreviousArticlePresent(siteCountryCode: string): boolean {
    return siteCountryCode === 'DE' && this.productData?.navisionArticleNumber !== '';
  }

  isProductCatalog16or17(): boolean {
    return this.productData?.productInformation?.paperCatalogPageNumber_16_17;
  }

  isProductCatalogOther(): boolean {
    return this.productData?.productInformation?.paperCatalogPageNumber;
  }

  isFamilyInfoPresent(productData): boolean {
    return (
      productData?.productInformation?.familyDescription.length ||
      productData?.productInformation?.familyDescriptionBullets.length
    );
  }

  isNotesInfoPresent(productData): boolean {
    return (
      productData?.productInformation?.orderNote ||
      productData?.productInformation?.orderNoteArticle ||
      productData?.productInformation?.deliveryNote ||
      productData?.productInformation?.deliveryNoteArticle ||
      productData?.productInformation?.assemblyNote ||
      productData?.productInformation?.usageNote.length
    );
  }

  dispatchPDFEvent(): void {
    this.eventService.dispatch(
      createFrom(DownloadPDFEvent, {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        context: { pageType: ItemListEntity.PDP, PDF_type: PDFType.BATTERYCOMPLIANCE, product: this.productData },
      }),
    );
  }
}
