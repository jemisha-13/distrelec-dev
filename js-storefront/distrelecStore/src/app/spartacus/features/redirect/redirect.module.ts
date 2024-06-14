import { APP_INITIALIZER, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseSiteService, OccEndpointsService } from '@spartacus/core';
import { first } from 'rxjs/operators';
import { RedirectService } from '@features/redirect/redirect.service';

const redirectInitializerFactory = (redirectService: RedirectService, baseSite: BaseSiteService) => () => {
  redirectService.addRedirectGuard();
  baseSite
    .getActive()
    .pipe(first())
    .subscribe(() => {
      redirectService.refreshDynamicMappings();
    });
};

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: redirectInitializerFactory,
      deps: [RedirectService, BaseSiteService],
      multi: true,
    },
  ],
})
export class RedirectModule {}
