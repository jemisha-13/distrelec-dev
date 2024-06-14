/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { PaymentService } from './payment.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: Payment', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [PaymentService],
    });
  });

  it('should ...', inject([PaymentService], (service: PaymentService) => {
    expect(service).toBeTruthy();
  }));
});
