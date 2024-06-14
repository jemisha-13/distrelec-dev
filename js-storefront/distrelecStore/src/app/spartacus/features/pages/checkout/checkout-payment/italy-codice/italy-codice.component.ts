/* eslint-disable indent */
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import { faCheck, faSquareCheck, faTimes } from '@fortawesome/free-solid-svg-icons';
import { VatRequestBody } from '@model/checkout.model';
import { CheckoutService } from '@services/checkout.service';
import { DistrelecUserService } from '@services/user.service';
import { CartStoreService } from '@state/cartState.service';
import { Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-italy-codice',
  templateUrl: './italy-codice.component.html',
  styleUrls: ['./italy-codice.component.scss'],
})
export class ItalyCodiceComponent implements OnInit, OnDestroy {
  @Input() codiceForm: UntypedFormGroup;

  codiceDestinarioText: string;
  legalEmailText: string;
  codiceCUPText: string;
  codiceCIGText: string;

  isCodiceExtended: boolean;
  isDisplayCodiceDestinario: boolean;
  isDisplayLegalEmail: boolean;

  faCheck = faCheck;
  faTimes = faTimes;
  faSquare = faSquare;
  faSquareCheck = faSquareCheck;

  userDetails_ = this.userService.userDetails_;

  private subscription = new Subscription();

  constructor(
    private checkoutService: CheckoutService,
    private userService: DistrelecUserService,
    private cartStoreService: CartStoreService,
  ) {}

  ngOnInit(): void {
    if (!this.legalEmail()) {
      this.codiceForm.addControl(
        'legalEmail',
        new UntypedFormControl(this.legalEmailText, [
          Validators.required,
          Validators.pattern(/^(\S+)@(\S+)?(legal|pec|cert|sicurezzapostale)(\S+)?\.(\S+)$/i),
        ]),
      );
    }

    if (!this.codiceDestinario()) {
      this.codiceForm.addControl(
        'codiceDestinario',
        new UntypedFormControl(this.codiceDestinarioText, [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(7),
        ]),
      );
    }

    this.isDisplayCodiceDestinario = !this.userDetails_.value.vat4 && !this.userDetails_.value.legalEmail;
    this.isDisplayLegalEmail = !this.userDetails_.value.vat4 && !!this.userDetails_.value.legalEmail;

    this.onControlChanges('codiceDestinario');
    this.onControlChanges('legalEmail');
    this.onControlChanges('codiceCUP', true);
    this.onControlChanges('codiceCIG', true);
    // If CUP or CIG is prefilled, check checkbox and display fields
    this.isCodiceExtended = !!this.codiceCUP().value || !!this.codiceCIG().value;
  }

  ngOnDestroy(): void {
    if (this.subscription && this.subscription.closed) {
      this.subscription.unsubscribe();
    }
    this.updateCodiceValuesOnPageSwitch();
  }

  // if user goes to a different page withour refresh, update cart and user objects to display the latest values
  updateCodiceValuesOnPageSwitch() {
    if (this.codiceDestinarioText) {
      this.userService.updateUserDetailsObject('vat4', this.codiceDestinarioText);
    }
    if (this.legalEmailText) {
      this.userService.updateUserDetailsObject('legalEmail', this.legalEmailText);
    }
    if (this.codiceCUPText) {
      this.cartStoreService.updateCartState('codiceCUP', this.codiceCUPText);
    }
    if (this.codiceCIGText) {
      this.cartStoreService.updateCartState('codiceCIG', this.codiceCIGText);
    }
  }

  // if either codice or legal email are valid
  // and the other field is entered invalid
  // then it will send the empty value for the invalid one
  // e.g ana@legal.com and 1234 will send empty codice
  onControlChanges(controlName: string, checkIfValid?: boolean): void {
    this.subscription.add(
      this.codiceForm
        .get(controlName)
        ?.valueChanges.pipe(debounceTime(500), distinctUntilChanged())
        .subscribe((value: string) => {
          if (controlName === 'codiceCUP' || controlName === 'codiceCIG') {
            this.handleCUPAndCUGChanges(true);
            this.assignText(controlName, value);
          } else if (controlName === 'codiceDestinario') {
            this.handleCodiceChanges();
          } else if (controlName === 'legalEmail') {
            this.handleLegalEmailChanges();
          }
        }),
    );
  }

  handleExtendedCodiceClick(): void {
    this.isCodiceExtended = !this.isCodiceExtended;
    this.handleCUPAndCUGChanges(this.isCodiceExtended);
  }

