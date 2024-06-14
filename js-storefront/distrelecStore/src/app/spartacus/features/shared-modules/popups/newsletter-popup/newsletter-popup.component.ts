import { Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faEnvelope, faTimesCircle } from '@fortawesome/free-regular-svg-icons';
import { AppendComponentService } from '@services/append-component.service';
import { NewsletterService } from '@services/newsletter.service';
import { DistCookieService } from '@services/dist-cookie.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { DISTRELEC_EMAIL_PATTERN } from '@helpers/constants';
import { close, successTick } from '@assets/icons/icon-index';

@Component({
  selector: 'app-newsletter-popup',
  templateUrl: './newsletter-popup.component.html',
  styleUrls: ['./newsletter-popup.component.scss'],
})
export class NewsletterPopupComponent implements OnInit, OnDestroy {
  subscribeForm: UntypedFormGroup;
  showConfirmModal: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  distCloseIcon = close;
  distSuccessTick = successTick;

  faEnvelope = faEnvelope;
  faTimesCircle = faTimesCircle;
  showSuccess: boolean;
  private subscription: Subscription = new Subscription();

  constructor(
    private fb: UntypedFormBuilder,
    private appendComponentService: AppendComponentService,
    private newsLetterSubscribeService: NewsletterService,
    private cookieService: DistCookieService,
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  initForm(): void {
    this.subscribeForm = this.fb.group({
      email: new UntypedFormControl('', [Validators.required, Validators.pattern(DISTRELEC_EMAIL_PATTERN)]),
    });
  }

  onSubmit(): void {
    const email = this.subscribeForm.get('email').value;
    this.subscription.add(
      this.newsLetterSubscribeService.submitNewsletterSubscribe(false, email, false, 'popup').subscribe((res) => {
        if (res.doubleOptIn) {
          this.showConfirmModal.next(true);
        } else {
          this.showSuccess = true;
        }
      }),
    );
  }

  hasError(formControlName: string, error: string): boolean {
    const formError =
      this.subscribeForm.get(formControlName).hasError(error) && this.subscribeForm.get(formControlName).touched;
    return formError;
  }

  onCloseButton(): void {
    this.appendComponentService.removeNewsletterPopupComponent();
    this.cookieService.set('popUpShownDelay', 'true', 30, '/');
    this.cookieService.delete('subscribePopupDelay', '/');
  }

  onModalClose(): void {
    this.appendComponentService.removeNewsletterPopupComponent();
    this.showConfirmModal.next(false);
  }
}
