/* eslint-disable @typescript-eslint/naming-convention */
import { Injectable, OnDestroy, Type, signal } from '@angular/core';
import {
  Category,
  CmsService,
  createFrom,
  CxEvent,
  EventService,
  LoginEvent as SpartacusLoginEvent,
  Page,
  PageType,
  Product,
  User,
  WindowRef,
  PageMetaService,
  PageMeta,
  ProductService,
} from '@spartacus/core';
import { Cart, CartAddEntrySuccessEvent, CartRemoveEntrySuccessEvent, OrderEntry } from '@spartacus/cart/base/root';
import { NavigationEvent } from '@spartacus/storefront';
import { combineLatest, from, Observable, of, Subject } from 'rxjs';
import {
  distinctUntilChanged,
  distinctUntilKeyChanged,
  filter,
  first,
  map,
  mapTo,
  mergeMap,
  shareReplay,
  switchMap,
  take,
  takeUntil,
  tap,
  toArray,
  withLatestFrom,
} from 'rxjs/operators';
import { isNil, omitBy } from 'lodash-es';

import { DistrelecUserService } from '@services/user.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CategoriesService } from '@services/categories.service';
import { LocalStorageService } from '@services/local-storage.service';
import { PageCategoryService } from '../page-category.service';
import { PriceService } from '@services/price.service';
import { DistrelecBasesitesService } from '@services/basesites.service';

import { PageHelper } from '@helpers/page-helper';
import { DECIMAL_RADIX } from '@helpers/constants';

import { Channel, CurrentSiteSettings } from '@model/site-settings.model';
import { CategoryPageData } from '@model/category.model';
import { AnalyticsCustomerType, EventUserDetails } from '../model/event-user-details';
import { ProductSuggestion } from '@model/product-suggestions.model';

import { PageViewEvent } from '@features/tracking/events/ga4/page-view-event';
import { LoginEvent } from '@features/tracking/events/ga4/login-event';
import { RegistrationEvent } from '@features/tracking/events/registration-event';
import { GtmEventCollectorService } from '@features/tracking/google-tag-manager/gtm-event-collector.service';
import { SiteSearchNoResultsEvent } from '@features/tracking/events/site-search-no-results-event';
import { PromotionClickEvent } from '@features/tracking/events/ga4/promotion-click-event';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { CheckoutEvent } from '@features/tracking/events/checkout-event';
import { EventProductGA4 } from '@features/tracking/model/event-product';
import { Ga4ViewItemListEvent } from '@features/tracking/events/ga4/ga4-view-item-list-event';
import { Ga4ViewItemEvent } from '@features/tracking/events/ga4/ga4-view-item-event';
import { GaListID, GaListName, GaListType } from '@features/tracking/model/event-ga-list-types';
import { ViewItemListEvent } from '@features/tracking/events/view-item-list-event';
import { ViewItemEvent } from '@features/tracking/events/view-item-event';
import { ItemListEntity, RouteEventParams } from '@features/tracking/model/generic-event-types';
import { Ga4CheckoutEvent } from '@features/tracking/events/ga4/ga4-checkout-event';
import { Ga4PurchaseEvent, Ga4PurchaseEventType } from '@features/tracking/events/ga4/ga4-purchase-event';
import { PurchaseEvent } from '@features/tracking/events/purchase-event';
import { Breadcrumb } from '@model/breadcrumb.model';
import { Ga4CartEvent } from '@features/tracking/events/ga4/ga4-cart-event';
import { Ga4CartEventType } from '@features/tracking/model/event-ga-cart-types';
import { CartViewEvent } from '@features/tracking/events/view-event';
import { AddToShoppingListEvent } from '@features/tracking/events/add-to-shopping-list-event';
import { Ga4AddToWishlistEvent } from '@features/tracking/events/ga4/ga4-add-to-wishlist-event';
import { Prices } from '@model/price.model';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';
import { Ga4SelectItem } from '@features/tracking/events/ga4/ga4-select-item-event';
import { SearchNavigationEvent } from '@features/tracking/events/search-navigation-event';
import { RSMigrationEvent } from '@features/tracking/events/ga4/RS-migration-event';
import { SetPasswordEvent } from '@features/tracking/events/set-password-event';
import { TermsAndConditionsPopupAcceptedEvent } from '@features/tracking/events/terms-conditions-popup-accepted-event';
import { SearchSuggestionEvent } from '@features/tracking/events/search-suggestion-event';
import { ProductImageClickEvent } from '../events/ga4/product-image-click-event';
import { CompareProductEvent } from '../events/ga4/compare-product-event';
import { DistCartService } from '@services/cart.service';
import { Ga4PrintPageEvent, PrintPageEvent } from '../events/print-page-event';
import { DownloadPDFEvent, Ga4DownloadPDFEvent } from '../events/download-pdf-event';
import { Ga4RegistrationStartEvent } from '../events/ga4/ga4-registration-start-event';
import { RegistrationStartEvent } from '../events/registration-start-event';
import { HomepageInteractionEvent } from '../events/homepage-interaction-event';
import { Ga4HomepageInteractionEvent } from '../events/ga4/ga4-homepage-interaction-event';
import { ICustomProduct } from '@model/product.model';
import { LoginService } from '@services/login.service';
import { HeaderInteractionEvent } from '../events/header-interaction-event';
import { Ga4HeaderInteractionEvent } from '../events/ga4/ga4-header-interaction-event';
import { Ga4Error404Event, Ga4Error404EventType } from '../events/ga4/ga4-error-404-event';
import { Error404Event } from '../events/ga4/error-404-event';
import { Ga4ViewPdpReviewsEvent } from '../events/ga4/ga4-pdp-review-event';
import { ViewPdpReviewsEvent } from '../events/ga4/pdp-review-event';
import { CheckoutGA4Type } from '../model/checkout-type';
import { HotBarCompareEvent } from '../events/hotbar-compare-event';
import { toObservable } from '@angular/core/rxjs-interop';
import { EventPageDetails } from '../model/event-page-details';
import { SearchEvent } from '../events/search-event';
import { Ga4SearchSuggestionEvent } from '../events/ga4/ga4-search-suggestion-event';
import { Ga4SearchEvent } from '../events/ga4/ga4-search-event';
import { FooterInteractionEvent, Ga4FooterInteractionEvent } from '../events/ga4/ga4-footer-interaction-event';

