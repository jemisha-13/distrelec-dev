import { Component, OnDestroy, OnInit } from '@angular/core';
import { ReturnReasonsService } from '@services/return-reasons.service';
import { GuestRMACreateRequestForm, MainReason, SubReason } from '@model/returns.model';
import { catchError, first, tap } from 'rxjs/operators';
import { BehaviorSubject, combineLatest, EMPTY, Observable, Subscription } from 'rxjs';
import { OccEndpointsService, TranslationService, UserIdService, WindowRef } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { LoginService } from '@services/login.service';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Component({
  selector: 'app-guest-returns-form',
  templateUrl: './guest-returns-form.component.html',
  styleUrls: ['./guest-returns-form.component.scss'],
})
export class GuestReturnsFormComponent implements OnInit, OnDestroy {
  // List of all (main) return reasons
  returnReasons: MainReason[];
  orderNotReceivedReasonId = '006';

  // The form
  guestRMACreateRequestFormRequest: GuestRMACreateRequestForm;
  customerTextInputFieldValue: string;
  guestRMACreateRequestForm: UntypedFormGroup;

  // Response
  responseSuccess: boolean;

  // User
  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;
  isProductReturnEnabled$: Observable<boolean> = this.distBaseSiteService.isProductReturnEnabled();
  private userId = 'anonymous';
  private subscriptions = new Subscription();

  constructor(
    private returnReasonsService: ReturnReasonsService,
    private translationService: TranslationService,
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
    private winRef: WindowRef,
    private userIdService: UserIdService,
    private formBuilder: UntypedFormBuilder,
    private loginService: LoginService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {
    this.subscriptions.add(
      this.userIdService
        .getUserId()
        .pipe(first())
        .subscribe((id) => {
          if (id !== 'anonymous') {
            this.userId = id?.toString();
          }
        }),
    );
  }

  ngOnInit(): void {
    // Get all reasons
    this.returnReasonsService.getMainReasons().subscribe((data) => {
      this.returnReasons = data;
    });

    this.guestRMACreateRequestForm = this.formBuilder.group({
      customerName: ['', Validators.required],
      orderNumber: ['', Validators.required],
      emailAddress: ['', Validators.email],
      phoneNumber: '',
      articleNumber: ['', [Validators.required, Validators.minLength(8)]],
      quantity: [null, [Validators.max(9999), Validators.required]],
      returnReason: ['', Validators.required],
      returnSubReason: ['', this.subReasonValidator()],
      customerText: ['', Validators.maxLength(50)],
    });

    this.responseSuccess = null;
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  onMainReasonChange(event: any) {
    this.guestRMACreateRequestForm.patchValue({
      returnReason: event,
      returnSubReason: '',
    });
  }

  getTranslatedSubReasonText(subReason: SubReason): string {
    return this.getTranslatedReasonText(subReason?.subReasonMessages[0]);
  }

  getTranslatedReasonText(key: string): string {
    let translatedText = '';
    combineLatest([this.translationService.translate(key)])
      .subscribe(([translatedReason]) => {
        translatedText = translatedReason;
      })
      .unsubscribe();

    return translatedText;
  }

  submitReturnRequest(): void {
    if (this.guestRMACreateRequestForm.invalid) {
      return;
    }

    this.guestRMACreateRequestFormRequest = {
      customerName: this.guestRMACreateRequestForm.controls.customerName.value,
      orderNumber: this.guestRMACreateRequestForm.controls.orderNumber.value,
      emailAddress: this.guestRMACreateRequestForm.controls.emailAddress.value,
      phoneNumber: this.guestRMACreateRequestForm.controls.phoneNumber?.value,
      articleNumber: this.guestRMACreateRequestForm.controls.articleNumber.value,
      quantity: this.guestRMACreateRequestForm.controls.quantity.value,
      returnReason:
        this.guestRMACreateRequestForm.controls.returnReason?.value?.mainReasonId !== '006'
          ? this.guestRMACreateRequestForm.controls.returnSubReason?.value?.subReasonId
          : this.guestRMACreateRequestForm.controls.returnReason?.value?.mainReasonId,
      returnSubReason:
        this.guestRMACreateRequestForm.controls.returnReason?.value?.mainReasonId !== '006'
          ? this.getTranslatedSubReasonText(this.guestRMACreateRequestForm.controls.returnSubReason?.value)
          : '',
      customerText: this.guestRMACreateRequestForm.controls.customerText?.value
        ? this.guestRMACreateRequestForm.controls.customerText?.value
        : '',
    };

    this.http
      .post<void>(
        this.occEndpointsService.buildUrl(`/users/${this.userId}/order-returns/guest-return/`),
        this.guestRMACreateRequestFormRequest,
      )
      .pipe(
        tap((): void => {
          this.responseSuccess = true;
          this.guestRMACreateRequestForm.reset({
            returnReason: null,
            returnSubReason: null,
          });
          delete this.guestRMACreateRequestFormRequest;
          this.scrollToResponseMessages();
        }),
        catchError(() => {
          this.responseSuccess = false;
          this.scrollToResponseMessages();
          return EMPTY;
        }),
      )
      .subscribe();
  }

  subReasonValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.guestRMACreateRequestForm) {
        const mainReason: MainReason = this.guestRMACreateRequestForm.controls.returnReason?.value;
        if (!control.value) {
          if (mainReason?.mainReasonId === this.orderNotReceivedReasonId) {
            return null;
          } else {
            return {
              returnSubReason: {
                value: control.value,
              },
            };
          }
        } else {
          return null;
        }
      }

      return null;
    };
  }

  scrollToResponseMessages() {
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.document.getElementById('guestReturnsScrollAnchor').scrollIntoView({
        behavior: 'smooth',
        block: 'start',
        inline: 'start',
      });
    }
  }
}
