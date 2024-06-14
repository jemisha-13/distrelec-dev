import { AfterViewInit, Component, ElementRef, Input, ViewChild } from '@angular/core';

export type ButtonColour = 'green' | 'white' | 'red' | 'grey' | 'orange' | 'outline-orange';
export type ButtonModifier = 'fixed-width' | 'sm' | 'sm-text';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss'],
})
export class ButtonComponent implements AfterViewInit {
  @Input() type = 'text';
  @Input() disabled = false;
  @Input() colour: ButtonColour = 'green';
  @Input() modifiers: ButtonModifier[] = [];
  @Input() title?: string;
  @Input() dataAttributes?: Record<string, string | number>;

  @Input() class?: string; // Prefer explicit modifiers

  @Input() buttonId?: string = 'app_button';

  @ViewChild('button')
  button: ElementRef;

  ngAfterViewInit() {
    const el = this.button.nativeElement;
    if (this.dataAttributes) {
      Object.keys(this.dataAttributes).forEach((key) => (el[`data-${key}`] = this.dataAttributes[key]));
    }
  }

  getClassList(): string {
    const base = 'button';
    let classList = `${base} ${base}--${this.colour}`;

    this.modifiers.forEach((m) => (classList += ` ${base}--${m}`));

    if (this.class) {
      // There is a global stylesheet with classes for .mat-button defined so replace them to avoid conflicts
      classList += ` ${this.class.replace(/mat-button/g, 'button')}`;
    }

    return classList;
  }
}
