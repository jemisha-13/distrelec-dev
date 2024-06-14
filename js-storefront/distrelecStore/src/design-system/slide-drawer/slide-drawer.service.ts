import { Injectable, NgZone, OnDestroy } from "@angular/core";
import { BehaviorSubject, fromEvent, Observable, ReplaySubject } from "rxjs";
import { NavigationEnd, Router } from '@angular/router';
import { filter, tap, startWith, switchMap, take, takeWhile } from 'rxjs/operators';
import { WindowRef } from '@spartacus/core';
import { DistBreakpointService } from '@services/breakpoint.service';
import { BREAKPOINT } from '@spartacus/storefront';
import { AppendComponentService } from '@services/append-component.service';
import { DistCartService } from '@services/cart.service';

export type SlideDirection = 'RIGHT' | 'LEFT' | 'TOP' | 'BOTTOM' | 'FROM_SEARCH';

@Injectable({
  providedIn: 'root',
})
export class SlideDrawerService implements OnDestroy {

  destroy$ = new ReplaySubject<void>();

  public onRouteChange$ = this.router.events.pipe(
    filter((event) => event instanceof NavigationEnd),
  ) as Observable<NavigationEnd>;
  public phasedOutProducts_: BehaviorSubject<string> = this.cartService.phasedOutProducts_;

  private escapeKeyEvent$ = fromEvent(this.winRef.document, 'keyup').pipe(
    filter((event: KeyboardEvent) => event.key === 'Escape'),
    tap(() => {
      this.appendComponentService.removeEnergyEfficiencyModal();
      this.appendComponentService.removeBackdropComponentFromBody();
    }),
  );

  private panelCloseTimer: ReturnType<typeof setTimeout>;

  private showDrawer_: BehaviorSubject<string> = new BehaviorSubject<string>('');
  private slideDirection_: BehaviorSubject<SlideDirection | ''> = new BehaviorSubject<SlideDirection | ''>('');
  private hasEscapedSearch_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(
    private router: Router,
    private winRef: WindowRef,
    private distBreakPointService: DistBreakpointService,
    private appendComponentService: AppendComponentService,
    private ngZone: NgZone,
    private cartService: DistCartService,
  ) {
    this.showDrawer_
      .pipe(
        tap((uid: string) => {
          this.appendComponentService.removeBackdropComponentFromBody();
          if (uid === '') {
            this.winRef.document.body.classList.remove('is-locked');
          } else {
            this.winRef.document.body.classList.add('is-locked');
          }
        }),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  public openPanel(event: Event, uid: string, direction?: SlideDirection): void {
    event.preventDefault();
    const headerElement = this.winRef.document.querySelector('header');
    const isHeaderSticky = headerElement?.classList.contains('sticky') ?? false;

    const target = isHeaderSticky && direction !== 'FROM_SEARCH' ? headerElement : undefined;

    this.addScrollbarPadding();
    this.showDrawer_.next(uid);
    this.slideDirection_.next(direction);
    this.appendComponentService.appendBackdropModal({ fadeIn: true }, target);

    this.escapeActionPanelOpen$().pipe(
      take(1),
    ).subscribe(() => {
      this.clearSearchTerm(true);
      this.closePanel();
    });

    if (this.showDrawer_.value !== 'search_results' && this.showDrawer_.value !== '') {
      this.clearSearchTerm(true);
    }


  }

  openPanelForLargeScreens(event) {
    this.distBreakPointService
      .isUp(BREAKPOINT.lg)
      .pipe(take(1))
      .subscribe((isTabletPlus) => {
        if (isTabletPlus === true) {
          event.stopPropagation();
          this.openPanel(event, 'cart-tray');
          this.ngZone.run(() => {
            this.panelCloseTimer = setTimeout(() => {
              this.closePanel();
            }, 3000);
          });
        }
      });
  }

  public cancelPanelCloseTimeout(event: Event): void {
    event.stopPropagation();
    clearTimeout(this.panelCloseTimer);
  }

  public closePanel(): void {
    this.showDrawer_.next('');
    this.removeScrollbarPadding();
    this.appendComponentService.removeBackdropComponentFromBody();
    this.phasedOutProducts_.next('');
  }

  public drawer$(): Observable <string> {
    return this.showDrawer_.asObservable();
  }

  public clearSearchTerm(value: boolean): void {
    this.hasEscapedSearch_.next(value);
  }

  private escapeActionPanelOpen$(): Observable<KeyboardEvent>  {
    return this.drawer$().pipe(
      startWith(true),
      takeWhile((val) => val !== ''),
      switchMap(() => this.escapeKeyEvent$),
    );
  }

  get hasEscapedSearch$(): Observable<boolean> {
    return this.hasEscapedSearch_.asObservable();
  }

  get slideDirection$(): Observable<SlideDirection | ''> {
    return this.slideDirection_.asObservable();
  }

  private getScrollbarWidth(): number {
    if (!this.winRef.isBrowser()) {
      return 0;
    }
    return this.winRef.nativeWindow.innerWidth - this.winRef.document.documentElement.clientWidth;
  }

  private addScrollbarPadding() {
    if (this.winRef.isBrowser()) {
      const scrollbarWidth = this.getScrollbarWidth();
      this.winRef.document.body.style.paddingRight = scrollbarWidth + 'px';
    }
  }

  private removeScrollbarPadding() {
    if (this.winRef.isBrowser()) {
      this.winRef.document.body.style.paddingRight = null;
    }
  }
}
