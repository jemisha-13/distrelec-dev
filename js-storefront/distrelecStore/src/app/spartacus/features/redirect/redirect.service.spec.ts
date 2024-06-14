import { TestBed } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

import { RedirectService } from './redirect.service';

describe('RedirectService', () => {
  let service: RedirectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
    });
    service = TestBed.inject(RedirectService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
