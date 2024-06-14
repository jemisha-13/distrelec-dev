import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-dist-text-field',
  templateUrl: './text-field.component.html',
  styleUrls: ['./text-field.component.scss'],
})
export class TextFieldComponent implements OnInit, OnChanges {
  @Input() fieldId: string;
  @Input() autoComplete: string;
  @Input() isInvalidState: boolean;
  @Input() isValidState: boolean;
  @Input() labelText: string;
  @Input() parentFormControlName: string;
  @Input() parentFormGroup: FormGroup;
  @Input() isSecure: boolean;
  @Input() maxLength: string;

  public showSecureText = false;

  public ngOnInit(): void {
    if (this.isSecure) {
      this.showSecureText = true;
    }
  }

  public ngOnChanges(): void {
    if (this.isSecure) {
      this.showSecureText = true;
    }
  }

  public toggleSecureField(): void {
    this.showSecureText = !this.showSecureText;
  }
}
