import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { faAngleLeft } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Subscription } from 'rxjs';
import { RegisterService } from 'src/app/spartacus/services/register.service';
import { Location } from '@angular/common';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { EventService } from '@spartacus/core';
import { RegistrationStartEvent } from '@features/tracking/events/registration-start-event';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class RegisterComponent implements OnInit, OnDestroy {
  accountSelectorForm: UntypedFormGroup;
  activeSiteId = '';
  errorMessage_: BehaviorSubject<string> = this.registerService.errorMessage_;

  stepsList_: BehaviorSubject<{ key: string; active: boolean }[]> = new BehaviorSubject<
    { key: string; active: boolean }[]
  >([
    { key: 'registration.general.account_type', active: true },
    { key: 'registration.b2b.company_title', active: false },
    { key: 'registration.general.your_details', active: false },
  ]);
  activeSubscription: Subscription;
  activeCountrySubscription: Subscription;

  faAngleLeft = faAngleLeft;

  constructor(
    private fb: UntypedFormBuilder,
    private countryService: CountryService,
    private registerService: RegisterService,
    private location: Location,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    this.accountSelectorForm = this.fb.group({
      radio: ['B2B'],
    });

    this.activeCountrySubscription = this.countryService
      .getActive()
      .subscribe((siteId) => (this.activeSiteId = siteId));

    this.eventService.dispatch({ type: 'registration_start' }, RegistrationStartEvent);
  }

  ngOnDestroy(): void {
    if (this.activeCountrySubscription && !this.activeCountrySubscription.closed) {
      this.activeCountrySubscription.unsubscribe();
    }
  }

  onClick(name: string) {
    this.accountSelectorForm.patchValue({
      radio: name,
    });

    if (name === 'B2C') {
      this.registerService.isDisplayInvoiceContainer_.next(false);
      this.stepsList_.next([
        { key: 'registration.general.account_type', active: true },
        { key: 'registration.general.your_details', active: false },
      ]);
    } else {
      this.registerService.isDisplayInvoiceContainer_.next(true);
      this.stepsList_.next([
        { key: 'registration.general.account_type', active: true },
        { key: 'registration.b2b.company_title', active: false },
        { key: 'registration.general.your_details', active: false },
      ]);
    }
  }

  back(): void {
    this.location.back();
  }
}
