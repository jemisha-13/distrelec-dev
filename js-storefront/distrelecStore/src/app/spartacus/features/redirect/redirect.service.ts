import { Injectable } from '@angular/core';
import { mapToCanActivate, Router } from '@angular/router';
import { OccEndpointsService, SiteContextUrlSerializer } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import { DynamicMappingRule } from '@features/redirect/model/redirect.model';
import { RedirectGuard } from '@features/redirect/redirect.guard';
import { Observable, of, ReplaySubject } from 'rxjs';
import { catchError, map, retry, take } from 'rxjs/operators';

const URL_MATCH_EXPRESSION_EQUALS = 'EQUALS';
const REDIRECT_URL_PATTERNS = ['cms/', 'manufacturer/'];

@Injectable({
  providedIn: 'root',
})
export class RedirectService {
  private dynamicMappingRules = new ReplaySubject<DynamicMappingRule[]>();

  constructor(
    private router: Router,
    private occEndpoints: OccEndpointsService,
    private http: HttpClient,
    private siteContextUrlSerializer: SiteContextUrlSerializer,
  ) {}

  refreshDynamicMappings() {
    const mappingRulesEndpoint = this.occEndpoints.buildUrl(`mapping-rules`);
    this.http
      .get<DynamicMappingRule[]>(mappingRulesEndpoint)
      .pipe(
        retry(3),
        catchError(() => of([])),
      )
      .subscribe((mappingRules) => this.dynamicMappingRules.next(mappingRules));
  }

  addRedirectGuard() {
    this.router.resetConfig(
      this.router.config.map((route) => {
        if (route.path === '**' || REDIRECT_URL_PATTERNS.some((pattern) => route.path?.includes(pattern))) {
          return {
            ...route,
            canActivate: [...mapToCanActivate([RedirectGuard]), ...route.canActivate],
          };
        }
        return route;
      }),
    );
  }

  getRedirectUrl(url: string): Observable<string | null> {
    return this.findDynamicMappingRule(url).pipe(
      map((dynamicMappingRule) => {
        if (dynamicMappingRule) {
          return dynamicMappingRule.destinationURL;
        }
        return null;
      }),
    );
  }

  findDynamicMappingRule(url: string): Observable<DynamicMappingRule> {
    const { url: urlWithoutContext } = this.siteContextUrlSerializer.urlExtractContextParameters(url);

    return this.dynamicMappingRules.pipe(
      take(1),
      map((rules) =>
        rules.find((rule) => {
          const sourceURL = rule.shortURL.substring(1); // Angular routes don't start with '/' but the rules do
          if (this.isEqualsMatchExpression(rule)) {
            return sourceURL === url || sourceURL === urlWithoutContext;
          } else {
            return url.endsWith(sourceURL);
          }
        }),
      ),
    );
  }

  isEqualsMatchExpression(dynamicMappingRule: DynamicMappingRule) {
    const urlMatchExpression = dynamicMappingRule.urlMatchExpression
      ? dynamicMappingRule.urlMatchExpression
      : URL_MATCH_EXPRESSION_EQUALS;
    return URL_MATCH_EXPRESSION_EQUALS === urlMatchExpression;
  }

  isInternalRedirect(destinationURL: string) {
    return destinationURL.startsWith('/');
  }
}
