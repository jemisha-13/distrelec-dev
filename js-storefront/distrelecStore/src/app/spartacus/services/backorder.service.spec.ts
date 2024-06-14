/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { BackorderService } from './backorder.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: Backorder', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [BackorderService],
    });
  });

  it('should ...', inject([BackorderService], (service: BackorderService) => {
    expect(service).toBeTruthy();
  }));
});
