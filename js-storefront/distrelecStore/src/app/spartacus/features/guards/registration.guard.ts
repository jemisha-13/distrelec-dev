import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class RegistrationGuard implements CanActivate {
  constructor(
    private router: Router,
    private baseSiteService: DistrelecBasesitesService,
  ) {}

  canActivate(): Observable<boolean> {
    return this.baseSiteService.isRegistrationDisabledForActiveSite().pipe(
      map((isRegistrationDisabled) => {
        if (isRegistrationDisabled) {
          this.router.navigate(['/rs-registration']);
          return false;
        }

        return true;
      }),
    );
  }
}
