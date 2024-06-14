import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class SideMenuCountService {
  userDetails_: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  selectedOrderSorting = 'byDate:desc';
  sideMenuCount$: Subject<any> = new Subject();
  searchName: string = '';
  userDetails$: BehaviorSubject<any>;
  userDetails: any;
  userRoles: any;
  totalCount: any;

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
  ) {}

  getSideMenuCount(): Observable<any> {
    return this.sideMenuCount$.asObservable();
  }

  setSideMenuCount() {
    this.searchApprovals();
  }

  searchApprovals() {
    let countURL = '/users/current/dashboardContents';
    this.callData(countURL);
  }

  callData(url: string) {
    this.http
      .get<any>(this.occEndpoints.buildUrl(url))
      .pipe(take(1))
      .subscribe(
        (data: any) => {
          if (data) {
            this.sideMenuCount$.next(data);
          }
        },
        (response) => {},
        () => {},
      );
  }

  findRole(role: string) {
    return this.userRoles.indexOf(role) > -1;
  }
}
