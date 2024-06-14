import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { pluck, tap } from 'rxjs/operators';
import { ContentPageNavNodes } from '@model/navigation-node.model';

@Injectable({
  providedIn: 'root',
})
export class ContentPageNavService {
  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
  ) {}

  protected navNodeData$: BehaviorSubject<ContentPageNavNodes> = new BehaviorSubject<ContentPageNavNodes>(null);

  createNavigation(data$: Observable<any>): Observable<ContentPageNavNodes> {
    if (!data$) {
      return of();
    }

    return data$.pipe(pluck('navRootNodes'));
  }

  getNavNodes(pageId: string): Observable<ContentPageNavNodes> {
    return this.http.get<ContentPageNavNodes>(this.getNavNodesEndpoint(pageId)).pipe(
      tap((items) => {
        this.navNodeData$.next(items);
      }),
    );
  }

  protected getNavNodesEndpoint(pageId: string): string {
    return this.occEndpoints.buildUrl('cms/pages/' + pageId + '/navigationNodes');
  }
}
