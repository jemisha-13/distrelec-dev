import { RoutesConfig, RoutingConfig } from '@spartacus/core';

export const defaultStorefrontRoutesConfig: RoutesConfig = {
  home: {
    paths: ['/'],
    protected: false,
    authFlow: true,
  },
  welcome: {
    paths: ['welcome'],
    protected: false,
    authFlow: true,
  },
  // semantic links for login related pages
  checkoutLogin: {
    paths: ['login/checkout'],
    protected: false,
    authFlow: true,
  },
  checkoutAddress: {
    paths: ['checkout/delivery'],
    protected: false,
    authFlow: true,
  },
  checkoutPayment: {
    paths: ['checkout/review-and-pay'],
    protected: false,
    authFlow: true,
  },
  cart: {
    paths: ['cart'],
    protected: false,
    authFlow: true,
  },
  account: {
    paths: ['my-account/not-authorized'],
    protected: false,
    authFlow: true,
  },
  backorder: {
    paths: ['checkout/backorderDetails'],
    protected: false,
    authFlow: true,
  },
  qualityLanding: {
    paths: ['cms/quality'],
    protected: false,
    authFlow: true,
  },
  productFamily: {
    paths: ['pf'],
    protected: false,
    authFlow: false,
  },
  manufacturer: {
    paths: ['manufacturer'],
    protected: false,
    authFlow: false,
  },
};

export const defaultRoutingConfig: RoutingConfig = {
  routing: {
    routes: defaultStorefrontRoutesConfig,
  },
};
