/* eslint-disable @typescript-eslint/naming-convention */
import { Injectable } from '@angular/core';
import {
  AuthService,
  BreadcrumbMeta,
  createFrom,
  EventService,
  LanguageService,
  Page, Product,
  ProductSearchPage,
  ProductService,
  TranslationService,
  WindowRef,
} from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { filter, first, map, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { combineLatest, Observable, of } from 'rxjs';
import { CategoriesService } from '@services/categories.service';
import { NavigationEvent } from '@spartacus/storefront';
import { BloomreachPdpViewEvent } from '@features/tracking/events/bloomreach/bloomreach-pdp-view-event';
import { BloomreachCustomerEvent } from '../events/bloomreach/bloomreach-customer-event';
import { DistrelecUserService } from '@services/user.service';
import { BloomreachLogoutEvent } from '../events/bloomreach/bloomreach-logout-event';
import { BloomreachManufacturerViewEvent } from '@features/tracking/events/bloomreach/bloomreach-manufacturer-view-event';
import { ManufactureService } from '@services/manufacture.service';
import { ManufacturerData } from '@model/manufacturer.model';
import { BloomreachProductFamilyViewEvent } from '@features/tracking/events/bloomreach/bloomreach-product-family-view-event';
import { ProductFamilyData } from '@model/product-family.model';
import { BloomreachCategoryViewEvent } from '../events/bloomreach/bloomreach-category-view-event';
import { CategoryPageData } from '@model/category.model';
import { BloomreachPurchaseOrderEvent } from '@features/tracking/events/bloomreach/bloomreach-purchase-order-event';
import { BloomreachPurchaseItemEvent } from '@features/tracking/events/bloomreach/bloomreach-purchase-item-event';
import { ProductFamilyService } from '@features/pages/product/core/services/product-family.service';
import { BloomreachPlpViewEvent } from '../events/bloomreach/bloomreach-plp-view-event';
import { BloomreachPasswordPageViewEvent } from '../events/bloomreach/bloomreach-rs-password-page-view-event';
import { BloomreachAccountActivationEvent } from '@features/tracking/events/bloomreach-account-activation-event';
import { BloomreachSetPasswordEvent } from '@features/tracking/events/bloomreach/bloomreach-set-password-event';
import { Prices } from '@model/price.model';
import { PriceService } from '@services/price.service';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { OrderEntry } from '@spartacus/cart/base/root';
import { CountryCodesEnum } from '@context-services/country.service';
import { ViewItemEvent } from '../events/view-item-event';
import { CurrentSiteSettings } from '@model/site-settings.model';

enum CustomerType {
  B2B = 'B2B',
  B2C = 'B2C',
}

@Injectable({
  providedIn: 'root',
})
export class BloomreachEventBuilder {
  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translationService
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  constructor(
    private authService: AuthService,
    private categoryService: CategoriesService,
    private distrelecUserService: DistrelecUserService,
    private eventService: EventService,
    private winRef: WindowRef,
    private manufacturerService: ManufactureService,
    private productFamilyService: ProductFamilyService,
    private languageService: LanguageService,
    private translationService: TranslationService,
    private priceService: PriceService,
    private productService: ProductService,
    private productAvailabilityService: ProductAvailabilityService,
    private allSiteSettingsService: AllsitesettingsService,
  ) {}

  registerEvents(): void {
    this.eventService.register(BloomreachCategoryViewEvent, this.buildCategoryViewEvent());
    this.eventService.register(BloomreachPdpViewEvent, this.buildPdpViewEvent());
    this.eventService.register(BloomreachCustomerEvent, this.buildIdenityCustomerEvent());
    this.eventService.register(BloomreachProductFamilyViewEvent, this.buildProductFamilyViewEvent());
    this.eventService.register(BloomreachManufacturerViewEvent, this.buildManufacturerPageViewEvent());
    this.eventService.register(BloomreachLogoutEvent, this.buildLogoutEvent());
  }

  buildCategoryViewEvent(): Observable<BloomreachCategoryViewEvent> {
    return this.eventService.get(NavigationEvent).pipe(
      filter((navigationEvent) => navigationEvent.semanticRoute === 'category'),
      switchMap((navigationEvent: NavigationEvent) =>
        this.categoryService.getCategoryData(navigationEvent?.context?.id).pipe(
          filter((categoryData) => !this.isPagePLP(categoryData)),
          withLatestFrom(this.homeBreadcrumb$),
          map(([categoryData, homeBreadcrumb]) => {
            const categoryPath: string = [
              homeBreadcrumb.label,
              categoryData?.breadcrumbs?.map((breadcrumb) => breadcrumb.name),
            ]
              .flat()
              .join(' ');

            return createFrom(BloomreachCategoryViewEvent, {
              type: 'category',
              body: {
                category_name: categoryData.sourceCategory.name,
                category_url: this.winRef.nativeWindow.location?.href,
                category_id: categoryData.sourceCategory.code,
                category_path: categoryPath,
              },
            });
          }),
        ),
      ),
    );
  }

  isPagePLP(categoryData): boolean {
    return (
      categoryData.showCategoriesOnly === false ||
      this.winRef.nativeWindow.location.href.includes('useTechnicalView=true')
    );
  }

  buildManufacturerPageViewEvent(): Observable<BloomreachManufacturerViewEvent> {
    return this.manufacturerService.getCurrentManufacturerData().pipe(
      filter<ManufacturerData>(Boolean),
      map((manufacturerData) =>
        createFrom(BloomreachManufacturerViewEvent, {
          type: 'manufacturer_view',
          body: {
            manufacturer_url: this.winRef.nativeWindow.location.href,
            manufacturer_id: manufacturerData.code,
            manufacturer_brand: manufacturerData.name,
          },
        }),
      ),
    );
  }

  buildIdenityCustomerEvent(): Observable<BloomreachCustomerEvent> {
    return this.distrelecUserService.getUserDetails().pipe(
      filter(Boolean),
      map((user: any) =>
        createFrom(BloomreachCustomerEvent, {
          type: 'customer',
          email_id: user.uid,
          erp_contact_id: user.contactId,
        }),
      ),
    );
  }

  buildLogoutEvent(): Observable<BloomreachLogoutEvent> {
    return this.authService.logoutInProgress$.pipe(
      filter((progress) => progress && !!this.distrelecUserService.userDetails_.value),
      map(() =>
        createFrom(BloomreachLogoutEvent, {
          type: 'logout',
        }),
      ),
    );
  }

  buildProductFamilyViewEvent(): Observable<BloomreachProductFamilyViewEvent> {
    return this.eventService.get(NavigationEvent).pipe(
      filter((event) => event.semanticRoute === 'productFamily'),
      switchMap(() =>
        this.productFamilyService.getCurrentFamilyData().pipe(
          filter<ProductFamilyData>(Boolean),
          take(1),
          map((productFamilyData) =>
            createFrom(BloomreachProductFamilyViewEvent, {
              type: 'product_family_view',
              body: {
                product_family_url: this.winRef.nativeWindow.location.href,
                product_family_id: productFamilyData.code,
                product_family_manufacturer: productFamilyData.name,
              },
            }),
          ),
        ),
      ),
    );
  }

  returnPriceWithoutVat(orderData): number {
    return orderData.customerType === CustomerType.B2C
      ? orderData.totalPrice.value - orderData.totalTax.value
      : orderData.totalPrice.value;
  }

  returnPrice(orderData): number {
    return orderData.customerType === CustomerType.B2B ? orderData.totalPriceWithTax.value : orderData.totalPrice.value;
  }

  buildPurchaseOrderEvent(orderData, isSubUser?: boolean): BloomreachPurchaseOrderEvent {
    const isGuestPurchase = orderData.customerType === 'GUEST';
    const productCodes = orderData.entries.map((entry) => entry.product.code);
    const productLists = orderData.entries.map((entry) => entry.product.name);

    const purchaseOrderEvent: BloomreachPurchaseOrderEvent = {
      type: 'sales_order',
      body: {
        purchase_id: orderData.code,
        purchase_status: orderData.status,
        voucher_code: orderData.erpVoucherInfoData?.code,
        voucher_value: orderData.erpVoucherInfoData?.fixedValue?.value,
        shipping_cost: orderData.deliveryCost?.value,
        local_currency: orderData.totalPrice?.currencyIso,
        location: this.winRef.nativeWindow.location.href,
        web_store_url: this.winRef.nativeWindow.location.origin,
        product_list: productLists,
        product_ids: productCodes,
        total_quantity: orderData.entries?.length,
        total_price: orderData.totalPrice?.value,
        total_price_without_tax: orderData.subTotal?.value,
        timestamp: orderData.created,
        customer_type: orderData.customerType,
        placement: isSubUser ? 'sub_user' : 'my_account',
        order_dispatch_date: orderData.orderDate,
      },
    };

    if (isGuestPurchase) {
      return createFrom(BloomreachPurchaseOrderEvent, {
        type: purchaseOrderEvent.type,
        body: {
          ...purchaseOrderEvent.body,
          email: orderData.billingAddress.email,
          placement: 'guest',
        },
      }) as BloomreachPurchaseOrderEvent;
    }

    return createFrom(BloomreachPurchaseOrderEvent, purchaseOrderEvent);
  }

  buildPurchaseItemEvent(
    orderData: Order,
    entry: OrderEntry,
    guestCheckoutType?: string | undefined,
    isSubUser?: boolean,
  ): BloomreachPurchaseItemEvent {
    const purchaseOrderItemEvent: BloomreachPurchaseItemEvent = {
      type: 'sales_order_item',
      body: {
        purchase_id: orderData.code,
        purchase_status: orderData.status,
        local_currency: orderData.totalPrice.currencyIso,
        web_store_url: this.winRef.nativeWindow.location.origin,
        product_id: entry.product.code,
        title: entry.product.name,
        brand: entry.product.distManufacturer.name,
        item_line_price: entry.totalPrice.value,
        item_line_quantity: entry.quantity,
        placement: isSubUser ? 'sub_user' : 'my_account',
      },
    };

    if (guestCheckoutType) {
      return createFrom(BloomreachPurchaseItemEvent, {
        type: purchaseOrderItemEvent.type,
        body: {
          ...purchaseOrderItemEvent.body,
          placement: 'guest',
        },
      }) as BloomreachPurchaseItemEvent;
    }

    return createFrom(BloomreachPurchaseItemEvent, purchaseOrderItemEvent);
  }

  buildPlpViewEvent(
    categoryData?: CategoryPageData,
    cmsPageData?: Page,
    response?: ProductSearchPage,
  ): BloomreachPlpViewEvent {
    const categoryPath = categoryData?.breadcrumbs?.map((breadcrumb) => breadcrumb.name).join(' ');
    const freeTextSearch = response?.freeTextSearch;

    return createFrom(BloomreachPlpViewEvent, {
      type: 'plp_view',
      body: {
        plp_category_name: categoryData?.sourceCategory?.name ?? 'Search',
        plp_url: this.winRef.location?.href,
        plp_id: categoryData?.sourceCategory?.code ?? '',
        plp_category_path: categoryPath ?? '',
        search_text: freeTextSearch ?? '',
      },
    });
  }

  buildPasswordPageviewEvent(email: string): Observable<BloomreachPasswordPageViewEvent> {
    return this.languageService.getActive().pipe(
      map((language: string) =>
        createFrom(BloomreachPasswordPageViewEvent, {
          type: 'rs_password_pageview',
          body: {
            time_stamp: this.getCurrentTimestamp(),
            email_id: email,
            web_store_url: this.winRef.nativeWindow.location.origin,
            language,
          },
        }),
      ),
    );
  }

  buildSetPasswordEvent(email: string): Observable<BloomreachSetPasswordEvent> {
    return this.languageService.getActive().pipe(
      map((language: string) =>
        createFrom(BloomreachSetPasswordEvent, {
          type: 'rs_password',
          body: {
            time_stamp: this.getCurrentTimestamp(),
            email_id: email,
            web_store_url: this.winRef.nativeWindow.location.origin,
            language,
          },
        }),
      ),
    );
  }

  buildAccountActivationEvent(erpId: string): Observable<BloomreachAccountActivationEvent> {
    return this.languageService.getActive().pipe(
      map((language: string) =>
        createFrom(BloomreachAccountActivationEvent, {
          type: 'rs_registered',
          body: {
            time_stamp: this.getCurrentTimestamp(),
            erp_contact_id: erpId,
          },
        }),
      ),
    );
  }

  buildPdpViewEvent(): Observable<BloomreachPdpViewEvent> {
    return this.eventService.get(NavigationEvent).pipe(
      filter((navigationEvent) => navigationEvent.semanticRoute === 'product'),
      switchMap((navigationEvent: NavigationEvent) =>
        this.productService.get(navigationEvent.context.id).pipe(
          filter((product) => Boolean(product)),
          switchMap((product) =>
            this.loadPrices(product.code).pipe(
              take(1),
              map((prices) => ({ ...product, ...prices })),
            ),
          ),
          withLatestFrom(this.allSiteSettingsService.getCurrentChannelData()),
          switchMap(([product, channel]: [Product, CurrentSiteSettings]) =>
            this.productAvailabilityService.getAvailability(product.code).pipe(
              take(1),
              map((stock) =>
                createFrom(BloomreachPdpViewEvent, {
                  type: 'pdp_view',
                  body: {
                    product_id: product.code,
                    pdp_url: this.winRef.nativeWindow.location.href,
                    pdp_brand: product.distManufacturer.name,
                    pdp_mpn: product.typeName,
                    pdp_product_title: product.typeName + '-' + product.name + ',' + product.distManufacturer.name,
                    pdp_price: this.priceService.getPriceForQuantity(
                      product.volumePricesMap,
                      product.orderQuantityMinimum,
                      channel.channel,
                    ),
                    pdp_currency: product.price.currencyIso,
                    pdp_image_url: product.images[2]["url"],
                    pdp_stock:
                      channel.country === 'CH' ? stock.stockLevelPickup?.[0]?.stockLevel : stock?.stockLevelTotal,
                  },
                }),
              ),
            ),
          ),
        ),
      ),
    );
  }

  private loadPrices(productCode: string): Observable<Prices> {
    return this.priceService.getPrices(productCode);
  }

  private getCurrentTimestamp(): string {
    const date = new Date();
    const enUSFormatter = new Intl.DateTimeFormat('nl-BE');

    return (
      enUSFormatter.format(date) +
      ' ' +
      date.toLocaleTimeString(undefined, {
        hour: '2-digit',
        minute: '2-digit',
      })
    );
  }
}
