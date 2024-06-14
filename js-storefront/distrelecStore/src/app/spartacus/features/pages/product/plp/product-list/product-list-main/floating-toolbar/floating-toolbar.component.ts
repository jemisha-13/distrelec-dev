import { ViewportScroller } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { faArrowUp, faExchangeAlt } from '@fortawesome/free-solid-svg-icons';
import { WindowRef } from '@spartacus/core';
import { Observable, Subscription } from 'rxjs';
import { delay, startWith, tap } from 'rxjs/operators';
import { DistBreakpointService } from '@services/breakpoint.service';
import { EventHelper } from '@features/tracking/event-helper.service';

export enum FloatingToolbarEvents {
  DEFAULT,
  ITEM_ADDED,
  ITEM_REMOVED,
}

@Component({
  selector: 'app-floating-toolbar',
  templateUrl: './floating-toolbar.component.html',
  styleUrls: ['./floating-toolbar.component.scss'],
})
export class FloatingToolbarComponent implements AfterViewInit, OnDestroy {
  @Input() selectedItems: number;
  @Output() clearAll = new EventEmitter<string>();

  faExchangeAlt = faExchangeAlt;
  faArrowUp = faArrowUp;
  floatingToolbarEvents = FloatingToolbarEvents;
  plpMainElement = null;
  isFloating = false;
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  isMobile: boolean;
  currentState: FloatingToolbarEvents;

  private event_: Observable<FloatingToolbarEvents>;
  private subscriptions: Subscription = new Subscription();

  constructor(
    private winRef: WindowRef,
    private viewportScroller: ViewportScroller,
    private elementRef: ElementRef,
    private breakpointService: DistBreakpointService,
    private eventHelper: EventHelper,
  ) {
    this.selectedItems = 0;
    this.currentState = FloatingToolbarEvents.DEFAULT;
    this.event_ = new Observable<FloatingToolbarEvents>();
  }

  @HostListener('window:scroll', ['$event'])
  calculateMiddleScrollingPosition() {
    if (!this.isMobile) {
      const viewportHeight = this.winRef.nativeWindow.innerHeight;
      const currentScrollPosition = this.viewportScroller.getScrollPosition()[1];
      const hostTop = this.elementRef.nativeElement.getBoundingClientRect().top;
      const toolbarBottomProperty = parseFloat(
        window.getComputedStyle(this.elementRef.nativeElement.querySelector('.js-floating-toolbar')).bottom,
      );
      const hostHeight = this.elementRef.nativeElement.offsetHeight;
      const scrollingCalculation = hostTop - viewportHeight + hostHeight + toolbarBottomProperty;

      this.isFloating = currentScrollPosition >= this.plpMainElement.offsetTop && scrollingCalculation >= 0;
    }
  }

  ngAfterViewInit() {
    this.plpMainElement = this.winRef.document.querySelector('.js-plp-main');

    this.subscriptions.add(
      this.isMobileBreakpoint$.subscribe((data) => {
        this.isMobile = data;
      }),
    );
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  get event(): Observable<FloatingToolbarEvents> {
    return this.event_;
  }

  @Input() set eventHandler(handler: Observable<FloatingToolbarEvents>) {
    this.event_ = handler.pipe(
      tap((event) => (this.currentState = event)),
      delay(2000),
      tap((_) => (this.currentState = FloatingToolbarEvents.DEFAULT)),
      startWith(FloatingToolbarEvents.DEFAULT),
    );
  }

  scrollToTop() {
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scrollTo({
        top: this.plpMainElement.offsetTop,
        behavior: 'smooth',
      });
    }
  }

  clearAllProductCookies(cookieName: string) {
    this.clearAll.emit(cookieName);
    this.trackClearAll();
  }

  trackCompare(): void {
    this.eventHelper.trackHotBarCompareEvent();
  }

  private trackClearAll(): void {
    this.eventHelper.trackHotBarClearAllEvent();
  }
}
