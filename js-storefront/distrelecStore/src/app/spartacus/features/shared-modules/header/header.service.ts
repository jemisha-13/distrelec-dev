import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { DistBreakpointService } from '@services/breakpoint.service';
import { WindowRef } from '@spartacus/core';
import { BehaviorSubject, ReplaySubject, combineLatest, Observable } from 'rxjs';
import { first, takeUntil } from 'rxjs/operators';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';

export enum HeaderHeights {
  XXL = 160,
  SM = 102,
  XXLWARNING = 210,
  SMWARNING = 154,
}

@Injectable({
  providedIn: 'root',
})
export class HeaderService implements OnDestroy {
  public isSticky: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public showStickyHeader: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public headerHeight = HeaderHeights.XXL;
  public headerPosition: BehaviorSubject<number> = new BehaviorSubject<number>(this.headerHeight);

  private hasActiveWarnings_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private timeoutId: number | undefined;
  private wasScrollingUp = false;
  private lastScrollValue = 0;
  private scrollCounter = 0;
  private scrollBuffer = 50;
  private forceStickyHeader = false;
  private resetFlag = true;

  private readonly destroyed$ = new ReplaySubject<boolean>(1);

  constructor(
    private slideDrawerService: SlideDrawerService,
    private winRef: WindowRef,
    private ngZone: NgZone,
    private breakpointService: DistBreakpointService,
  ) {
    this.breakpointService
      .isMobileBreakpoint()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((value: boolean) => {
        this.headerHeight = value ? HeaderHeights.SM : HeaderHeights.XXL;
        this.getHeaderPosition();
      });

    combineLatest([this.slideDrawerService.slideDirection$, this.slideDrawerService.drawer$()]).subscribe(
      ([direction, drawerState]) => {
        const headerElement = this.winRef.document.querySelectorAll('header')[0];

        if (headerElement === undefined) {
          return;
        }

        this.clearTimeout();
        if (direction === 'FROM_SEARCH' && drawerState === 'search_results') {
          headerElement.classList.add('fixed');
          this.forceStickyHeader = true;
          return;
        } else {
          this.forceStickyHeader = false;
        }

        if (drawerState === '' && direction === 'FROM_SEARCH') {
          this.ngZone.run(() => {
            setTimeout(() => headerElement.classList.remove('fixed'), 400);
          });
        } else {
          headerElement.classList.remove('fixed');
        }
      },
    );
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  public checkScroll(): void {
    this.getHeaderPosition();

    this.resetFlag = true;

    const scroll = this.winRef.isBrowser() ? this.winRef.nativeWindow.scrollY : 0;
    const isStickyAt = scroll > this.lastScrollValue ? this.headerHeight : 0;
    this.isSticky.next(scroll > isStickyAt);

    //check if we should show the header or not when it's sticky
    const isScrollingUp = scroll < this.lastScrollValue;

    if (this.wasScrollingUp === isScrollingUp) {
      this.scrollCounter += Math.abs(scroll - this.lastScrollValue);
    } else {
      this.scrollCounter = 0;
    }

    this.wasScrollingUp = isScrollingUp;
    if (scroll >= isStickyAt) {
      this.lastScrollValue = scroll;
    }

    if (this.forceStickyHeader) {
      this.showStickyHeader.next(true);
    } else if (this.scrollCounter >= this.scrollBuffer) {
      this.showStickyHeader.next(isScrollingUp);
    }
  }

  public resetScroll(): void {
    if (this.resetFlag) {
      this.isSticky.next(false);
      this.showStickyHeader.next(false);
      this.scrollCounter = 0;
      this.lastScrollValue = 0;
      this.wasScrollingUp = false;
      this.resetFlag = false;
    }
  }

  public recalculateHeaderHeight(): void {
    this.breakpointService
      .isMobileBreakpoint()
      .pipe(first())
      .subscribe((isMobile: boolean) => {
        let height: HeaderHeights;
        if (this.hasActiveWarnings_.value) {
          height = isMobile ? HeaderHeights.SMWARNING : HeaderHeights.XXLWARNING;
        } else {
          height = isMobile ? HeaderHeights.SM : HeaderHeights.XXL;
        }
        this.headerHeight = height;
        this.getHeaderPosition();
      });
  }

  set hasActiveWarnings(value: boolean) {
    this.hasActiveWarnings_.next(value);
  }

  get hasActiveWarnings$(): Observable<boolean> {
    return this.hasActiveWarnings_.asObservable();
  }

  private getHeaderPosition(): void {
    const scroll = this.winRef.isBrowser() ? this.winRef.nativeWindow.scrollY : 0;

    if (this.showStickyHeader.value === true) {
      this.headerPosition.next(this.headerHeight);
    } else {
      this.headerPosition.next(scroll < this.headerHeight ? this.headerHeight - scroll : this.headerHeight);
    }
  }

  private clearTimeout() {
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
      this.timeoutId = undefined;
    }
  }
}
