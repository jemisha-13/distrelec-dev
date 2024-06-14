import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import {
  ConfigInitializerService,
  getContextParameterDefault,
  getContextParameterValues,
  LANGUAGE_CONTEXT_ID,
  LanguageInitializer,
  LanguageService,
  LanguageStatePersistenceService,
  SiteContextConfig,
} from '@spartacus/core';
import { DistCookieService } from '@services/dist-cookie.service';
import { DefaultLanguageService } from '@services/default-language.service';

@Injectable({ providedIn: 'root' })
export class DistrelecLanguageInitializer extends LanguageInitializer {
  constructor(
    languageService: LanguageService,
    languageStatePersistenceService: LanguageStatePersistenceService,
    configInit: ConfigInitializerService,
    private defaultLanguageService: DefaultLanguageService,
    private cookieService: DistCookieService,
  ) {
    super(languageService, languageStatePersistenceService, configInit);
  }

  /**
   * On subscription to the returned observable:
   *
   * Sets the default value taken from config, unless the active language has been already initialized.
   */
  protected setFallbackValue(): Observable<unknown> {
    return this.configInit.getStable('context').pipe(tap((config: SiteContextConfig) => this.setDefault(config)));
  }

  /**
   * Sets the active language value based on the default value from the config, cookie or URL
   */
  protected setDefault(config: SiteContextConfig): void {
    if (!this.languageService.isInitialized()) {
      const contextParam = this.getValidLanguage(config);
      this.languageService.setActive(contextParam);
    }
  }

  private getCookieContext(): string | undefined {
    try {
      return JSON.parse(this.cookieService.get('siteContext')).lang;
    } catch {
      return undefined;
    }
  }

  /**
   * Priority for setting initial context values is
   * 1. URL context path (override and set cookie) -- only way to set values across domains (handled by SiteContextRoutesHandler)
   * 2. Cookie (previously set value)
   * 3. Browser default value
   * 4. BaseStore default value
   *
   * If we have a value for 1,2 or 3, but it is not a valid value for the current BaseStore then the default value is used
   */
  private getValidLanguage(config: SiteContextConfig): string {
    const validValues = getContextParameterValues(config, LANGUAGE_CONTEXT_ID);

    const selectedLanguage = this.getCookieContext() ?? this.defaultLanguageService.getLanguage();

    if (validValues.includes(selectedLanguage)) {
      return selectedLanguage;
    }
    return getContextParameterDefault(config, LANGUAGE_CONTEXT_ID);
  }
}
