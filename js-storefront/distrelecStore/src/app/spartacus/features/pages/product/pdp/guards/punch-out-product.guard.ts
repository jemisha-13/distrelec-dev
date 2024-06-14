import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, NavigationEnd, Router, UrlTree } from '@angular/router';
import { GlobalMessageService, GlobalMessageType, WindowRef } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { Translatable } from '@spartacus/core/src/i18n/translatable';
import { filter, map, take } from 'rxjs/operators';
import { ProductEnhancementsService } from '@features/pages/product/core/services/product-enhancements.service';

@Injectable({
  providedIn: 'root',
})
export class PunchOutProductGuard  {
  constructor(
    private router: Router,
    private globalMessageService: GlobalMessageService,
    private productEnhancements: ProductEnhancementsService,
    private window: WindowRef,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    if (this.isSmarteditPreview(route) || !this.window.isBrowser()) {
      return of(true);
    }

    const productCode = route.params?.productCode;

    return this.productEnhancements.getPunchOutReason(productCode).pipe(
      take(1),
      map((exclusionReason: string) => {
        const errorMessage = this.getPunchoutErrorMessage(exclusionReason, productCode);
        if (errorMessage) {
          this.showErrorAfterNavigation(errorMessage);
          return this.router.parseUrl('/');
        }
        return true;
      }),
    );
  }

  private isSmarteditPreview(route: ActivatedRouteSnapshot) {
    return route.url[0]?.path === 'cx-preview' && route.queryParams.cmsTicketId;
  }

  private showErrorAfterNavigation(errorMessage: Translatable): void {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        take(1),
      )
      .subscribe(() => {
        this.globalMessageService.add(errorMessage, GlobalMessageType.MSG_TYPE_ERROR, 10000);
      });
  }

  private getPunchoutErrorMessage(exclusionReason: string, productCode: string): Translatable | null {
    if (!exclusionReason) {
      return null;
    }

    switch (exclusionReason) {
      case 'PunchoutProductExcluded':
        return {
          key: 'article.not.found.error',
          params: { 0: productCode },
        };
      case 'ProductAvailableToB2BOnly':
        return {
          key: 'article.business.only.error',
        };
      default:
        return null;
    }
  }
}
