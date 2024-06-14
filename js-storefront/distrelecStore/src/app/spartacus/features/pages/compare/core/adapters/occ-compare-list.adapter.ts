import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  CompareEntry,
  CompareList,
  CompareProductAddedResponse,
  CompareProductRemovedResponse,
  CompareShortData,
} from '@model/compare.model';
import { OccEndpointsService } from '@spartacus/core';
import { DistCookieService } from '@services/dist-cookie.service';
import { Observable } from 'rxjs';

// This would be an adapter  occ  one
@Injectable({
  providedIn: 'root',
})
export class OccCompareListAdapter {
  constructor(
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
    private cookieService: DistCookieService,
  ) {}

  /**
   * POST request to save product to compare list
   * Only for logged in users
   */
  saveCompareProduct(productCode: string): Observable<CompareProductAddedResponse> {
    return this.http.post<CompareProductAddedResponse>(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/compare`, {
        queryParams: {
          fields: 'DEFAULT',
        },
      }),
      { entries: this.getCompareProductPayload(productCode) },
    );
  }

  /**
   * POST request to remove product from compare list
   * Only for logged in users
   */
  removeCompareProduct(productCodes: string[], compareListId: string): Observable<CompareProductRemovedResponse> {
    return this.http.post<CompareProductRemovedResponse>(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/remove`, {
        queryParams: {
          // eslint-disable-next-line @typescript-eslint/naming-convention
          'listIds[]': [compareListId],
          // eslint-disable-next-line @typescript-eslint/naming-convention
          'productCodes[]': productCodes,
          fields: 'DEFAULT',
        },
      }),
      {},
    );
  }

  /**
   * GET request to load Unique Id and Total Count for compare product list
   * For logged in and not logged in users
   */
  loadCompareListShortData(): Observable<CompareShortData> {
    return this.http.get<CompareShortData>(
      this.occEndpointsService.buildUrl(`/users/current/shoppingList/compare/listCount`, {
        queryParams: {
          fields: 'DEFAULT',
        },
      }),
    );
  }

  getProductsFromCookies(): string {
    return JSON?.parse(this.cookieService.get('compareProductCodes')).join(':');
  }

  /**
   * GET request to load compare product list
   * For logged in and not logged in users
   */
  loadCompareProductList(userId: string, uniqueId?: string): Observable<CompareList> {
    return this.http.get<CompareList>(
      this.occEndpointsService.buildUrl(`/users/${userId}/shoppingList/compare`, {
        queryParams: {
          compareList: uniqueId ?? this.getProductsFromCookies(),
          fields: 'DEFAULT',
        },
      }),
    );
  }

  getCompareProductPayload(productCode: string): CompareEntry[] {
    return [
      {
        comment: '',
        desired: '1',
        product: { code: `${productCode}` },
      },
    ];
  }
}
