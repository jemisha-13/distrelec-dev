import { TestBed } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { RedirectGuard } from './redirect.guard';

describe('RedirectGuard', () => {
  let guard: RedirectGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
    });
    guard = TestBed.inject(RedirectGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
