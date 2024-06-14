import { Injectable } from '@angular/core';
import { BaseSiteService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { pluck } from 'rxjs/operators';

export interface GtmConfig {
  gtmTagId?: string;
  gtmPreview?: string;
  gtmAuth?: string;
  gtmCookiesWin?: string;
}

@Injectable({
  providedIn: 'root',
})
export class GtmConfigService {
  constructor(private baseSiteService: BaseSiteService) {}

  getBaseSiteConfiguration(): Observable<GtmConfig> {
    return this.baseSiteService.get().pipe(pluck('tracking'));
  }
}
