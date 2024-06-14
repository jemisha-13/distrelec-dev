import { Component, Input } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faCheck, faTimes } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-it-bisnode-form',
  templateUrl: './it-bisnode-form.component.html',
  styleUrls: ['./it-bisnode-form.component.scss'],
})
export class ItBisnodeFormComponent {
  @Input() generalRegForm: UntypedFormGroup;
  @Input() onControlTouch: (args: any) => void;

  faTimes = faTimes;
  faCheck = faCheck;
  isCodice = true;

  triggerCodice(value: boolean) {
    this.isCodice = value;
    if (value) {
      this.generalRegForm.removeControl('legalEmail');
      this.generalRegForm.addControl(
        'vat4',
        new UntypedFormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(7)]),
      );
    } else {
      this.generalRegForm.removeControl('vat4');
      this.generalRegForm.addControl(
        'legalEmail',
        new UntypedFormControl('', [
          Validators.required,
          Validators.pattern(/^(\S+)@(\S+)?(legal|pec|cert|sicurezzapostale)(\S+)?\.(\S+)$/i),
        ]),
      );
    }
  }
}
