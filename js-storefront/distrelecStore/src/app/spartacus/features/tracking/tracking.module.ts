import { NgModule } from '@angular/core';
import { WindowRef, provideConfig } from '@spartacus/core';
import { BaseTmsModule, TmsConfig } from '@spartacus/tracking/tms/core';
import { GtmModule } from '@spartacus/tracking/tms/gtm';
import { GtmCollectorConfig } from '@spartacus/tracking/tms/gtm/config/default-gtm.config';

import { environment } from '@environment';
import { GtmEventCollectorService } from './google-tag-manager/gtm-event-collector.service';
import { GtmEventBuilder } from './google-tag-manager/gtm-event-builder.service';
import { FactFinderEventBuilder } from './fact-finder/fact-finder-event.builder';
import { FactFinderEventCollectorService } from './fact-finder/fact-finder-event-collector.service';
import { BloomreachEventCollectorService } from './bloomreach/bloomreach-event-collector.service';
import { BlmCollectorConfig } from './bloomreach/bloomreach-config.service';

import { TrackingInitializerModule } from './tracking-initializer/tracking-initializer.module';
import { TRACKING_INITIALIZER } from './tracking-initializer/tracking-initializer';
import { CheqInitializer } from './initializers/cheq-initializer';

import { PageViewEvent } from './events/ga4/page-view-event';
import { LoginEvent } from './events/ga4/login-event';
import { RegistrationEvent } from './events/registration-event';
import { ProductImageClickEvent } from './events/ga4/product-image-click-event';
import { ProductImage3dClickEvent } from './events/ga4/product-image-3d-click-event';
import { CompareProductEvent } from './events/ga4/compare-product-event';
import { SiteSearchNoResultsEvent } from './events/site-search-no-results-event';
import { PromotionClickEvent } from './events/ga4/promotion-click-event';
import { FactFinderLoginEvent } from './events/fact-finder-login-event';
import { FactFinderCartEvent } from './events/fact-finder-cart-event';
import { FactFinderCheckoutEvent } from '@features/tracking/events/fact-finder-checkout-event';
import { FactFinderClickEvent } from '@features/tracking/events/fact-finder-click-event';
import { FactFinderRecommendationClickEvent } from './events/fact-finder-recommendation-click-event';
import { AddToSubscriptionPopupEvent } from './events/add-to-subscription-popup-event';
import { BloomreachEventBuilder } from './bloomreach/bloomreach-event-builder.service';
import { BloomreachPlpViewEvent } from '@features/tracking/events/bloomreach/bloomreach-plp-view-event';
import { BloomreachPdpViewEvent } from './events/bloomreach/bloomreach-pdp-view-event';
import { BloomreachCustomerEvent } from './events/bloomreach/bloomreach-customer-event';
import { BloomreachLogoutEvent } from './events/bloomreach/bloomreach-logout-event';
import { BloomreachManufacturerViewEvent } from '@features/tracking/events/bloomreach/bloomreach-manufacturer-view-event';
import { BloomreachProductFamilyViewEvent } from '@features/tracking/events/bloomreach/bloomreach-product-family-view-event';
import { BloomreachCategoryViewEvent } from './events/bloomreach/bloomreach-category-view-event';
import { BloomreachPurchaseOrderEvent } from '@features/tracking/events/bloomreach/bloomreach-purchase-order-event';
import { BloomreachPurchaseItemEvent } from '@features/tracking/events/bloomreach/bloomreach-purchase-item-event';
import { BloomreachUpdateCartEvent } from './events/bloomreach/bloomreach-update-cart-event';
import { BloomreachPasswordPageViewEvent } from './events/bloomreach/bloomreach-rs-password-page-view-event';
import { Ga4ViewItemListEvent } from '@features/tracking/events/ga4/ga4-view-item-list-event';
import { Ga4ViewItemEvent } from '@features/tracking/events/ga4/ga4-view-item-event';
import { Ga4CheckoutEvent } from '@features/tracking/events/ga4/ga4-checkout-event';
import { Ga4PurchaseEvent } from '@features/tracking/events/ga4/ga4-purchase-event';
import { Ga4CartEvent } from '@features/tracking/events/ga4/ga4-cart-event';
import { Ga4AddToWishlistEvent } from '@features/tracking/events/ga4/ga4-add-to-wishlist-event';
import { Ga4SelectItem } from '@features/tracking/events/ga4/ga4-select-item-event';
import { RSMigrationEvent } from '@features/tracking/events/ga4/RS-migration-event';
import { BloomreachAccountActivationEvent } from '@features/tracking/events/bloomreach-account-activation-event';
import { BloomreachSetPasswordEvent } from '@features/tracking/events/bloomreach/bloomreach-set-password-event';
import { Ga4ViewPdpReviewsEvent } from './events/ga4/ga4-pdp-review-event';
import { Ga4Error404Event } from './events/ga4/ga4-error-404-event';
import { Ga4RegistrationStartEvent } from './events/ga4/ga4-registration-start-event';
import { Ga4HomepageInteractionEvent } from './events/ga4/ga4-homepage-interaction-event';
import { Ga4HeaderInteractionEvent } from './events/ga4/ga4-header-interaction-event';
import { Ga4PrintPageEvent } from './events/print-page-event';
import { Ga4DownloadPDFEvent } from './events/download-pdf-event';
import { HotBarCompareEvent } from './events/hotbar-compare-event';
import { HotBarClearAllEvent } from './events/hotbar-clear-all-event';
import { Ga4SearchSuggestionEvent } from './events/ga4/ga4-search-suggestion-event';
import { Ga4SearchEvent } from './events/ga4/ga4-search-event';
import { Ga4FooterInteractionEvent } from './events/ga4/ga4-footer-interaction-event';
import { Ga4ViewPromotionEvent } from "@features/tracking/events/ga4/ga4-view-promotion-event";

