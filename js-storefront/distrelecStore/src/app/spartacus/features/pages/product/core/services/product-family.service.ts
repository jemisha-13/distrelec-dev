import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CategoriesService } from '@services/categories.service';
import { ProductFamilyData } from '@model/product-family.model';

@Injectable({
  providedIn: 'root',
})
export class ProductFamilyService {
  protected familyData$: BehaviorSubject<ProductFamilyData> = new BehaviorSubject<ProductFamilyData>(null);

  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    private categoryService: CategoriesService,
  ) {}

  getFamilyData(familyCode: string): Observable<ProductFamilyData> {
    return this.http
      .get<any>(this.getFamilyEndpoint(familyCode, true))
      .pipe(tap((data) => this.familyData$.next(data)));
  }

  getFamilyBreadcrumb(familyCategoryCode: string): Observable<any> {
    return this.categoryService.getCategoryData(familyCategoryCode);
  }

  getCurrentFamilyData(): Observable<ProductFamilyData> {
    return this.familyData$.asObservable();
  }

  protected getFamilyEndpoint(familyCode: string, fullFields?: boolean): string {
    return fullFields
      ? this.occEndpoints.buildUrl('/productfamily/' + familyCode, { queryParams: { fields: 'FULL' } })
      : this.occEndpoints.buildUrl('/productfamily/' + familyCode);
  }
}
