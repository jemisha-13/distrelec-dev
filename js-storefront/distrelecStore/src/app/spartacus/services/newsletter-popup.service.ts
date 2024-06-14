import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { IsCheckoutPages } from '@helpers/is-checkout-pages';
import { IsNoSubscribePages } from '@helpers/is-no-subscribe-pages';
import { SiteConfigService } from '@services/siteConfig.service';
import { AuthService, CmsService, EventService, User, WindowRef } from '@spartacus/core';
import { DistCookieService } from '@services/dist-cookie.service';
import { combineLatest, Subscription } from 'rxjs';
import { filter, tap } from 'rxjs/operators';
import { AppendComponentService } from './append-component.service';
import { DistrelecUserService } from './user.service';
import { AddToSubscriptionPopupEvent } from '@features/tracking/events/add-to-subscription-popup-event';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Injectable({
  providedIn: 'root',
})
export class NewsletterPopupService implements OnDestroy {
  isCheckoutPages = IsCheckoutPages;
  isNoSubscribePage = IsNoSubscribePages;

  subscribePopupDelay = this.returnSubscribePopupValue();
  subscriptions = new Subscription();
  timeout: ReturnType<typeof setTimeout>;

  constructor(
    private appendComponentService: AppendComponentService,
    private cookieService: DistCookieService,
    private cmsService: CmsService,
    private ngZone: NgZone,
    private siteConfigService: SiteConfigService,
    private winRef: WindowRef,
    private authService: AuthService,
    private userService: DistrelecUserService,
    private eventService: EventService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {}

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  checkCookieStatus(): void {
    this.subscriptions.add(
      combineLatest([
        this.cmsService.getCurrentPage(),
        this.authService.isUserLoggedIn(),
        this.distBaseSiteService.isNewsletterEnabled(),
      ])
        .pipe(
          filter(([page, _]) => page !== undefined),
          tap(([page, isUserLoggedIn, isNewsletterEnabled]) => {
            if (isNewsletterEnabled) {
              if (
                (this.cookieService.get('popUpShownDelay') !== 'true' && !isUserLoggedIn) ||
                this.isUserLoggedInAndPopupShouldDisplay(isUserLoggedIn, this.userService.userDetails_.value)
              ) {
                this.setCookieIfPopupShownFalse();
              } else if (this.isPopupShownValidPage(page.template)) {
                if (this.cookieService.get('popUpShownDelay') === 'false') {
                  this.cookieService.set('popUpShownDelay', 'true', 30, '/');
                }
                this.cookieService.delete('subscribePopupDelay', '/');
                this.subscribePopupDelay = null;
                clearTimeout(this.timeout);
              }
            }
          }),
        )
        .subscribe(),
    );
  }

  isUserLoggedInAndPopupShouldDisplay(isUserLoggedIn: boolean, userInfo?: User): boolean {
    return isUserLoggedIn && !userInfo?.newsletterPopup && this.cookieService.get('popUpShownDelay') !== 'true';
  }

  returnSubscribePopupValue(): number {
    return this.cookieService.get('subscribePopupDelay')
      ? Number(JSON.parse(this.cookieService.get('subscribePopupDelay')))
      : 120000;
  }

  setCookieIfPopupShownFalse(): void {
    const currentDelay = Number(this.cookieService.get('subscribePopupDelay'));
    if (currentDelay && currentDelay !== this.subscribePopupDelay) {
      this.subscribePopupDelay = this.returnSubscribePopupValue();
    } else {
      this.cookieService.delete('popUpShownDelay', '/');
      this.cookieService.set('popUpShownDelay', 'false', 30, '/');
      this.cookieService.set('subscribePopupDelay', this.subscribePopupDelay.toString(), 1, '/');
    }
    this.appendWithTimeout(this.subscribePopupDelay);
  }

  isPopupShownValidPage(template: string): boolean {
    return (
      !this.isCheckoutPages(template) &&
      !this.isNoSubscribePage(template) &&
      !this.winRef.location.href.includes('/checkout/payment/')
    );
  }

  appendWithTimeout(timeoutValue: number): void {
    clearTimeout(this.timeout);

    if (this.winRef.isBrowser()) {
      this.ngZone.run(() => {
        this.timeout = setTimeout(() => {
          this.subscribePopupDelay = timeoutValue;
          if (this.isPopupShownValidPage(this.siteConfigService.getCurrentPageTemplate())) {
            this.eventService.dispatch(new AddToSubscriptionPopupEvent()); //a-b testing event trigger inside data layer
            this.appendComponentService.appendNewsLetterPopUp();
            this.cookieService.delete('popUpShownDelay', '/');
            this.cookieService.set('popUpShownDelay', 'true', 30, '/');
            this.cookieService.delete('subscribePopupDelay', '/');
            this.subscriptions.unsubscribe();
          }
        }, timeoutValue);
      });
    }
  }
}
