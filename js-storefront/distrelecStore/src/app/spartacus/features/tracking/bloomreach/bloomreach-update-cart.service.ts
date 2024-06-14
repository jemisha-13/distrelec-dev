/* eslint-disable @typescript-eslint/naming-convention */
import { Injectable } from '@angular/core';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CmsService, createFrom, EventService, WindowRef } from '@spartacus/core';
import { CartStoreService } from '@state/cartState.service';
import { BloomreachUpdateCartEvent } from '../events/bloomreach/bloomreach-update-cart-event';
import { take, tap } from 'rxjs/operators';
import { round } from 'lodash-es';
import { PriceService } from '@services/price.service';
import { combineLatest } from 'rxjs';
import { OrderEntry } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
})
export class BloomreachUpdateCartTracking {
  originalValue: number;
  price: number;
  discountPercentage: number;

  constructor(
    private allSiteSettingsService: AllsitesettingsService,
    private winRef: WindowRef,
    private cmsService: CmsService,
    private cartStore: CartStoreService,
    private eventService: EventService,
    private priceService: PriceService,
  ) {}

  trackAddOrRemoveCartEvent(action: string, entry: OrderEntry): void {
    combineLatest([this.priceService.getPrices(entry.product.code), this.cmsService.getCurrentPage()])
      .pipe(
        take(1),
        tap(([prices, page]) => {
          this.price = this.priceService.getPriceBasedOnChannel(
            prices.price,
            this.allSiteSettingsService.currentChannelData$.getValue().channel,
          );
          this.originalValue = prices.price.originalValue;
          this.discountPercentage = prices.price.discountValue;

          this.eventService.dispatch(
            createFrom(BloomreachUpdateCartEvent, {
              type: 'update_cart',
              body: {
                action,
                page_type: page.pageId,
                web_store_url: this.winRef.nativeWindow.location.origin,
                webshop_url_lang:
                  this.winRef.nativeWindow.location.origin +
                  '/' +
                  this.allSiteSettingsService.currentChannelData$.value.language +
                  '/',
                language: this.allSiteSettingsService.currentChannelData$.value.language,
                local_currency: this.allSiteSettingsService.currentChannelData$.value.currency,

                product_id: entry.product.code,
                category_id: entry.product.categories[0].code,
                brand: entry.product.distManufacturer.name,
                price: round(this.price, 2) ?? 0,
                currency: entry?.totalPrice.currencyIso ?? '',
                discount_percentage: this.discountPercentage ?? 0,
                discount_value: this.discountPercentage ? round(this.originalValue / this.discountPercentage, 2) : 0,
                original_price: this.originalValue ?? this.price,
                product_list: JSON.stringify(this.returnProductIdsAndQuantity()),
                product_ids: JSON.stringify(this.returnProductIdsList()),
              },
            } as BloomreachUpdateCartEvent),
          );
        }),
      )
      .subscribe();
  }

  trackEmptyProductCartEvent(): void {
    this.cmsService
      .getCurrentPage()
      .pipe(
        take(1),
        tap((page) => {
          this.eventService.dispatch(
            createFrom(BloomreachUpdateCartEvent, {
              type: 'update_cart',
              body: {
                action: 'empty',
                page_type: page.pageId,
                web_store_url: this.winRef.nativeWindow.location.origin,
                webshop_url_lang:
                  this.winRef.nativeWindow.location.origin +
                  '/' +
                  this.allSiteSettingsService.currentChannelData$.value.language +
                  '/',
                language: this.allSiteSettingsService.currentChannelData$.value.language,
                local_currency: this.allSiteSettingsService.currentChannelData$.value.currency,
              },
            } as BloomreachUpdateCartEvent),
          );
        }),
      )
      .subscribe();
  }

  returnProductIdsAndQuantity(): { product_id: string; quantity: number }[] {
    return this.cartStore
      .getCartEntries()
      .map((entry) => ({ product_id: entry.product.code, quantity: entry.quantity }));
  }

  returnProductIdsList(): string[] {
    return this.cartStore.getCartEntries().map((entry) => entry.product?.code);
  }
}