  handleCUPAndCUGChanges(isCUPCIGSelected: boolean): void {
    const vat4 = this.codiceForm.controls.codiceDestinario;
    const legalEmail = this.codiceForm.controls.legalEmail;

    const body = {
      vat4: vat4?.value && !vat4?.errors ? vat4?.value : '',
      legalEmail: legalEmail?.value && !legalEmail?.errors ? legalEmail?.value : '',
      codiceCUP: this.checkIfCupCigIsValid(
        this.codiceForm.controls.codiceCUP?.value,
        this.codiceForm.get('codiceCUP').valid,
        isCUPCIGSelected,
      ),
      codiceCIG: this.checkIfCupCigIsValid(
        this.codiceForm.controls.codiceCIG?.value,
        this.codiceForm.get('codiceCIG').valid,
        isCUPCIGSelected,
      ),
    };

    this.postVatRequest(body);
  }

  checkIfCupCigIsValid(value: string, isValid: boolean, isCUPCIGSelected: boolean): string {
    return isCUPCIGSelected && isValid ? value : '';
  }

  handleCodiceChanges(): void {
    let body = {};
    if (this.codiceForm.controls.legalEmail?.value) {
      if (this.codiceForm.controls.legalEmail?.valid) {
        if (this.codiceForm.controls.codiceDestinario?.valid) {
          body = this.setVatRequestBody();
          this.postVatRequest(body);
        } else {
          body = this.setVatBodyEmptyCodice();
          this.postVatRequest(body);
        }
      } else {
        if (this.codiceForm.controls.codiceDestinario?.valid) {
          body = this.setVatBodyEmptyEmail();
          this.postVatRequest(body);
        }
      }
    } else if (this.codiceForm.controls.codiceDestinario?.valid) {
      body = this.setVatBodyEmptyEmail();
      this.postVatRequest(body);
    }
  }

  handleLegalEmailChanges(): void {
    let body = {};
    if (this.codiceForm.controls.codiceDestinario?.value) {
      if (this.codiceForm.controls.codiceDestinario?.valid) {
        if (this.codiceForm.controls.legalEmail?.valid) {
          body = this.setVatRequestBody();
          this.postVatRequest(body);
        } else {
          body = this.setVatBodyEmptyEmail();
          this.postVatRequest(body);
        }
      } else {
        if (this.codiceForm.controls.legalEmail?.valid) {
          body = this.setVatBodyEmptyCodice();
          this.postVatRequest(body);
        }
      }
    } else if (this.codiceForm.controls.legalEmail?.valid) {
      body = this.setVatRequestBody();
      this.postVatRequest(body);
    }
  }

  setVatRequestBody(): VatRequestBody {
    return {
      vat4: this.codiceForm.controls.codiceDestinario?.value,
      legalEmail: this.codiceForm.controls.legalEmail?.value,
      codiceCUP: this.codiceForm.controls.codiceCUP?.value,
      codiceCIG: this.codiceForm.controls.codiceCIG?.value,
    };
  }

  setVatBodyEmptyCodice(): VatRequestBody {
    return {
      vat4: '',
      legalEmail: this.codiceForm.controls.legalEmail?.value,
      codiceCUP: this.codiceForm.controls.codiceCUP?.value,
      codiceCIG: this.codiceForm.controls.codiceCIG?.value,
    };
  }

  setVatBodyEmptyEmail(): VatRequestBody {
    return {
      vat4: this.codiceForm.controls.codiceDestinario?.value,
      legalEmail: '',
      codiceCUP: this.codiceForm.controls.codiceCUP?.value,
      codiceCIG: this.codiceForm.controls.codiceCIG?.value,
    };
  }

  assignText(controlName, value) {
    switch (controlName) {
      case 'codiceDestinario': {
        this.codiceDestinarioText = value;
        break;
      }
      case 'legalEmail': {
        this.legalEmailText = value;
        break;
      }
      case 'codiceCUP': {
        this.codiceCUPText = value;
        break;
      }
      case 'codiceCIG': {
        this.codiceCIGText = value;
        break;
      }
    }
  }

  postVatRequest(body): void {
    this.subscription.add(this.checkoutService.setCodiceFields(body).subscribe());
  }

  displayLegalEmail($event): void {
    $event.preventDefault();
    this.isDisplayCodiceDestinario = false;
    this.isDisplayLegalEmail = true;
  }

  displayCodiceDestinario($event): void {
    $event.preventDefault();
    this.isDisplayCodiceDestinario = true;
    this.isDisplayLegalEmail = false;
  }

  isValidField(controlName: string): boolean {
    return this.codiceForm.get(controlName)?.value && !this.codiceForm.get(controlName)?.errors;
  }

  isInvalidField(controlName: string): boolean {
    return this.codiceForm.get(controlName)?.errors && this.codiceForm.get(controlName).value;
  }

  codiceDestinario(): AbstractControl {
    return this.codiceForm.get('codiceDestinario');
  }

  legalEmail(): AbstractControl {
    return this.codiceForm.get('legalEmail');
  }

  codiceCUP(): AbstractControl {
    return this.codiceForm.get('codiceCUP');
  }

  codiceCIG(): AbstractControl {
    return this.codiceForm.get('codiceCIG');
  }
}
