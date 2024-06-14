import { Provider } from '@angular/core';

import { I18nextInitializer } from '@spartacus/core';
import { DistI18nextInitializer } from './dist-i18next-initializer';

export const distI18nextProviders: Provider[] = [
  {
    provide: I18nextInitializer,
    useClass: DistI18nextInitializer,
  },
];
