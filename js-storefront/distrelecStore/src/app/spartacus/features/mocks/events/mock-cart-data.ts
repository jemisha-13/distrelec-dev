import { PriceType } from '@spartacus/core';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { CustomerType } from '@model/site-settings.model';
import { Cart } from '@spartacus/cart/base/root';

export const mockCartData: Cart = {
  type: 'cartWsDTO',
  calculated: true,
  code: 's2b000026R6',
  deliveryCost: {
    currencyIso: 'CHF',
    priceType: PriceType.BUY,
    value: 0,
  },
  entries: [
    {
      configurationInfos: [],
      customerReference: '',
      moqAdjusted: false,
      quotationId: '',
      quotationReference: '',
      statusSummaryList: [],
      stepAdjusted: false,
      taxValue: 0.1034,
      type: 'entry',
      addedFrom: ItemListEntity.SUGGESTED_SEARCH,
      alternateAvailable: false,
      alternateQuantity: 0,
      availabilities: [
        {
          estimatedDate: new Date('2024-01-17T00:00:00+0000'),
          quantity: 35,
        },
        {
          estimatedDate: new Date('2024-01-22T00:00:00+0000'),
          quantity: 165,
        },
      ],
      backOrderProfitable: true,
      backOrderedQuantity: 0,
      baseListPrice: {
        currencyIso: 'CHF',
        priceType: PriceType.BUY,
        value: 0.1034,
      },
      basePrice: {
        currencyIso: 'CHF',
        priceType: PriceType.BUY,
        value: 0.1034,
      },
      bom: false,
      cancellableQuantity: 0,
      deliveryQuantity: 0,
      dummyItem: false,
      entryNumber: 0,
      isBackOrder: false,
      isQuotation: false,
      mandatoryItem: false,
      mview: 'F',
      pendingQuantity: 0,
      quantity: 200,
      returnableQuantity: 0,
      totalListPrice: {
        currencyIso: 'CHF',
        priceType: PriceType.BUY,
        value: 20.7,
      },
      totalPrice: {
        currencyIso: 'CHF',
        priceType: PriceType.BUY,
        value: 20.7,
      },
    },
  ],
  guid: 'fa3350b3-3b2f-4f8f-ab16-1c82792085b9',
  subTotal: {
    currencyIso: 'CHF',
    priceType: PriceType.BUY,
    value: 375.7,
  },
  totalDiscounts: {
    currencyIso: 'CHF',
    priceType: PriceType.BUY,
    value: 0,
  },
  totalItems: 2,
  totalPrice: {
    currencyIso: 'CHF',
    priceType: PriceType.BUY,
    value: 406.15,
  },
  totalPriceWithTax: {
    currencyIso: 'CHF',
    value: 436.6,
  },
  totalTax: {
    currencyIso: 'CHF',
    priceType: PriceType.BUY,
    value: 30.45,
  },
  user: {
    name: 'Anonymous',
    uid: 'anonymous',
    type: CustomerType.B2C,
  },
  blockedProducts: '',
  completeDelivery: false,
  creditBlocked: false,
  endOfLifeProducts: [],
  movCurrency: 'CHF',
  movLimit: 25,
  paymentMode: {
    paymentInfo: {},
    code: 'CreditCard',
    defaultPaymentMode: true,
    invoicePayment: false,
  },
  phasedOutProducts: [],
  punchedOutProducts: '',
  reevooEligible: false,
  updatedMOQProducts: [],
  waldom: false,
};
