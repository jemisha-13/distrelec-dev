/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { NotifyMeService } from './notify-me.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: NotifyMe', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [NotifyMeService],
    });
  });

  it('should ...', inject([NotifyMeService], (service: NotifyMeService) => {
    expect(service).toBeTruthy();
  }));
});
