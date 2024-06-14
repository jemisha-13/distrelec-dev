import { Injectable, OnDestroy } from '@angular/core';
import { ConfigInitializerService, getContextParameterDefault, SiteContextConfig, WindowRef } from '@spartacus/core';
import { Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';

import { DistCookieService } from '@services/dist-cookie.service';
import { ChannelService } from '../services/channel.service';
import { Channel } from '@model/site-settings.model';
import { CHANNEL_CONTEXT_ID } from '../providers/custom-context-ids';

@Injectable({ providedIn: 'root' })
export class ChannelInitializer implements OnDestroy {
  protected subscription: Subscription;

  constructor(
    private channelService: ChannelService,
    private configInit: ConfigInitializerService,
    private cookieService: DistCookieService,
    private winRef: WindowRef,
  ) {}

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  initialize(): void {
    this.subscription = this.configInit
      .getStable('context')
      .pipe(tap((config: SiteContextConfig) => this.setValue(config)))
      .subscribe();
  }

  protected setValue(config: SiteContextConfig): void {
    const contextParam = this.getCookieContext() ?? (getContextParameterDefault(config, CHANNEL_CONTEXT_ID) as Channel);
    this.channelService.setActive(contextParam);
  }

  private getCookieContext(): Channel | undefined {
    try {
      return JSON.parse(this.cookieService.get('siteContext')).channel as Channel;
    } catch {
      return undefined;
    }
  }
}
