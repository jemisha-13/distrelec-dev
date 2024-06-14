import { HttpClient } from '@angular/common/http';
import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { AppendComponentService } from '@services/append-component.service';
import { CommunicationPreferenceService } from '@services/communication.service';
import { first, tap } from 'rxjs/operators';
import { faAngleLeft, faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { OccEndpointsService, TranslationService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { SiteConfigService } from '@services/siteConfig.service';
import { SiteIdEnum } from '@model/site-settings.model';

@Component({
  selector: 'app-account-communication-preferences-center',
  templateUrl: './account-communication-preferences-center.component.html',
  styleUrls: ['./account-communication-preferences-center.component.scss'],
})
export class AccountCommunicationPreferencesCenterComponent implements OnInit, OnDestroy {
  buttonText = 'form.save_preferences';
  responseType = '';
  responseMessage = '';
  isDisabled = false;
  checkAllNewslettersOptions = false;
  checkAllObsolescenceNotifications = false;
  isSelectedNewsletter = false;
  communicationPref: any;
  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;
  showConfirmModal: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  siteCountryCode$: Observable<string> = this.countryService.getActive();
  emailConsent: boolean;
  newsletterConsent: boolean;
  currentSiteId: string = this.siteConfigService.getCurrentSiteId();

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private userCommunicationService: CommunicationPreferenceService,
    private appendComponentService: AppendComponentService,
    private ngZone: NgZone,
    private windowRef: WindowRef,
    private translation: TranslationService,
    private countryService: CountryService,
    private siteConfigService: SiteConfigService,
  ) {}

  ngOnInit() {
    this.subscriptions.add(
      this.userCommunicationService.getUserCommunicationPreferences().subscribe((data: any) => {
        this.communicationPref = data;
        this.emailConsent = data.emailConsent;
        this.newsletterConsent = data.newsLetterConsent;
        this.initialConsentForAllEmailAndObsol();
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  //initial setup for all email and obsol categories as we don't receive those from BE.
  initialConsentForAllEmailAndObsol() {
    if (this.communicationPref.emailConsent && this.communicationPref.allCatSelected && this.newsletterConsent) {
      this.checkCommunicationOptions();
    } else {
      if (!this.communicationPref.selectAllemailConsents) {
        if (this.isEmailAndNewsletterSubcategoriesSelected()) {
          this.communicationPref.selectAllemailConsents = true;
        }

        if (!this.communicationPref.obsolescenceConsent && this.currentSiteId !== SiteIdEnum.DE) {
          for (const category of this.communicationPref.categories) {
            if (category.isObsolCategorySelected) {
              this.communicationPref.obsolescenceConsent = true;
              this.communicationPref.selectAllemailConsents = true;
              break;
            }
          }
        }
      }
    }
  }

  openModelNewsletter(newsletter: string) {
    if (newsletter === 'one') {
      this.appendComponentService.appendContentPopup('', 'form.communication_preference_image_one');
    }
    if (newsletter === 'two') {
      this.appendComponentService.appendContentPopup('', 'form.communication_preference_image_two');
    }
    if (newsletter === 'three') {
      this.appendComponentService.appendContentPopup('', 'form.communication_preference_image_three');
    }
  }

  cancel() {
    this.userCommunicationService.getUserCommunicationPreferences().subscribe((data: any) => {
      this.communicationPref = data;
      this.scrollToTop();
      this.responseType = 'success';
      this.translation
        .translate('form.com_preferences_reset')
        .pipe(first())
        .subscribe((val) => (this.responseMessage = val));
      this.initialConsentForAllEmailAndObsol();
    });
  }

  //save updated communication preferences
  saveCommunicationPreferences() {
    this.buttonText = 'form.please_wait';
    this.isDisabled = true;
    const obsoleCategories = [];

    //prepare data to be posted
    const data = {
      customerSurveysConsent: this.communicationPref.customerSurveysConsent,
      emailConsent: this.communicationPref.emailConsent,
      knowHowConsent: this.communicationPref.knowHowConsent,
      newsLetterConsent: this.communicationPref.newsLetterConsent,
      obsolescenceConsent: this.communicationPref.obsolescenceConsent,
      personalisationConsent: this.communicationPref.personalisationConsent,
      personalisedRecommendationConsent: this.communicationPref.personalisedRecommendationConsent,
      phoneConsent: this.communicationPref.phoneConsent,
      postConsent: this.communicationPref.postConsent,
      profilingConsent: this.communicationPref.profilingConsent,
      saleAndClearanceConsent: this.communicationPref.saleAndClearanceConsent,
      selectAllemailConsents: this.communicationPref.selectAllemailConsents,
      smsConsent: this.communicationPref.smsConsent,
      termsAndConditionsConsent: this.communicationPref.termsAndConditionsConsent,
      obsoleCategories: [],
      allCatSelected: true,
    };

    //pushing available categories into post data
    this.communicationPref?.categories.forEach((currentValue, index) => {
      obsoleCategories.push({
        code: currentValue.categoryCode,
        obsolCategorySelected: currentValue.isObsolCategorySelected,
      });
      if (currentValue.isObsolCategorySelected === false) {
        data.allCatSelected = false; //if not all selected
      }
    });

    data.obsoleCategories = obsoleCategories;

    this.http
      .post(this.occEndpoints.buildUrl(`/users/current/communication-preferences`), data, { responseType: 'text' })
      .pipe(
        first(),
        tap(() => {
          if (!this.emailConsent && data.emailConsent) {
            this.showConfirmModal.next(true);
          }
        }),
      )
      .subscribe(
        () => {
          //update user communication preferences on successful updating
          this.scrollToTop();
        },
        (response) => {
          this.responseType = 'danger'; //error
          if (response?.error?.errors && response?.error?.errors.length > 0) {
            this.translation
              .translate('text.preferences.error')
              .pipe(first())
              .subscribe((val) => (this.responseMessage = val));
          }
          this.scrollToTop();
          this.buttonText = 'form.save_preferences';
          this.isDisabled = false;
        },
        () => {
          //completed
          this.responseType = 'success';
          this.translation
            .translate('form.com_preferences_saved')
            .pipe(first())
            .subscribe((val) => (this.responseMessage = val));

          this.ngZone.run(() => {
            setTimeout(() => {
              this.scrollToTop();
              this.buttonText = 'form.save_preferences';
              this.isDisabled = false;
              this.responseType = '';
              this.responseMessage = '';
            }, 5000);
          });
        },
      );
  }

  //status for select all categories and obsoloscenes
  checkCommunicationOptions() {
    if (this.isEmailAndNewsletterSubcategoriesSelected()) {
      this.communicationPref.selectAllemailConsents = true;
      this.communicationPref.emailConsent = true;
    } else if (this.isEmailAndNewsletterSubcategoriesUnselected()) {
      this.communicationPref.selectAllemailConsents = false;
      this.communicationPref.emailConsent = false;
    }
    this.communicationPref.obsolescenceConsent = this.checkCategorySelected();
  }

  checkCategorySelected(): boolean {
    const categorySelected = this.communicationPref.categories.filter(
      (category) => category.isObsolCategorySelected,
    ).length;
    return this.currentSiteId !== SiteIdEnum.DE && categorySelected > 0 ? true : false;
  }

  isEmailAndNewsletterSubcategoriesSelected(): boolean {
    return (
      this.communicationPref.newsLetterConsent ||
      this.communicationPref.saleAndClearanceConsent ||
      this.communicationPref.knowHowConsent ||
      this.communicationPref.personalisedRecommendationConsent ||
      this.communicationPref.customerSurveysConsent ||
      this.checkCategorySelected()
    );
  }

  isEmailAndNewsletterSubcategoriesUnselected(): boolean {
    return (
      !this.communicationPref.newsLetterConsent &&
      !this.communicationPref.saleAndClearanceConsent &&
      !this.communicationPref.knowHowConsent &&
      !this.communicationPref.personalisedRecommendationConsent &&
      !this.communicationPref.customerSurveysConsent &&
      !this.checkCategorySelected()
    );
  }

  onEmailConsentChange(): void {
    this.communicationPref.emailConsent = !this.communicationPref.emailConsent;

    if (this.communicationPref.emailConsent) {
      this.updateEmailAndNewsletterConsents(true);
      this.communicationPref.selectAllemailConsents = true;
    } else {
      this.updateEmailAndNewsletterConsents(false);
      this.communicationPref.selectAllemailConsents = false;
    }
  }

  updateEmailAndNewsletterConsents(value: boolean): void {
    const consents = [
      'newsLetterConsent',
      'saleAndClearanceConsent',
      'knowHowConsent',
      'personalisedRecommendationConsent',
      'customerSurveysConsent',
    ];

    Object.entries(this.communicationPref).map(([key, _]) => {
      if (consents.includes(key)) {
        this.communicationPref[key] = value;
      }
    });

    this.communicationPref.categories.forEach((category) => {
      category.isObsolCategorySelected = value;
    });
    this.checkCommunicationOptions();
  }

  //change checked status for categories
  onCategoryChange(event) {
    const target = event.target || event.srcElement || event.currentTarget;
    const idAttr = target.attributes.id;
    const checked = target.checked;
    if (checked === true) {
      this.communicationPref.selectAllemailConsents = true;
    }
    this.communicationPref?.categories.forEach((currentValue, index) => {
      if (currentValue.categoryCode === idAttr.nodeValue) {
        this.communicationPref.categories[index].isObsolCategorySelected = checked;
      }
    });
    this.checkCommunicationOptions();
  }

  onPrefChange(event) {
    const target = event.target || event.srcElement || event.currentTarget;
    const idAttr = target.attributes.id;
    const checked = target.checked;
    const idValue = idAttr.nodeValue;
    this.communicationPref.idValue = checked;
    switch (idValue) {
      case 'newsLetterConsent':
        this.communicationPref.newsLetterConsent = checked;
        break;
      case 'accountCommunication.saleAndClearanceConsent':
        this.communicationPref.saleAndClearanceConsent = checked;
        break;
      case 'accountCommunication.knowHowConsent':
        this.communicationPref.knowHowConsent = checked;
        break;
      case 'accountCommunication.personalisedRecommendationConsent':
        this.communicationPref.personalisedRecommendationConsent = checked;
        break;
      case 'accountCommunication.customerSurveysConsent':
        this.communicationPref.customerSurveysConsent = checked;
        break;
    }
    this.checkCommunicationOptions();
  }

  checkAllNewsletters(event) {
    const target = event.target || event.srcElement || event.currentTarget;
    const checked = target.checked;
    if (checked === true) {
      this.communicationPref.selectAllemailConsents = true;
    }
    this.communicationPref.newsLetterConsent = checked;
    this.communicationPref.saleAndClearanceConsent = checked;
    this.communicationPref.knowHowConsent = checked;
    this.communicationPref.personalisedRecommendationConsent = checked;
    this.communicationPref.customerSurveysConsent = checked;
    this.checkCommunicationOptions();
  }

  checkAllObsolescence(event) {
    const target = event.target || event.srcElement || event.currentTarget;
    const checked = target.checked;
    this.communicationPref.obsolescenceConsent = checked;
    this.communicationPref?.categories.forEach((currentValue, index) => {
      this.communicationPref.categories[index].isObsolCategorySelected = checked;
    });
    this.checkCommunicationOptions();
  }

  scrollToTop() {
    if (this.windowRef.isBrowser()) {
      this.windowRef.nativeWindow.scroll({
        top: 0,
        left: 0,
        behavior: 'smooth',
      });
    }
  }
  onModalClose() {
    this.showConfirmModal.next(false);
  }
}
