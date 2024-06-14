import { Component } from '@angular/core';
import { ScrollBarComponent } from './scroll-bar.component';

@Component({
  selector: 'app-dist-scroll-bar-demo',
  template: `
    <div #scrollContainer class="scroll-container">
      <div style="height: 50px; overflow: scroll;scrollbar-width: none; width: 200px; display: flex;">
        <p>
          This is dummy data to simulate overflow
          <br />
        </p>
        <p>
          This is dummy data to simulate overflow
          <br />
        </p>
        <p>
          This is dummy data to simulate overflow
          <br />
        </p>
        <p>
          This is dummy data to simulate overflow
          <br />
        </p>
        <p>
          This is dummy data to simulate overflow
          <br />
        </p>
      </div>
    </div>

    <div *ngIf="showScrollBar_ | async" class="custom-scroll" [class.horizontal]="scrollAxis === 'x'">
      <div
        class="thumb"
        [ngStyle]="
          scrollAxis === 'y'
            ? {
                transform: 'translate3d(0,' + (scrollbarPosition_ | async) + 'px, 0)',
                height: (scrollbarThumbSize_ | async) + 'px'
              }
            : {
                transform: 'translate3d(' + (scrollbarPosition_ | async) + 'px, 0, 0)',
                width: (scrollbarThumbSize_ | async) + 'px'
              }
        "
        (mousedown)="startDrag($event)"
      ></div>
    </div>
  `,
  styleUrls: ['./scroll-bar.component.scss'],
})
export class ScrollBarDemoComponent extends ScrollBarComponent {}
