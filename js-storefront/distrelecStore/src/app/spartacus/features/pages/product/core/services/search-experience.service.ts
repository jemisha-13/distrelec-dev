import { Injectable } from '@angular/core';
import { BaseSiteService, LanguageService } from '@spartacus/core';
import { SearchExperience, SearchExperienceConfig } from '@model/misc.model';
import { combineLatest } from 'rxjs';
import { catchError, first, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class SearchExperienceService {
  private searchExperienceConfig?: SearchExperienceConfig;

  constructor(
    private languageService: LanguageService,
    private baseSiteService: BaseSiteService,
  ) {}

  init(): Promise<void> {
    return new Promise((resolve, reject) => {
      combineLatest([this.baseSiteService.get(), this.languageService.getActive()])
        .pipe(
          first(),
          map(([{ baseStore }, language]) => {
            const experience = baseStore.searchExperienceMap?.find(
              (searchExperience) => searchExperience.key === language,
            );
            return experience?.value;
          }),
          tap((searchExperienceConfig) => (this.searchExperienceConfig = searchExperienceConfig)),
          catchError((error) => {
            console.error('Error while resolving search experience ', error);
            reject();
            throw error;
          }),
        )
        .subscribe(() => resolve());
    });
  }

  isFusion(): boolean {
    return this.searchExperienceConfig?.code === SearchExperience.Fusion;
  }

  isRangeQuerySupported(): boolean {
    return this.isFusion();
  }

  getSearchConfiguration(): SearchExperienceConfig {
    return this.searchExperienceConfig;
  }

  getCategoryParam(): string {
    return `filter_${this.getCategoryKey()}`;
  }

  getCategoryKey(): string {
    return this.isFusion() ? 'categoryCodes' : 'categoryCodePathROOT';
  }
}
