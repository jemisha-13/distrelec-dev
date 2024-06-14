import { Component } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { EventService, UserIdService, WindowRef, createFrom } from '@spartacus/core';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { Channel, CurrentSiteSettings } from '@model/site-settings.model';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { filter, map, tap } from 'rxjs/operators';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { HeaderInteractionEvent } from '@features/tracking/events/header-interaction-event';
import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';

@Component({
  selector: 'app-channel-switcher',
  templateUrl: './channel-switcher.component.html',
  styleUrls: ['./channel-switcher.component.scss'],
})
export class ChannelSwitcherComponent {
  userId$ = this.userIdService.getUserId();
  activeChannel_ = this.channelService.activeChannel_;
  currency$ = this.siteSettingsService.currentChannelData$.pipe(
    filter((data) => !!data?.currency),
    map((data: CurrentSiteSettings) => data.currency),
  );
  subscriptions: Subscription = new Subscription();

  isUK$: Observable<boolean> = this.countryService.getActive().pipe(
    map((country) => country === 'GB' || country === 'XI'),
    tap((isUK) => {
      if (isUK) {
        this.channelService.setActive('B2B');
      }
    }),
  );

  constructor(
    protected userIdService: UserIdService,
    private channelService: ChannelService,
    private winRef: WindowRef,
    private countryService: CountryService,
    private siteSettingsService: AllsitesettingsService,
    private eventService: EventService,
  ) {}

  public switchChannel(activeChannel: Channel): void {
    if (activeChannel === 'B2B') {
      this.channelService.setActive('B2C');
    } else {
      this.channelService.setActive('B2B');
    }
    this.dispatchHeaderInteractionEvent();
    this.winRef.location.reload();
  }

  private dispatchHeaderInteractionEvent(): void {
    this.eventService.dispatch(
      createFrom(HeaderInteractionEvent, { type: Ga4HeaderInteractionEventType.HEADER_TOGGLE_SELECTION }),
    );
  }
}
