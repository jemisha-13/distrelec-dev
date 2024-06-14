import { Injectable } from '@angular/core';
import {
  createFrom,
  EventService,
  LoginEvent,
  LogoutEvent,
  OCC_USER_ID_CURRENT,
  Query,
  QueryService,
  RoutingService,
  UserIdService,
  WindowRef,
} from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { catchError, filter, first, switchMap, tap } from 'rxjs/operators';
import { ShoppingList, ShoppingListEntry, ShoppingListPayloadItem, ShoppingLists } from '@model/shopping-list.model';
import { AddToShoppingListEvent } from '@features/tracking/events/add-to-shopping-list-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ShoppingListAdapterService } from '../adapters/shopping-list.adapter';
import { LoginService } from '@services/login.service';

@Injectable({
  providedIn: 'root',
})
export class ShoppingListService {
  shoppingListProductPayload$: BehaviorSubject<{
    products: ShoppingListPayloadItem[];
    itemListEntity: ItemListEntity;
  }> = new BehaviorSubject({ products: [], itemListEntity: null });

  protected loadShoppingListsQuery: Query<ShoppingLists> = this.query.create(
    () =>
      combineLatest([this.loginService.isLoginProgress$, this.userIdService.takeUserId()]).pipe(
        filter(([isLoginProgress, _]) => !isLoginProgress),
        switchMap(([_, userId]) => {
          if (userId === OCC_USER_ID_CURRENT) {
            return this.shoppingListAdapterService.loadShoppingListsCurrent();
          }
          return this.shoppingListAdapterService.loadShoppingListsAnonymous();
        }),
      ),
    { reloadOn: [LoginEvent, LogoutEvent] },
  );

  private subscriptions = new Subscription();

  constructor(
    private eventService: EventService,
    private userIdService: UserIdService,
    private shoppingListAdapterService: ShoppingListAdapterService,
    private query: QueryService,
    private routingService: RoutingService,
    private winRef: WindowRef,
    private loginService: LoginService,
  ) {
    // Subscribe to this query here since header loads twice - to avoid duplicated calls
    this.subscriptions.add(this.loadShoppingListsQuery.get().subscribe());
  }

  updateShoppingListState(newShoppingList: ShoppingList): void {
    this.shoppingListAdapterService.updateShoppingListState(newShoppingList);
  }

  /** Get state observable with all shopping lists */
  getShoppingListsState(): Observable<ShoppingLists> {
    return this.shoppingListAdapterService.getShoppingListsState();
  }

  /** New shopping list is created and internal state is updated */
  createShoppingList(name): Observable<any> {
    return this.shoppingListAdapterService.addShoppingList(name);
  }

  /** Shopping list is deleted and internal state is updated */
  deleteShoppingList(uid: string): Observable<any> {
    return this.shoppingListAdapterService.removeShoppingList(uid).pipe(
      tap((value) => value),
      catchError(() => null),
    );
  }

  /** Removes product from the shopping list
   * POST request to delete product
   * GET request to retrieve new shopping list and update the state
   */
  removeProductFromList(listId: string, productCode: string): Observable<ShoppingList> {
    return this.shoppingListAdapterService.removeProductFromList(listId, productCode);
  }

  /** Shopping list is deleted and internal state is updated with the response */
  calculateShoppingList(listId: string): Observable<ShoppingList> {
    return this.shoppingListAdapterService.calculateShoppingList(listId);
  }

  getAllShoppingList(): Observable<any> {
    return this.shoppingListAdapterService.loadShoppingListsCurrent();
  }

  getShoppingLisById(uid: string): Observable<ShoppingList> {
    return this.shoppingListAdapterService.loadShoppingListById(uid);
  }

  addToShoppingList(
    uid: string,
    payloadItems: ShoppingListPayloadItem[],
    itemListEntity: ItemListEntity,
  ): Observable<any> {
    return this.shoppingListAdapterService.addProductToShoppingList(uid, payloadItems).pipe(
      tap((response: ShoppingList) => {
        this.handleAddToShoppingListEvent(payloadItems, response, itemListEntity);
      }),
    );
  }

  updateEntry(uid: string, productCount: number, productCode: string): Observable<ShoppingList> {
    return this.shoppingListAdapterService.updateEntry(uid, productCount, productCode);
  }

  updateShoppingList(uid: string, name: string): Observable<ShoppingLists> {
    return this.shoppingListAdapterService.updateNameOfShoppingList(uid, name);
  }

  redirectToFirstShoppingList(): void {
    this.getShoppingListsState()
      .pipe(
        first(),
        tap((lists) => {
          if (lists.list.length > 0) {
            return this.routingService.go([`/shopping/${lists[0]?.uniqueId}`], { replaceUrl: true });
          }
        }),
      )
      .subscribe();
  }

  getCurrentShoppingListId(): string {
    if (this.winRef.location?.href?.split('/shopping')[1] === '') {
      return '';
    }
    return this.winRef.location?.href?.split('/shopping/')[1].split('?')[0] ?? '';
  }

  replaceComaInPricing(price: string): string {
    if (price?.includes(',')) {
      return price.replace(',', '.');
    }
    return price;
  }

  handleAddToShoppingListEvent(
    payloadItems: ShoppingListPayloadItem[],
    response: ShoppingList,
    itemListEntity: ItemListEntity,
  ) {
    const payloadCodes = payloadItems.map((item) => item.product.code);
    const shoppingListAddedEntries: ShoppingListEntry[] = response.entries
      .filter((entry) => payloadCodes.includes(entry.product.code))
      .map((entry) => {
        entry.desired = payloadItems.filter((item) => item.product.code === entry.product.code)[0].desired;
        return entry;
      });

    this.dispatchAddToShoppingListEvent(shoppingListAddedEntries, itemListEntity);
  }

  private dispatchAddToShoppingListEvent(
    shoppingListEntries: ShoppingListEntry[],
    itemListEntity: ItemListEntity,
  ): void {
    this.eventService.dispatch(createFrom(AddToShoppingListEvent, { shoppingListEntries, itemListEntity }));
  }
}
