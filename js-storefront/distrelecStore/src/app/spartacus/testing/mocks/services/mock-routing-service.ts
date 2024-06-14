import { of } from 'rxjs';
import { PageType } from '@spartacus/core';

const router = {
  state: {
    url: '/',
    queryParams: {},
    params: {},
    context: { id: '1', type: PageType.PRODUCT_PAGE },
    cmsRequired: false,
  },
};

export class MockRoutingService {
  getRouterState() {
    return of(router);
  }
  getPageContext() {
    return of(router.state.context);
  }
  go = () => Promise.resolve(true);
  isNavigating() {
    return of(false);
  }
}
