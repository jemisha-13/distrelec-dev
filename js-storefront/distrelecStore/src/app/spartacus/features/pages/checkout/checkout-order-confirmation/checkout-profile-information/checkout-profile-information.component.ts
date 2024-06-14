import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, first } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { CheckoutService } from '@services/checkout.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { faAngleDown, faAngleRight, faCheck, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { Order } from '@spartacus/order/root';
import { AppendComponentService } from '@services/append-component.service';
import { B2BCustomerData } from '@model/misc.model';

@Component({
  selector: 'app-checkout-profile-information',
  templateUrl: './checkout-profile-information.component.html',
  styleUrls: ['./checkout-profile-information.component.scss'],
})
export class CheckoutProfileInformationComponent implements OnInit {
  @Input() order: Order;

  functionsList$: Observable<void>;
  departmentsList$: Observable<void>;

  profileForm: FormGroup;

  faAngleDown: IconDefinition = faAngleDown;
  faAngleRight: IconDefinition = faAngleRight;
  faCheck: IconDefinition = faCheck;

  hasFunction = true;
  hasDepartment = true;
  userProfileSubmitted: boolean;

  constructor(
    private checkoutService: CheckoutService,
    private fb: FormBuilder,
    private appendComponentService: AppendComponentService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.getFunctionAndDepartmentLists();
    this.setProfileForm();
    this.populateCustomerInfo();
  }

  updateUserProfile(): void {
    this.appendComponentService.startScreenLoading();
    this.checkoutService
      .updateUserProfile(this.profileForm.get('jobRole').value, this.profileForm.get('areaOfUse').value)
      .pipe(
        first(),
        catchError((error: HttpErrorResponse) => {
          this.appendComponentService.stopScreenLoading();
          return of(false);
        }),
      )
      .subscribe(() => {
        this.appendComponentService.stopScreenLoading();
        this.userProfileSubmitted = true;
        this.cdr.markForCheck();
      });
  }

  private populateCustomerInfo(): void {
    const customerData: B2BCustomerData = this.order.b2bCustomerData;

    this.hasFunction = !!customerData.functionCode;
    this.hasDepartment = !!customerData.contactAddress.departmentCode;

    if (this.hasDepartment) {
      this.profileForm.controls.areaOfUse.disable();
    }

    if (this.hasFunction) {
      this.profileForm.controls.jobRole.disable();
    }
  }

  private setProfileForm(): void {
    this.profileForm = this.fb.group({
      areaOfUse: new FormControl(null, [Validators.required, Validators.maxLength(4)]),
      jobRole: new FormControl(null, [Validators.required, Validators.maxLength(4)]),
    });
  }

  private getFunctionAndDepartmentLists(): void {
    this.functionsList$ = this.checkoutService.getFunctions();
    this.departmentsList$ = this.checkoutService.getDepartments();
  }
}
