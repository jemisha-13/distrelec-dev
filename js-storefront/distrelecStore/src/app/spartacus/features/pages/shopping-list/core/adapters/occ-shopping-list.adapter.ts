import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ShoppingList, ShoppingListPayloadItem, ShoppingListUpdateEntryResponse } from '@model/shopping-list.model';
import { OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';

// This would be an adapter  occ  one
@Injectable({
  providedIn: 'root',
})
export class OccShoppingListAdapter {
  constructor(
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
  ) {}

  /**
   * GET request to load all user's shopping lists
   */
  loadShoppingLists(scope: string = 'DEFAULT'): Observable<any> {
    return this.http.get<any>(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/all`, { queryParams: { field: scope } }),
    );
  }

  /**
   * GET request to load the shopping list by ID
   */
  loadShoppingListById(listId: string): Observable<ShoppingList> {
    return this.http.get<ShoppingList>(this.occEndpointsService.buildUrl(`/users/current/shoppingList/${listId}`));
  }

  /**
   * POST request to save new shopping list
   */
  saveNewShoppingList(name: string): Observable<any> {
    return this.http.post(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/create`, { queryParams: { name } }),
      {},
    );
  }

  /**
   * POST request to save product to the shopping list
   */
  saveProductToShoppingList(uid: string, payloadItems: ShoppingListPayloadItem[]): Observable<any> {
    return this.http.post(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/add`, { queryParams: { existingListUid: uid } }),
      { entries: payloadItems },
    );
  }

  /**
   * PUT request to update product in the shopping list
   */
  updateProductInShoppingList(
    uid: string,
    productCount,
    productCode: string,
    name?: string,
  ): Observable<ShoppingListUpdateEntryResponse> {
    return this.http.put<ShoppingListUpdateEntryResponse>(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/update/entry`, {
        queryParams: { desired: productCount, listId: uid, productCode, name },
      }),
      {},
    );
  }

  /**
   * PUT request to update name of the shopping list
   */
  updateNameOfShoppingList(uid: string, name: string): Observable<any> {
    return this.http.put(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/${uid}/update`, { queryParams: { name } }),
      {},
    );
  }

  /**
   * DELETE request to remove the shopping list
   */
  removeShoppingList(uid: string): Observable<any> {
    return this.http.delete(this.occEndpointsService.buildUrl(`/users/current/shoppingList/${uid}/delete`));
  }

  /**
   * POST request to remove product from the shopping list
   */
  removeProductFromShoppingList(listId: string, productCodes): Observable<any> {
    return this.http.post(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/remove`, {
        queryParams: { 'listIds[]': listId, 'productCodes[]': productCodes },
      }),
      {},
    );
  }

  /**
   * PATCH request to load the shopping list by ID
   */
  recalculateShoppingList(listId: string): Observable<ShoppingList> {
    return this.http.patch<ShoppingList>(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/${listId}/calculate`, {
        queryParams: { fields: 'DEFAULT' },
      }),
      {},
    );
  }
}
