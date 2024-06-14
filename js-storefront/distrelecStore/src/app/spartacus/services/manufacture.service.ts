import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CmsService, OccEndpointsService, RouterState, RoutingService } from '@spartacus/core';
import { EMPTY, Observable } from 'rxjs';
import { catchError, filter, map, shareReplay, switchMap } from 'rxjs/operators';
import { ManufacturerData, ManufacturerListResponse } from '@model/manufacturer.model';

@Injectable({
  providedIn: 'root',
})
export class ManufactureService {
  hasManufacturerContent$: Observable<boolean> = this.cmsService
    .getContentSlot('ManufacturerTopPosition')
    .pipe(map((content) => content.components?.length > 0));

  protected manufacturerMenuData$: Observable<ManufacturerListResponse>;
  protected manufacturerDataCache = new Map<string, Observable<ManufacturerData>>();

  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    private cmsService: CmsService,
    private routingService: RoutingService,
  ) {}

  getManufacturerData(manCode: string): Observable<ManufacturerData> {
    if (!this.manufacturerDataCache.has(manCode)) {
      this.manufacturerDataCache.set(
        manCode,
        this.http
          .get<ManufacturerData>(this.occEndpoints.buildUrl(`/manufacturer/${encodeURIComponent(manCode)}`))
          .pipe(
            catchError(() => {
              this.routingService.goByUrl('/not-found');
              return EMPTY;
            }),
            shareReplay({
              bufferSize: 1,
              refCount: false,
            }),
          ),
      );
    }

    return this.manufacturerDataCache.get(manCode);
  }

  getCurrentManufacturerData(): Observable<ManufacturerData> {
    return this.routingService.getRouterState().pipe(
      filter((routerState) => !routerState.nextState),
      map((routerState: RouterState) => routerState.state.params.manufacturerCode),
      filter((manufacturerCode) => !!manufacturerCode),
      switchMap((manufacturerCode) => this.getManufacturerData(manufacturerCode)),
    );
  }

  getManufactures(): Observable<ManufacturerListResponse> {
    if (this.manufacturerMenuData$) {
      return this.manufacturerMenuData$;
    } else {
      this.manufacturerMenuData$ = this.http
        .get<ManufacturerListResponse>(this.occEndpoints.buildUrl('/manufacturerMenu'))
        .pipe(shareReplay({ bufferSize: 1, refCount: true }));
      return this.manufacturerMenuData$;
    }
  }
}
