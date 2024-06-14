import { InjectionToken } from '@angular/core';

export const TRACKING_INITIALIZER = new InjectionToken('TrackingInitializer');

export interface TrackingInitializer {
  init(): void;
}
