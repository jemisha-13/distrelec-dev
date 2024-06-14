/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { DistOrderService } from './dist-order.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { OrderConnector, OrderAdapter } from '@spartacus/order/core';

describe('Service: Order', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [DistOrderService, OrderConnector, OrderAdapter],
    });
  });

  it('should ...', inject([DistOrderService], (service: DistOrderService) => {
    expect(service).toBeTruthy();
  }));
});
