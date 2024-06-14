import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faUser } from '@fortawesome/free-solid-svg-icons';
import { BaseSite, BaseSiteService, createFrom, EventService, User, UserIdService } from '@spartacus/core';
import { Observable, Subscription } from 'rxjs';
import { DistrelecUserService } from '@services/user.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { filter, map } from 'rxjs/operators';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { Channel } from '@model/site-settings.model';
import { HomepageInteractionEvent } from '@features/tracking/events/homepage-interaction-event';
import { DashboardContents } from '@model/my-account.model';

@Component({
  selector: 'app-home-banner-login',
  templateUrl: './home-banner-login.component.html',
  styleUrls: ['./home-banner-login.component.scss'],
})
export class HomeBannerLoginComponent implements OnInit, OnDestroy {
  @Input() isLoggedIn: boolean;

  isQuotationsEnabled$ = this.siteService.get().pipe(map((data: BaseSite) => data.baseStore.quotationsEnabled));

  isB2B$: Observable<Channel> = this.channelService.activeChannel_.pipe(filter((channel) => channel === 'B2B'));

  userId: string;
  activeSiteId = '';
  userData_: Observable<User> = this.distrelecUserService.userDetails_;
  activeCountrySubscription: Subscription;
  userSubscription: Subscription;
  userDashboardContents$: Observable<DashboardContents>;
  faUser = faUser;

  constructor(
    private userIdService: UserIdService,
    private router: Router,
    private distrelecUserService: DistrelecUserService,
    private countryService: CountryService,
    private siteService: BaseSiteService,
    private channelService: ChannelService,
    private eventService: EventService,
  ) {}

  btnClick(path: string) {
    this.router.navigate([`${path}`]);
  }

  dispatchHomepageInteractionEvent(interaction): void {
    this.eventService.dispatch(createFrom(HomepageInteractionEvent, { type: interaction }));
  }

  ngOnInit(): void {
    this.userSubscription = this.userIdService.getUserId().subscribe((data) => {
      this.userId = data;
      this.userDashboardContents$ = this.distrelecUserService.getContents(data);
    });

    this.activeCountrySubscription = this.countryService
      .getActive()
      .subscribe((siteId) => (this.activeSiteId = siteId));
  }

  ngOnDestroy(): void {
    if (this.activeCountrySubscription && !this.activeCountrySubscription.closed) {
      this.activeCountrySubscription.unsubscribe();
    }
    if (this.userSubscription && !this.userSubscription.closed) {
      this.userSubscription.unsubscribe();
    }
  }
}
