import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { CmsWarningComponent } from '@model/cms.model';
import { CmsComponentData } from '@spartacus/storefront';
import { BehaviorSubject, Observable, ReplaySubject } from 'rxjs';
import { takeUntil, filter, map, delay, take } from 'rxjs/operators';
import { LocalStorageService } from '@services/local-storage.service';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { HeaderService } from '@features/shared-modules/header/header.service';
import { cancel } from '@assets/icons/icon-index';
import { NavigationEnd, Router } from '@angular/router';
import { WindowRef } from '@spartacus/core';
import { DistIcon } from '@model/icon.model';
import { DistCookieService } from '@services/dist-cookie.service';

const CLOSED_WARNING_MESSAGES = 'closedWarningBanners';
const DEFAULT_HEIGHT = '53px';
const SINGLE_LINE_HEIGHT = 21;

type MetaData = { uid: string; modifiedTime: Date };

@Component({
  selector: 'app-dist-warning',
  templateUrl: './dist-warning.component.html',
  styleUrls: ['./dist-warning.component.scss'],
})
export class DistWarningComponent implements OnInit, OnDestroy, AfterViewInit {
  private static messages$: BehaviorSubject<CmsWarningComponent[]> = new BehaviorSubject([]);
  private static totalInstances = 0;

  @ViewChild('warning', { static: false })
  warningElement: ElementRef;
  @ViewChild('ghost', { static: false })
  ghostElement: ElementRef;

  isExpanded$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  showToggle$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  animationPlaying$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  warningMessages$: Observable<CmsWarningComponent[]> = DistWarningComponent.messages$.asObservable();

  instance = DistWarningComponent.totalInstances++;
  cancel: DistIcon = cancel;
  pageLoaded = false;

  private destroy$ = new ReplaySubject<void>();

  constructor(
    private cms: CmsComponentData<CmsWarningComponent>,
    private localStorage: LocalStorageService,
    private headerService: HeaderService,
    private slideDrawerService: SlideDrawerService,
    private router: Router,
    private winRef: WindowRef,
    private cookieService: DistCookieService,
    private renderer: Renderer2,
  ) {}

  @HostListener('window:scroll', [])
  onScroll() {
    if (this.isExpanded$.value) {
      this.isExpanded$.next(false);
    }
  }

  ngOnInit(): void {
    this.loadMessages();

    this.router.events
      .pipe(
        takeUntil(this.destroy$),
        filter((event) => event instanceof NavigationEnd),
      )
      .subscribe(() => {
        if (this.instance === 0) {
          DistWarningComponent.messages$.next([]);
          this.isExpanded$.next(false);
          this.updateHeaderHeight();
        }
        this.loadMessages();
      });

    if (this.instance !== 0) {
      return;
    }

    this.slideDrawerService
      .drawer$()
      .pipe(
        takeUntil(this.destroy$),
        filter((val) => !!val),
      )
      .subscribe(() => {
        this.isExpanded$.next(false);
      });
  }

  ngAfterViewInit(): void {
    if (this.instance !== 0) {
      return;
    }

    if (this.winRef.isBrowser()) {
      this.isExpanded$.pipe(takeUntil(this.destroy$), delay(0)).subscribe((value: boolean) => {
        this.toggleState();
        this.calculateHeight(value);
        this.updateHeaderHeight();
      });
    }
  }

