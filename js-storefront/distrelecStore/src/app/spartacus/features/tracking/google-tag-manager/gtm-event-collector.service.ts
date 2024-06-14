/* eslint-disable @typescript-eslint/naming-convention */
/* eslint-disable prefer-arrow/prefer-arrow-functions */
/* eslint-disable space-before-function-paren */
import { Injectable, OnDestroy } from '@angular/core';
import { CxEvent, WindowRef } from '@spartacus/core';
import { TmsCollector, TmsCollectorConfig, WindowObject } from '@spartacus/tracking/tms/core';
import { GtmConfig, GtmConfigService } from '@features/tracking/google-tag-manager/gtm-config.service';

declare global {
  interface Window {
    dataLayer: [];
  }
}

type ConsentEvent = [string, string, { analytics_storage: string }];

@Injectable({
  providedIn: 'root',
})
export class GtmEventCollectorService implements TmsCollector, OnDestroy {
  private queueOfEvents = [];
  private config: TmsCollectorConfig;
  private isCookieBannerInteracted = false;
  private isConsentListenerRemoved: boolean;

  constructor(
    protected winRef: WindowRef,
    protected gtmConfigService: GtmConfigService,
  ) {}

  private static getDataLayerProperty(config: TmsCollectorConfig): string {
    return config.dataLayerProperty ?? 'dataLayer';
  }

  ngOnDestroy(): void {
    this.removeEventListenerForConsent();
  }

  init(config: any, windowObject: WindowObject): void {
    const dataLayer = GtmEventCollectorService.getDataLayerProperty(config);
    windowObject[dataLayer] = windowObject[dataLayer] ?? [];

    this.config = config;
    this.addEventListenerForCookieBannerLoaded();

    this.gtmConfigService.getBaseSiteConfiguration().subscribe((gtmConfig: GtmConfig) => {
      if (gtmConfig.gtmTagId) {
        (function (w: WindowObject, d: Document, s: string, l: string, i: string) {
          w[l] = w[l] || [];
          w[l].push({ 'gtm.start': new Date().getTime(), event: 'gtm.js' });
          const f = d.getElementsByTagName(s)[0];
          const j = d.createElement(s) as HTMLScriptElement;
          const dl = l !== 'dataLayer' ? '&l=' + l : '';
          const preview = gtmConfig.gtmPreview !== undefined ? '&gtm_preview=' + gtmConfig.gtmPreview : '';
          const auth = gtmConfig.gtmAuth !== undefined ? '&gtm_auth=' + gtmConfig.gtmAuth : '';
          const cookie = gtmConfig.gtmCookiesWin !== undefined ? '&gtm_cookies_win=' + gtmConfig.gtmCookiesWin : '';
          j.async = true;
          j.src = 'https://www.googletagmanager.com/gtm.js?id=' + i + dl + preview + auth + cookie;
          f.parentNode?.insertBefore(j, f);
        })(windowObject, this.winRef.document, 'script', dataLayer, gtmConfig.gtmTagId);
      }
    });
  }

  pushEvent<T extends CxEvent>(config: TmsCollectorConfig, windowObject: WindowObject, event: any): void {
    if (!this.isCookieBannerInteracted) {
      this.queueOfEvents.push(event);
    } else {
      windowObject[GtmEventCollectorService.getDataLayerProperty(config)].push(event);
      this.removeEventListenerForConsent();
    }
  }

  private rePushEvents(): void {
    let event = this.queueOfEvents.shift();

    while (event) {
      this.pushEvent(this.config, this.winRef.nativeWindow, event);
      event = this.queueOfEvents.shift();
    }
  }

  private readonly onCookieBannerClick = () => {
    this.isCookieBannerInteracted = true;
    this.rePushEvents();
    this.removeEventListenerForConsent();
  };

  private addEventListenerForCookieBannerLoaded(): void {
    this.winRef.nativeWindow?.addEventListener('cookieBannerLoaded', () => {
      const cookieBanner = this.winRef.nativeWindow.document.getElementById('ensNotifyBanner');

      this.winRef.nativeWindow?.removeEventListener('cookieBannerLoaded', null);

      if (this.winRef.nativeWindow.getComputedStyle(cookieBanner)?.display === 'block') {
        this.addEventListenerForCookieBannerClick();
      } else {
        this.onCookieBannerClick();
      }
    });
  }

  private addEventListenerForCookieBannerClick(): void {
    this.winRef.nativeWindow?.addEventListener('cookieBannerClicked', () => {
      // Add timeout since in GTM custom.timer is implemented with 1 second wait
      setTimeout(() => this.onCookieBannerClick(), 1000);
    });
  }

  private removeEventListenerForConsent(): void {
    if (!this.isConsentListenerRemoved) {
      this.winRef.nativeWindow?.removeEventListener('cookieBannerClicked', this.onCookieBannerClick);
      this.isConsentListenerRemoved = true;
    }
  }
}
