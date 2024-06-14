import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faAngleDown, faCheck, faTimes } from '@fortawesome/free-solid-svg-icons';
import { CountryOfOrigin } from '@model/product.model';

export type EventTypes = 'click' | 'blur' | 'change';

@Component({
  selector: 'app-country-select',
  templateUrl: './country-select.component.html',
  styleUrls: ['./country-select.component.scss'],
})
export class CountrySelectComponent implements OnInit {
  @Input() generalRegForm: UntypedFormGroup;
  @Input() registrationType: string;
  @Input() isValidNumberForRegion: boolean;
  @Input() isExportShop: boolean;
  @Input() countryList: CountryOfOrigin[];

  @Output() selectEventEmitter: EventEmitter<EventTypes> = new EventEmitter();

  faAngleDown = faAngleDown;
  faCheck = faCheck;
  faTimes = faTimes;

  constructor() {}

  ngOnInit(): void {
    // Check if the form control value is in the countryList array
    const selectedValue = this.generalRegForm.get('countryCodeOther').value;

    const countryExists = this.countryList.some((option) => option.isocode === selectedValue);

    if (!countryExists) {
      this.generalRegForm.get('countryCodeOther').setValue('');
    }
  }

  onEventTrigger(eventType: EventTypes) {
    this.selectEventEmitter.emit(eventType);
  }
}
