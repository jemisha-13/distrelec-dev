import { Component, Input, OnChanges, SimpleChange } from '@angular/core';

@Component({
  selector: 'app-password-strength-bar',
  templateUrl: './password-strength-bar.component.html',
  styleUrls: ['./password-strength-bar.component.scss'],
})
export class PasswordStrengthBarComponent implements OnChanges {
  @Input() public passwordToCheck: string;

  strengthScore = 0;

  checkStrength(password: string): number {
    let passedMatches = 0;

    // Rules which determine strength score
    const regex = /[!@#\$%\^\&*\)\(\[\]\{\}+=.,;:"'?_-]+/.test(password);
    const lowerAndUpper = /[a-z]+/.test(password) && /[A-Z]+/.test(password);
    const numbers = /[0-9]+/.test(password);
    const lengthMin = password.length > 8;
    const lengthRec = password.length > 12;

    const flags = [regex, lowerAndUpper, numbers, lengthMin, lengthRec];

    for (const flag of flags) {
      passedMatches += flag ? 1 : 0;
    }

    if (passedMatches === 0 && password.length !== 0) {
      passedMatches = 1;
    }

    return passedMatches;
  }

  ngOnChanges(changes: { [propName: string]: SimpleChange }): void {
    const password = changes.passwordToCheck.currentValue;
    this.strengthScore = password ? this.checkStrength(password) : 0;
  }
}
