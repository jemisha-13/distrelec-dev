import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { SlideDrawerService, SlideDirection } from './slide-drawer.service';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import { distinctUntilChanged, map, mapTo, startWith, switchMap, tap } from 'rxjs/operators';
import { BehaviorSubject, Subscription } from 'rxjs';
import { WindowRef } from '@spartacus/core';
import { HeaderService } from '@features/shared-modules/header/header.service';
import { DistCartService } from '@services/cart.service';

@Component({
  selector: 'app-dist-slide-drawer',
  templateUrl: './slide-drawer.component.html',
  styleUrls: ['./slide-drawer.component.scss'],
})
export class SlideDrawerComponent {
  @Input() direction: SlideDirection = 'LEFT';
  @Input() title: string;
  @Input() secondTitle: string;
  @Input() uid: string;
  @Input() enableSecondPanel = false;

  public searchTop = this.headerService.headerPosition;
  public isBrowser = this.winRef.isBrowser();
  public faChevronLeft = faChevronLeft;
  public showSecondPanel_ = new BehaviorSubject<boolean>(false);
  public phasedOutProducts_: BehaviorSubject<string> = this.cartService.phasedOutProducts_;

  //TODO: this seems very cicular and prone to an infinate loop, we should look at altering this at some point.
  public showDrawer$ = this.slideDrawerService.drawer$().pipe(
    map((val) => val === this.uid),
    distinctUntilChanged(),
    switchMap((val) =>
      this.slideDrawerService.onRouteChange$.pipe(
        tap(() => this.closePanel()),
        mapTo(val),
        startWith(val),
      ),
    ),
  );

  constructor(
    private slideDrawerService: SlideDrawerService,
    private winRef: WindowRef,
    private headerService: HeaderService,
    private cartService: DistCartService,
  ) {}

  public closePanel(evt?: Event): void {
    if (evt) {
      evt.stopPropagation();
    }
    this.closeSecondPanel();
    this.slideDrawerService.closePanel();
  }

  public openSecondPanel(): void {
    this.showSecondPanel_.next(true);
  }

  public closeSecondPanel(): void {
    this.showSecondPanel_.next(false);
  }
}
