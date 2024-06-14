import { TestBed } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { FusionSessionService } from './fusion-session.service';

describe('FusionSessionService', () => {
  let service: FusionSessionService;
  const testSessionId = '12JfjK3';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FusionSessionService],
      imports: [CommonTestingModule],
    });
    service = TestBed.inject(FusionSessionService);
  });

  it('should initially return null as the default value for the behavior subject', (done) => {
    service.getSessionId().subscribe((value) => {
      expect(value).toBeNull();
      done();
    });
  });

  it('should return the set sessionId when updated', (done) => {
    service.setSessionId(testSessionId);
    service.getSessionId().subscribe((value) => {
      expect(value).toBe(testSessionId);
      done();
    });
  });

  it('should not update sessionId if the new value is same as current value', (done) => {
    service.setSessionId(testSessionId);
    const spy = spyOn(service['sessionId'], 'next');
    service.setSessionId(testSessionId);
    expect(spy).not.toHaveBeenCalled();
    done();
  });
});