const stripHtmlTags = (input: string): string => {
  if (typeof input !== 'string') {
    return '';
  }

  return input.replace(/<([^>]+)>/gi, '');
};
@Injectable({
  providedIn: 'root',
})
export class GtmEventBuilder implements OnDestroy {
  siteName: string;
  channel: Channel;
  buildOrderConfirmationEvent;
  buildCheckoutEvent;
  pageLanguage;

  currentUrl: string = null;
  previousUrl: string = null;

  private pageMeta$: Observable<PageMeta> = this.metaService.getMeta().pipe(distinctUntilKeyChanged('title'));
  private pageCategory = signal('');
  private pageCategory$ = toObservable(this.pageCategory);

  private cmsPageLanguageEN = this.cmsService.getCurrentPage().pipe(
    filter<Page>(Boolean),
    map((page) => page?.properties?.languageEN),
    distinctUntilChanged(),
  );

  private user$: Observable<EventUserDetails> = combineLatest([
    this.userService.getUserDetails(),
    this.siteSettingService.getCurrentChannelData(),
    this.cmsPageLanguageEN,
    this.cartService.getCartDataFromStore(),
  ]).pipe(
    tap(([_, settings]) => (this.channel = settings.channel)),
    map(([user, settings, language, cart]) => {
      this.pageLanguage = language;
      return this.getUser(user, settings, cart);
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  private page$: Observable<EventPageDetails> = combineLatest([
    this.pageCategory$,
    this.pageMeta$,
    this.siteSettingService.getCurrentChannelData(),
  ]).pipe(
    map(([category, meta, settings]) => {
      return {
        document_title: meta.title,
        url: this.windowRef.location.href,
        category: category,
        market: settings?.country,
      };
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  private readonly destroyed$: Subject<void> = new Subject<void>();

  constructor(
    private distBaseSiteService: DistrelecBasesitesService,
    private eventService: EventService,
    private eventCollector: GtmEventCollectorService,
    private windowRef: WindowRef,
    private userService: DistrelecUserService,
    private siteSettingService: AllsitesettingsService,
    private pageCategoryService: PageCategoryService,
    private cmsService: CmsService,
    private priceService: PriceService,
    private productCategoryService: CategoriesService,
    private localStorage: LocalStorageService,
    private pageHelper: PageHelper,
    private cartService: DistCartService,
    private loginService: LoginService,
    private metaService: PageMetaService,
    private productService: ProductService,
  ) {
    this.distBaseSiteService
      .getBaseSiteData()
      .pipe(
        take(1),
        tap((data) => (this.siteName = data.name)),
        takeUntil(this.destroyed$),
      )
      .subscribe();
  }

  private static parseUrlToSearchParams(url: string) {
    const startIndex = url.indexOf('?');
    if (startIndex === -1) {
      return new URLSearchParams();
    }
    const search = url.slice(url.indexOf('?'), url.length);
    return new URLSearchParams(search);
  }

  private static urlHasPromotionParameters(url: string): boolean {
    const parameters = GtmEventBuilder.parseUrlToSearchParams(url);
    return parameters.has('int_cid');
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  registerEvents() {
    this.registerPageViewEvent();
    this.registerLoginAndRegistrationEvent();
    this.registerCartEvents();
    this.registerPromotionEvent();
    this.registerGoogleAnalytics4Events();
    this.registerRSEvents();
    this.registerGa4SearchEvents();
  }

  buildSiteSearchNoResultsEvent(searchTerm: string): Observable<SiteSearchNoResultsEvent> {
    return this.cmsService.getCurrentPage().pipe(
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),

      map(([_, user, page]: [Page, EventUserDetails, EventPageDetails]) =>
        createFrom(SiteSearchNoResultsEvent, {
          searchTerm,
          user,
          page: this.getPage(page, this.previousUrl),
        } as SiteSearchNoResultsEvent),
      ),
    );
  }

  registerGoogleAnalytics4Events(): void {
    this.eventService.register(Ga4CheckoutEvent, this.buildG4CheckoutEvent());
    this.eventService.register(Ga4PurchaseEvent, this.buildG4PurchaseEvent());
    this.eventService.register(Ga4ViewItemListEvent, this.buildGA4ViewItemListEvent());
    this.eventService.register(Ga4ViewItemEvent, this.buildGA4ViewItemEvent());
    this.eventService.register(Ga4AddToWishlistEvent, this.buildGA4AddToWishlistEvent());
    this.eventService.register(Ga4SelectItem, this.buildGA4SelectItemEvent());
    this.eventService.register(Ga4Error404Event, this.buildGa4Error404Event());
    this.eventService.register(Ga4PrintPageEvent, this.buildGA4PrintPageEvent());
    this.eventService.register(Ga4DownloadPDFEvent, this.buildGA4DownloadPDFEvent());
    this.eventService.register(Ga4HomepageInteractionEvent, this.buildGa4HomepageInteractionEvent());
    this.eventService.register(Ga4RegistrationStartEvent, this.buildGa4RegistrationStartEvent());
    this.eventService.register(Ga4HeaderInteractionEvent, this.buildGa4HeaderInteractionEvent());
    this.eventService.register(Ga4ViewPdpReviewsEvent, this.buildGAViewPDPReviewsEvent());
    this.eventService.register(Ga4FooterInteractionEvent, this.buildGA4FooterInteractionEvent());
  }

  buildGA4FooterInteractionEvent(): Observable<Ga4FooterInteractionEvent> {
    return this.eventService.get(FooterInteractionEvent).pipe(
      map((event: FooterInteractionEvent) => {
        return createFrom(Ga4FooterInteractionEvent, {
          event: 'footer_interaction',
          menu_name: event.context.menu_name,
          menu_item: event.context.menu_item,
        } as Ga4FooterInteractionEvent);
      }),
    ) as Observable<Ga4FooterInteractionEvent>;
  }

  buildGA4ViewItemListEvent(): Observable<Ga4ViewItemListEvent | null> {
    return this.eventService.get(ViewItemListEvent).pipe(
      filter((event) => !!event.products.length),
      tap(() => this.resetEvents({ ecommerce: null })),
      withLatestFrom(
        this.getUserObservable(),
        this.getPageObservable(),
        this.productCategoryService.getCurrentCategoryData(),
      ),
      map(
        ([event, user, page, categoryData]: [
          ViewItemListEvent,
          EventUserDetails,
          EventPageDetails,
          CategoryPageData,
        ]) => {
          const { products, listType } = event;
          const { id: itemListId, name: itemListName } = this.getGaListType(listType);

          const category = event.page
            ? this.pageCategoryService.getPageCategory(event.page, null, categoryData)
            : page.category;

          if (itemListId) {
            const propsFromRoute = this.getRouteEventPropsForList(itemListId as GaListID);
            let currency = '0';

            if (listType === ItemListEntity.SUGGESTED_SEARCH) {
              currency = (products[0] as ProductSuggestion).priceData.currency;
            } else {
              currency = (products[0] as Product | ICustomProduct).price?.currencyIso;
            }

            return createFrom(Ga4ViewItemListEvent, {
              user,
              page: { ...this.getPage(page, this.previousUrl), category },
              ecommerce: {
                ...propsFromRoute,
                item_list_id: itemListId,
                item_list_name: itemListName,
                currency: this.priceService.getCurrencyFromPrice(products[0].volumePricesMap),
                items: products.map((product, index: number) => {
                  const itemBrand = product.distManufacturer?.name;
                  const salesStatus = product.salesStatus;
                  const modifiedParams = this.addCategoryLevelParams(product);

                  const params: EventProductGA4 = {
                    item_id: product.code,
                    item_name: stripHtmlTags(product.name),
                    affiliation: this.siteName,
                    index,
                    ...(itemBrand ? { item_brand: itemBrand } : {}),
                    item_list_id: itemListId,
                    item_list_name: itemListName,
                    ...(salesStatus ? { location_id: salesStatus } : {}),
                    quantity: 1,
                    item_moq: product.orderQuantityMinimum,
                  };

                  if (listType === ItemListEntity.SUGGESTED_SEARCH) {
                    params.price = parseFloat((product as ProductSuggestion).priceData.price);
                    params.quantity = 1;
                    params.item_moq = parseInt(<string>(product as ProductSuggestion).itemMin, DECIMAL_RADIX);
                  } else {
                    params.price = this.priceService.getPriceForQuantity(
                      product.volumePricesMap,
                      product.orderQuantityMinimum,
                      'B2B',
                    );
                  }

                  const mergeParams: EventProductGA4 = { ...params, ...modifiedParams };

                  return mergeParams;
                }),
                ...(event.totalResults && { item_list_total_results: event.totalResults }),
              },
              filterPosition: 'sidebar',
              viewPreference: 'compact',
            } as Ga4ViewItemListEvent);
          } else {
            return null;
          }
        },
      ),
    );
  }

  buildGA4ViewItemEvent(): Observable<Ga4ViewItemEvent> {
    return this.eventService.get(ViewItemEvent).pipe(
      tap(() => this.resetEvents({ ecommerce: null })),
      withLatestFrom(this.user$, this.page$),
      map(([event, user, page]: [ViewItemEvent, EventUserDetails, EventPageDetails]) => {
        const product = event.product;

        return createFrom(Ga4ViewItemEvent, {
          user,
          page: this.getPage(page, this.previousUrl),
          ecommerce: {
            ...this.getRouteEventPropsForProduct(),
            currency: this.priceService.getCurrencyFromPrice(product.volumePricesMap),
            value: this.priceService.getPriceForQuantity(product.volumePricesMap, product.orderQuantityMinimum, 'B2B'),
            items: [this.createGA4ItemFromProduct(product, event.itemListUrlParam, 0, 1)],
          },
        } as Ga4ViewItemEvent);
      }),
    );
  }

  getGaListType(itemListEntity: string): GaListType {
    let itemListId: string;
    let itemListName: string;

    switch (itemListEntity) {
      case ItemListEntity.SEARCH:
        itemListId = GaListID.SEARCH;
        itemListName = GaListName.SEARCH;
        break;
      case ItemListEntity.CATEGORY:
        itemListId = GaListID.CATEGORY;
        itemListName = GaListName.CATEGORY;
        break;
      case ItemListEntity.MANUFACTURER:
        itemListId = GaListID.MANUFACTURER;
        itemListName = GaListName.MANUFACTURER;
        break;
      case ItemListEntity.PFP:
        itemListId = GaListID.PFP;
        itemListName = GaListName.PFP;
        break;
      case ItemListEntity.PDP:
        itemListId = GaListID.PDP;
        itemListName = GaListName.PDP;
        break;
      case ItemListEntity.ALTERNATIVE:
        itemListId = GaListID.ALTERNATIVE;
        itemListName = GaListName.ALTERNATIVE;
        break;
      case ItemListEntity.ALTERNATIVE_UPPER:
        itemListId = GaListID.ALTERNATIVE_UPPER;
        itemListName = GaListName.ALTERNATIVE_UPPER;
        break;
      case ItemListEntity.ACCESSORIES:
        itemListId = GaListID.ACCESSORIES;
        itemListName = GaListName.ACCESSORIES;
        break;
      case ItemListEntity.COMPATIBLE:
        itemListId = GaListID.COMPATIBLE;
        itemListName = GaListName.COMPATIBLE;
        break;
      case ItemListEntity.OPTIONAL:
        itemListId = GaListID.OPTIONAL;
        itemListName = GaListName.OPTIONAL;
        break;
      case ItemListEntity.MANDATORY:
        itemListId = GaListID.MANDATORY;
        itemListName = GaListName.MANDATORY;
        break;
      case ItemListEntity.SHOPPING:
        itemListId = GaListID.SHOPPING;
        itemListName = GaListName.SHOPPING;
        break;
      case ItemListEntity.COMPARE:
        itemListId = GaListID.COMPARE;
        itemListName = GaListName.COMPARE;
        break;
      case ItemListEntity.SUGGESTED_SEARCH:
        itemListId = GaListID.SUGGESTED_SEARCH;
        itemListName = GaListName.SUGGESTED_SEARCH;
        break;
      case ItemListEntity.FEATURED:
        itemListId = GaListID.FEATURED;
        itemListName = GaListName.FEATURED;
        break;
      case ItemListEntity.CART_SEARCH:
        itemListId = GaListID.CART_SEARCH;
        itemListName = GaListName.CART_SEARCH;
        break;
      case ItemListEntity.CART_RELATED:
        itemListId = GaListID.CART_RELATED;
        itemListName = GaListName.CART_RELATED;
        break;
      case ItemListEntity.CART:
        itemListId = GaListID.CART;
        itemListName = GaListName.CART;
        break;
      case ItemListEntity.FEEDBACK_CAMPAIGN:
        itemListId = GaListID.FEEDBACK_CAMPAIGN;
        itemListName = GaListName.FEEDBACK_CAMPAIGN;
        break;
      case ItemListEntity.STICKY_CART:
        itemListId = GaListID.STICKY_CART;
        itemListName = GaListName.STICKY_CART;
        break;
      case ItemListEntity.BACKORDER:
        itemListId = GaListID.BACKORDER;
        itemListName = GaListName.BACKORDER;
        break;
      case ItemListEntity.BOM:
        itemListId = GaListID.BOM;
        itemListName = GaListName.BOM;
        break;
      case ItemListEntity.QUICK_ORDER:
        itemListId = GaListID.QUICK_ORDER;
        itemListName = GaListName.QUICK_ORDER;
        break;
      case ItemListEntity.ORDER_APPROVAL:
        itemListId = GaListID.ORDER_APPROVAL;
        itemListName = GaListName.ORDER_APPROVAL;
        break;
      case ItemListEntity.ORDER_DETAILS:
        itemListId = GaListID.ORDER_DETAILS;
        itemListName = GaListName.ORDER_DETAILS;
        break;
      default:
        itemListId = null;
        itemListName = null;
        break;
    }

    return {
      id: itemListId,
      name: itemListName,
    };
  }

  buildG4CheckoutEvent(): Observable<Ga4CheckoutEvent> {
    return this.eventService.get(CheckoutEvent).pipe(
      tap(() => this.resetEvents({ ecommerce: null })),
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([event, user, page]: [CheckoutEvent, EventUserDetails, EventPageDetails]) => {
        const { cart, checkoutEventType, isFastCheckout, paymentType } = event;
        const cartEntries = cart.entries;
        const shipping_tier = checkoutEventType === CheckoutGA4EventType.ADD_SHIPPING_INFO && cart.deliveryMode.code;
        const items: EventProductGA4[] = cartEntries.map(
          (entry, index: number): EventProductGA4 => this.createGA4ItemFromOrderEntry(entry, index),
        );

        return createFrom(Ga4CheckoutEvent, {
          event: checkoutEventType,
          page: this.getPage(page, this.previousUrl),
          ecommerce: {
            currency: cart.subTotal.currencyIso,
            ...(paymentType ? { payment_type: paymentType } : {}),
            ...(shipping_tier ? { shipping_tier } : {}),
            value: cart.subTotal.value,
            items,
          },
          user,
          checkout_type: this.getCheckoutType(isFastCheckout),
        }) as Ga4CheckoutEvent;
      }),
    );
  }

  registerGa4SearchEvents(): void {
    this.eventService.register(Ga4SearchSuggestionEvent, this.buildGa4SearchSuggestionEvent());
    this.eventService.register(Ga4SearchEvent, this.buildGa4SearchEvent());
  }

  buildGa4SearchSuggestionEvent(): Observable<Ga4SearchSuggestionEvent> {
    return this.eventService.get(SearchSuggestionEvent).pipe(
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([event, user, page]: [SearchSuggestionEvent, EventUserDetails, EventPageDetails]) =>
        createFrom(Ga4SearchSuggestionEvent, {
          event: 'suggestedSearch',
          ...event,
          user,
          page: this.getPage(page, this.previousUrl),
        }),
      ),
    );
  }

  buildGa4SearchEvent(): Observable<Ga4SearchEvent> {
    return this.eventService.get(SearchEvent).pipe(
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([event, user, page]: [SearchEvent, EventUserDetails, EventPageDetails]) =>
        createFrom(Ga4SearchEvent, {
          event: 'search',
          ...event,
          user,
          page: this.getPage(page, this.previousUrl),
        }),
      ),
    );
  }

  buildGa4RegistrationStartEvent(): Observable<Ga4RegistrationStartEvent> {
    return this.eventService.get(RegistrationStartEvent).pipe(
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([event, user, page]: [RegistrationStartEvent, EventUserDetails, EventPageDetails]) =>
        createFrom(Ga4RegistrationStartEvent, {
          event: event.type,
          user,
          page: this.getPage(page, this.previousUrl),
        }),
      ),
    );
  }

  buildG4PurchaseEvent(): Observable<Ga4PurchaseEvent> {
    return this.eventService.get(PurchaseEvent).pipe(
      tap(() => this.resetEvents({ ecommerce: null })),
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([event, user, page]: [PurchaseEvent, EventUserDetails, EventPageDetails]) => {
        const { orderData, isFastCheckout } = event;
        const items: EventProductGA4[] = orderData.entries.map(
          (entry: OrderEntry, index: number): EventProductGA4 => this.createGA4ItemFromOrderEntry(entry, index),
        );

        return createFrom(Ga4PurchaseEvent, {
          event: Ga4PurchaseEventType.PURCHASE,
          page: this.getPage(page, this.previousUrl),
          ecommerce: {
            currency: orderData.totalPrice?.currencyIso,
            transaction_id: orderData.code,
            value: orderData.totalPrice?.value,
            shipping: orderData.deliveryCost?.value,
            tax: orderData.totalTax?.value,
            items,
          },
          user: {
            ...user,
            email: event.orderData?.billingAddress?.email,
            guest_checkout: event.orderData?.guestCustomer,
          },
          checkout_type: this.getCheckoutType(isFastCheckout),
        }) as Ga4PurchaseEvent;
      }),
    );
  }

  buildGa4HomepageInteractionEvent(): Observable<Ga4HomepageInteractionEvent> {
    return this.eventService.get(HomepageInteractionEvent).pipe(
      withLatestFrom(this.user$, this.page$),
      map(([event, user, page]: [HomepageInteractionEvent, EventUserDetails, EventPageDetails]) =>
        createFrom(Ga4HomepageInteractionEvent, {
          event: 'homepage_content_interaction',
          interaction_type: event.type,
          user,
          page: this.getPage(page, this.previousUrl),
        }),
      ),
    );
  }

  registerRSEvents(): void {
    this.eventService.register(RSMigrationEvent, this.buildRSSetPasswordEvent());
    this.eventService.register(RSMigrationEvent, this.buildRSTermsAndConditionsPopupAcceptedEvent());
  }

  buildRSSetPasswordEvent(): Observable<RSMigrationEvent> {
    return this.eventService.get(SetPasswordEvent).pipe(
      withLatestFrom(this.page$),
      map(
        ([event, page]: [SetPasswordEvent, EventPageDetails]) =>
          createFrom(RSMigrationEvent, {
            event: event.event,
            page: this.getPage(page, this.previousUrl),
          }) as RSMigrationEvent,
      ),
    );
  }

  buildRSTermsAndConditionsPopupAcceptedEvent(): Observable<RSMigrationEvent> {
    return this.eventService.get(TermsAndConditionsPopupAcceptedEvent).pipe(
      withLatestFrom(this.user$, this.page$),
      map(([event, user, page]: [TermsAndConditionsPopupAcceptedEvent, EventUserDetails, EventPageDetails]) => {
        return createFrom(RSMigrationEvent, {
          event: event.event,
          page: this.getPage(page, this.previousUrl),
          user,
        }) as RSMigrationEvent;
      }),
    );
  }

  buildImageClickEvent(code: string): Observable<ProductImageClickEvent> {
    return this.user$.pipe(
      withLatestFrom(this.page$),
      map(([user, page]) => {
        return createFrom(ProductImageClickEvent, {
          productImage: code,
          page: this.getPage(page, this.previousUrl),
          user,
          event: 'imageClick',
        });
      }),
    );
  }

  buildCompareListEvent(compareProduct): Observable<CompareProductEvent> {
    return this.user$.pipe(
      tap(() => this.resetEvents({ ecommerce: null })),
      mergeMap((user) =>
        this.loadPrices(compareProduct.code).pipe(
          take(1),
          map((prices) => ({
            user,
            product: { ...compareProduct, ...prices },
          })),
        ),
      ),
      withLatestFrom(this.page$),
      map(([{ user, product }, page]: [{ user: EventUserDetails; product: ICustomProduct }, EventPageDetails]) => {
        const listType = ItemListEntity.COMPARE;

        return createFrom(CompareProductEvent, {
          compareProduct: compareProduct.code,
          user,
          page: this.getPage(page, this.previousUrl),
          event: 'compare_products',
          ecommerce: {
            item_list_id: this.getGaListType(listType).id,
            item_list_name: this.getGaListType(listType).name,
            currency: this.priceService.getCurrencyFromPrice(product.volumePricesMap),
            items: [this.createGA4ItemFromProduct(product, listType, 0, 1)],
          },
        });
      }),
    );
  }

  buildHotBarCompareEvent(): Observable<HotBarCompareEvent> {
    return this.user$.pipe(
      withLatestFrom(this.page$),
      map(([user, page]) =>
        createFrom(HotBarCompareEvent, {
          user,
          page: this.getPage(page, this.previousUrl),
          event: 'hotbar_compare',
        }),
      ),
    );
  }

  buildHotBarClearAllEvent(): Observable<HotBarCompareEvent> {
    return this.user$.pipe(
      withLatestFrom(this.page$),
      map(([user, page]) =>
        createFrom(HotBarCompareEvent, {
          user,
          page: this.getPage(page, this.previousUrl),
          event: 'hotbar_clear_all',
        }),
      ),
    );
  }

  getPageObservable(): Observable<EventPageDetails> {
    return this.page$;
  }

  getUserObservable(): Observable<EventUserDetails> {
    return this.user$;
  }

  private buildGa4Error404Event(): Observable<Ga4Error404Event> {
    return this.eventService.get(Error404Event).pipe(
      switchMap((event) =>
        combineLatest([of(event), this.siteSettingService.getCurrentChannelData(), this.getPageObservable()]),
      ),
      map(
        ([event, settings, page]) =>
          createFrom(Ga4Error404Event, {
            page: this.getPage(page, this.previousUrl),
            event: Ga4Error404EventType.ERROR_404,
            user: this.getUser(event.user, settings),
          }) as Ga4Error404Event,
      ),
    );
  }

  private resetEvents(...events: unknown[]): void {
    if (this.windowRef.isBrowser()) {
      events.forEach((event) => {
        this.eventCollector.pushEvent({}, this.windowRef.nativeWindow, event);
      });
    }
  }

  private getUser(user: User, settings: CurrentSiteSettings, cart?: Cart): EventUserDetails {
    const customerType = user?.customerType ?? settings.channel;
    const email =
      cart?.user?.type?.toLowerCase() === 'guest' ? cart?.user?.uid.split('|')[1] : user?.contactAddress?.email;
    return {
      logged_in: user !== null,
      language: this.pageLanguage,
      ...(user?.encryptedUserID && { user_id: user?.encryptedUserID }),
      customer_type: AnalyticsCustomerType[customerType],
      ...(email && { email }),
      ...(cart?.user?.type && { guest_checkout: cart?.user?.type?.toLowerCase() === 'guest' }),
      mg: !!user?.rsCustomer,
    };
  }

  private getPage(page: EventPageDetails, previousUrl: string): EventPageDetails {
    return {
      ...page,
      ...(previousUrl && { page_referrer: `${this.windowRef.nativeWindow.location.origin}${this.previousUrl}` }),
    };
  }

  private waitForSiteSettings = (event) =>
    this.siteSettingService.getCurrentChannelData().pipe(filter<CurrentSiteSettings>(Boolean), first(), mapTo(event));

  private registerPageViewEvent() {
    this.eventService.register(PageViewEvent, this.buildPageViewEvent());
  }

  private buildPageViewEvent(): Observable<PageViewEvent> {
    return this.eventService.get(NavigationEvent).pipe(
      // Filter out default Search page events as they are dispatched manually from ProductListComponentService
      filter((event) => event.semanticRoute !== 'search' || (event as SearchNavigationEvent).force),

      // Wait for the site settings to be loaded
      switchMap(this.waitForSiteSettings),

      // Load CMS Page from context
      switchMap((event) =>
        this.cmsService.getPageState(event.context).pipe(
          map((page) => ({ event, page })),
          distinctUntilChanged((prev, curr) => JSON.stringify(prev.event.params) === JSON.stringify(curr.event.params)),
        ),
      ),

      // Add category data or product metadata if required
      switchMap(({ event, page }) => {
        if (event.context.type === PageType.CATEGORY_PAGE) {
          return this.productCategoryService.getCurrentCategoryData().pipe(
            filter<CategoryPageData>(
              (category) => category && category.sourceCategory.code === event.params?.categoryCode,
            ),
            distinctUntilChanged((prev, curr) => prev.sourceCategory.code === curr.sourceCategory.code),
            map((category) => ({ event, page, category, productMeta: undefined })),
          );
        } else if (event.context.type === PageType.PRODUCT_PAGE) {
          return this.productService
            .get(event.context.id)
            .pipe(map((product) => ({ event, page, category: undefined, productMeta: product.metaData })));
        }
        return of({ event, page, category: undefined, productMeta: undefined });
      }),
      tap(({ event, page, category }) =>
        this.pageCategory.set(this.pageCategoryService.getPageCategory(page, event.params, category)),
      ),
      withLatestFrom(this.userService.getUserDetails(), this.siteSettingService.getCurrentChannelData()),
      map(([{ event, page, category, productMeta }, user, siteSettings]) => {
        this.previousUrl = this.currentUrl;
        this.currentUrl = this.windowRef.nativeWindow.location.search
          ? this.windowRef.nativeWindow.location.pathname + this.windowRef.nativeWindow.location.search
          : this.windowRef.nativeWindow.location.pathname;

        const pageObject = {
          document_title: page?.properties?.seo?.metaTitle ?? productMeta.metaTitle,
          url: this.windowRef.nativeWindow.location.href,
          category: this.pageCategoryService.getPageCategory(page, event.params, category),
          market: siteSettings?.country,
        };

        return createFrom(PageViewEvent, {
          user: this.getUser(user, siteSettings),
          page: this.getPage(pageObject, this.previousUrl),
        } as PageViewEvent);
      }),
    );
  }

  private registerLoginAndRegistrationEvent() {
    this.eventService.register(LoginEvent, this.buildLoginAndRegistrationEvent());
  }

  private buildLoginAndRegistrationEvent(): Observable<LoginEvent> {
    return this.eventService.get(SpartacusLoginEvent).pipe(
      switchMap(() => this.loginService.isLoginProgress$),

      filter((isLoginProgress) => !isLoginProgress),

      withLatestFrom(this.getUserObservable(), this.getPageObservable()),

      filter(([_, user]) => !!user.user_id),

      map(([_, user, page]) =>
        createFrom(this.localStorage.getItem('registration-login') ? RegistrationEvent : LoginEvent, {
          user,
          page: {
            ...page,
            url: this.windowRef.nativeWindow.location.href,
            page_referrer: `${this.windowRef.nativeWindow.location.origin}${this.previousUrl}`,
          },
        } as LoginEvent | RegistrationEvent),
      ),
    );
  }

  private buildGa4HeaderInteractionEvent(): Observable<Ga4HeaderInteractionEvent> {
    return this.eventService.get(HeaderInteractionEvent).pipe(
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([event, user, page]) =>
        createFrom(Ga4HeaderInteractionEvent, {
          event: event.type,
          user,
          page: this.getPage(page, this.previousUrl),
        } as Ga4HeaderInteractionEvent),
      ),
    );
  }

