import { APP_INITIALIZER, NgModule, Optional } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnsightenBootstrapService } from '@features/tracking/ensighten-bootstrap.service';
import {
  TRACKING_INITIALIZER,
  TrackingInitializer,
} from '@features/tracking/tracking-initializer/tracking-initializer';
import { WindowRef } from '@spartacus/core';

// Initialize the Ensighten privacy module first and then the
// other tracking services so consent can be managed properly
// Runs only in Client Side Renders.
const trackingFactoryInitializer = (
  winRef: WindowRef,
  ensightenService: EnsightenBootstrapService,
  initializers: TrackingInitializer[],
) => {
  if (!winRef.isBrowser()) {
    return () => ensightenService.initServer();
  }

  return () => ensightenService.init().then(() => initializers?.forEach((initializer) => initializer.init()));
};

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: trackingFactoryInitializer,
      deps: [WindowRef, EnsightenBootstrapService, [new Optional(), TRACKING_INITIALIZER]],
      multi: true,
    },
  ],
})
export class TrackingInitializerModule {}
