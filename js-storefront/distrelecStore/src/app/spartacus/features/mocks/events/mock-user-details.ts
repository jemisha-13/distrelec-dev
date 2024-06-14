import { AnalyticsCustomerType, EventUserDetails } from "@features/tracking/model/event-user-details";

export const MOCK_USER_DETAILS: EventUserDetails = {
    logged_in: false,
    language: "english",
    customer_type: AnalyticsCustomerType.B2C,
    mg: false
  }