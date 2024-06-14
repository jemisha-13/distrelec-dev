import { TestBed } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { LanguageService } from '@spartacus/core';
import { of } from 'rxjs';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { DistSearchBoxService } from '../../services/dist-search-box.service';
import { FusionSuggestionQueryService } from './fusion-suggestion-query.service';

describe('FusionSuggestionQueryService', () => {
  let service: FusionSuggestionQueryService;

  beforeEach(() => {
    const mockLanguageService = {
      getActive: jasmine.createSpy('getActive').and.returnValue(of('en')),
    };

    const mockCountryService = {
      getActive: jasmine.createSpy('getActive').and.returnValue(of('ch')),
    };

    const mockChannelService = {
      getActive: jasmine.createSpy('getActive').and.returnValue(of('b2b')),
    };

    const mockDistSearchBoxService = {
      get categoryCode() {
        return 'CAT-1234';
      },
    };

    TestBed.configureTestingModule({
      providers: [
        FusionSuggestionQueryService,
        { provide: LanguageService, useValue: mockLanguageService },
        { provide: CountryService, useValue: mockCountryService },
        { provide: ChannelService, useValue: mockChannelService },
        { provide: DistSearchBoxService, useValue: mockDistSearchBoxService },
      ],
      imports: [CommonTestingModule],
    });
    service = TestBed.inject(FusionSuggestionQueryService);
  });

  it('should correctly return typeahead query params', (done) => {
    service.typeaheadQueryParams().subscribe((result: any) => {
      expect(result.country).toEqual('ch');
      expect(result.language).toEqual('en');
      expect(result.channel).toEqual('b2b');
      expect(result.fq).toEqual('category1Code:"CAT-1234"');

      const mockLanguageService = TestBed.inject(LanguageService);
      const mockCountryService = TestBed.inject(CountryService);
      const mockChannelService = TestBed.inject(ChannelService);
      const mockDistSearchBoxService = TestBed.inject(DistSearchBoxService);

      expect(mockLanguageService.getActive).toHaveBeenCalled();
      expect(mockCountryService.getActive).toHaveBeenCalled();
      expect(mockChannelService.getActive).toHaveBeenCalled();

      expect(mockDistSearchBoxService.categoryCode).toEqual('CAT-1234');

      done();
    });
  });
});
