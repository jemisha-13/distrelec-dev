import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faAngleDown, faCheck, faTimes } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Subscription } from 'rxjs';
import { first } from 'rxjs/operators';
import { RegisterService } from 'src/app/spartacus/services/register.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { CountryOfOrigin } from '@model/product.model';

@Component({
  selector: 'app-country-list',
  templateUrl: './country-list.component.html',
  styleUrls: ['./country-list.component.scss'],
})
export class CountryListComponent implements OnInit, OnDestroy {
  @Input() generalRegForm: UntypedFormGroup;
  @Input() disableCountryCodeOtherB2B$: BehaviorSubject<boolean>;
  @Output() countrySelected: EventEmitter<string> = new EventEmitter();

  faTimes = faTimes;
  faCheck = faCheck;
  faAngleDown = faAngleDown;

  countryListEX_: BehaviorSubject<CountryOfOrigin> = new BehaviorSubject<CountryOfOrigin>(null);
  returnCountryCodeEU: boolean;

  vatValidationHandlerSubscription: Subscription;

  constructor(
    private registerService: RegisterService,
    private countryService: CountryService,
  ) {}

  retrieveCountryList() {
    this.registerService
      .getCountryCodes(this.generalRegForm)
      .pipe(first())
      .subscribe((value) => {
        this.countryListEX_.next(value);
      });
  }

  ngOnInit() {
    this.retrieveCountryList();
    this.returnCountryCodeEU = this.registerService.returnCountryCodeEU(this.generalRegForm.get('countryCode').value);
    this.countryService
      .getActive()
      .pipe(first())
      .subscribe((country) => {
        if (country !== 'EX' && this.generalRegForm.controls.countryCodeOther) {
          this.generalRegForm.controls.countryCodeOther.setValue(country);
        }
      });

    this.vatValidationHandlerSubscription = this.disableCountryCodeOtherB2B$.subscribe((isDisabled) => {
      if (isDisabled) {
        this.generalRegForm.controls.countryCodeOther.disable();
      }
    });
  }

  ngOnDestroy(): void {
    this.vatValidationHandlerSubscription.unsubscribe();
  }
}
