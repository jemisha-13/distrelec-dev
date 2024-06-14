/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { PasswordService } from './password.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: Password', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [PasswordService],
    });
  });

  it('should ...', inject([PasswordService], (service: PasswordService) => {
    expect(service).toBeTruthy();
  }));
});
