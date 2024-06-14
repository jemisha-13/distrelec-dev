import { inject, TestBed } from '@angular/core/testing';
import { SalesStatusService } from './sales-status.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: SalesStatus', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [SalesStatusService],
    });
  });

  it('should ...', inject([SalesStatusService], (service: SalesStatusService) => {
    expect(service).toBeTruthy();
  }));
});
