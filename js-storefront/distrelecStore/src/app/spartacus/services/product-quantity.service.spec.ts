/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { ProductQuantityService } from './product-quantity.service';
import { BaseSiteService } from '@spartacus/core';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: ProductQuantity', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [ProductQuantityService],
    });
  });

  it('should ...', inject([ProductQuantityService], (service: ProductQuantityService) => {
    expect(service).toBeTruthy();
  }));
});
