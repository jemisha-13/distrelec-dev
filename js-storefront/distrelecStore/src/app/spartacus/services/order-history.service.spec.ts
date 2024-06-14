import { TestBed } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

import { OrderHistoryService } from './order-history.service';

describe('OrderHistoryService', () => {
  let service: OrderHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
    });
    service = TestBed.inject(OrderHistoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
