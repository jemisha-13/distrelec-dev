import { ChangeDetectorRef, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { BackorderService } from '@services/backorder.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { CartStoreService } from '@state/cartState.service';
import { Observable, Subscription } from 'rxjs';
import { DISTRELEC_EMAIL_PATTERN } from '@helpers/constants';

@Component({
  selector: 'app-back-order-save',
  templateUrl: './back-order-save.component.html',
  styleUrls: ['./back-order-save.component.scss'],
})
export class BackOrderSaveComponent implements OnInit, OnDestroy {
  @Input() entries;
  @Output() saveSelected = new EventEmitter<boolean>();
  @Output() emailSubmitted = new EventEmitter<boolean>();

  customerEmail: string;
  faCheckCircle = faCheckCircle;
  productCodes = [];
  emailSuccess: boolean = null;
  hideForm = false;
  notifyMeSelected = true;
  screenWidth: number;
  notifyForm: UntypedFormGroup;

  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  isMobile: boolean;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private formBuilder: UntypedFormBuilder,
    private backorderService: BackorderService,
    private cdRef: ChangeDetectorRef,
    private cartStoreService: CartStoreService,
    private breakpointService: DistBreakpointService,
  ) {}

  ngOnInit() {
    this.subscriptions.add(
      this.isMobileBreakpoint$.subscribe((data) => {
        if (!data) {
          this.notifyMeSelected = true;
        }
        this.isMobile = data;
      }),
    );

    this.entries.forEach((entry) => {
      this.productCodes.push(entry.product.code);
    });
    this.customerEmail = this.assignEmailForNewsletterForm();
  }

  get email() {
    return this.notifyForm?.get('email');
  }

  assignEmailForNewsletterForm(): string {
    let cartEmail = this.cartStoreService.getCartUser().uid;
    cartEmail = cartEmail && cartEmail?.includes('|') ? cartEmail.split('|')[1] : cartEmail;
    this.notifyForm = this.formBuilder.group({
      email: new UntypedFormControl(this.customerEmail, [Validators.pattern(DISTRELEC_EMAIL_PATTERN)]),
    });
    this.notifyForm.get('email').setValidators(Validators.email);

    return cartEmail;
  }

  handleSubmit(): void {
    this.backorderService.notifyMe(this.customerEmail, this.productCodes).subscribe((res) => {
      this.emailSubmitted.emit(true);
      this.hideForm = true;
      this.emailSuccess = res;
      this.cdRef.detectChanges();
    });
  }

  handleClick(): void {
    this.saveSelected.emit(true);
  }

  handleNotifyClick(): void {
    this.notifyMeSelected = !this.notifyMeSelected;
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}
