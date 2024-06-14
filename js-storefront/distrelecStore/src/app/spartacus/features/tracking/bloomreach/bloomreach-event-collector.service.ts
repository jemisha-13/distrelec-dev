/* eslint-disable max-len */
/* eslint-disable prefer-arrow/prefer-arrow-functions */
/* eslint-disable @typescript-eslint/no-shadow */
/* eslint-disable @typescript-eslint/naming-convention */
/* eslint-disable @typescript-eslint/no-unused-expressions */
// @ts-nocheck
import { Injectable } from '@angular/core';
import { CxEvent, WindowRef } from '@spartacus/core';
import { TmsCollector, WindowObject } from '@spartacus/tracking/tms/core';
import { BlmCollectorConfig, BlmrConfig } from './bloomreach-config.service';
import { DistCookieService } from '@services/dist-cookie.service';
import { LocalStorageService } from '@services/local-storage.service';

declare global {
  interface Window {
    exponea: {
      track: (eventName: string, eventBody: any) => any;
    };
  }
}

@Injectable({
  providedIn: 'root',
})
export class BloomreachEventCollectorService implements TmsCollector {
  constructor(
    protected winRef: WindowRef,
    protected blmCollectorConfig: BlmCollectorConfig,
    protected ssrCookieService: DistCookieService,
    private localStorage: LocalStorageService,
  ) {}

  isCookieConsentGiven(): boolean {
    return (
      (this.ssrCookieService.get('DISTRELEC_ENSIGHTEN_PRIVACY_Marketing') === '1' ||
        this.localStorage.getItem('distrelec_ENSIGHTEN_INTERACTED_URLS')?.Marketing?.Bloomreach === 1) &&
      !this.isBloomreachBlocked()
    );
  }

  isBloomreachBlocked(): boolean {
    return this.localStorage.getItem('distrelec_ENSIGHTEN_BLOCKED_URLS')
      ? this.localStorage
          .getItem('distrelec_ENSIGHTEN_BLOCKED_URLS')
          ?.filter((preference) => preference.displayName === 'Bloomreach').length !== 0
      : false;
  }

  init(config: any, windowObject: WindowObject): void {
    this.blmCollectorConfig.getBaseSiteConfiguration().subscribe((bloomreachConfig: BlmrConfig) => {
      this.executeBloomreachScript(bloomreachConfig);
      if (this.isCookieConsentGiven()) {
        this.winRef.nativeWindow.exponea.start();
      }
    });
  }

