import { DistrelecBasesitesService } from '@services/basesites.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';

export const orderReturnsGuard = () => {
  const distBaseSiteService = inject(DistrelecBasesitesService);
  const router = inject(Router);

  return distBaseSiteService.isProductReturnDisabled().pipe(
    map((isProductReturnDisabled) => {
      if (isProductReturnDisabled) {
        router.navigateByUrl('/my-account/order-history');
        return false;
      }
      return true;
    }),
  );
};
