import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { PageHelper } from '@helpers/page-helper';
import { Feature } from '@spartacus/core';
import { ViewConfig } from '@spartacus/storefront';
import { FactFinderSearchQueryService } from './fact-finder-search-query.service';

describe('FactFinderSearchQueryService', () => {
  let service: FactFinderSearchQueryService;
  let mockPageHelper;
  let mockViewConfig: ViewConfig;
  let mockRouter;

  beforeEach(() => {
    mockPageHelper = jasmine.createSpyObj('PageHelper', [
      'isCategoryPage',
      'isManufacturerDetailPage',
      'isProductFamilyPage',
      'isNewProductsPage',
      'isClearancePage',
    ]);

    mockViewConfig = {
      view: {
        defaultPageSize: 10,
      },
    };

    mockRouter = { url: 'en/circuit-protection/thermistors/c/cat-DNAV_PL_0311' };

    TestBed.configureTestingModule({
      providers: [
        FactFinderSearchQueryService,
        { provide: PageHelper, useValue: mockPageHelper },
        { provide: ViewConfig, useValue: mockViewConfig },
        { provide: Router, useValue: mockRouter },
      ],
      imports: [CommonTestingModule, RouterTestingModule],
    });
    service = TestBed.inject(FactFinderSearchQueryService);
  });

  it('should build product specifications query', () => {
    const feature = {
      name: 'FeatureName',
      featureValues: [{ value: 'Value' }],
    } as Feature;

    const result = service.buildProductSpecificationsQuery(feature);
    expect(result).toEqual({ key: 'filter_FeatureName', value: 'Value' });
  });

  it('should encode the feature name and value', () => {
    const feature = {
      name: 'Feature±Name',
      featureValues: [{ value: 'Value±' }],
    } as Feature;

    const result = service.buildProductSpecificationsQuery(feature);
    expect(result.key).toContain('filter_Feature%2526plusmn%253BName');
    expect(result.value).toBe('Value%2526plusmn%253B');
  });

  it('should append feature unit if present', () => {
    const feature = {
      name: 'FeatureName',
      featureValues: [{ value: 'Value' }],
      featureUnit: { name: 'Unit' },
    } as Feature;

    const result = service.buildProductSpecificationsQuery(feature);
    expect(result).toEqual({ key: 'filter_FeatureName~~Unit', value: 'Value' });
  });

  it('should return search criteria with default page size', () => {
    const params = { pageSize: null, currentPage: 1, sort: 'Price:asc', sid: '11111', q: 'LED' };

    spyOn(service as any, 'mapQuery').and.returnValue({ query: 'LED' });
    spyOn(service as any, 'addPageSpecificSearchParameters').and.callThrough();

    const result = service.buildSearchCriteria(params);

    expect(result).toEqual(
      jasmine.objectContaining({
        query: 'LED',
        currentPage: 1,
        sort: 'Price:asc',
        sid: '11111',
        pageSize: 10,
      }),
    );
    expect((service as any).mapQuery).toHaveBeenCalledWith({ q: 'LED' });
    expect((service as any).addPageSpecificSearchParameters).toHaveBeenCalledWith({ q: 'LED' });
  });

  it('should add mainCategory to query when on a category page', () => {
    mockPageHelper.isCategoryPage.and.returnValue(true);
    const result = service.buildSearchCriteria({});

    expect(mockPageHelper.isCategoryPage).toHaveBeenCalled();
    expect(result).toEqual(
      jasmine.objectContaining({
        query: '::mainCategory:cat-DNAV_PL_0311',
      }),
    );
  });

  it('should add filter_productFamilyCode to query when on a product family page', () => {
    mockRouter.url = 'en/led-bulbs-led-warmdim-tungsram/pf/3573892';
    mockPageHelper.isProductFamilyPage.and.returnValue(true);
    const result = service.buildSearchCriteria({});

    expect(mockPageHelper.isProductFamilyPage).toHaveBeenCalled();
    expect(result).toEqual(
      jasmine.objectContaining({
        query: '::filter_productFamilyCode:3573892',
      }),
    );
  });

  it('should add filter_manufacturerCode to query when on a manufacturer page', () => {
    mockRouter.url = 'en/manufacturer/abus/man_abu';
    mockPageHelper.isManufacturerDetailPage.and.returnValue(true);
    const result = service.buildSearchCriteria({});

    expect(mockPageHelper.isManufacturerDetailPage).toHaveBeenCalled();
    expect(result).toEqual(
      jasmine.objectContaining({
        query: '::filter_manufacturerCode:man_abu',
      }),
    );
  });
});
