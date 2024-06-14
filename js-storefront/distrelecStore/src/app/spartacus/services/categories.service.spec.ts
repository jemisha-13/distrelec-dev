/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { CategoriesService } from './categories.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: Categories', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [CategoriesService],
    });
  });

  it('should ...', inject([CategoriesService], (service: CategoriesService) => {
    expect(service).toBeTruthy();
  }));
});