@NgModule({
  declarations: [],
  imports: [BaseTmsModule.forRoot(), GtmModule, TrackingInitializerModule],
  providers: [
    provideConfig({
      tagManager: {
        gtm: {
          debug: !environment.production,
          collector: GtmEventCollectorService,
          events: [
            CompareProductEvent,
            LoginEvent,
            PageViewEvent,
            ProductImageClickEvent,
            ProductImage3dClickEvent,
            PromotionClickEvent,
            RegistrationEvent,
            SiteSearchNoResultsEvent,
            AddToSubscriptionPopupEvent,
            Ga4ViewItemListEvent,
            Ga4ViewItemEvent,
            Ga4SearchEvent,
            Ga4ViewPromotionEvent,
            Ga4CheckoutEvent,
            Ga4PurchaseEvent,
            Ga4CartEvent,
            Ga4AddToWishlistEvent,
            Ga4SelectItem,
            Ga4RegistrationStartEvent,
            Ga4HomepageInteractionEvent,
            Ga4HeaderInteractionEvent,
            Ga4SearchSuggestionEvent,
            RSMigrationEvent,
            Ga4Error404Event,
            Ga4ViewPdpReviewsEvent,
            Ga4PrintPageEvent,
            Ga4DownloadPDFEvent,
            Ga4FooterInteractionEvent,
            HotBarCompareEvent,
            HotBarClearAllEvent,
          ],
        } as GtmCollectorConfig,
        bloomreach: {
          debug: !environment.production,
          collector: BloomreachEventCollectorService,
          events: [
            BloomreachPlpViewEvent,
            BloomreachPdpViewEvent,
            BloomreachCustomerEvent,
            BloomreachLogoutEvent,
            BloomreachManufacturerViewEvent,
            BloomreachProductFamilyViewEvent,
            BloomreachCategoryViewEvent,
            BloomreachPurchaseOrderEvent,
            BloomreachPurchaseItemEvent,
            BloomreachUpdateCartEvent,
            BloomreachAccountActivationEvent,
            BloomreachPasswordPageViewEvent,
            BloomreachSetPasswordEvent,
          ],
        } as unknown as BlmCollectorConfig,
        factFinder: {
          debug: !environment.production,
          collector: FactFinderEventCollectorService,
          events: [
            FactFinderLoginEvent,
            FactFinderCartEvent,
            FactFinderCheckoutEvent,
            FactFinderClickEvent,
            FactFinderRecommendationClickEvent,
          ],
        },
      },
    } as TmsConfig),
    {
      provide: TRACKING_INITIALIZER,
      useClass: CheqInitializer,
      multi: true,
    },
  ],
})
export class TrackingModule {
  constructor(
    eventBuilder: GtmEventBuilder,
    factFinderEventBuilder: FactFinderEventBuilder,
    bloomreachEventBuilder: BloomreachEventBuilder,
    public winRef: WindowRef,
  ) {
    if (this.winRef.isBrowser()) {
      eventBuilder.registerEvents();
      factFinderEventBuilder.registerEvents();
      bloomreachEventBuilder.registerEvents();
    }
  }
}
