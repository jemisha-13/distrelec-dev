import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-tab',
  template: `
    <ng-container *ngIf="active">
      <ng-content></ng-content>
    </ng-container>
  `,
})
export class TabComponent {
  @Input() tabTitle = '';
  @Input() active = false;
}
