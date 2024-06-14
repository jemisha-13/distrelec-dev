import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { createFrom, EventService, OccEndpointsService, User, WindowRef } from '@spartacus/core';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';
import { first, tap } from 'rxjs/operators';
import { DistLogoutService } from '@services/logout.service';
import { DistrelecUserService } from '@services/user.service';
import { Router } from '@angular/router';
import { SiteIdEnum } from '@model/site-settings.model';
import { MarketingConsentNotificationPopupComponent } from '@features/shared-modules/marketing-consent-notification-popup/marketing-consent-notification-popup.component';
import { TermsAndConditionsPopupAcceptedEvent } from '@features/tracking/events/terms-conditions-popup-accepted-event';
import { EventHelper } from '@features/tracking/event-helper.service';

@Component({
  selector: 'app-communication-pref-popup',
  templateUrl: './communication-pref-popup.component.html',
  styleUrls: ['./communication-pref-popup.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class CommunicationPrefPopupComponent implements OnInit {
  communicationPref = {
    termsAndConditionsConsent: false,
    emailConsent: false,
    smsConsent: false,
    phoneConsent: false,
    postConsent: false,
    personalisationConsent: false,
    profilingConsent: false,
    newsLetterConsent: false,
    saleAndClearanceConsent: false,
    knowHowConsent: false,
    personalisedRecommendationConsent: false,
    customerSurveysConsent: false,
    obsolescenceConsent: false,
    selectAllemailConsents: false,
  };
  user: User;

  constructor(
    private appendComponentService: AppendComponentService,
    private distUserService: DistrelecUserService,
    private eventService: EventService,
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private logoutService: DistLogoutService,
    private router: Router,
    private winRef: WindowRef,
    private eventHelper: EventHelper,
  ) {}

  ngOnInit() {
    this.winRef.document.body.classList.add('is-locked');

    this.distUserService
      .getUserInformation()
      .pipe(
        first(),
        tap((userInfo) => (this.user = userInfo)),
      )
      .subscribe();
  }

  //save updated communication preferences
  completeSetup() {
    if (this.communicationPref.emailConsent) {
      this.communicationPref.newsLetterConsent = true;
      this.communicationPref.saleAndClearanceConsent = true;
      this.communicationPref.knowHowConsent = true;
      this.communicationPref.personalisedRecommendationConsent = true;
      this.communicationPref.customerSurveysConsent = true;
      this.communicationPref.obsolescenceConsent = true;
      this.communicationPref.selectAllemailConsents = true;
    }

    this.http
      .post(this.occEndpoints.buildUrl(`/users/current/communication-preferences`), this.communicationPref, {
        responseType: 'text',
      })
      .subscribe(
        () => {
          if (this.communicationPref.emailConsent && this.user.customersBaseSite === SiteIdEnum.DE) {
            this.addConfirmPopup(this.user.uid);
          }
          this.hidePopup();
          this.resetCommPreferencesOnUser();
          if (this.user.rsCustomer) {
            this.eventService.dispatch(createFrom(TermsAndConditionsPopupAcceptedEvent, { event: 'tc_consent' }));
            this.router.navigate(['/rs-welcome']);
          } else {
            this.router.navigate(['/welcome']);
          }
        },
        (response) => {
          response?.error?.errors.forEach((err) => {
            alert(err.message);
          });
        },
      );

    if (this.user.rsCustomer) {
      this.handleAccountActivationEventTracking();
    }
  }

  resetCommPreferencesOnUser() {
    this.distUserService.userDetails_.next({
      ...this.distUserService.userDetails_.value,
      consentConditionRequired: false,
    });
  }

  hidePopup() {
    this.appendComponentService.removeCommunicationPopupComponent();
    this.winRef.document.body.classList.remove('is-locked');
  }

  addConfirmPopup(uid: string) {
    const marketingNotificationComponent = this.appendComponentService.appendComponent(
      MarketingConsentNotificationPopupComponent,
    );
    marketingNotificationComponent.instance.confirmEmail = uid;
    marketingNotificationComponent.instance.modalClosed.subscribe(() =>
      this.appendComponentService.destroyComponent(marketingNotificationComponent),
    );
  }

  cancel() {
    this.logoutService.postLogoutRequest().then(() => this.hidePopup());
  }

  private handleAccountActivationEventTracking() {
    this.eventHelper.trackBloomreachRSRegistrationEvent(this.user.contactId);
  }
}
