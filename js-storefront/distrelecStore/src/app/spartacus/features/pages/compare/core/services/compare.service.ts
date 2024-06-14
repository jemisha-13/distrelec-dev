import { Injectable, OnDestroy } from '@angular/core';
import {
  createFrom,
  EventService,
  LoginEvent,
  LogoutEvent,
  OCC_USER_ID_CURRENT,
  Product,
  Query,
  QueryService,
  UserIdService,
} from '@spartacus/core';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { CompareProductEvent } from '@features/tracking/events/ga4/compare-product-event';
import { CompareList, CompareProductAddedResponse, CompareProductRemovedResponse } from '@model/compare.model';
import { CompareListAdapterService } from '../adapters/compare-list.adapter';
import { RemoveCompareProductEvent } from '@features/tracking/events/remove-compare-product-event';
import { LoginService } from '@services/login.service';
import { EventHelper } from '@features/tracking/event-helper.service';

@Injectable({
  providedIn: 'root',
})
export class CompareService implements OnDestroy {
  protected loadCompareListQuery: Query<CompareList> = this.query.create(
    () =>
      combineLatest([this.loginService.isLoginProgress$, this.userIdService.takeUserId()]).pipe(
        filter(([isLoginProgress, _]) => !isLoginProgress),
        switchMap(([isLoginProgress, userId]) => {
          if (userId === OCC_USER_ID_CURRENT) {
            return this.compareListAdapterService.loadCompareListForCurrent();
          }
          return this.compareListAdapterService.loadCompareListForAnonymous();
        }),
      ),
    { reloadOn: [LoginEvent, LogoutEvent, RemoveCompareProductEvent] },
  );

  private subscriptions = new Subscription();

  constructor(
    private userIdService: UserIdService,
    private eventService: EventService,
    private compareListAdapterService: CompareListAdapterService,
    private query: QueryService,
    private loginService: LoginService,
    private eventHelper: EventHelper,
  ) {
    // Subscribe to this query here since header loads twice - to avoid duplicated calls
    this.subscriptions.add(this.loadCompareListQuery.get().subscribe());
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  getCompareListState(): Observable<CompareList> {
    return this.compareListAdapterService.getCompareListState();
  }

  removeCompareProduct(productCodes: string[]): Observable<CompareProductRemovedResponse> {
    return this.userIdService.takeUserId().pipe(
      switchMap((userId) => {
        if (userId === OCC_USER_ID_CURRENT) {
          return this.compareListAdapterService.removeCompareProductForCurrent(productCodes);
        }
        return this.compareListAdapterService.removeCompareProductForAnonymous(productCodes);
      }),
      tap(() =>
        this.eventService.dispatch(
          createFrom(RemoveCompareProductEvent, { productCodes } as RemoveCompareProductEvent),
        ),
      ),
    );
  }

  addCompareProduct(product: Product): Observable<CompareProductAddedResponse | CompareList> {
    return this.userIdService.takeUserId().pipe(
      switchMap((userId) => {
        if (userId === OCC_USER_ID_CURRENT) {
          return this.compareListAdapterService.addCompareProductForCurrent(product.code);
        }
        return this.compareListAdapterService.addCompareProductForAnonymous(product.code);
      }),
      tap(() => this.dispatchAddToCompareListEvent(product)),
    );
  }

  private dispatchAddToCompareListEvent(compareProduct: Product): void {
    this.eventHelper.trackCompareListEvent(compareProduct);
  }
}
