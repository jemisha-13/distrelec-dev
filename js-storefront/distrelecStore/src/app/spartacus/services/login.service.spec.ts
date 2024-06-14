/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed } from '@angular/core/testing';
import { LoginService } from './login.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: Login', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [LoginService],
    });
  });

  it('should ...', inject([LoginService], (service: LoginService) => {
    expect(service).toBeTruthy();
  }));
});
