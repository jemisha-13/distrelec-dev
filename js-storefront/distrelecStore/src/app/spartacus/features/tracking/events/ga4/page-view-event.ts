import { CxEvent } from '@spartacus/core';
import { AnalyticsCustomerType } from '../../model/event-user-details';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class PageViewEvent extends CxEvent {
  static type = 'pageview';

  event = PageViewEvent.type;

  user: {
    logged_in: boolean;
    language: string;
    user_id?: string;
    first_purchase?: string;
    customer_type?: AnalyticsCustomerType;
  };

  page: EventPageDetails;
}
