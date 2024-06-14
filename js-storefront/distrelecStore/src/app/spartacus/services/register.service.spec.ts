/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { RegisterService } from './register.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { TranslationService } from '@spartacus/core';

describe('Service: Register', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [RegisterService, TranslationService],
    });
  });

  it('should ...', inject([RegisterService], (service: RegisterService) => {
    expect(service).toBeTruthy();
  }));
});
