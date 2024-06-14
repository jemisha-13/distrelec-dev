import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class CommunicationPreferenceService {
  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
  ) {}

  getUserCommunicationPreferences(): Observable<any> {
    return this.http.get<any>(this.occEndpoints.buildUrl('/users/current/communication-preferences')).pipe(take(1));
  }
}
