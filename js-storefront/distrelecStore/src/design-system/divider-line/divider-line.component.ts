import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-divider-line',
  templateUrl: './divider-line.component.html',
  styleUrls: ['./divider-line.component.scss'],
})
export class DividerLineComponent {
  @Input() weight: 'thin' | 'thick' = 'thin';
  @Input() type: 'dark' | 'light' = 'light';
  @Input() marginTop = 0;
  @Input() marginBottom = 0;
}
