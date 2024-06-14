import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Error404Event } from '@features/tracking/events/ga4/error-404-event';
import { Ga4Error404EventType } from '@features/tracking/events/ga4/ga4-error-404-event';
import { DistrelecUserService } from '@services/user.service';
import { EventService, OCC_USER_ID_ANONYMOUS, OCC_USER_ID_CURRENT, UserIdService, createFrom } from '@spartacus/core';
import { Subscription, combineLatest } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss'],
})
export class ErrorPageComponent implements OnInit, OnDestroy {
  titleKey = 'error-not-found.title';
  subtitleKey = 'error-not-found.subtitle';

  private subscriptions = new Subscription();

  constructor(
    private router: Router,
    private eventsService: EventService,
    private userService: DistrelecUserService,
    private userIdService: UserIdService,
  ) {}

  ngOnInit(): void {
    this.dispatchError404Event();

    if (this.router.url.endsWith('/error')) {
      this.titleKey = 'error-generic.title';
      this.subtitleKey = 'error-generic.subtitle';
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  dispatchError404Event(): void {
    this.subscriptions.add(
      combineLatest([this.userIdService.getUserId(), this.userService.getUserDetails()])
        .pipe(
          tap(([userId, user]) => {
            if ((userId === OCC_USER_ID_CURRENT && user) || userId === OCC_USER_ID_ANONYMOUS) {
              this.eventsService.dispatch(createFrom(Error404Event, { event: Ga4Error404EventType.ERROR_404, user }));
            }
          }),
        )
        .subscribe(),
    );
  }
}
