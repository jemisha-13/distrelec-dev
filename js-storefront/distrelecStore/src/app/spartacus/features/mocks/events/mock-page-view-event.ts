import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_PAGE_VIEW_EVENT = {
  event: 'pageview',
  user: {
    logged_in: false,
    language: undefined,
    customer_type: AnalyticsCustomerType.B2C,
    mg: false,
  },
  page: {
    document_title: 'Distrelec Switzerland - Technical Components Distributor',
    url: 'https://distrelec-ch.local:4200/en/',
    category: 'homepage',
    market: 'CH',
  },
};
