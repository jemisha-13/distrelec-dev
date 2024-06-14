import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FusionSearchQueryService } from './fusion-search-query.service';

import { FusionPaginationQueryKey } from '../model/fusion-query-params.model';
import { BaseSiteService, LanguageService } from '@spartacus/core';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { CategoriesService } from '@services/categories.service';
import { DistrelecUserService } from '@services/user.service';
import { ViewConfig } from '@spartacus/storefront';
import { PageHelper } from '@helpers/page-helper';
import { of } from 'rxjs';
import { DistCookieService } from '@services/dist-cookie.service';

describe('FusionSearchQueryService', () => {
  let service: FusionSearchQueryService;
  let mockLanguageService;
  let mockCountryService;
  let mockChannelService;
  let mockCategoriesService;
  let mockDistrelecUserService;
  let mockPageHelper;
  let mockBaseSiteService;
  let mockCookieService;

  const mockUserDetails = {
    orgUnit: { erpCustomerId: '111112234' },
    encryptedUserID: '43343-3434-3232-3323',
  };

  const expectedMock = {
    [FusionPaginationQueryKey.PAGE]: 0,
    [FusionPaginationQueryKey.PAGE_SIZE]: 50,
    [FusionPaginationQueryKey.SORT]: 'manufacturerName_s asc',
    country: 'ch',
    language: 'en',
    channel: 'b2b',
    customerId: '111112234',
    query: 'bulb',
    sid: '11111',
    userId: '43343-3434-3232-3323',
    sessionId: '1659312139.1699437035',
  };

  const paramsMock = {
    currentPage: 1,
    pageSize: 50,
    sort: 'Manufacturer:asc',
    q: 'bulb',
    sid: '11111',
  };

  beforeEach(() => {
    mockLanguageService = jasmine.createSpyObj('LanguageService', { getActive: of('en') });
    mockCountryService = jasmine.createSpyObj('CountryService', { getActive: of('ch') });
    mockChannelService = jasmine.createSpyObj('ChannelService', { getActive: of('b2b') });
    mockCategoriesService = jasmine.createSpyObj('CategoriesService', {
      getCurrentCategoryData: of({ code: 'filter_CAT-1234' }),
    });
    mockDistrelecUserService = jasmine.createSpyObj('DistrelecUserService', { getUserDetails: of(mockUserDetails) });
    mockPageHelper = jasmine.createSpyObj('PageHelper', [
      'isCategoryPage',
      'isManufacturerDetailPage',
      'isProductFamilyPage',
      'isNewProductsPage',
      'isClearancePage',
    ]);
    mockBaseSiteService = jasmine.createSpyObj('BaseSiteService', { get: of('distrelec_SE') });
    mockCookieService = jasmine.createSpyObj('DistCookieService', { get: 'GA1.1.1659312139.1699437035' });

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        FusionSearchQueryService,
        { provide: LanguageService, useValue: mockLanguageService },
        { provide: CountryService, useValue: mockCountryService },
        { provide: ChannelService, useValue: mockChannelService },
        { provide: CategoriesService, useValue: mockCategoriesService },
        { provide: DistrelecUserService, useValue: mockDistrelecUserService },
        { provide: ViewConfig, useValue: { view: { defaultPageSize: 20 } } },
        { provide: PageHelper, useValue: mockPageHelper },
        { provide: BaseSiteService, useValue: mockBaseSiteService },
        { provide: DistCookieService, useValue: mockCookieService },
      ],
    });

    service = TestBed.inject(FusionSearchQueryService);
  });

  it('should build fusion search criteria', () => {
    mockPageHelper.isClearancePage.and.returnValue(true);

    const result = service.buildSearchCriteria(paramsMock);

    expect(result).toEqual(
      jasmine.objectContaining({
        ...expectedMock,
        fq: ['productStatus:(""OFFER"")'],
      }),
    );
  });

  it('should build fusion search criteria', () => {
    mockPageHelper.isProductFamilyPage.and.returnValue(true);

    const result = service.buildSearchCriteria(paramsMock);

    expect(result).toEqual(
      jasmine.objectContaining({
        ...expectedMock,
        fq: ['productFamilyCode:("")'],
      }),
    );
  });

  it('should build fusion search criteria', () => {
    mockPageHelper.isManufacturerDetailPage.and.returnValue(true);

    const result = service.buildSearchCriteria(paramsMock);

    expect(result).toEqual(
      jasmine.objectContaining({
        ...expectedMock,
        fq: ['manufacturerCode:("")'],
      }),
    );
  });
});
