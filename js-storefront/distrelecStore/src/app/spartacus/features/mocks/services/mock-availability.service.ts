import { of } from 'rxjs';
import { MOCK_AVAILABILITY_FULLY_STOCKED } from '../mock-pdp-availability-SC-NORMAL';

export class MockDistAvailabilityService {
  getAvailability() {
    return of(MOCK_AVAILABILITY_FULLY_STOCKED);
  }
}
