import { Component, Input, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, ValidatorFn, Validators } from '@angular/forms';
import { faCheck, faCircleNotch, faTimes } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Subscription } from 'rxjs';
import { VatValidationData } from '@model/registration.model';
import { RegisterService } from 'src/app/spartacus/services/register.service';
import { RegisterServiceHelper } from '@helpers/register-helpers';
import { isActiveSiteInternational } from '../../../../../site-context/utils';
import { SiteContextConfig } from '@spartacus/core';

@Component({
  selector: 'app-vat-validation',
  templateUrl: './vat-validation.component.html',
  styleUrls: ['./vat-validation.component.scss'],
})
export class VatValidationComponent implements OnInit, OnDestroy {
  @Input() generalRegForm: UntypedFormGroup;
  @Input() vatIdValidation$: BehaviorSubject<VatValidationData>;
  @Input() onControlTouch: (controlName: string) => void;
  @Input() disableCountryCodeOtherB2B$: BehaviorSubject<boolean>;
  @Input() isLoading$: BehaviorSubject<boolean>;

  faTimes = faTimes;
  faCheck = faCheck;
  faCircleNotch = faCircleNotch;

  countryCode: string;
  registrationType: string;
  vatIdPrefix: string;
  isExportShop: boolean;
  vatValidationSubscription: Subscription;

  constructor(
    // Please keep renderer for onControlTouch() which is passed from the parent class
    private renderer: Renderer2,
    private registerService: RegisterService,
    private registerServiceHelper: RegisterServiceHelper,
    private config: SiteContextConfig,
  ) {}

  ngOnInit() {
    this.initialize();
    this.vatValidationSubscription = this.vatIdValidation$.subscribe(() => this.initialize());
    this.isExportShop = isActiveSiteInternational(this.config);
  }

  ngOnDestroy(): void {
    if (this.vatValidationSubscription) {
      this.vatValidationSubscription.unsubscribe();
    }
  }

  private initialize() {
    this.countryCode = this.getCountryCode();
    this.assignOrganisationNoControl();
    this.registrationType = this.generalRegForm.get('type').value;
    this.setVatPrefix();
  }

  private assignOrganisationNoControl() {
    if (this.registerService.isCountryWithOrgNumber(this.generalRegForm.get('countryCode').value)) {
      // Organisational number is always required if it is present
      // Populate value if company already has org number but keep the control for the payload
      this.generalRegForm.addControl('orgNumber', new UntypedFormControl('', this.setOrgNumberValidators()));
    }
  }

  private setOrgNumberValidators(): ValidatorFn[] {
    const countryCode = this.generalRegForm.get('countryCode').value;
    return [
      Validators.required,
      Validators.pattern(this.registerServiceHelper.getOrgNumberPattern(countryCode)),
      Validators.maxLength(11),
    ];
  }

  private setVatPrefix(): void {
    this.vatIdPrefix = this.registerService.getVatIdPrefix(this.countryCode);
  }

  private getCountryCode(): string {
    return this.isExportShop
      ? this.generalRegForm.get('countryCodeOther').value
      : this.generalRegForm.get('countryCode').value;
  }
}
