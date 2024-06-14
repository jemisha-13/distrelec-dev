/* eslint-disable max-len */
import { TestBed } from '@angular/core/testing';
import { DistSearchBoxService } from '@features/pages/product/core/services/dist-search-box.service';
import { DistFusionProductSearchAdapter } from './dist-fusion-product-search.adapter';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionService } from '../../services/abstract-session.service';
import { LanguageService, TranslationService } from '@spartacus/core';
import { StoreModule } from '@ngrx/store';
import { of } from 'rxjs';

import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { FusionSuggestionQueryService } from '../services/fusion-suggestion-query.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { FusionConfig } from '../model/fusion-config.model';
import { HttpHeaders } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { TransferState } from '@angular/core';
import { MockTranslationService } from '@testing/i18n/mock-translation.service';

const languageServiceMock = {
  getActive: () => of('en'),
};

const mockFusionConfig = {
  fusionBaseUrl: 'https://distrelec-dev.b.lucidworks.cloud/api',
  fusionSearchAPIKey: '55e532bb-c80e-4346-8d56-c44ca9caef2a',
  fusionProfileSuffix: null,
} as FusionConfig;

describe('DistFusionProductSearchAdapter', () => {
  let adapter: DistFusionProductSearchAdapter;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule, HttpClientTestingModule, StoreModule.forRoot({}), RouterTestingModule],
      providers: [
        DistFusionProductSearchAdapter,
        AllsitesettingsService,
        CommonTestingModule,
        DistSearchBoxService,
        SessionService,
        FusionSuggestionQueryService,
        CountryService,
        TransferState,
        AllsitesettingsService,
        { provide: LanguageService, useValue: languageServiceMock },
        { provide: TranslationService, useClass: MockTranslationService },
      ],
    });

    adapter = TestBed.inject(DistFusionProductSearchAdapter);
    httpMock = TestBed.inject(HttpTestingController);

    // @ts-ignore
    adapter.fusionConfig$ = of(mockFusionConfig);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(adapter).toBeTruthy();
  });

  it('should call fusion search', (done) => {
    const mockConfig = {
      q: 'bulb',
      channel: 'B2B',
      country: 'se',
      language: 'en',
      rows: 50,
      sid: '12JfjK3',
      sort: '',
      start: 0,
    };
    adapter.search('bulb', mockConfig).subscribe((res) => {
      expect(res.request).toEqual(mockConfig);
      done();
    });

    const req = httpMock.expectOne((request) => request.url.includes(`/apps/webshop/query/search`));
    expect(req.request.method).toBe('GET');
    req.flush({ request: mockConfig });
  });

  it('should set header for fusion api', () => {
    // @ts-ignore
    const headers: HttpHeaders = adapter.setHeadersFusionApi(mockFusionConfig);
    expect(headers.has('x-api-key')).toBeTruthy();
    expect(headers.get('x-api-key')).toBe(mockFusionConfig.fusionSearchAPIKey);
  });

  it('should call callFusionTypeAhead and return suggestions', (done) => {
    const mockSuggestions = {
      response: {
        docs: [
          {
            buyable: true,
            productNumber: '11049090',
            title: 'Light Bulb and Tool Kit for Torches MagLite',
            name: 'Light Bulb and Tool Kit for Torches MagLite',
            url: '/light-bulb-and-tool-kit-for-torches-maglite-mag-lite-lmxa201l/p/11049090',
            imageURL: '/Web/WebShopImages/landscape_small/1-/01/spare-lamps.jpg',
            categoryCodePath: 'cat-L2D_379658/cat-DNAV_1004/cat-DNAV_PL_110409',
            category: 'Lighting',
            currency: 'SEK',
            scalePricesGross: '{"1":116.125,"5":109.375,"10":105.125}',
            singleMinPriceNet: 92.9,
            singleMinPriceGross: 116.125,
            energyEffiency: '{}',
            itemStep: 1.0,
            itemMin: 1.0,
            titleShort: 'Light Bulb and Tool Kit for Torches',
            manufacturer: 'Mag-Lite',
            productUrl: '/light-bulb-and-tool-kit-for-torches-maglite-mag-lite-lmxa201l/p/11049090',
            typeName: 'LMXA201L',
            score: 230.98283,
          },
        ],
        categorySuggestions: [
          {
            title: 'Bulbs & Tubes',
            name: 'Bulbs & Tubes',
            url: '/lighting/bulbs-tubes/c/cat-L3D_531399',
            imageURL: '/Web/WebShopImages/portrait_small/ho/ne/16339_iPhone.jpg',
            productUrl: '/lighting/bulbs-tubes/c/cat-L3D_531399',
            score: 230.98283,
          },
        ],
        manufacturerSuggestions: [
          {
            name: 'Osram',
            url: '/manufacturer/osram/man_osr',
          },
        ],
      },
    };

    // @ts-ignore
    adapter.buildSuggestionParams$ = of({
      country: 'se',
      language: 'en',
      channel: 'B2B',
      fq: 'category1Code_s:"CAT-1234"',
    });

    adapter.loadSuggestions('bulb').subscribe((suggestions) => {
      expect(suggestions).toEqual(mockSuggestions);
      done();
    });

    const req = httpMock.expectOne((request) => request.url.includes(`/apps/webshop/query/typeahead`));
    expect(req.request.method).toBe('GET');
    req.flush(mockSuggestions);
  });
});
