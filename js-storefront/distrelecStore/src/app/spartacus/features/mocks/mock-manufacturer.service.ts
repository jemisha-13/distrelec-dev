import { BehaviorSubject, Observable, of } from 'rxjs';

const MOCK_MANUFACTURER_DATA = {
  name: 'man_tek',
  type: 'distManufacturerWsDTO',
  code: '/manufacturer/tektronix/man_tek',
};

// eslint-disable-next-line @typescript-eslint/naming-convention
export const MockManufacturerService = {
  manufacturerData$: new BehaviorSubject<any>(MOCK_MANUFACTURER_DATA),

  getCurrentManufacturerData(): Observable<any> {
    return of(MOCK_MANUFACTURER_DATA);
  },
};