  private registerCartEvents() {
    this.eventService.register(Ga4CartEvent, this.buildAddToCartEvent());
    this.eventService.register(Ga4CartEvent, this.buildRemoveFromCartEvent());
    this.eventService.register(Ga4CartEvent, this.buildViewCartEvent());
  }

  private buildAddToCartEvent(): Observable<Ga4CartEvent> {
    return this.eventService.get(CartAddEntrySuccessEvent).pipe(
      // filter out events with a cartId as they don't contain price information
      filter((event) => !event.cartId),

      tap(() => this.resetEvents({ ecommerce: null })),

      withLatestFrom(this.getUserObservable(), this.getPageObservable()),

      map(([event, user, page]: [CartAddEntrySuccessEvent, EventUserDetails, EventPageDetails]) => {
        const entry = event.entry;
        const quantity = event.quantity;

        return createFrom(Ga4CartEvent, {
          event: Ga4CartEventType.ADD_TO_CART,
          ecommerce: {
            currency: entry.basePrice.currencyIso,
            value: entry.totalPrice.value * quantity,
            items: [this.createGA4ItemFromOrderEntry(entry, 0, quantity)],
          },
          user,
          page: this.getPage(page, this.previousUrl),
        } as Ga4CartEvent);
      }),
    );
  }

