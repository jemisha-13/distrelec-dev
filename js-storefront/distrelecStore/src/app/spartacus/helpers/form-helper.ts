import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';

export const phonePrefixList = [
  { isocode: 'AT', prefix: '+43' },
  { isocode: 'BE', prefix: '+32' },
  { isocode: 'CZ', prefix: '+420' },
  { isocode: 'DK', prefix: '+45' },
  { isocode: 'EE', prefix: '+372' },
  { isocode: 'FI', prefix: '+358' },
  { isocode: 'FR', prefix: '+33' },
  { isocode: 'DE', prefix: '+49' },
  { isocode: 'HU', prefix: '+36' },
  { isocode: 'IT', prefix: '+39' },
  { isocode: 'LV', prefix: '+371' },
  { isocode: 'LT', prefix: '+370' },
  { isocode: 'NL', prefix: '+31' },
  { isocode: 'NO', prefix: '+47' },
  { isocode: 'PL', prefix: '+48' },
  { isocode: 'RO', prefix: '+40' },
  { isocode: 'SK', prefix: '+421' },
  { isocode: 'SE', prefix: '+46' },
  { isocode: 'CH', prefix: '+41' },
  { isocode: 'EX', prefix: '+41' },
];

export function validateAllFormFields(formGroup: UntypedFormGroup) {
  Object.keys(formGroup.controls).forEach((field) => {
    const control = formGroup.get(field);
    if (control instanceof UntypedFormControl) {
      control.markAsTouched({ onlySelf: true });
    } else if (control instanceof UntypedFormGroup) {
      this.validateAllFormFields(control);
    }
  });
}
