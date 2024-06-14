import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PriceType } from '@spartacus/core';
import { CartRemoveEntrySuccessEvent, CartAddEntrySuccessEvent, OrderEntry } from '@spartacus/cart/base/root';
import '@model/index';
import { Order } from '@spartacus/order/root';

const orderDataMock = {
  productCode: '30132220',
  quantity: 1,
  cartId: 'fa3350b3-3b2f-4f8f-ab16-1c82792085b9',
  cartCode: 's2b000026R6',
  userId: 'guest',
  entry: {
    alternateAvailable: false,
    alternateQuantity: 0,
    availabilities: [
      {
        estimatedDate: '2023-09-08T00:00:00+0000',
        quantity: 1,
      },
    ],
    backOrderProfitable: true,
    backOrderedQuantity: 0,
    baseListPrice: {
      currencyIso: 'CHF',
      formattedValue: '736,00',
      priceType: 'BUY',
      value: 736,
    },
    basePrice: {
      currencyIso: 'CHF',
      formattedValue: '736,00',
      priceType: 'BUY',
      value: 736,
    },
    bom: false,
    cancellableQuantity: 1,
    deliveryQuantity: 0,
    dummyItem: false,
    entryNumber: 0,
    isBackOrder: false,
    isQuotation: false,
    mandatoryItem: false,
    mview: 'BTO',
    pendingQuantity: 0,
    product: {
      alternativeAliasMPN: '',
      availableInSnapEda: false,
      baseOptions: [],
      batteryComplianceCode: 'N',
      buyable: true,
      buyableReplacementProduct: false,
      categories: [
        {
          code: 'cat-DNAV_PL_190906',
          level: 4,
          name: 'Monitors',
          nameEN: 'Monitors',
          productCount: 0,
          selected: false,
          seoMetaDescription:
            'Distrelec Switzerland stocks a wide range of Monitors. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
          seoMetaTitle: 'Monitors|$(siteName)',
          seoSections: [],
          url: '/office-computing-network-products/peripherals/monitors-accessories/monitors/c/cat-DNAV_PL_190906',
        },
      ],
      code: '30235023',
      codeErpRelevant: '30235023',
      configurable: false,
      customsCode: '8528.5200',
      dimensions: '720 x 470 x 270 MM',
      distManufacturer: {
        code: 'man_vsc',
        name: 'ViewSonic',
        nameSeo: 'viewsonic',
        urlId: '/manufacturer/viewsonic/man_vsc',
        emailAddresses: [],
        image: [
          {
            key: 'brand_logo',
            value: {
              format: 'brand_logo',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/lo/go/ViewSonic_logo.jpg',
            },
          },
        ],
        phoneNumbers: [],
        productGroups: [],
        seoMetaDescription:
          'Shop 48 ViewSonic products at ${siteName}. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
        seoMetaTitle: 'ViewSonic Distributor | ${siteName}',
        websites: [],
      },
      ean: '',
      elfaArticleNumber: '30235023',
      eligibleForReevoo: true,
      energyEfficiency: 'G',
      energyEfficiencyLabelImage: [
        {
          key: 'landscape_large',
          value: {
            format: 'landscape_large',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/48/35/EE_Label_464835.jpg',
          },
        },
        {
          key: 'landscape_medium',
          value: {
            format: 'landscape_medium',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/48/35/EE_Label_464835.jpg',
          },
        },
        {
          key: 'portrait_small',
          value: {
            format: 'portrait_small',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/48/35/EE_Label_464835.jpg',
          },
        },
        {
          key: 'landscape_small',
          value: {
            format: 'landscape_small',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/48/35/EE_Label_464835.jpg',
          },
        },
        {
          key: 'portrait_medium',
          value: {
            format: 'portrait_medium',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/48/35/EE_Label_464835.jpg',
          },
        },
      ],
      enumber: '',
      formattedSvhcReviewDate: '',
      grossWeight: 10060,
      grossWeightUnit: 'Gram',
      hasSvhc: false,
      images: [
        {
          format: 'landscape_small',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/01/ViewSonic-VP2785-2K-30235023-01.jpg',
        },
        {
          format: 'landscape_medium',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/01/ViewSonic-VP2785-2K-30235023-01.jpg',
        },
        {
          format: 'landscape_large',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/01/ViewSonic-VP2785-2K-30235023-01.jpg',
        },
        {
          format: 'portrait_small',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/01/ViewSonic-VP2785-2K-30235023-01.jpg',
        },
        {
          format: 'portrait_medium',
          imageType: 'PRIMARY',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/01/ViewSonic-VP2785-2K-30235023-01.jpg',
        },
      ],
      inShoppingList: false,
      itemCategoryGroup: 'BANS',
      movexArticleNumber: '457302',
      name: 'Monitor, VP, 27 (68.6 cm), 2560 x 1440, IPS, 16:9',
      nameEN: 'Monitor, VP, 27 (68.6 cm), 2560 x 1440, IPS, 16:9',
      navisionArticleNumber: '457302',
      orderQuantityMinimum: 1,
      orderQuantityStep: 1,
      productFamilyUrl: '/en/monitors-viewsonic/pf/3719750',
      productImages: [{}],
      purchasable: true,
      replacementReason: '',
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
      typeName: 'VP2785-2K',
      url: '/monitor-vp-27-68-cm-2560-1440-ips-16-viewsonic-vp2785-2k/p/30235023',
      volumePrices: [
        {
          basePrice: 736,
          currencyIso: 'CHF',
          formattedValue: '792,67',
          minQuantity: 1,
          pricePerX: 0,
          pricePerXBaseQty: 0,
          pricePerXUOM: 'PC',
          pricePerXUOMDesc: 'piece',
          pricePerXUOMQty: 0,
          priceType: 'BUY',
          priceWithVat: 792.672,
          value: 792.672,
          vatPercentage: 7.7,
          vatValue: 56.672,
        },
      ],
      volumePricesMap: [
        {
          key: 1,
          value: [
            {
              key: 'list',
              value: {
                basePrice: 736,
                currencyIso: 'CHF',
                formattedValue: '792,67',
                minQuantity: 1,
                pricePerX: 0,
                pricePerXBaseQty: 0,
                pricePerXUOM: 'PC',
                pricePerXUOMDesc: 'piece',
                pricePerXUOMQty: 0,
                priceType: 'BUY',
                priceWithVat: 792.672,
                value: 792.672,
                vatPercentage: 7.7,
                vatValue: 56.672,
              },
            },
          ],
        },
      ],
    },
    quantity: 1,
    returnableQuantity: 0,
    totalListPrice: {
      currencyIso: 'CHF',
      formattedValue: '736,00',
      priceType: 'BUY',
      value: 736,
    },
    totalPrice: {
      currencyIso: 'CHF',
      formattedValue: '736,00',
      priceType: 'BUY',
      value: 736,
    },
    configurationInfos: [],
    customerReference: '',
    moqAdjusted: false,
    quotationId: '',
    quotationReference: '',
    statusSummaryList: [],
    stepAdjusted: false,
    taxValue: 0,
    type: 'entry',
  },
};

export const MOCK_ENTRY_DATA_ADD = {
  ...orderDataMock,
} as unknown as CartAddEntrySuccessEvent;

export const MOCK_ENTRY_DATA_REMOVE = {
  ...orderDataMock,
} as unknown as CartRemoveEntrySuccessEvent;
