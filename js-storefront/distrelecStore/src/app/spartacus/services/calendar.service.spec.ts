/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { CalendarService } from './calendar.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: Calendar', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [CalendarService],
    });
  });

  it('should ...', inject([CalendarService], (service: CalendarService) => {
    expect(service).toBeTruthy();
  }));
});
