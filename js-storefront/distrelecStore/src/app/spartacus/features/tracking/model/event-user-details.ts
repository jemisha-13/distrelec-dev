export enum AnalyticsCustomerType {
  B2B = 'b2b',
  B2B_KEY_ACCOUNT = 'b2b_key_account',
  B2C = 'b2c',
  B2E = 'b2e',
  GUEST = 'guest',
}

export interface EventUserDetails {
  logged_in: boolean;
  language: string;
  user_id?: string;
  customer_type: AnalyticsCustomerType;
  email?: string;
  registration_type?: string;
  lookup_used?: boolean;
  guest_checkout?: boolean;
  mg?: boolean;
}
