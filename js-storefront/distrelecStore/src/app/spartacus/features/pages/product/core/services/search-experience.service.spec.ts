import { TestBed } from '@angular/core/testing';
import { BaseSiteService, LanguageService } from '@spartacus/core';
import { of } from 'rxjs';
import { SearchExperienceService } from './search-experience.service';

describe('SearchExperienceService', () => {
  let searchExperienceService: SearchExperienceService;
  let languageService: LanguageService;
  let baseSiteService: BaseSiteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SearchExperienceService,
        { provide: LanguageService, useValue: jasmine.createSpyObj('LanguageService', ['getActive']) },
        { provide: BaseSiteService, useValue: jasmine.createSpyObj('BaseSiteService', ['get']) },
      ],
    });
    searchExperienceService = TestBed.inject(SearchExperienceService);
    languageService = TestBed.inject(LanguageService);
    baseSiteService = TestBed.inject(BaseSiteService);
  });

  it('should return true when the active language does not match the FUSION code', async () => {
    (languageService.getActive as jasmine.Spy).and.returnValue(of('en'));

    (baseSiteService.get as jasmine.Spy).and.returnValue(
      of({
        baseStore: {
          searchExperienceMap: [
            { key: 'en', value: { code: 'FUSION' } },
            { key: 'de', value: { code: 'FACTFINDER' } },
          ],
        },
      }),
    );

    await searchExperienceService.init();

    const result = searchExperienceService.isFusion();
    expect(result).toBe(true);
  });

  it('should return false when the active language does not match the FUSION code', async () => {
    (languageService.getActive as jasmine.Spy).and.returnValue(of('de'));

    (baseSiteService.get as jasmine.Spy).and.returnValue(
      of({
        baseStore: {
          searchExperienceMap: [
            { key: 'en', value: { code: 'FUSION' } },
            { key: 'de', value: { code: 'FACTFINDER' } },
          ],
        },
      }),
    );

    await searchExperienceService.init();

    const result = searchExperienceService.isFusion();
    expect(result).toBe(false);
  });

  it('should return false when searchExperienceMap is undefined', async () => {
    (languageService.getActive as jasmine.Spy).and.returnValue(of('en'));

    (baseSiteService.get as jasmine.Spy).and.returnValue(
      of({
        baseStore: {
          searchExperienceMap: undefined,
        },
      }),
    );

    await searchExperienceService.init();

    const result = searchExperienceService.isFusion();
    expect(result).toBe(false);
  });
});
