/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { DistCartHttpService } from './cart-http.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: CartHttp', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [DistCartHttpService],
    });
  });

  it('should ...', inject([DistCartHttpService], (service: DistCartHttpService) => {
    expect(service).toBeTruthy();
  }));
});
