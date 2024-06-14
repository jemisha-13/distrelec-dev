/* eslint-disable */
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { UrlSerializer, Router } from '@angular/router';
import { ConverterService, Product, ProductSearchPage } from '@spartacus/core';
import { ViewConfig } from '@spartacus/storefront';
import { SearchExperienceService } from '@features/pages/product/core/services/search-experience.service';
import { PageHelper } from '@helpers/page-helper';
import { DistFusionProductSearchPageNormalizer } from './dist-fusion-product-search-page-normalizer';
import { FusionProductSearch } from '@model/fusion-product-search.model';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { mockPageHelper } from '@features/mocks/mock-page-helper';
import { MockSearchExperienceService } from '@features/mocks/mock-search-experience.service';
import {
  mockFusionCategoryResponse,
  mockFusionCategoryResult,
} from '@features/mocks/fusion-data/mock-fusion-category-page-data';
import {
  mockFusionSearchResponse,
  mockFusionSearchResult,
} from '@features/mocks/fusion-data/mock-fusion-search-page-data';
import {
  mockFusionClearanceResponse,
  mockFusionClearanceResult,
} from '@features/mocks/fusion-data/mock-fusion-clearance-data';
import {
  mockFusionManufacturerResponse,
  mockFusionManufacturerResult,
} from '@features/mocks/fusion-data/mock-fusion-manufacturer-page-data';
import { mockFusionPFResponse, mockFusionPFResult } from '@features/mocks/fusion-data/mock-fusion-pf-page-data';

describe('DistFusionProductSearchPageNormalizer', () => {
  let normalizer: DistFusionProductSearchPageNormalizer;
  let router: Router;

  const mockUrlSerializer = {
    parse: jasmine.createSpy('parse').and.returnValue({
      queryParams: {},
    }),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule, RouterTestingModule],
      providers: [
        DistFusionProductSearchPageNormalizer,
        ConverterService,
        {
          provide: Router,
          useValue: {
            get url() {
              return 'en/optoelectronics/leds/c/cat-L3D_525297?useTechnicalView=true';
            },
          },
        },
        {
          provide: ViewConfig,
          useValue: { view: { defaultPageSize: 50 } },
        },
        {
          provide: SearchExperienceService,
          useClass: MockSearchExperienceService,
        },
        {
          provide: PageHelper,
          useValue: mockPageHelper,
        },
        {
          provide: UrlSerializer,
          useValue: mockUrlSerializer,
        },
      ],
    });

    normalizer = TestBed.inject(DistFusionProductSearchPageNormalizer);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(normalizer).toBeTruthy();
  });

  it('should convert source data to model data for category plp pages', () => {
    spyOnProperty(router, 'url').and.returnValue(
      'en/optoelectronics/leds/c/cat-L3D_525297?useTechnicalView=true&currentPage=1&pageSize=25&sort=Price:asc',
    );
    const model: ProductSearchPage = normalizer.convert(
      mockFusionCategoryResponse as unknown as FusionProductSearch,
      {},
    );
    expect(model).toEqual(mockFusionCategoryResult as unknown as ProductSearchPage);
  });

  it('should convert source data to model data for search plp pages', () => {
    spyOnProperty(router, 'url').and.returnValue('en/search?q=bulb&sid=gXdlIkfR4o');
    const model: ProductSearchPage = normalizer.convert(mockFusionSearchResponse as unknown as FusionProductSearch, {});
    expect(model).toEqual(mockFusionSearchResult as unknown as ProductSearchPage);
  });

  it('should convert source data to model data for new cms plp pages (clearance, new products etc...)', () => {
    spyOnProperty(router, 'url').and.returnValue('en/clearance');
    const model: ProductSearchPage = normalizer.convert(
      mockFusionClearanceResponse as unknown as FusionProductSearch,
      {},
    );
    expect(model).toEqual(mockFusionClearanceResult as unknown as ProductSearchPage);
  });

  it('should convert source data to model data for manufacturer plp pages', () => {
    spyOnProperty(router, 'url').and.returnValue('en/manufacturer/fluke/man_flu');
    const model: ProductSearchPage = normalizer.convert(
      mockFusionManufacturerResponse as unknown as FusionProductSearch,
      {},
    );
    expect(model).toEqual(mockFusionManufacturerResult as unknown as ProductSearchPage);
  });

  it('should convert source data to model data for product family plp pages', () => {
    spyOnProperty(router, 'url').and.returnValue('en/handheld-oscilloscopes-kits-scopemeter-fluke/pf/3653025');
    const model: ProductSearchPage = normalizer.convert(mockFusionPFResponse as unknown as FusionProductSearch, {});
    expect(model).toEqual(mockFusionPFResult as unknown as ProductSearchPage);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });
});