  private buildRemoveFromCartEvent(): Observable<Ga4CartEvent> {
    return this.eventService.get(CartRemoveEntrySuccessEvent).pipe(
      tap(() => this.resetEvents({ ecommerce: null })),
      distinctUntilKeyChanged('entry'),
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),

      map(([event, user, page]: [CartRemoveEntrySuccessEvent, EventUserDetails, EventPageDetails]) => {
        const entry = event.entry;

        return createFrom(Ga4CartEvent, {
          event: Ga4CartEventType.REMOVE_FROM_CART,
          ecommerce: {
            currency: entry.basePrice.currencyIso,
            value: entry.totalPrice.value,
            items: [this.createGA4ItemFromOrderEntry(entry, 0)],
          },
          user,
          page: this.getPage(page, this.previousUrl),
        } as Ga4CartEvent);
      }),
    );
  }

  private buildViewCartEvent(): Observable<Ga4CartEvent> {
    return this.eventService.get(CartViewEvent).pipe(
      tap(() => this.resetEvents({ ecommerce: null })),

      withLatestFrom(this.getUserObservable(), this.getPageObservable()),

      map(([event, user, page]: [CartViewEvent, EventUserDetails, EventPageDetails]) =>
        createFrom(Ga4CartEvent, {
          event: Ga4CartEventType.VIEW_CART,
          ecommerce: {
            currency: event.cart.subTotal?.currencyIso,
            value: event.cart.subTotal?.value,
            items: event.cart.entries.map((entry, index) => this.createGA4ItemFromOrderEntry(entry, index)),
          },
          user,
          page: this.getPage(page, this.previousUrl),
        } as Ga4CartEvent),
      ),
    );
  }

  private buildGA4AddToWishlistEvent(): Observable<Ga4AddToWishlistEvent> {
    return this.eventService.get(AddToShoppingListEvent).pipe(
      tap(() => this.resetEvents({ ecommerce: null })),
      mergeMap((event) =>
        from(event.shoppingListEntries).pipe(
          mergeMap((entry) =>
            this.loadPrices(entry.product.code).pipe(
              take(1),
              map((prices) => ({
                ...entry,
                product: { ...entry.product, ...prices },
                itemListEntity: event.itemListEntity,
              })),
            ),
          ),
          toArray(),
        ),
      ),
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),

      map(([shoppingListItems, user, page]) => {
        const totalPrice = shoppingListItems.reduce((prev, curr) => {
          const qty = curr.desired ?? curr.product.orderQuantityMinimum;
          return prev + this.priceService.getPriceForQuantity(curr.product.volumePricesMap, qty, 'B2B') * qty;
        }, 0);

        return createFrom(Ga4AddToWishlistEvent, {
          event: 'add_to_wishlist',
          ecommerce: {
            currency: this.priceService.getCurrencyFromPrice(shoppingListItems[0].product.volumePricesMap),
            value: totalPrice,
            items: shoppingListItems.map((entry, index) =>
              this.createGA4ItemFromProduct(entry.product, entry.itemListEntity, index, 1),
            ),
          },
          user,
          page: this.getPage(page, this.previousUrl),
        } as Ga4AddToWishlistEvent);
      }),
    );
  }

  private buildGA4PrintPageEvent(): Observable<Ga4PrintPageEvent> {
    return this.eventService.get(PrintPageEvent).pipe(
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([event, user, page]: [PrintPageEvent, EventUserDetails, EventPageDetails]) =>
        createFrom(Ga4PrintPageEvent, {
          event: 'print_page',
          pageType: event.context.pageType,
          user,
          page: this.getPage(page, this.previousUrl),
        } as Ga4PrintPageEvent),
      ),
    );
  }

  private buildGA4DownloadPDFEvent(): Observable<Ga4DownloadPDFEvent> {
    return this.eventService.get(DownloadPDFEvent).pipe(
      tap(() => this.resetEvents({ ecommerce: null })),
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([event, user, page]: [DownloadPDFEvent, EventUserDetails, EventPageDetails]) => {
        const item = this.createGA4ItemFromProduct(event.context.product, event.context.pageType, 0, 1);
        item.currency = this.getCurrencyForSelectItemEvent(event.context.pageType, null, event.context.product);
        return createFrom(Ga4DownloadPDFEvent, {
          event: 'download_PDF',
          user,
          page: this.getPage(page, this.previousUrl),
          pageType: event.context.pageType,
          PDF_type: event.context.PDF_type,
          ecommerce: {
            items: [item],
          },
        } as Ga4DownloadPDFEvent);
      }),
    );
  }

  private buildGA4SelectItemEvent(): Observable<Ga4SelectItem> {
    return this.eventService.get(ProductClickEvent).pipe(
      tap(() => this.resetEvents({ ecommerce: null })),

      withLatestFrom(this.getUserObservable(), this.getPageObservable()),

      map(([event, user, page]: [ProductClickEvent, EventUserDetails, EventPageDetails]) => {
        let items: EventProductGA4[];
        const currency: string = this.getCurrencyForSelectItemEvent(
          event.listType,
          event.entry ?? null,
          event.product ?? null,
        );

        if (event.entry) {
          items = [this.createGA4ItemFromOrderEntry(event.entry, event.index, 1)];
        } else {
          items = [this.createGA4ItemFromProduct(event.product, event.listType, event.index, 1)];
        }

        return createFrom(Ga4SelectItem, {
          event: 'select_item',
          page: this.getPage(page, this.previousUrl),
          ecommerce: {
            item_list_id: this.getGaListType(event.listType).id,
            item_list_name: this.getGaListType(event.listType).name,
            ...(currency ? { currency } : {}),
            items,
          },
          user,
          filterPosition: 'sidebar',
          viewPreference: 'compact',
        } as Ga4SelectItem);
      }),
    );
  }

  private buildGAViewPDPReviewsEvent(): Observable<Ga4ViewPdpReviewsEvent> {
    return this.eventService.get(ViewPdpReviewsEvent).pipe(
      withLatestFrom(this.getUserObservable(), this.getPageObservable()),
      map(([_, user, page]: [ViewPdpReviewsEvent, EventUserDetails, EventPageDetails]) =>
        createFrom(Ga4ViewPdpReviewsEvent, {
          user,
          page: this.getPage(page, this.previousUrl),
        } as Ga4ViewPdpReviewsEvent),
      ),
    );
  }

  private registerPromotionEvent() {
    this.eventService.register(PromotionClickEvent, this.buildPromotionClickEvent());
  }

  private buildPromotionClickEvent(): Observable<PromotionClickEvent> {
    return this.eventService.get(NavigationEvent).pipe(
      filter((event) => !(event instanceof SearchNavigationEvent)),
      filter(({ url }) => GtmEventBuilder.urlHasPromotionParameters(url)),
      tap(() => this.resetEvents({ ecommerce: null })),
      withLatestFrom(this.page$),
      map(([url, page]) => {
        const parameters = GtmEventBuilder.parseUrlToSearchParams(url.url);
        const int_cid = parameters.get('int_cid');

        if (!int_cid) {
          console.error('int_cid parameter is null');
          return null;
        }

        const parts = int_cid.split('_');

        const campaign_date = parts[0];
        const promotion_id = campaign_date + parts[1];
        const promotion_name = parts[1];
        const creative_name = parts[2]?.split('-')[0];
        const creative_slot = parts[3];

        return createFrom(PromotionClickEvent, {
          event: 'select_promotion',
          page: this.getPage(page, this.previousUrl),
          ecommerce: {
            campaign_date,
            promotion_id,
            promotion_name,
            creative_name,
            creative_slot,
            location_id: '0',
            items: [{}],
          },
        } as PromotionClickEvent);
      }),
    );
  }

  private getRouteEventParams(): RouteEventParams {
    const params = new URLSearchParams(this.windowRef.location?.search ?? '');

    const filters = this.getRouteFilters(params);

    const eventParams: RouteEventParams = {
      code: this.getRoutePageCode(),
      searchTerm: params.get('q'),
      pos: params.get('pos'),
      origPos: params.get('origPos'),
      currentPage: params.get('currentPage') ?? '1',
      pageSize: params.get('pageSize') ?? '50',
      origPageSize: params.get('origPageSize'),
      trackQuery: params.get('trackQuery'),
      redirectQuery: params.get('redirectQuery'),
      sort: params.get('sort'),
      filters: filters.length > 0 ? filters : null,
    };

    return omitBy(eventParams, isNil);
  }

  private getRoutePageCode(): string | null {
    if (!(this.pageHelper.isManufacturerDetailPage() || this.pageHelper.isCategoryPage())) {
      return null;
    }

    return this.windowRef.location.pathname.split('/').pop();
  }

  private getRouteFilters(params: URLSearchParams): { name: string; value: string }[] {
    return Array.from(params.entries())
      .filter(([key, value]) => key.startsWith('filter_'))
      .map(([key, value]) => ({ name: key, value }));
  }

  private mapToGA4PropNames(params: RouteEventParams): Partial<Ga4ViewItemListEvent['ecommerce']> {
    return omitBy(
      {
        item_list_code: params.code,
        item_list_page_number: params.currentPage ? parseInt(params.currentPage, DECIMAL_RADIX) : null,
        item_list_page_size: params.pageSize ? parseInt(params.pageSize, DECIMAL_RADIX) : null,
        item_list_sort: params.sort,
        item_list_filters: params.filters?.map(({ name, value }) => ({ filter_name: name, filter_value: value })),
        search_term: params.searchTerm || params.trackQuery,
        redirect_search_term: params.redirectQuery,
      },
      isNil,
    );
  }

  private getRouteEventPropsForList(listId: GaListID) {
    let plpEventProperties: Partial<Ga4ViewItemListEvent['ecommerce']> = {};
    if ([GaListID.SEARCH, GaListID.CATEGORY, GaListID.MANUFACTURER, GaListID.PFP].includes(listId)) {
      plpEventProperties = this.mapToGA4PropNames(this.getRouteEventParams());
    }
    return plpEventProperties;
  }

  private getRouteEventPropsForProduct() {
    const params = this.getRouteEventParams();
    return omitBy(
      {
        search_term: params.searchTerm || params.trackQuery,
        redirect_search_term: params.redirectQuery,
      },
      isNil,
    );
  }

  private createGA4ItemFromOrderEntry(entry: OrderEntry, index: number, quantity?: number): EventProductGA4 {
    const product = entry.product;
    const itemListId = this.getGaListType(entry.addedFrom).id;
    const itemListName = this.getGaListType(entry.addedFrom).name;
    const modifiedParams = this.addCategoryLevelParams(product);

    const params: EventProductGA4 = {
      item_id: product?.code,
      item_name: stripHtmlTags(product?.name),
      affiliation: this.siteName,
      index,
      item_brand: product?.distManufacturer?.name,
      ...(itemListId ? { item_list_id: itemListId } : {}),
      ...(itemListName ? { item_list_name: itemListName } : {}),
      location_id: product?.salesStatus,
      price: entry.basePrice?.value,
      quantity: quantity ?? entry.quantity,
      item_moq: product?.orderQuantityMinimum,
    };

    const mergeParams: EventProductGA4 = { ...params, ...modifiedParams };

    return mergeParams;
  }

  private createGA4ItemFromProduct(
    product: Product | ICustomProduct | ProductSuggestion,
    itemListEntity: ItemListEntity | string,
    index: number,
    quantity?: number,
  ): EventProductGA4 {
    const categories = product.breadcrumbs ?? product.categories;
    const itemListId = this.getGaListType(itemListEntity).id;
    const itemListName = this.getGaListType(itemListEntity).name;
    const itemBrand = product.distManufacturer?.name;
    const salesStatus = product.salesStatus;
    const qty = quantity ?? product.orderQuantityMinimum;
    let moq = product.orderQuantityMinimum;
    let productPrice: number;

    if (product.volumePricesMap) {
      productPrice = this.priceService.getPriceForQuantity(
        product.volumePricesMap,
        product.orderQuantityMinimum,
        'B2B',
      );
    } else if (itemListEntity === ItemListEntity.CART_RELATED) {
      productPrice = (product as Product).price.basePrice;
    } else if (itemListEntity === ItemListEntity.SUGGESTED_SEARCH) {
      productPrice = parseFloat((product as ProductSuggestion).priceData.price);
      moq = parseInt(<string>(product as ProductSuggestion).itemMin, DECIMAL_RADIX);
    }

    const productItemParams: EventProductGA4 = {
      item_id: product.code,
      item_name: stripHtmlTags(product.name),
      affiliation: this.siteName,
      index,
      ...(itemBrand ? { item_brand: itemBrand } : {}),
      ...(itemListId ? { item_list_id: itemListId } : {}),
      ...(itemListName ? { item_list_name: itemListName } : {}),
      ...(salesStatus ? { location_id: salesStatus } : {}),
      ...(productPrice ? { price: productPrice } : {}),
      ...(qty ? { quantity: qty } : {}),
      item_moq: moq,
    };

    categories?.forEach((categoryItem: Breadcrumb | Category) => {
      if (categoryItem.level) {
        const itemCategoryLevel = 'item_category' + (categoryItem.level === 1 ? '' : categoryItem.level);
        productItemParams[itemCategoryLevel] = categoryItem.name;
      }
    });

    return productItemParams;
  }

  private loadPrices(productCode: string): Observable<Prices> {
    return this.priceService.getPrices(productCode);
  }

  private getCurrencyForSelectItemEvent(
    listType: ItemListEntity,
    entry: OrderEntry,
    product: Product | ProductSuggestion | ICustomProduct,
  ): string {
    if (entry) {
      return entry.basePrice.currencyIso;
    } else {
      if (product.volumePricesMap) {
        return this.priceService.getCurrencyFromPrice(product.volumePricesMap);
      } else if (listType === ItemListEntity.CART_RELATED) {
        return (product as Product).price.currencyIso;
      } else if (listType === ItemListEntity.SUGGESTED_SEARCH) {
        return (product as ProductSuggestion).priceData.currency;
      }
    }
  }

  private getCheckoutType(isFastCheckout: boolean): CheckoutGA4Type {
    return isFastCheckout ? CheckoutGA4Type.FAST_CHECKOUT : CheckoutGA4Type.REGULAR_CHECKOUT;
  }

  private addCategoryLevelParams(product: Product): EventProductGA4 {
    const categories = product?.breadcrumbs ?? product?.categories;

    if (!categories) {
      return;
    }

    //union type error on reduce but keeping typings ts bug
    //@ts-ignore

    return categories.reduce((acc, categoryItem) => {
      const itemCategoryLevel = `item_category${categoryItem.level === 1 ? '' : categoryItem.level}`;
      return {
        ...acc,
        [itemCategoryLevel]: categoryItem.name,
      };
    }, {});
  }
}
