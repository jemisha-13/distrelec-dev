import { PaymentDetails, Price } from '@spartacus/core';
import { PaymentType } from '@spartacus/cart/base/root';

export interface ProgressBarInterface {
  loginRegisterStep?: string;
  billingStep?: string;
  confirmStep?: string;
}

export interface GeneratedVoucher {
  code: string;
  value: Price;
  validFrom: string;
  validUntil: string;
}

export interface RetrieveERPCode {
  erpCode: string;
  status: 'waiting' | 'ok';
  erpVoucher?: GeneratedVoucher;
  timeout?: boolean;
}

export interface GuestSubmitForm {
  password: string;
  checkPwd: string;
  uid: string;
  guid: string;
}

export interface HiddenPaymentForm {
  debugMode: boolean;
  parameters: {
    entry: Parameters[];
  };
  postUrl: boolean;
}

export interface Parameters {
  key: string;
  value: string;
}

export interface VatRequestBody {
  vat4: string;
  legalEmail: string;
  codiceCUP: string;
  codiceCIG: string;
}

export interface Payment {
  paymentOptions: {
    paymentModes: PaymentType[];
  };
  canRequestInvoicePaymentMode: boolean;
  invoicePaymentModeRequested: boolean;
}

export interface PaymentDetailsResponse {
  payments: PaymentDetails[];
}

export enum PaymentModes {
  CREDITCARD = 'CreditCard',
  PAYPAL = 'PayPal',
  NEWCREDITCARD = 'NewCreditCard',
}

export enum PaymentMethod {
  CREDIT_CARD = 'Credit card',
  INVOICE = 'Invoice',
  PAYPAL = 'PayPal',
}