  ngOnDestroy(): void {
    if (DistWarningComponent.totalInstances === 1) {
      DistWarningComponent.messages$.next([]);
      this.updateHeaderHeight();
    }
    DistWarningComponent.totalInstances--;
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleView(): void {
    this.pageLoaded = true;
    this.animationPlaying$.next(true);
    this.isExpanded$.next(!this.isExpanded$.value);
    this.slideDrawerService.clearSearchTerm(true);
    this.slideDrawerService.closePanel();
  }

  closeInfoMessage(uid: string): void {
    const messages: Set<CmsWarningComponent> = new Set(DistWarningComponent.messages$.value);
    const closed: Set<MetaData> = new Set(this.localStorage.getItem(CLOSED_WARNING_MESSAGES)) ?? new Set();

    messages.forEach((message: CmsWarningComponent) => {
      if (message.uid === uid) {
        closed.add({ uid: message.uid, modifiedTime: message.modifiedTime });
        messages.delete(message);
        DistWarningComponent.messages$.next(Array.from(messages));
      }
    });

    this.localStorage.setItem(CLOSED_WARNING_MESSAGES, Array.from(closed));
    this.animationPlaying$.next(this.isExpanded$.value);
    this.isExpanded$.next(false);
    this.updateHeaderHeight();
    this.cookieService.set(CLOSED_WARNING_MESSAGES, 'true', 28, '/');
  }

  getHeadline(): string {
    if (DistWarningComponent.messages$.value.length === 0) {
      return '';
    }

    const message = Array.from(DistWarningComponent.messages$.value)[0];
    return message?.headline ?? '';
  }

  getBody(): string {
    if (DistWarningComponent.messages$.value.length === 0) {
      return '';
    }
    const message = Array.from(DistWarningComponent.messages$.value)[0];

    if (!this.getHeadline() && !this.isExpanded$.value) {
      const span: HTMLElement = this.renderer.createElement('span');
      span.innerHTML = message?.body;
      if (span.children.length > 0) {
        this.showToggle$.next(true);
      }

      return span.firstChild.textContent;
    }

    return message?.body ?? '';
  }

  onAnimationEnd(): void {
    this.animationPlaying$.next(false);
  }

  private calculateHeight(isExpanded: boolean): void {
    if (DistWarningComponent.messages$.value.length === 0) {
      return;
    }

    const mainEl: HTMLElement = this.warningElement?.nativeElement;
    const ghostEl: HTMLElement = this.ghostElement?.nativeElement;
    const MAX_BANNER_HEIGHT = 400;

    const height = ghostEl.clientHeight > MAX_BANNER_HEIGHT ? MAX_BANNER_HEIGHT : ghostEl.clientHeight;
    mainEl.style.maxHeight = isExpanded ? `${height}px` : DEFAULT_HEIGHT;
    if (isExpanded) {
      mainEl.style.height = `${height}px`;
    }
  }

  private removeOldMessages(): void {
    if (this.cookieService.get(CLOSED_WARNING_MESSAGES) === '') {
      this.localStorage.setItem(CLOSED_WARNING_MESSAGES, []);
    }
  }

  private loadMessages(): void {
    this.cms.data$
      .pipe(
        take(1),
        filter((data) => (data.body || data.headline) !== undefined),
        map((data: CmsWarningComponent) => {
          const messages: string[] = DistWarningComponent.messages$.value.map((_) => JSON.stringify(_));
          messages.push(JSON.stringify(data));

          const closedUids: string[] = ((this.localStorage.getItem(CLOSED_WARNING_MESSAGES) ?? []) as Array<MetaData>)
            .filter((value: MetaData) => value.uid === data.uid && value.modifiedTime === data.modifiedTime)
            .map((value: MetaData) => value.uid);

          DistWarningComponent.messages$.next(
            Array.from(new Set(messages)) //remove duplicates by converting string array to Set then parse back to objects
              .map((_) => JSON.parse(_))
              .filter((message: CmsWarningComponent) => !closedUids.includes(message.uid))
              .sort((a, b) => new Date(b.modifiedTime).getTime() - new Date(a.modifiedTime).getTime()),
          );
        }),
      )
      .subscribe(() => {
        if (this.winRef.isBrowser() && this.instance === 0) {
          this.removeOldMessages();
          this.updateHeaderHeight();
        }
      });
  }

  private toggleState(): void {
    const message: CmsWarningComponent = Array.from(DistWarningComponent.messages$.value)[0];
    if ((message?.body && !message?.headline) || (!message?.body && message?.headline)) {
      const computed = this.winRef.nativeWindow.getComputedStyle(this.ghostElement.nativeElement);
      const height = parseFloat(computed.height) - parseFloat(computed.paddingTop) - parseFloat(computed.paddingBottom);
      this.showToggle$.next(height > SINGLE_LINE_HEIGHT);
    } else {
      this.showToggle$.next(true);
    }
  }

  private updateHeaderHeight(): void {
    this.headerService.hasActiveWarnings = this.hasWarnings;
    this.headerService.recalculateHeaderHeight();
  }

  private get hasWarnings(): boolean {
    return DistWarningComponent.messages$.value.length > 0;
  }
}
