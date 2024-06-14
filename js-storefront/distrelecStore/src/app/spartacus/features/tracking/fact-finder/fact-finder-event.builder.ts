import { Injectable } from '@angular/core';
import { createFrom, EventService, LoginEvent, WindowRef } from '@spartacus/core';
import { ProductDetailsPageEvent } from '@spartacus/storefront';
import { CartAddEntrySuccessEvent } from '@spartacus/cart/base/root';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, first, map, switchMap, tap } from 'rxjs/operators';
import { isNull, omitBy } from 'lodash-es';

import { FactFinderProductParameters } from '@model/factfinder.model';
import { FactFinderCartQueryService } from '@features/tracking/fact-finder/fact-finder-cart-query.service';
import { CartStoreService } from '@state/cartState.service';
import { DistrelecUserService } from '@services/user.service';

import { FactFinderLoginEvent } from '@features/tracking/events/fact-finder-login-event';
import { FactFinderEvent } from '@features/tracking/events/fact-finder-event';
import { FactFinderCartEvent } from '../events/fact-finder-cart-event';
import { FactFinderCheckoutEvent } from '@features/tracking/events/fact-finder-checkout-event';
import { FactFinderClickEvent } from '@features/tracking/events/fact-finder-click-event';
import { FactFinderRecommendationClickEvent } from '../events/fact-finder-recommendation-click-event';
import { SessionService } from '@features/pages/product/core/services/abstract-session.service';
import { SearchEventPayload } from '../model/event-search';

@Injectable({
  providedIn: 'root',
})
export class FactFinderEventBuilder {
  constructor(
    private eventService: EventService,
    private userService: DistrelecUserService,
    private winRef: WindowRef,
    private cartStoreService: CartStoreService,
    private factFinderCartQueryService: FactFinderCartQueryService,
    private sessionService: SessionService,
  ) {}

  registerEvents(): void {
    this.registerLoginEvent();
    this.registerClickEvent();
  }

  buildCheckoutEvent(orderData): Observable<FactFinderCheckoutEvent> {
    return this.getCommonEventProperties().pipe(
      first(),
      map((commonProperties) => {
        const productQueries = this.factFinderCartQueryService.getProductQueries();
        const productQueryParams = Object.keys(productQueries).reduce<Record<string, string>>(
          (acc, productCode) => ({
            ...acc,
            [`trackQuery[${productCode}]`]: productQueries[productCode],
          }),
          {},
        );

        return createFrom(FactFinderCheckoutEvent, {
          ...commonProperties,
          ...productQueryParams,
          order: commonProperties.userId ? orderData.code : orderData.guid,
        } as FactFinderCheckoutEvent);
      }),
    );
  }

  buildCartEvent(productQueryParams: FactFinderProductParameters): Observable<FactFinderCartEvent> {
    return this.eventService.get(CartAddEntrySuccessEvent).pipe(
      first(),
      switchMap(() =>
        combineLatest([this.getCommonEventProperties(), this.cartStoreService.getCartIdAsObservable()]).pipe(
          first(),
          map(([commonProperties, cartId]) =>
            createFrom(FactFinderCartEvent, {
              ...commonProperties,
              ...productQueryParams,
              cart: cartId,
            } as unknown as FactFinderCartEvent),
          ),
          tap((event) => this.factFinderCartQueryService.trackProductQuery(event)),
        ),
      ),
    );
  }

  buildRecommendationProductClickEvent(product): Observable<FactFinderRecommendationClickEvent> {
    const parsedURL = new URL(product.url, this.winRef.location.origin);
    const mainId = parsedURL.searchParams.get('mainId');

    return this.getCommonEventProperties().pipe(
      map((commonProperties) =>
        createFrom(FactFinderRecommendationClickEvent, {
          ...commonProperties,
          product: product.code,
          mainId,
        } as FactFinderRecommendationClickEvent),
      ),
    );
  }

  private registerLoginEvent() {
    this.eventService.register(FactFinderLoginEvent, this.buildLoginEvent());
  }

  private getCommonEventProperties(): Observable<Omit<FactFinderEvent, 'event'>> {
    return combineLatest([this.userService.getUserDetails(), this.sessionService.getSessionId()]).pipe(
      map(([userDetails, sessionId]) => ({
        sid: sessionId,
        ...(userDetails && { userId: userDetails.encryptedUserID }),
      })),
    );
  }

  private getRouteEventProperties(): Observable<SearchEventPayload> {
    const params = new URLSearchParams(this.winRef.location?.search ?? '');

    const unpackedParams = {
      track: params.get('track'),
      pos: params.get('pos'),
      origPos: params.get('origPos'),
      page: params.get('page'),
      pageSize: params.get('pageSize'),
      origPageSize: params.get('origPageSize'),
      trackQuery: params.get('trackQuery'),
      filterapplied: params.has('filterapplied') ? params.get('filterapplied').replace(/ /g, '+') : null,
      sort: params.get('sort'),
    };

    return of(omitBy(unpackedParams, isNull) as SearchEventPayload);
  }

  private buildLoginEvent(): Observable<FactFinderLoginEvent> {
    return this.eventService.get(LoginEvent).pipe(
      switchMap(() =>
        this.getCommonEventProperties().pipe(
          filter((props) => !!props.userId),
          first(),
        ),
      ),
      map((commonProperties) =>
        createFrom(FactFinderLoginEvent, {
          ...commonProperties,
        } as FactFinderLoginEvent),
      ),
    );
  }

  private registerClickEvent() {
    this.eventService.register(FactFinderClickEvent, this.buildClickEvent());
  }

  private buildClickEvent(): Observable<FactFinderClickEvent> {
    return this.eventService.get(ProductDetailsPageEvent).pipe(
      // using withLatestFrom instead of switchMap here causes app init to block
      switchMap((productDetailsPageEvent) => this.combineWithLatestQueryProperties(productDetailsPageEvent)),

      map(([productDetailsPageEvent, commonProperties, routeProperties]) =>
        createFrom(FactFinderClickEvent, {
          ...commonProperties,
          ...routeProperties,
          event: 'click',
          product: productDetailsPageEvent.code,
        } as FactFinderClickEvent),
      ),
    );
  }

  private combineWithLatestQueryProperties<T>(
    data: T,
  ): Observable<[T, Omit<FactFinderEvent, 'event'>, SearchEventPayload]> {
    return combineLatest([this.getCommonEventProperties(), this.getRouteEventProperties()]).pipe(
      filter(([_, routeProperties]) => routeProperties.track === 'true'),
      map(([commonProperties, routeProperties]) => [data, commonProperties, routeProperties]),
    );
  }
}
