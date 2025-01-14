/* eslint-disable @typescript-eslint/naming-convention */

import { PriceType } from '@spartacus/core';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';
import { CustomerType } from '@model/site-settings.model';

export const MOCK_CHECKOUT_DATA: any = {
  cart: {
    type: 'cartWsDTO',
    calculated: true,
    code: 's2b00002M77',
    deliveryCost: {
      currencyIso: 'CHF',
      priceType: PriceType.BUY,
      value: 8,
    },
    entries: [
      {
        alternateAvailable: false,
        alternateQuantity: 0,
        availabilities: [
          {
            estimatedDate: '2024-01-23T00:00:00+0000',
            quantity: 10,
          },
        ],
        backOrderProfitable: true,
        backOrderedQuantity: false,
        baseListPrice: {
          currencyIso: 'CHF',
          priceType: PriceType.BUY,
          value: 7.75,
        },
        basePrice: {
          currencyIso: 'CHF',
          priceType: PriceType.BUY,
          value: 7.75,
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
        product: {
          alternativeAliasMPN: '',
          activePromotionLabels: [
            {
              active: true,
              code: 'noMover',
              label: 'Bundle',
              priority: 1,
              rank: 1,
            },
          ],
          availableInSnapEda: false,
          availableToB2B: true,
          availableToB2C: true,
          baseOptions: [],
          batteryComplianceCode: 'N',
          breadcrumbs: [],
          buyable: true,
          buyableReplacementProduct: false,
          categories: [],
          code: '30132220',
          codeErpRelevant: '30132220',
          configurable: false,
          customsCode: '8541.4100',
          dimensions: '37 x 6 x 6 MM',
          distManufacturer: {
            code: 'man_rnd',
            name: 'RND',
            nameSeo: 'rnd',
            urlId: '/manufacturer/rnd/man_rnd',
            emailAddresses: [],
            image: [
              {
                key: 'brand_logo',
                value: {
                  format: 'brand_logo',
                  url: 'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/tm/ap/rnd_logo_bitmap.jpg',
                },
              },
            ],
            phoneNumbers: [],
            productGroups: [],
            seoMetaDescription:
              'Shop 0 RND products at ${siteName}. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
            seoMetaTitle: 'RND Distributor | ${siteName}',
            websites: [],
          },
          ean: '653415291800',
          elfaArticleNumber: '30132220',
          eligibleForReevoo: false,
          enumber: '',
          formattedSvhcReviewDate: '',
          grossWeight: '0.3',
          grossWeightUnit: 'Gram',
          hasSvhc: false,
          images: null,
          inShoppingList: false,
          itemCategoryGroup: 'NORM',
          movexArticleNumber: '356596',
          name: 'THT LED 5mm Round Red 30mcd 645nm',
          nameEN: 'THT LED 5mm Round Red 30mcd 645nm',
          navisionArticleNumber: '356596',
          orderQuantityMinimum: 50,
          orderQuantityStep: 1,
          productFamilyName: 'LEDs, ø 5 mm',
          productFamilyUrl: '/en/leds-mm-rnd/pf/1722426',
          energyEfficiency: '',
          energyEfficiencyLabelImage: [{ key: '', value: { format: '', url: '' } }],
          energyPower: '',
          exclusionReason: '',
          productImages: [{}, {}],
          origPosition: '',
          otherAttributes: [],
          possibleOtherAttributes: null,
          productCode: '',
          purchasable: true,
          replacementReason: '',
          categoryCodePath: '',
          svhcReviewDate: '',
          technicalAttributes: [],
          commonAttrs: [],
          downloads: [],
          endOfLifeDate: null,
          rohs: '2015/863/EU Conform',
          rohsCode: '15',
          salesStatus: '30',
          salesUnit: 'piece',
          signalWord: '',
          stock: {
            isValueRounded: false,
            stockLevel: 0,
            stockLevelStatus: 'inStock',
          },
          transportGroupData: {
            bulky: false,
            code: '1000',
            dangerous: false,
            nameErp: 'Std / Non DG / No Calibration',
            relevantName: 'Std / Non DG / No Calibration',
          },
          typeName: 'RND 135-00129',
          url: '/tht-led-5mm-round-red-30mcd-645nm-rnd-rnd-135-00129/p/30132220',
          weeeCategory: '-',
        },
        quantity: 10,
        returnableQuantity: 0,
        totalListPrice: {
          currencyIso: 'CHF',
          priceType: PriceType.BUY,
          value: 77.5,
        },
        totalPrice: {
          currencyIso: 'CHF',
          priceType: PriceType.BUY,
          value: 77.5,
        },
      },
    ],
    guid: '646ad461-afb4-4047-a988-78056785412a',
    subTotal: {
      currencyIso: 'CHF',
      priceType: PriceType.BUY,
      value: 77.5,
    },
    totalDiscounts: {
      currencyIso: 'CHF',
      priceType: PriceType.BUY,
      value: 0,
    },
    totalItems: 1,
    totalPrice: {
      currencyIso: 'CHF',
      priceType: PriceType.BUY,
      value: 92.45,
    },
    totalPriceWithTax: {
      currencyIso: 'CHF',
      value: 99.4,
    },
    totalTax: {
      currencyIso: 'CHF',
      priceType: PriceType.BUY,
      value: 6.95,
    },
    user: {
      name: 'guest',
      type: CustomerType.GUEST,
      uid: 'c7497b21-d9dc-435b-a71d-6de6d22a19e3|donapil4726@wuzak.com',
      email: 'tester3@distrelec.com',
      guest_checkout: true,
    },
    completeDelivery: false,
    creditBlocked: false,
    eligibleForFastCheckout: false,
    hasUnallowedBackorder: false,
    movCurrency: 'CHF',
    movLimit: 25,
    paymentMode: {
      code: 'CreditCard',
      paymentInfo: {},
      defaultPaymentMode: true,
      creditCardPayment: true,
      hop: true,
      icons: [
        {
          url: 'https://pretest.media.distrelec.com/medias/LGO-Accept-MasterCard-min.png?context=bWFzdGVyfHJvb3R8MjU5N3xpbWFnZS9wbmd8aDMxL2hlOS84ODE1MjI5MDQyNzE4LnBuZ3xkYTcxNjdiMGU5MTBmZGMxMWE1ZDJjOTYxMGRmNTFiMzRkZWQ4ZGUwMmE1YzIwN2NlNjExZTBkODdhMTRhZDlk',
        },
        {
          url: 'https://pretest.media.distrelec.com/medias/LGO-Accept-Visa-Online-darkBG-min.png?context=bWFzdGVyfHJvb3R8OTQxfGltYWdlL3BuZ3xoMzIvaGU5Lzg4MTUyMjkwNzU0ODYucG5nfDUxNTE5NmMwMjE0NmI4MTlhNDIxMjBmODU5MzMyMDg2MjhmY2Y3ODQwYzQ2ZDM2YTRmMTNiYzc3NDdhODdhYzg',
        },
      ],
      iframe: true,
      invoicePayment: false,
      name: 'Credit Card',
      selectable: true,
      translationKey: 'payment.mode.cc',
      url: 'https://spg.evopayments.eu/pay/payssl.aspx',
    },
    punchedOutProducts: '',
    reevooEligible: false,
    waldom: false,
  },
  checkoutEventType: CheckoutGA4EventType.BEGIN_CHECKOUT,
};
