import { Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, Validators } from '@angular/forms';
import { faUser } from '@fortawesome/free-solid-svg-icons';
import { LoginService } from '@services/login.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { NewsletterService } from '@services/newsletter.service';
import { DISTRELEC_EMAIL_PATTERN } from '@helpers/constants';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Component({
  selector: 'app-newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.scss'],
})
export class NewsletterComponent implements OnInit, OnDestroy {
  disableSubmitButton = true;

  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;
  isMessageSuccess: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  showConfirmModal: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  faUser = faUser;
  subscribeForm = this.formBuilder.group({
    email: new UntypedFormControl('', [Validators.pattern(DISTRELEC_EMAIL_PATTERN)]),
  });
  isNewsletterEnabled$: Observable<boolean> = this.distBaseSiteService.isNewsletterEnabled();

  private activeSubscription: Subscription;

  /*  TODO: isCheckout and isPersonalisation values are set to false,
  but must be changed when we get to checkout page
  */
  private isCheckout = false;
  private isPersonalisation = false;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private newsletterSubscribeService: NewsletterService,
    private loginService: LoginService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {}

  get email() {
    return this.subscribeForm.get('email');
  }

  onSubmit(): void {
    this.activeSubscription = this.newsletterSubscribeService
      .submitNewsletterSubscribe(this.isCheckout, this.subscribeForm.value.email, this.isPersonalisation, 'footer')
      .subscribe((data) => {
        if (data.doubleOptIn) {
          this.showConfirmModal.next(true);
        } else {
          if (data.errorMessage === '') {
            this.isMessageSuccess.next(true);
            this.disableSubmitButton = false;
          }
        }
      });
  }

  ngOnInit(): void {
    this.subscribeForm.get('email').setValidators([Validators.required, Validators.email]);
  }

  ngOnDestroy(): void {
    if (this.activeSubscription && !this.activeSubscription.closed) {
      this.activeSubscription.unsubscribe();
    }
  }

  onModalClose() {
    this.showConfirmModal.next(false);
    this.isMessageSuccess.next(true);
  }
}
