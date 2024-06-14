/* eslint-disable @typescript-eslint/naming-convention */
import { Injectable } from '@angular/core';
import { BaseSiteService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { pluck, tap } from 'rxjs/operators';

export interface BlmrConfig {
  bloomreachScriptToken?: string;
}

@Injectable({
  providedIn: 'root',
})
export class BlmCollectorConfig {
  bloomreachScriptToken: string;

  constructor(private baseSiteService: BaseSiteService) {}

  getBaseSiteConfiguration(): Observable<any> {
    return this.baseSiteService.get().pipe(
      pluck('bloomreachScriptToken'),
      tap((token) => (this.bloomreachScriptToken = token)),
    );
  }
}
