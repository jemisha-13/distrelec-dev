import { Injectable, OnDestroy } from '@angular/core';
import { Observable, of, ReplaySubject, Subscription } from 'rxjs';
import { SiteContext } from '@spartacus/core';
import { Channel } from '@model/site-settings.model';
import { DistrelecUserService } from '@services/user.service';

export const validChannels: Channel[] = ['B2B', 'B2C'];

@Injectable({
  providedIn: 'root',
})
export class ChannelService implements OnDestroy, SiteContext<Channel> {
  activeChannel_ = new ReplaySubject<Channel>(1);

  private subscriptions = new Subscription();

  constructor(private userService: DistrelecUserService) {
    this.subscriptions.add(
      this.userService.getUserDetails().subscribe((user) => {
        if (user?.derivedChannel) {
          this.setActive(user.derivedChannel);
        }
      }),
    );
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  setActive(channel: Channel) {
    this.activeChannel_.next(channel?.toUpperCase() as Channel);
  }

  getActive(): Observable<Channel> {
    return this.activeChannel_;
  }

  getDefaultValue(): Channel {
    return 'B2B';
  }

  isValid(channel: string): boolean {
    return validChannels.includes(channel?.toUpperCase() as Channel);
  }

  getAll(): Observable<Channel[]> {
    return of(validChannels);
  }
}
