import { Provider } from '@angular/core';
import { BasePageMetaResolver, PageMetaResolver, provideConfig } from '@spartacus/core';
import { DistProductPageMetaResolver } from './product-page-meta.resolver';
import { DistCategoryPageMetaResolver } from './category-page-meta.resolver';
import { DistContentPageMetaResolver } from './content-page-meta.resolver';
import { DistSearchPageMetaResolver } from './search-page-meta.resolver';
import { DistFamilyPageMetaResolver } from './family-page-meta.resolver';
import { DistManufacturerStoreDetailPageMetaResolver } from './manufacturer-page-meta.resolver';
import { DistEmptySearchPageMetaResolver } from './empty-search-page-meta.resolver';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';
import { DistShoppingListPageMetaResolver } from './shopping-list-page-meta.resolver';
import { DistStoreContentPageMetaResolver } from './store-content-page-meta.resolver';
import { DistHomePageMetaResolver } from './home-page-meta.resolver';
import { pageMetaConfig } from './page-meta.config';

export const resolversProviders: Provider[] = [
  provideConfig(pageMetaConfig),
  {
    provide: BasePageMetaResolver,
    useClass: DistBasePageMetaResolver,
  },
  {
    provide: PageMetaResolver,
    useClass: DistProductPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistCategoryPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistContentPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistStoreContentPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistSearchPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistEmptySearchPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistFamilyPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistManufacturerStoreDetailPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistShoppingListPageMetaResolver,
    multi: true,
  },
  {
    provide: PageMetaResolver,
    useClass: DistHomePageMetaResolver,
    multi: true,
  },
];
