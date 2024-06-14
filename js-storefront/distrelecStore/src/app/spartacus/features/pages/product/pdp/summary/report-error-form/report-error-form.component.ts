import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { ErrorFeedbackForm, ReportErrorFormService } from './report-error-form.service';
import { DISTRELEC_EMAIL_PATTERN } from '@helpers/constants';
import { DistrelecUserService } from '@services/user.service';
import { AuthService } from '@spartacus/core';

@Component({
  selector: 'app-report-error-form',
  templateUrl: './report-error-form.component.html',
  styleUrls: ['./report-error-form.component.scss'],
})
export class ReportErrorFormComponent implements OnInit, OnDestroy {
  @Input() isDisplayReportErrorForm: BehaviorSubject<boolean>;

  @Input() productId: string;
  @Input() productTitle: string;

  reportErrorForm: UntypedFormGroup;
  emailValidation = true;
  faTimes = faTimes;
  displayChecboxError: boolean;
  isLoggedIn$: Observable<boolean> = this.authService.isUserLoggedIn();

  isFormSubmitted = false;
  isFailed = false;

  name: string;
  email: string;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private fb: UntypedFormBuilder,
    private reportErrorFormService: ReportErrorFormService,
    private cdRef: ChangeDetectorRef,
    private userService: DistrelecUserService,
    private authService: AuthService,
  ) {}

  closeForm() {
    this.isDisplayReportErrorForm.next(false);
  }

  onSubmit() {
    if (this.reportErrorForm.valid && this.productId) {
      let errorReason = '';
      this.reportErrorForm.value.checkArray.forEach((el: string) => {
        errorReason += errorReason === '' ? el : ', ' + el;
      });

      const form: ErrorFeedbackForm = {
        productId: this.productId,
        errorReason,
        errorDescription: this.reportErrorForm.value.textArea,
        customerName: this.reportErrorForm.value.name,
        customerEmailId: this.reportErrorForm.value.email,
      };

      this.subscriptions.add(
        this.reportErrorFormService.sendErrorFeedback(form).subscribe({
          next: () => (this.isFailed = false),
          error: () => (this.isFailed = true),
          complete: () => {
            this.isFormSubmitted = true;
            this.cdRef.detectChanges();
          },
        }),
      );
    }
  }

  onCheckboxChange(e) {
    // Listen to checbox changes and push the values to formArray when checked
    // Or remove when unchecked
    const checkArray: UntypedFormArray = this.reportErrorForm.get('checkArray') as UntypedFormArray;

    if (e.target.checked) {
      checkArray.push(new UntypedFormControl(e.target.value));
    } else {
      let i = 0;
      checkArray.controls.forEach((item: UntypedFormControl) => {
        if (item.value === e.target.value) {
          checkArray.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  ngOnInit() {
    this.name = this.userService.userDetails_.value
      ? `${this.userService.userDetails_.value.firstName} ${this.userService.userDetails_.value.lastName}`
      : '';
    this.email = this.userService.userDetails_.value?.displayUid ?? '';

    this.reportErrorForm = this.fb.group({
      checkArray: this.fb.array([], [Validators.required]),
      textArea: ['', [Validators.required]],
      email: [this.email, [Validators.required, Validators.pattern(DISTRELEC_EMAIL_PATTERN)]],
      name: [this.name, [Validators.required]],
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
