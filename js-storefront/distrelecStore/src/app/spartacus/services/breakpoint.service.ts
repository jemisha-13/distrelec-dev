import { Injectable } from '@angular/core';
import { BREAKPOINT, BreakpointService } from '@spartacus/storefront';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class DistBreakpointService extends BreakpointService {
  isMobileBreakpoint(): Observable<boolean> {
    return this.isDown(BREAKPOINT.sm);
  }
  isMobileOrTabletBreakpoint(): Observable<boolean> {
    return this.isDown(BREAKPOINT.lg);
  }

  isTabletBreakpoint(): Observable<boolean> {
    return this.isUp(BREAKPOINT.md).pipe(
      switchMap((mdUp) => {
        if (mdUp) {
          return this.isDown(BREAKPOINT.lg);
        } else {
          return of(false);
        }
      }),
    );
  }

  isDesktopBreakpoint(): Observable<boolean> {
    return this.isUp(BREAKPOINT.xl);
  }
}
