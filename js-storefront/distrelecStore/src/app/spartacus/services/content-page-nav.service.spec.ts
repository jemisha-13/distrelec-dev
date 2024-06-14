/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { ContentPageNavService } from './content-page-nav.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: Categories', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [ContentPageNavService],
    });
  });

  it('should ...', inject([ContentPageNavService], (service: ContentPageNavService) => {
    expect(service).toBeTruthy();
  }));
});
