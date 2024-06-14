import { Injectable } from '@angular/core';
import { OccShoppingListAdapter } from './occ-shopping-list.adapter';
import { ShoppingList, ShoppingListPayloadItem, ShoppingLists } from '@model/shopping-list.model';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

const initialState: ShoppingLists = {
  list: [],
};

// This would be an adapter that makes calls to OCC adapter
// And holds the state of product list data
@Injectable({
  providedIn: 'root',
})
export class ShoppingListAdapterService {
  private readonly store$ = new BehaviorSubject<ShoppingLists>(initialState);

  constructor(private occShoppingListAdapter: OccShoppingListAdapter) {}

  loadShoppingListsCurrent(): Observable<ShoppingLists> {
    return this.occShoppingListAdapter
      .loadShoppingLists('FULL')
      .pipe(tap((shoppingLists) => this.setShoppingListState(shoppingLists)));
  }

  loadShoppingListById(uid: string): Observable<ShoppingList> {
    return this.occShoppingListAdapter
      .loadShoppingListById(uid)
      .pipe(tap((shoppingList) => this.updateShoppingList(uid, shoppingList)));
  }

  loadShoppingListsAnonymous(): Observable<ShoppingLists> {
    this.setShoppingListState(initialState);
    return this.getShoppingListsState();
  }

  addProductToShoppingList(uid: string, payloadItems: ShoppingListPayloadItem[]) {
    return this.occShoppingListAdapter.saveProductToShoppingList(uid, payloadItems).pipe(
      tap((response: ShoppingList) => {
        this.setShoppingListState(this.updateShoppingList(uid, response));
      }),
    );
  }

  addShoppingList(name: string): Observable<ShoppingList> {
    return this.occShoppingListAdapter.saveNewShoppingList(name).pipe(
      tap((response: ShoppingList) => {
        this.setShoppingListState({ list: this.store$.value.list.concat([response]) });
      }),
    );
  }

  removeProductFromList(listId: string, productCodes: string): Observable<ShoppingList> {
    return this.occShoppingListAdapter.removeProductFromShoppingList(listId, productCodes).pipe(
      switchMap(() => this.occShoppingListAdapter.loadShoppingListById(listId)),
      tap((response: ShoppingList) => this.setShoppingListState(this.updateShoppingList(listId, response))),
    );
  }

  removeShoppingList(uid: string): Observable<ShoppingLists> {
    return this.occShoppingListAdapter.removeShoppingList(uid).pipe(
      tap(() => {
        // The only shopping list can never be removed apparently
        if (this.store$.value.list.length > 1) {
          const indexOfList = this.store$.value.list.map((list) => list.uniqueId).indexOf(uid);
          const newShoppingListsState = this.store$.value.list;
          newShoppingListsState.splice(indexOfList, 1);
          this.setShoppingListState({ list: newShoppingListsState });
        }
        return this.store$.value;
      }),
    );
  }

  calculateShoppingList(listId: string): Observable<ShoppingList> {
    return this.occShoppingListAdapter
      .recalculateShoppingList(listId)
      .pipe(tap((response: ShoppingList) => this.setShoppingListState(this.updateShoppingList(listId, response))));
  }

  getShoppingListsState(): Observable<ShoppingLists> {
    return this.store$.asObservable();
  }

  updateShoppingListState(newShoppingList: ShoppingList): void {
    this.updateShoppingList(newShoppingList.uniqueId, newShoppingList);
  }

  // TODO: implement 1 second waiting before firing requests like it is on cart in the component
  updateEntry(uid: string, productCount: number, productCode: string): Observable<any> {
    return this.occShoppingListAdapter.updateProductInShoppingList(uid, productCount, productCode);
  }

  updateNameOfShoppingList(uid: string, name: string): Observable<ShoppingLists> {
    return this.occShoppingListAdapter.updateNameOfShoppingList(uid, name).pipe(
      switchMap(() => {
        let updatedList = this.store$.value.list.find((list) => list.uniqueId === uid);
        updatedList = { ...updatedList, name };
        this.setShoppingListState(this.updateShoppingList(uid, updatedList));
        return this.getShoppingListsState();
      }),
    );
  }

  private updateShoppingList(listId: string, response: ShoppingList): ShoppingLists {
    const newShoppingListsState = this.store$.value.list.map((list: ShoppingList) => {
      if (list.uniqueId === listId) {
        return response;
      }
      return list;
    });
    return { list: newShoppingListsState };
  }

  private setShoppingListState(compareList: ShoppingLists): void {
    this.store$.next(compareList);
  }
}
