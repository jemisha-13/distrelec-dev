import { Component, DestroyRef, HostListener, OnDestroy, OnInit, inject } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { AuthService, CmsService, EventService, LoginEvent, Page, User } from '@spartacus/core';
import { SmartEditLauncherService } from '@spartacus/smartedit/root';
import { SiteMetaPropertiesService } from '@services/site-meta-properties.service';
import { AppendComponentService } from '@services/append-component.service';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { filter, first, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { NavigationStart, Router } from '@angular/router';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { DistGuestUserService } from '@services/guest-user.service';
import { MaintenancePageService } from '@services/maintenance-page.service';
import { HeaderService } from '@features/shared-modules/header/header.service';
import { NewsletterPopupService } from '@services/newsletter-popup.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'spartacus';
  schema = {
    // eslint-disable-next-line @typescript-eslint/naming-convention
    '@context': 'http://schema.org',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    '@type': 'Organization',
    name: 'Distrelec',
    legalName: 'Distrelec Group AG',
    url: 'https://www.distrelec.ch',
    logo: 'https://www.distrelec.ch/medias/distrelec-webshop-logo.jpg?context=bWFzdGVyfHJvb3R8MTc2OTd8aW1hZ2UvanBlZ3xoMTEvaDU3LzkxNTMzMDU2NzM3NTguanBnfDZhZjBlM2I1Zjg2NWIxODcyYzMwMjhjMTg0YzAxODk4YjcxMGNiM2E3YmMxMjFhZjdlY2YzNDcyZDlhYTFhN2M',
    description:
      'The Distrelec Schweiz AG is a leading High Service Distributor for electronics, automation, and measuring equipment and tools.',
    foundingDate: '1973',
    founder: {
      // eslint-disable-next-line @typescript-eslint/naming-convention
      '@type': 'Person',
      name: 'Adolf Dätwyler',
    },
    address: {
      // eslint-disable-next-line @typescript-eslint/naming-convention
      '@type': 'PostalAddress',
      streetAddress: 'Grabenstrasse 6',
      addressLocality: 'Nänikon',
      addressRegion: 'Canton of Zürich',
      postalCode: '8606',
      addressCountry: 'Switzerland',
    },
    sameAs: [
      'https://www.facebook.com/Distrelec',
      'https://twitter.com/distrelec',
      'https://www.linkedin.com/company/distrelec-distrelec-schuricht-and-elfa-distrelec_',

      'https://plus.google.com/+DistrelecGroup',
    ],
  };

  public showStickyHeader = this.headerService.showStickyHeader.asObservable();
  public isSticky = this.headerService.isSticky.asObservable();
  public hasWarningMessage$: Observable<boolean> = this.headerService.hasActiveWarnings$;
  eventServiceSubscription: Subscription = new Subscription();
  distrelecUserSubscription: Subscription = new Subscription();
  navigationSubscription: Subscription = new Subscription();
  pageTemplate$ = this.cmsService.getCurrentPage().pipe(filter<Page>(Boolean));
  isCheckoutPageTemplate = false;
  isCheckout$: Observable<boolean> = this.pageTemplate$.pipe(
    map((page: Page) => {
      const result = page.template === 'OrderConfirmationPageTemplate' || page.template === 'CheckoutPageTemplate';
      this.isCheckoutPageTemplate = result;
      return result;
    }),
  );

  destroyRef = inject(DestroyRef);

  constructor(
    private siteMetaProperties: SiteMetaPropertiesService,
    private componentService: AppendComponentService,
    protected authService: AuthService,
    private distrelecUserService: DistrelecUserService,
    private guestUserService: DistGuestUserService,
    private newsletterPopupService: NewsletterPopupService,
    private router: Router,
    private allSiteSettingsService: AllsitesettingsService,
    private maintenancePageService: MaintenancePageService,
    private cmsService: CmsService,
    private headerService: HeaderService,
    private smartEditLauncherService: SmartEditLauncherService,
    private eventService: EventService,
  ) {}

  @HostListener('window:scroll', ['$event'])
  checkScroll() {
    if (this.isCheckoutPageTemplate || this.smartEditLauncherService.isLaunchedInSmartEdit()) {
      this.headerService.resetScroll();
    } else {
      this.headerService.checkScroll();
    }
  }

  ngOnInit() {
    this.navigationSubscription.add(
      this.router.events
        .pipe(filter((event) => event instanceof NavigationStart))
        .pipe(
          tap(() => this.guestUserService.verifyGuestStatus()),
          withLatestFrom(this.distrelecUserService.getUserDetails().asObservable()),
          filter(([event, userInfo]: [NavigationStart, User]) => Boolean(userInfo)),
          tap(([event, userInfo]: [NavigationStart, User]) => {
            if (this.isCommunicationsPopupDisplayed(event.url, userInfo)) {
              this.componentService.appendCommunicationsPopUp();
            }
          }),
        )
        .subscribe(),
    );

    this.allSiteSettingsService.init();
    this.siteMetaProperties.addMetaTags();

    this.distrelecUserSubscription.add(
      this.authService
        .isUserLoggedIn()
        .pipe(first())
        .subscribe((isLoggedIn: boolean) => {
          if (isLoggedIn) {
            // check communication flag for the user
            this.distrelecUserService
              .getUserInformation()
              .pipe(first())
              .subscribe((userInfo: User) => {
                if (this.isCommunicationsPopupDisplayed(this.router.routerState.snapshot.url, userInfo)) {
                  this.componentService.appendCommunicationsPopUp();
                }
                this.distrelecUserSubscription.unsubscribe();
              });
          }
        }),
    );

    this.guestUserService.verifyGuestStatus();

    if (!this.smartEditLauncherService.isLaunchedInSmartEdit()) {
      this.newsletterPopupService.checkCookieStatus();
      this.maintenancePageService.showMaintenanceIfActive();
    }

    //fix [Auth] Login action clearing CMS state
    this.eventService
      .get(LoginEvent)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        switchMap(() => this.cmsService.getCurrentPage()),
        filter((currentPage) => currentPage === undefined),
      )
      .subscribe(() => this.cmsService.refreshLatestPage());
  }

  ngOnDestroy(): void {
    this.eventServiceSubscription.unsubscribe();
    this.distrelecUserSubscription.unsubscribe();
    this.navigationSubscription.unsubscribe();
  }

  isCommunicationsPopupDisplayed(currentUrl: string, userInfo: User): boolean {
    return this.isBlockedFromDisplayingOnPage(currentUrl) && userInfo?.consentConditionRequired;
  }

  private isBlockedFromDisplayingOnPage(currentUrl: string): boolean {
    return !(currentUrl.includes('/cms/datenschutz') || currentUrl.includes('/cms/agb'));
  }
}
