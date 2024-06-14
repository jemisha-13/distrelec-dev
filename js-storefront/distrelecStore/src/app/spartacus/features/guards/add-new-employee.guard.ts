import { DistrelecBasesitesService } from '@services/basesites.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { combineLatest } from 'rxjs';

export const addNewEmployeeGuard = () => {
  const distBaseSiteService = inject(DistrelecBasesitesService);
  const router = inject(Router);

  return combineLatest([
    distBaseSiteService.isRegistrationDisabledForActiveSite(),
    distBaseSiteService.isSubUserManagementDisabled(),
  ]).pipe(
    map(([isRegistrationDisabled, isSubUserManagementDisabled]) => {
      if (isRegistrationDisabled || isSubUserManagementDisabled) {
        router.navigateByUrl('/my-account/company/user-management');
        return false;
      }
      return true;
    }),
  );
};
