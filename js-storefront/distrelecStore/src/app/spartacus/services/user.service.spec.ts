import { inject, TestBed } from '@angular/core/testing';
import { DistrelecUserService } from './user.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: User', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [DistrelecUserService],
    });
  });

  it('should ...', inject([DistrelecUserService], (service: DistrelecUserService) => {
    expect(service).toBeTruthy();
  }));
});
