import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-better-world-label',
  templateUrl: './better-world-label.component.html',
  styleUrl: './better-world-label.component.scss',
})
export class BetterWorldLabelComponent {
  @Input() isPlp: boolean;
  @Input() productCode: string;
}
