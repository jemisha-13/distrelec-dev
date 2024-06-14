import { Injectable, OnDestroy } from '@angular/core';
import { BaseSiteService, LanguageService } from '@spartacus/core';
import { DistCookieService } from '@services/dist-cookie.service';
import { Channel } from '@model/site-settings.model';
import { Subscription } from 'rxjs';
import { ChannelService } from './channel.service';
import { CountryService } from './country.service';

interface Context {
  lang: string;
  country: string;
  channel: Channel;
}

@Injectable({
  providedIn: 'root',
})
export class ContextCookiePersistenceService implements OnDestroy {
  private readonly context: Partial<Context>;
  private subscriptions = new Subscription();

  constructor(
    private siteService: BaseSiteService,
    private languageService: LanguageService,
    private channelService: ChannelService,
    private countryService: CountryService,
    private cookieService: DistCookieService,
  ) {
    this.context = this.getCookieContext();
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  init() {
    this.subscribeToContextChanges();
  }

  private getCookieContext(): Partial<Context> {
    if (this.cookieService.check('siteContext')) {
      return JSON.parse(this.cookieService.get('siteContext'));
    } else {
      return {};
    }
  }

  private subscribeToContextChanges(): void {
    this.subscriptions.add(
      this.languageService.getActive().subscribe((language) => {
        this.context.lang = language;
        this.setCookie();
      }),
    );

    this.subscriptions.add(
      this.channelService.getActive().subscribe((channel) => {
        this.context.channel = channel;
        this.setCookie();
      }),
    );

    this.subscriptions.add(
      this.countryService.getActive().subscribe((country) => {
        this.context.country = country;
        this.setCookie();
      }),
    );
  }

  private setCookie() {
    if (this.context) {
      this.cookieService.set('siteContext', JSON.stringify(this.context), { expires: 7, path: '/' });
    }
  }
}
