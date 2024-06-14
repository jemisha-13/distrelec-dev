import { TestBed } from '@angular/core/testing';
import { DistCookieService } from '@services/dist-cookie.service';
import { of } from 'rxjs';
import { OccEndpointsService } from '@spartacus/core';
import { FactFinderSessionService } from './fact-finder-session.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('FactFinderSessionService', () => {
  let service: FactFinderSessionService;
  let cookieService: DistCookieService;
  let mockOccEndpointsService;
  let mockSsrCookieService;
  let httpMock;

  const mockSessionId = '12235656';

  beforeEach(() => {
    mockOccEndpointsService = jasmine.createSpyObj('OccEndpointsService', ['buildUrl']);
    mockSsrCookieService = jasmine.createSpyObj('SsrCookieService', ['get', 'set']);

    TestBed.configureTestingModule({
      imports: [CommonTestingModule, HttpClientTestingModule],
      providers: [
        FactFinderSessionService,
        { provide: OccEndpointsService, useValue: mockOccEndpointsService }, // provide the spy as the service
        { provide: DistCookieService, useValue: mockSsrCookieService }, // provide the spy as the service
      ],
    });

    service = TestBed.inject(FactFinderSessionService);
    cookieService = TestBed.inject(DistCookieService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return the session ID if it exists', () => {
    spyOn(service, 'getSessionId').and.returnValue(of(mockSessionId));

    service.getSessionId().subscribe((sessionId) => {
      expect(sessionId).toBe(mockSessionId);
    });
  });

  it('should fetch a new session ID if it does not exist', () => {
    mockOccEndpointsService.buildUrl.and.returnValue('/ffsession?format=json');
    mockSsrCookieService.get.and.returnValue(null);

    service.getSessionId().subscribe((sessionId) => {
      expect(sessionId).toBe(mockSessionId);
    });

    const req = httpMock.expectOne('/ffsession?format=json');
    expect(req.request.method).toBe('GET');
    req.flush({ sessionId: mockSessionId });
  });

  it('should set the session ID', () => {
    service.setSessionId(mockSessionId);
    expect(cookieService.set).toHaveBeenCalledWith('sid', mockSessionId);
  });
});
