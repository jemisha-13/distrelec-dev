/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { ProductAvailabilityService } from './product-availability.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: ProductAvailability', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [ProductAvailabilityService],
    });
  });

  it('should ...', inject([ProductAvailabilityService], (service: ProductAvailabilityService) => {
    expect(service).toBeTruthy();
  }));
});
