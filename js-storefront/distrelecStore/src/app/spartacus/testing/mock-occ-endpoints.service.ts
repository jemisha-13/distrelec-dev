import { OccEndpointsService } from '@spartacus/core';

export class MockOccEndpointsService implements Partial<OccEndpointsService> {
  buildUrl(endpointKey: string, _urlParams?: Record<string, unknown>, _queryParams?: Record<string, unknown>) {
    if (!endpointKey.startsWith('/')) {
      endpointKey = '/' + endpointKey;
    }
    return endpointKey;
  }
  getBaseUrl() {
    return '';
  }
  isConfigured() {
    return true;
  }
}
