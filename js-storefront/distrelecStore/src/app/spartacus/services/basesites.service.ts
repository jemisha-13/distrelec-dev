import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService, BaseSite, BaseSiteService, BaseStore, OccEndpointsService } from '@spartacus/core';
import { combineLatest, Observable, of } from 'rxjs';
import { first, map, shareReplay, switchMap, take } from 'rxjs/operators';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { DistrelecUserService } from '@services/user.service';

@Injectable({
  providedIn: 'root',
})
export class DistrelecBasesitesService {
  channelUid: string = this.channelService.getDefaultValue();

  activeBaseStore$: Observable<BaseStore> = this.baseSiteService.getActive().pipe(
    first(),
    switchMap((currentStore) =>
      this.http.get<BaseStore>(
        this.occEndpointsService.buildUrl('basestores/' + currentStore + '_' + this.channelUid.toLowerCase()),
      ),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  baseSiteData$: Observable<BaseSite> = this.baseSiteService
    .get()
    .pipe(first(), shareReplay({ bufferSize: 1, refCount: true }));

  constructor(
    private baseSiteService: BaseSiteService,
    private occEndpointsService: OccEndpointsService,
    private http: HttpClient,
    private channelService: ChannelService,
    private authService: AuthService,
    private distrelecUserService: DistrelecUserService,
  ) {}

  getBaseStoreData(): Observable<BaseStore> {
    return this.activeBaseStore$;
  }

  getBaseSiteData(): Observable<BaseSite> {
    return this.baseSiteData$;
  }

  isQuotationEnabledForActiveStore(): Observable<boolean> {
    return combineLatest([this.authService.isUserLoggedIn(), this.activeBaseStore$]).pipe(
      take(1),
      switchMap(([isLoggedIn, baseStore]) => {
        if (isLoggedIn) {
          if (baseStore.quotationsEnabled) {
            return of(true);
          }
          return this.distrelecUserService.getUserInformation().pipe(
            take(1),
            map((userInfo) => userInfo?.erpSelectedCustomer),
          );
        }
        return of(baseStore.quotationsEnabled);
      }),
    );
  }

  isRegistrationEnabledForActiveSite(): Observable<boolean> {
    return this.baseSiteData$.pipe(map((baseSite) => baseSite.enableRegistration));
  }

  isRegistrationDisabledForActiveSite(): Observable<boolean> {
    return this.baseSiteData$.pipe(map((baseSite) => !baseSite.enableRegistration));
  }

  isAddToCartEnabledForActiveSite(): Observable<boolean> {
    return combineLatest([this.authService.isUserLoggedIn(), this.baseSiteData$]).pipe(
      take(1),
      switchMap(([isLoggedIn, baseSite]) => {
        if (isLoggedIn) {
          if (baseSite.enableAddToCart) {
            return of(true);
          }
          return this.distrelecUserService.getUserInformation().pipe(
            take(1),
            map((userInfo) => userInfo?.erpSelectedCustomer),
          );
        }
        return of(baseSite.enableAddToCart);
      }),
    );
  }

  isAddToCartDisabledForActiveSite(): Observable<boolean> {
    return combineLatest([this.authService.isUserLoggedIn(), this.baseSiteData$]).pipe(
      take(1),
      switchMap(([isLoggedIn, baseSite]) => {
        if (isLoggedIn) {
          if (baseSite.enableAddToCart) {
            return of(false);
          }
          return this.distrelecUserService.getUserInformation().pipe(
            take(1),
            map((userInfo) => {
              const isCustomerEnabledToPurchase = userInfo?.erpSelectedCustomer;
              return !isCustomerEnabledToPurchase;
            }),
          );
        }
        return of(!baseSite.enableAddToCart);
      }),
    );
  }

  isNewsletterEnabled(): Observable<boolean> {
    return this.baseSiteData$.pipe(map((baseSite) => baseSite.enableNewsletter));
  }
  isMyAccountRedirectEnabled(): Observable<boolean> {
    return this.baseSiteData$.pipe(map((baseSite) => baseSite.enableMyAccountRedirect));
  }

  isProductReturnEnabled(): Observable<boolean> {
    return this.baseSiteData$.pipe(map((baseSite) => baseSite.enableProductReturn));
  }

  isProductReturnDisabled(): Observable<boolean> {
    return this.baseSiteData$.pipe(map((baseSite) => !baseSite.enableProductReturn));
  }

  isSubUserManagementEnabled(): Observable<boolean> {
    return this.baseSiteData$.pipe(map((baseSite) => baseSite.enableSubUserManagement));
  }

  isSubUserManagementDisabled(): Observable<boolean> {
    return this.baseSiteData$.pipe(map((baseSite) => !baseSite.enableSubUserManagement));
  }
}
