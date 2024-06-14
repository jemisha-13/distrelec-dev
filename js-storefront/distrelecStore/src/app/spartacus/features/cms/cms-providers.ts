import { Provider } from '@angular/core';
import { OccCmsPageAdapter } from '@spartacus/core';
import { DistOccCmsPageAdapter } from '@features/cms/dist-occ-cms-page.adapter';

export const cmsProviders: Provider[] = [
  {
    provide: OccCmsPageAdapter,
    useClass: DistOccCmsPageAdapter,
  },
];
