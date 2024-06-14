import { Injectable } from '@angular/core';
import { Page, RoutingService } from '@spartacus/core';
import { Observable, ReplaySubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { breadcrumbConfig } from '../helpers/breadcrumb-config';
import { Breadcrumb } from '@model/breadcrumb.model';

@Injectable({
  providedIn: 'root',
})
export class BreadcrumbService {
  private breadcrumbs$ = new ReplaySubject<Breadcrumb[]>(1);

  constructor(private routingService: RoutingService) {}

  setBreadcrumbs(value: Breadcrumb[]): void {
    this.breadcrumbs$.next(value);
  }

  getBreadcrumbs(): Observable<Breadcrumb[]> {
    return this.breadcrumbs$.asObservable();
  }

  getCrumb(): Observable<string> {
    return this.routingService.getRouterState().pipe(map((item) => item?.state?.context?.id));
  }

  isBreadcrumbHiddenOnPage(page: Page): boolean {
    return breadcrumbConfig.ignoredPageLabels.includes(page?.label);
  }
}
