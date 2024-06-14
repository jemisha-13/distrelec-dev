import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of, ReplaySubject } from 'rxjs';
import { OccEndpointsService, WindowRef } from '@spartacus/core';
import { ProductEnhancement } from '@model/product.model';
import { MockOccEndpointsService } from '@testing/mock-occ-endpoints.service';
import { ProductEnhancementsService } from './product-enhancements.service';

describe('ProductEnhancementsService', () => {
  let service: ProductEnhancementsService;
  let httpMock: HttpTestingController;
  let occEndpoints: OccEndpointsService;
  let winRef: WindowRef;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ProductEnhancementsService,
        WindowRef,
        {
          provide: OccEndpointsService,
          useClass: MockOccEndpointsService,
        },
      ],
    });

    service = TestBed.inject(ProductEnhancementsService);
    httpMock = TestBed.inject(HttpTestingController);
    occEndpoints = TestBed.inject(OccEndpointsService);
    winRef = TestBed.inject(WindowRef);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call HttpClient when load() is called', (done) => {
    const productCodes = ['123', '456'];
    const expectedUrl = `/products/enhance?productCodes=${productCodes.join(',')}`;

    spyOn(occEndpoints, 'buildUrl').and.returnValue(expectedUrl);

    service.load(productCodes);

    const req = httpMock.expectOne((request) => request.url === expectedUrl);
    expect(req.request.method).toBe('GET');

    req.flush({
      productEnhancements: [
        { productCode: '123', inShoppingList: true },
        { productCode: '456', inShoppingList: false },
      ],
    });
    done();
  });

  it('should not call HttpClient when running on the server', () => {
    spyOn(winRef, 'isBrowser').and.returnValue(false);

    service.load(['123', '456']);
    httpMock.expectNone((request) => request.url.includes('/products/enhance'));
  });

  it('should load enhancements and return the correct data via get()', (done) => {
    const productCodes = ['123', '456'];
    const mockResponse = {
      productEnhancements: [
        { productCode: '123', inShoppingList: true /* ...other properties... */ },
        { productCode: '456', inShoppingList: false /* ...other properties... */ },
      ],
    };

    const expectedUrl = `/products/enhance?productCodes=${productCodes.join(',')}`;
    spyOn(occEndpoints, 'buildUrl').and.returnValue(expectedUrl);

    service.load(productCodes);

    const req = httpMock.expectOne((request) => request.url.includes('/products/enhance'));
    expect(req.request.method).toBe('GET');

    req.flush(mockResponse);

    // Wait for the cache to update and check if get() returns the expected data
    setTimeout(() => {
      service.get(productCodes[0]).subscribe((result1) => {
        service.get(productCodes[1]).subscribe((result2) => {
          expect(result1.productCode).toBe('123');
          expect(result1.inShoppingList).toBe(true);

          expect(result2.productCode).toBe('456');
          expect(result2.inShoppingList).toBe(false);

          done();
        });
      });
    }, 0);
  });

  it('should handle HttpErrors when load() is called', (done) => {
    const productCodes = ['123', '456'];
    const expectedUrl = `/products/enhance?productCodes=${productCodes.join(',')}`;

    spyOn(occEndpoints, 'buildUrl').and.returnValue(expectedUrl);

    // @ts-ignore
    service.fetchEnhancements(productCodes).subscribe((result) => {
      expect(result).toEqual([]);
      done();
    });

    httpMock
      .expectOne((request) => request.url === expectedUrl)
      .flush(null, { status: 500, statusText: 'Internal Server Error' });

    done();
  });

  it('should return observable from cache when get() is called', (done) => {
    const productCode = '123';
    const mockEnhancement = { productCode: '123', inShoppingList: true };

    // Add a mock enhancement to the cache
    // @ts-ignore
    service.cache[productCode] = new ReplaySubject<ProductEnhancement>();
    // @ts-ignore
    service.cache[productCode].next(mockEnhancement);

    service.get(productCode).subscribe((enhancement) => {
      expect(enhancement).toEqual(mockEnhancement);

      // Ensure HttpClient was not called
      httpMock.expectNone((request) => request.url.includes('/products/enhance'));

      done();
    });
  });

  it('should call HttpClient when get() is called with a non-cached productCode', (done) => {
    const productCode = '123';
    const mockEnhancement = { productCode: '123', inShoppingList: true };

    const expectedUrl = `/products/enhance?productCodes=${productCode}`;
    spyOn(occEndpoints, 'buildUrl').and.returnValue(expectedUrl);

    service.get(productCode).subscribe((enhancement) => {
      expect(enhancement).toEqual(mockEnhancement);
      done();
    });

    const req = httpMock.expectOne((request) => request.url === expectedUrl);
    expect(req.request.method).toBe('GET');

    req.flush({
      productEnhancements: [mockEnhancement],
    });
  });

  it('should return false when product is not in the shopping list', (done) => {
    const productCode = '123';
    const enhancement = { productCode, inShoppingList: false /* ...other properties... */ };

    // Mock get function to bypass cache
    spyOn(service, 'get').and.returnValue(of(enhancement));

    service.isInShoppingList(productCode).subscribe((result) => {
      expect(result).toBe(false);
      done();
    });
  });

  it('should return true when product is in the shopping list', (done) => {
    const productCode = '123';
    const enhancement = { productCode, inShoppingList: true /* ...other properties... */ };

    // Mock get function to bypass cache
    spyOn(service, 'get').and.returnValue(of(enhancement));

    service.isInShoppingList(productCode).subscribe((result) => {
      expect(result).toBe(true);
      done();
    });
  });
});
