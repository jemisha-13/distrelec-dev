/* eslint-disable @typescript-eslint/naming-convention */
import { RegistrationEvent } from '@features/tracking/events/registration-event';
import { LoginEvent } from '@features/tracking/events/ga4/login-event';
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_REGISTRATION_EVENT: RegistrationEvent = {
  event: 'register',
  user: {
    logged_in: true,
    language: 'english',
    user_id: '9a39691b0d6a5f54fc21f8603cdd58ddb736b339c712f0bad30bcb7d5c79817d84c4ed85427cfe0c',
    customer_type: AnalyticsCustomerType.B2C,
    guest_checkout: false,
  },
};

export const MOCK_LOGIN_EVENT: LoginEvent = {
  event: 'login',
  user: {
    logged_in: true,
    language: 'english',
    user_id: '9a39691b0d6a5f54fc21f8603cdd58ddb736b339c712f0bad30bcb7d5c79817d84c4ed85427cfe0c',
    customer_type: AnalyticsCustomerType.B2C,
    guest_checkout: false,
  },
};
