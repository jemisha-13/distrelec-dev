import { Provider } from '@angular/core';
import { HttpErrorHandler } from '@spartacus/core';
import { DeactivatedUserErrorHandler } from './deactivated-user.handler';
import { TooManyRequestsHandler } from '@handlers/too-many-requests.handler';
import { ServiceUnavailableHandler } from '@handlers/service-unavailable.handler';

export const handlersProviders: Provider[] = [
  {
    provide: HttpErrorHandler,
    useExisting: DeactivatedUserErrorHandler,
    multi: true,
  },
  {
    provide: HttpErrorHandler,
    useExisting: TooManyRequestsHandler,
    multi: true,
  },
  {
    provide: HttpErrorHandler,
    useExisting: ServiceUnavailableHandler,
    multi: true,
  },
];
