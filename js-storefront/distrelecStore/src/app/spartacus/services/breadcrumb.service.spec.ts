/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { BreadcrumbService } from './breadcrumb.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: Breadcrumb', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [BreadcrumbService],
    });
  });

  it('should ...', inject([BreadcrumbService], (service: BreadcrumbService) => {
    expect(service).toBeTruthy();
  }));
});