  executeBloomreachScript(bloomreachConfig: string): void {
    !(function (e, n, t, i, r, o) {
      function s(e) {
        if ('number' != typeof e) {
          return e;
        }
        const n = new Date();
        return new Date(n.getTime() + 1e3 * e);
      }
      const a = 4e3;
      const c = 'xnpe_async_hide';

      function p(e) {
        return e.reduce(
          function (e, n) {
            return (
              (e[n] = function () {
                e._.push([n.toString(), arguments]);
              }),
              e
            );
          },
          {
            _: [],
          },
        );
      }

      function m(e, n, t) {
        const i = t.createElement(n);
        i.src = e;
        const r = t.getElementsByTagName(n)[0];
        return r.parentNode.insertBefore(i, r), i;
      }

      function u(e) {
        return '[object Date]' === Object.prototype.toString.call(e);
      }
      (o.target = o.target || 'https://api.exponea.com'),
        (o.file_path = o.file_path || o.target + '/js/exponea.min.js'),
        (r[n] = p([
          'anonymize',
          'initialize',
          'identify',
          'getSegments',
          'update',
          'track',
          'trackLink',
          'trackEnhancedEcommerce',
          'getHtml',
          'showHtml',
          'showBanner',
          'showWebLayer',
          'ping',
          'getAbTest',
          'loadDependency',
          'getRecommendation',
          'reloadWebLayers',
          '_preInitialize',
          '_initializeConfig',
        ])),
        (r[n].notifications = p(['isAvailable', 'isSubscribed', 'subscribe', 'unsubscribe'])),
        (r[n].segments = p(['subscribe'])),
        (r[n].snippetVersion = 'v2.7.0'),
        (function (e, n, t) {
          (e[n]['_' + t] = {}),
            (e[n]['_' + t].nowFn = Date.now),
            (e[n]['_' + t].snippetStartTime = e[n]['_' + t].nowFn());
        })(r, n, 'performance'),
        (function (e, n, t, i, r, o) {
          e[r] = {
            sdk: e[i],
            sdkObjectName: i,
            skipExperiments: !!t.new_experiments,
            sign: t.token + '/' + (o.exec(n.cookie) || ['', 'new'])[1],
            path: t.target,
          };
        })(r, e, o, n, i, RegExp('__exponea_etc__' + '=([\\w-]+)')),
        (function (e, n, t) {
          m(e.file_path, n, t);
        })(o, t, e),
        (function (e, n, t, i, r, o, p) {
          if (e.new_experiments) {
            !0 === e.new_experiments && (e.new_experiments = {});
            let l;
            const f = e.new_experiments.hide_class || c;
            const _ = e.new_experiments.timeout || a;
            const g = encodeURIComponent(o.location.href.split('#')[0]);
            e.cookies &&
              e.cookies.expires &&
              ('number' == typeof e.cookies.expires || u(e.cookies.expires)
                ? (l = s(e.cookies.expires))
                : e.cookies.expires.tracking &&
                  ('number' == typeof e.cookies.expires.tracking || u(e.cookies.expires.tracking)) &&
                  (l = s(e.cookies.expires.tracking))),
              l && l < new Date() && (l = void 0);
            const d =
              e.target +
              '/webxp/' +
              n +
              '/' +
              o[t].sign +
              '/modifications.min.js?http-referer=' +
              g +
              '&timeout=' +
              _ +
              'ms' +
              (l ? '&cookie-expires=' + Math.floor(l.getTime() / 1e3) : '');
            'sync' === e.new_experiments.mode && o.localStorage.getItem('__exponea__sync_modifications__')
              ? (function (e, n, t, i, r) {
                  (t[r][n] = '<' + n + ' src="' + e + '"></' + n + '>'),
                    i.writeln(t[r][n]),
                    i.writeln(
                      '<' +
                        n +
                        '>!' +
                        r +
                        '.init && document.writeln(' +
                        r +
                        '.' +
                        n +
                        '.replace("/' +
                        n +
                        '/", "/' +
                        n +
                        '-async/").replace("><", " async><"))</' +
                        n +
                        '>',
                    );
                })(d, n, o, p, t)
              : (function (e, n, t, i, r, o, s, a) {
                  o.documentElement.classList.add(e);
                  const c = m(t, i, o);

                  function p() {
                    r[a].init || m(t.replace('/' + i + '/', '/' + i + '-async/'), i, o);
                  }

                  function u() {
                    o.documentElement.classList.remove(e);
                  }
                  (c.onload = p), (c.onerror = p), r.setTimeout(u, n), (r[s]._revealPage = u);
                })(f, _, d, n, o, p, r, t);
          }
        })(o, t, i, 0, n, r, e),
        (function (e, n, t) {
          let i;
          e[n]._initializeConfig(t),
            (null === (i = t.experimental) || void 0 === i ? void 0 : i.non_personalized_weblayers) &&
              e[n]._preInitialize(t),
            (e[n].start = function (i) {
              i &&
                Object.keys(i).forEach(function (e) {
                  return (t[e] = i[e]);
                }),
                e[n].initialize(t);
            });
        })(r, n, o);
    })(document, 'exponea', 'script', 'webxpClient', window, {
      target: 'https://api.uk.exponea.com',
      compliance: { opt_in: false },
      token: bloomreachConfig,
      experimental: {
        non_personalized_weblayers: true,
      },
      // replace with current customer ID or leave commented out for an anonymous customer
      // customer: window.currentUserId,
      track: {
        google_analytics: false,
      },
    });
  }

  pushEvent<T extends CxEvent>(config: BlmCollectorConfig, windowObject: WindowObject, event): void {
    if (['customer'].includes(event.type)) {
      this.winRef.nativeWindow.exponea.identify({
        email_id: event.email_id,
        erp_contact_id: event.erp_contact_id,
      });
      return;
    }

    if (['rs_password_pageview'].includes(event.type)) {
      this.winRef.nativeWindow.exponea.identify({
        email_id: event.body.email_id,
      });
    }

    if (['logout'].includes(event.type)) {
      this.winRef.nativeWindow.exponea.anonymize();
      return;
    }

    this.winRef.nativeWindow.exponea.track(event.type, event.body);
  }
}
