import { Injectable } from '@angular/core';
import { BaseSiteService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { combineLatest, from, Observable, of } from 'rxjs';
import { catchError, map, take, tap } from 'rxjs/operators';
import { environment } from '@environment';

interface MaintenanceResponse {
  baseSites: { maintenanceActive: boolean; uid: string }[];
}

@Injectable({
  providedIn: 'root',
})
export class MaintenancePageService {
  private readonly baseSiteMaintenanceEndpoint = `/basesites/mutable?format=json`;
  private readonly maintenancePageDomain = this.winRef.location.origin;
  private readonly maintenancePagePath = environment.production ? `/error/503.html` : `/assets/error/503.html`;

  constructor(
    private occEndpoints: OccEndpointsService,
    private baseSite: BaseSiteService,
    private winRef: WindowRef,
  ) {}

  showMaintenanceIfActive() {
    if (!this.winRef.isBrowser()) {
      return;
    }

    combineLatest([this.getBaseSiteMaintenanceStatus(), this.baseSite.getActive()])
      .pipe(
        map(([response, activeBaseSite]) => {
          const activeBaseSiteMaintenance = response.baseSites.find((baseSite) => baseSite.uid === activeBaseSite);
          return activeBaseSiteMaintenance?.maintenanceActive;
        }),
        take(1),
        tap((maintenanceActive) => {
          if (maintenanceActive) {
            this.showMaintenancePageInIframe();
          }
        }),
      )
      .subscribe();
  }

  private getBaseSiteMaintenanceStatus(): Observable<MaintenanceResponse> {
    const url = `${this.occEndpoints.getBaseUrl({ baseSite: false })}${this.baseSiteMaintenanceEndpoint}`;

    // When maintenance mode is active the wildcard CORS header does not allow authentication headers
    // we use fetch directly instead of HttpClient to skip the interceptors which add them
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS#sect3
    return from(
      fetch(url, {
        // This is supported but type definitions are missing, using ts-ignore to suppress error
        // https://developer.mozilla.org/en-US/docs/Web/API/AbortSignal/timeout_static
        // @ts-ignore
        signal: AbortSignal.timeout(5000),
      }).then((response) => response.json()),
    ).pipe(
      catchError(() => {
        this.showMaintenancePageInIframe();
        return of({ baseSites: [] });
      }),
    );
  }

  private showMaintenancePageInIframe() {
    const iframe = document.createElement('iframe');
    iframe.src = this.getMaintenancePageUrl();
    iframe.style.position = 'fixed';
    iframe.style.top = '0';
    iframe.style.left = '0';
    iframe.style.width = '100%';
    iframe.style.height = '100%';
    iframe.style.border = 'none';
    iframe.style.background = 'white';
    iframe.style.zIndex = '9999';
    document.body.appendChild(iframe);
    document.body.style.overflow = 'hidden';
  }

  private getMaintenancePageUrl() {
    return `${this.maintenancePageDomain}${this.maintenancePagePath}`;
  }
}
