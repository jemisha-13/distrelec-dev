import { Component, Input, OnDestroy } from '@angular/core';
import { AppendComponentService } from '@services/append-component.service';
import { BackDropModuleService } from './backdrop-module.service';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { Subscription } from 'rxjs';
import { filter, take } from 'rxjs/operators';

export interface BackdropOptions {
  lightTheme?: boolean;
  opacity?: string;
  fadeIn?: boolean;
  target?: string;
  onClick?: () => void;
}

@Component({
  selector: 'app-backdrop-module',
  templateUrl: './backdrop-module.component.html',
  styleUrls: ['./backdrop-module.component.scss'],
})
export class BackdropModuleComponent implements OnDestroy {
  @Input() data: BackdropOptions;

  subscriptions = new Subscription();

  constructor(
    private appendComponentService: AppendComponentService,
    private backDropModuleService: BackDropModuleService,
    private slideDrawerService: SlideDrawerService,
  ) {}

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  onClick(): void {
    if (this.data.target === 'APP-ENERGY-EFFICIENCY-LABEL') {
      this.appendComponentService.removeEnergyEfficiencyModal();
      this.removeBackdrop();
    } else {
      this.appendComponentService.destroyTemproryRef();
      this.removeBackdrop();

      this.backDropModuleService.overlayClickSignal_.next(true);
      this.subscriptions.add(
        this.slideDrawerService
          .drawer$()
          .pipe(filter(Boolean), take(1))
          .subscribe(() => this.slideDrawerService.closePanel()),
      );
    }
  }

  removeBackdrop(): void {
    this.appendComponentService.removeBackdropComponentFromBody(this.data.target);
    this.data.opacity = '';
  }
}
