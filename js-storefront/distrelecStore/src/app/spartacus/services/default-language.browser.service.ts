import { Injectable } from '@angular/core';
import { WindowRef } from '@spartacus/core';
import { DefaultLanguageService } from '@services/default-language.service';

@Injectable({
  providedIn: 'root',
})
export class BrowserDefaultLanguageService extends DefaultLanguageService {
  constructor(private windowRef: WindowRef) {
    super();
  }

  getLanguage(): string {
    if (!this.windowRef.isBrowser()) {
      return '';
    }
    return this.windowRef.nativeWindow.navigator.language.slice(0, 2);
  }
}
