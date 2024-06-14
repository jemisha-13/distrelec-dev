import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { EventService, UserIdService, createFrom } from '@spartacus/core';
import { Observable } from 'rxjs';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { HeaderInteractionEvent } from '@features/tracking/events/header-interaction-event';
import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';

@Component({
  selector: 'app-header-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
})
export class HeaderListComponent {
  userId$: Observable<string> = this.userIdService.getUserId();

  constructor(
    private userIdService: UserIdService,
    private router: Router,
    private slideDrawerService: SlideDrawerService,
    private eventService: EventService,
  ) {}

  displayLoginModule(userId: string, event: MouseEvent): void {
    if (userId === 'current') {
      this.router.navigate(['/shopping']);
    } else {
      this.slideDrawerService.openPanel(event, 'list-drawer-uid');
    }
    this.dispatchHeaderInteractionEvent();
  }

  private dispatchHeaderInteractionEvent(): void {
    this.eventService.dispatch(
      createFrom(HeaderInteractionEvent, { type: Ga4HeaderInteractionEventType.HEADER_MYLIST_CLICK }),
    );
  }
}
