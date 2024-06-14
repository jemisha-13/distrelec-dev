import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { ManufactureService } from '@services/manufacture.service';
import { RedirectCountService } from '@services/redirect-count.service';

@Injectable({
  providedIn: 'root',
})
export class ManufacturerRedirectGuard {
  constructor(
    private router: Router,
    private manufacturerService: ManufactureService,
    private redirectCount: RedirectCountService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> | boolean {
    if (this.redirectCount.exceeds(1)) {
      this.redirectCount.reset();
      return true;
    }

    const manufacturerCode = route.params.manufacturerCode;
    return this.manufacturerService.getManufacturerData(manufacturerCode).pipe(
      take(1),
      map((manufacturerData) => {
        const currentPath = '/' + route.url.map((u) => u.path).join('/');
        if (currentPath === manufacturerData.urlId) {
          this.redirectCount.reset();
          return true;
        }

        this.redirectCount.increment();
        return this.router.createUrlTree([manufacturerData.urlId], {
          queryParams: route.queryParams,
        });
      }),
    );
  }
}
