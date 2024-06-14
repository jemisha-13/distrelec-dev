import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
import { CommunicationPreferenceService } from '@services/communication.service';
import { MOCK_USER_PREFERENCES } from '@features/mocks/mock-user-preferences-data';

describe('CommunicationPreferenceService', () => {
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  let occEndpointBuilderSpy: jasmine.SpyObj<OccEndpointsService>;
  let communicationService: CommunicationPreferenceService;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
    occEndpointBuilderSpy = jasmine.createSpyObj('OccEndpointsService', ['buildUrl']);
    communicationService = new CommunicationPreferenceService(httpClientSpy, occEndpointBuilderSpy);
  });

  it('Should return user preference data when calling endpoint', () => {
    httpClientSpy.get.and.returnValue(of(MOCK_USER_PREFERENCES));
    communicationService
      .getUserCommunicationPreferences()
      .pipe()
      .subscribe((result) => {
        expect(result).toEqual(MOCK_USER_PREFERENCES);
      });
  });
});
