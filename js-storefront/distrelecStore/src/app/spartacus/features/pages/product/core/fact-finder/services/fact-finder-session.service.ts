import { Injectable } from '@angular/core';
import { map, shareReplay, tap } from 'rxjs/operators';
import { OccEndpointsService } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import { DistCookieService } from '@services/dist-cookie.service';
import { Observable, of } from 'rxjs';
import { SessionService } from '../../services/abstract-session.service';

@Injectable({ providedIn: 'root' })
export class FactFinderSessionService extends SessionService {
  private sessionId: string;
  private sessionIdRequest$: Observable<string>;

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private cookieService: DistCookieService,
  ) {
    super();
    this.sessionId = this.cookieService.get('sid');
  }

  getSessionId(): Observable<string> {
    if (this.sessionId) {
      return of(this.sessionId);
    }
    if (!this.sessionIdRequest$) {
      this.sessionIdRequest$ = this.refreshSessionId();
    }
    return this.sessionIdRequest$;
  }

  setSessionId(sessionId: string): void {
    this.sessionId = sessionId;
    this.cookieService.set('sid', sessionId);
  }

  private refreshSessionId() {
    return this.http.get<{ sessionId: string }>(this.occEndpoints.buildUrl('/ffsession?format=json')).pipe(
      map((response) => response.sessionId),
      tap((sessionId: string) => this.setSessionId(sessionId)),
      shareReplay({
        bufferSize: 1,
        refCount: true,
      }),
    );
  }
}
