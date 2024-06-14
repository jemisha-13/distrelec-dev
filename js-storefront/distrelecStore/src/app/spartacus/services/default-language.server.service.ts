import { Inject, Injectable } from '@angular/core';
import { REQUEST } from '@spartacus/setup/ssr';
import { DefaultLanguageService } from '@services/default-language.service';

@Injectable({
  providedIn: 'root',
})
export class ServerDefaultLanguageService extends DefaultLanguageService {
  constructor(@Inject(REQUEST) private request: Request) {
    super();
  }

  getLanguage(): string {
    return this.request.headers['accept-language']?.split(',')?.[0]?.substring(0, 2) ?? '';
  }
}
