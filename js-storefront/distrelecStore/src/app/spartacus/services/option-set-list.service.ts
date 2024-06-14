import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { CountryResponse, DepartmentList, FunctionList, TitleList } from '@model/my-account.model';

@Injectable({
  providedIn: 'root',
})
export class OptionSetListService {
  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
  ) {}

  data$: Observable<any>;
  functionData$: Observable<FunctionList>;
  departmentData$: Observable<DepartmentList>;
  countryData$: Observable<CountryResponse>;
  titleData$: Observable<TitleList>;

  /* get function list as option set  */
  getFunctionList(): Observable<FunctionList> {
    this.functionData$ = this.http
      .get<FunctionList>(this.occEndpoints.buildUrl('/functions'))
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));

    return this.functionData$;
  }

  /* get department list as option set  */
  getDepartmentList(): Observable<DepartmentList> {
    this.departmentData$ = this.http
      .get<DepartmentList>(this.occEndpoints.buildUrl('/departments'))
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));

    return this.departmentData$;
  }

  /* get title list as option set  */
  getTitleList(): Observable<TitleList> {
    this.titleData$ = this.http
      .get<TitleList>(this.occEndpoints.buildUrl('/titles'))
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));

    return this.titleData$;
  }

  /* get contact list as option set  */
  getContactsList(): Observable<any> {
    this.data$ = this.http
      .get<any>(this.occEndpoints.buildUrl('/users/current/contacts-of-customer'))
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));

    return this.data$;
  }

  /* get country list as option set  */
  getCountryList(): Observable<CountryResponse> {
    this.countryData$ = this.http
      .get<CountryResponse>(this.occEndpoints.buildUrl('/countries/delivery-countries?fields=FULL'))
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));

    return this.countryData$;
  }
}
