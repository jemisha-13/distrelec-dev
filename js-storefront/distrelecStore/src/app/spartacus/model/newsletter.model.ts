export interface SubscribeNewsletter {
  doubleOptIn: boolean;
  errorMessage: string;
}

export interface UnsubscribeNewsletter {
  category: string;
  customerSurveyId: string;
  email: string;
  isMarketingCookieEnabled: string;
  knowhowId: string;
  newsletterId: string;
  outboundId: string;
  personaliseRecommendationId: string;
  salesAndClearanceId: string;
  ymktTrackingEnabled: boolean;
}

export interface UnsubscribeUrlParams {
  category: string;
  smcId: string;
  unsubscribeCategory: string;
}
