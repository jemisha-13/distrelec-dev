import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService, User } from '@spartacus/core';
import { BehaviorSubject, EMPTY, Observable, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { CustomerType } from '@model/site-settings.model';
import { DashboardContents } from '@model/my-account.model';

export enum FieldScope {
  BASIC = 'BASIC',
  DEFAULT = 'DEFAULT',
  FULL = 'FULL',
}

@Injectable({
  providedIn: 'root',
})
export class DistrelecUserService {
  userDetails_: BehaviorSubject<User> = new BehaviorSubject<User>(null);
  private dashboardContents: BehaviorSubject<DashboardContents> = new BehaviorSubject(null);

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
  ) {}

  updateUserDetailsObject(key: string, newValue: string) {
    this.userDetails_.next({ ...this.userDetails_.getValue(), [key]: newValue });
  }

  /* get general customer information  */
  getUserInformation(): Observable<User> {
    if (!this.userDetails_.value) {
      return this.http.get<any>(this.occEndpoints.buildUrl(`/users/current`)).pipe(
        map((data: User) => {
          this.userDetails_.next(data);
          return data;
        }),
      );
    }
    return of(this.userDetails_.value);
  }

  getCurrentDashboardContents$(): Observable<DashboardContents> {
    return this.dashboardContents.asObservable();
  }

  /* get customer information for sections on homepage banner  */
  getContents(userType): Observable<DashboardContents> {
    if (userType !== 'anonymous') {
      const url = this.occEndpoints.buildUrl(`/users/${userType}/dashboardContents?fields=${FieldScope.DEFAULT}`);

      return this.http.get<any>(url).pipe(tap((dashboardData) => this.dashboardContents.next(dashboardData)));
    }
    return EMPTY;
  }

  changeEmail(newLogin: string, password: string): Observable<any> {
    const url = this.occEndpoints.buildUrl(`/users/current/email`);
    return this.http.put<any>(url, {
      newLogin,
      password,
    });
  }

  getUserDetails(): BehaviorSubject<User> {
    return this.userDetails_;
  }

  getCustomerType(): CustomerType {
    return this.userDetails_.getValue()?.customerType;
  }

  isB2B(): boolean {
    return this.getCustomerType() === CustomerType.B2B;
  }

  isB2BKey(): boolean {
    return this.getCustomerType() === CustomerType.B2B_KEY_ACCOUNT;
  }

  isB2BAny(): boolean {
    return this.isB2B() || this.isB2BKey();
  }
}
