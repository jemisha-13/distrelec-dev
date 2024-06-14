import { Injectable } from '@angular/core';
import { EventService, GlobalMessageService, GlobalMessageType } from '@spartacus/core';
import { NavigationEvent } from '@spartacus/storefront';
import { BehaviorSubject, from, fromEvent, Observable, Subscription, timer } from 'rxjs';
import { filter, first, map, mergeMap, throttleTime } from 'rxjs/operators';
import { LoginService } from './login.service';
import { DistLogoutService } from './logout.service';

@Injectable({
  providedIn: 'root',
})
export class IdleTimeoutService {
  public lastActive$: Observable<Date>;

  subscriptions: Subscription;

  idleTimerSubscription: Subscription;

  //Timer tick is a period of checking the last active date
  private timerTick = 500;

  //Put a small delay between event emitters to help the performance
  private eventDelay = 500;

  //Track the value through the local storage for accesing it on different browser tabs
  private localStorageKey = '__lastActive';

  // Events that is going to interrupt the idle state
  private events: string[] = [
    'keydown',
    'click',
    'wheel',
    'mousemove',
    'DOMMouseScroll',
    'mousewheel',
    'touchmove',
    'MSPointerMove',
  ];

  private lastActive: BehaviorSubject<Date>;

  constructor(
    private eventService: EventService,
    private globalMessageService: GlobalMessageService,
    private logoutService: DistLogoutService,
  ) {}

  initIdleTimeout(timeoutSeconds: number) {
    if (this.subscriptions === undefined) {
      this.subscriptions = new Subscription();

      const lastActiveDate = this.getLastActiveFromLocalStorage() ?? new Date();
      this.lastActive = new BehaviorSubject<Date>(lastActiveDate);
      this.lastActive$ = this.lastActive.asObservable();

      this.subscriptions.add(
        from(this.events)
          .pipe(
            mergeMap((event) => fromEvent(document, event)),
            throttleTime(this.eventDelay),
          )
          .subscribe(() => this.recordLastActiveDate()),
      );

      this.subscriptions.add(
        fromEvent<StorageEvent>(window, 'storage')
          .pipe(
            filter(
              (event) => event.storageArea === localStorage && event.key === this.localStorageKey && !!event.newValue,
            ),
            map((event) => new Date(event?.newValue)),
          )
          .subscribe((newDate) => this.lastActive.next(newDate)),
      );

      this.recordLastActiveDate();
      this.startTimeout(timeoutSeconds);
    }
  }

  getLastActiveDate(): Date {
    return this.lastActive.value;
  }

  destroyIdleSubscriptions() {
    if (this.subscriptions) {
      this.subscriptions.unsubscribe();
      this.subscriptions = undefined;
    }
    if (this.idleTimerSubscription) {
      this.idleTimerSubscription.unsubscribe();
    }
  }

  getLastActiveFromLocalStorage(): Date | null {
    const valueFromStorage = localStorage.getItem(this.localStorageKey);
    if (!valueFromStorage) {
      return null;
    }
    return new Date(valueFromStorage);
  }

  displaySessionExpiredBanner() {
    //After logout navigation display the session expired banner for 10 seconds
    this.eventService
      .get(NavigationEvent)
      .pipe(first())
      .subscribe(() => {
        this.globalMessageService.add({ key: 'httpHandlers.sessionExpired' }, GlobalMessageType.MSG_TYPE_ERROR, 10000);
      });
  }

  private startTimeout(timeoutSeconds: number) {
    this.idleTimerSubscription = timer(0, this.timerTick).subscribe((_) => {
      const currentDate = new Date();
      const lastActiveDate = this.getLastActiveFromLocalStorage();
      const seconds = (currentDate.getTime() - lastActiveDate.getTime()) / 1000;
      // When the user is idle more than timeout time
      if (seconds > timeoutSeconds) {
        const redirectUrl = '/login';
        this.logoutService.postLogoutRequest(redirectUrl);

        this.destroyIdleSubscriptions();

        this.displaySessionExpiredBanner();
      }
    });
  }

  private recordLastActiveDate() {
    const currentDate = new Date();
    localStorage.setItem(this.localStorageKey, currentDate.toString());
    this.lastActive.next(currentDate);
  }
}
