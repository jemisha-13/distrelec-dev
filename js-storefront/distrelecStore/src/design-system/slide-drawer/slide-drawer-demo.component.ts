import { Component, Input, ViewChild } from '@angular/core';
import { SlideDirection, SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { SlideDrawerComponent } from '@design-system/slide-drawer/slide-drawer.component';

@Component({
  selector: 'app-slide-drawer-demo',
  template: `
    <header [class.is-fixed]="direction === 'FROM_SEARCH'">Demo Header</header>
    <div class="button-container">
      <app-dist-button (click)="onClick($event)" width="w-fixed">Open Drawer</app-dist-button>
    </div>
    <app-dist-slide-drawer
      #drawer
      [direction]="direction"
      [title]="title"
      [secondTitle]="secondTitle"
      [uid]="uid"
      [enableSecondPanel]="enableSecondPanel"
    >
      <p>Hello!</p>

      <app-dist-button *ngIf="enableSecondPanel" (click)="openSecondPanel()" width="w-fixed">
        Open Second Level
      </app-dist-button>

      <ng-container *ngIf="showSecondPanel" secondPanel>
        <p>Hello!</p>
      </ng-container>
    </app-dist-slide-drawer>
  `,
  styles: [
    `
      header {
        position: relative;
        height: 160px;
        margin-bottom: 20px;
        padding: 15px;
        text-align: center;
        border-bottom: 1px solid #ccc;
        background-color: #fff;
      }

      .is-fixed {
        z-index: 1050;
      }

      .button-container {
        display: flex;
        justify-content: center;
      }
    `,
  ],
})
export class SlideDrawerDemoComponent {
  @Input() direction: SlideDirection = 'LEFT';
  @Input() title = 'Title';
  @Input() secondTitle = 'Second Title';
  @Input() uid = 'demo-panel';
  @Input() enableSecondPanel = false;

  showSecondPanel = false;

  // eslint-disable-next-line @typescript-eslint/naming-convention
  private _drawer: SlideDrawerComponent;

  @ViewChild('drawer', { static: false })
  set drawer(value: SlideDrawerComponent) {
    this._drawer = value;
  }

  get drawer(): SlideDrawerComponent {
    return this._drawer;
  }

  constructor(private slideDrawerService: SlideDrawerService) {}

  onClick($event) {
    this.slideDrawerService.openPanel($event, this.uid);
  }

  openSecondPanel() {
    this.drawer.openSecondPanel();
  }
}
