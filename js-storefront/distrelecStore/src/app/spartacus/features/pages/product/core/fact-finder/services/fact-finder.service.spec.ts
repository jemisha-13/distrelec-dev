import { TestBed, inject } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { FactFinderSessionService } from './fact-finder-session.service';

describe('FactfinderService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [FactFinderSessionService],
    });
  });

  it('should ...', inject([FactFinderSessionService], (service: FactFinderSessionService) => {
    expect(service).toBeTruthy();
  }));
});
