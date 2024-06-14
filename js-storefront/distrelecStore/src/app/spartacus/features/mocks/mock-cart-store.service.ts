import { BehaviorSubject, Observable, of } from 'rxjs';
import { Price, PriceType } from '@spartacus/core';

export const MOCK_CART_ENTRY = {
  alternateAvailable: false,
  alternateQuantity: 0,
  backOrderProfitable: true,
  backOrderedQuantity: 0,
  basePrice: {
    currencyIso: 'CHF',
    priceType: 'BUY',
    value: 13917.75,
  },
  bom: false,
  cancellableQuantity: 0,
  deliveryQuantity: 0,
  dummyItem: false,
  entryNumber: 1,
  isBackOrder: false,
  isQuotation: false,
  mandatoryItem: false,
  pendingQuantity: 0,
  product: {
    alternativeAliasMPN: '',
    availableInSnapEda: false,
    baseOptions: [],
    batteryComplianceCode: 'N',
    breadcrumbs: [
      {
        code: 'cat-L2D_527482',
        introText:
          'Any successful industrial operation depends on the skills of its employees as well as the performance of its vital machinery. In order to gather data on your technology as well as to further develop and optimise your production line, test and measurement instruments are instrumental.',
        level: 1,
        name: 'Test & Measurement',
        nameEN: 'Test & Measurement',
        selected: false,
        seoMetaDescription:
          'Shop our range of Test & Measurement supplies & accessories. Free Next Day Delivery. Get the latest deals on Test & Measurement.',
        seoMetaTitle: 'Test & Measurement | Meters & Gauges - Distrelec Switzerland',
        seoSections: [
          {
            header: 'What is Test and Measurement?',
            text: 'Test and measurement instruments are used mainly when working with electronics and are essential in revealing electrical faults that can be dangerous and cause downtime. They provide valuable data concerning the device under test’s state and performance. It is important to invest in quality products in this area as the data gathered through testing informs processes and design of your projects.',
          },
          {
            header: 'What are the types of test?',
            text: 'There are different areas that can be tested and measured, and hence just as many types of test instruments. Amongst them, multimeters, oscilloscopes and thermal imagers are some of the most popular and well known, however, our product assortment includes many more test and measurement instruments to suit the needs of maintenance technicians in different industries. Testing can focus on different units such as energy, power and capacitance.',
          },
          {
            header: 'Range of Test and Measurement equipment',
            text: 'To determine which test and measurement products are essential for you and which ones could update your projects in line with the new industrial revolution, we offer resources like product guides on our KnowHow hub as well as local expert teams who are happy to assist you in your choice. Our range of equipment is extensive - have a browse and discover the different types of instruments available to perform the tasks you require.',
          },
        ],
        url: '/test-measurement/c/cat-L2D_527482',
      },
      {
        code: 'cat-DNAV_1309',
        introText: 'At Distrelec, we offer a wide range of oscilloscopes for many applications.',
        level: 2,
        name: 'Oscilloscopes',
        nameEN: 'Oscilloscopes',
        selected: false,
        seoMetaDescription:
          'Distrelec Switzerland stocks a wide range of Oscilloscopes. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
        seoMetaTitle: 'Oscilloscopes|$(siteName)',
        seoSections: [
          {
            header: 'What are oscilloscopes used for?',
            text: 'An oscilloscope is a type of measurement device that captures, processes, displays and analyses the waveform and bandwidth of electronic signals through a graph. <br/><br/>Some of the main criteria distinguishing different oscilloscope models are their bandwidth, rise time and sample rate. When choosing the right oscilloscope for your application, these are crucial:<br/>• Bandwidth<br/>The bandwidth of the scope together with the probe should be at least 5× higher than the maximum signal bandwidth.<br/><br/>• Rise time<br/>For the rise time, follow this formula: rise time < 1/5× fastest rise time of your signal.<br/><br/>• Sample rate<br/>Your sample rate should be at least 5× higher than the highest frequency in your circuit.',
          },
          {
            header: 'Different types of oscilloscopes',
            text: '• Digital Storage Oscilloscope (DSO) <br/>A DSO is the most common type of oscilloscope. It shows the signal under test in the form of a digital waveform. <br/><br/>• Mixed Signal, Mixed Domain Oscilloscope (MSO/MDO) <br/>An MSO combines the function of the DSO with a logic analyser and can display signals in both the time and frequency domains. <br/><br/>• Handheld oscilloscope <br/>Handheld oscilloscopes are useful for maintenance technicians working in the field or with heavy machinery. <br/><br/>• PC-based oscilloscope <br/>PC-based oscilloscopes are connected to a computer or laptop. They are high-performance devices that often surpass the capabilities of a DSO. <br/><br/>• Automotive PC oscilloscope <br/>Use an automotive PC oscilloscope to measure everything electronic within a vehicle. Often signals change too quickly to be measurable with a multimeter.',
          },
        ],
        url: '/test-measurement/oscilloscopes/c/cat-DNAV_1309',
      },
      {
        code: 'cat-DNAV_PL_140903',
        introText:
          'Oscilloscopes are test instruments that are used to capture and display the behaviour of electronic signals. They are sometimes referred to as “o-scopes” or simply “scopes”. Most oscilloscopes use a probe which connects the test point within a circuit and then amplifies the voltage at that point.',
        level: 3,
        name: 'Benchtop Oscilloscopes',
        nameEN: 'Benchtop Oscilloscopes',
        selected: false,
        seoMetaDescription:
          'Distrelec Switzerland stocks a wide range of Benchtop Oscilloscopes. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
        seoMetaTitle: 'Benchtop Oscilloscopes|$(siteName)',
        seoSections: [
          {
            text: 'Modern oscilloscopes utilise digital signal processing in-order to capture and then display the analogue signal. These are referred to as Digital Storage Oscilloscopes (DSOs). If the oscilloscope is able to accept digital signals, it is categorised as a mixed-signal oscilloscope (MSO). The other type is a mixed-domain oscilloscope (MDO). These have an integrated spectrum analyser feature.',
          },
          {
            text: 'Oscilloscopes are used across numerous industries for a range of applications, including validation, characterisation, troubleshooting, and manufacturing testing. They are extremely helpful for observing, analysing, and recording the behaviour of an electrical signal.',
          },
          {
            text: 'Here at Distrelec, we offer a comprehensive range of oscilloscopes, including a number of benchtop Oscilloscopes from leading manufacturers such as Keysight, Tektronix, Rohde & Schwarz and Peaktech. These are available in a wide variety of specifications.',
          },
          {
            text: 'When it comes to getting your items, we appreciate that you may need them as soon as possible. Our standard delivery turnaround is 1-2 days, and we also offer a super-express freight delivery option on some items/locations. Shipping costs are calculated and displayed at checkout.',
          },
        ],
        url: '/test-measurement/oscilloscopes/benchtop-oscilloscopes/c/cat-DNAV_PL_140903',
      },
    ],
    buyable: true,
    buyableReplacementProduct: false,
    categories: [
      {
        code: 'cat-DNAV_PL_140903',
        introText:
          'Oscilloscopes are test instruments that are used to capture and display the behaviour of electronic signals. They are sometimes referred to as “o-scopes” or simply “scopes”. Most oscilloscopes use a probe which connects the test point within a circuit and then amplifies the voltage at that point.',
        level: 3,
        name: 'Benchtop Oscilloscopes',
        nameEN: 'Benchtop Oscilloscopes',
        selected: false,
        seoMetaDescription:
          'Distrelec Switzerland stocks a wide range of Benchtop Oscilloscopes. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
        seoMetaTitle: 'Benchtop Oscilloscopes|$(siteName)',
        seoSections: [
          {
            text: 'Modern oscilloscopes utilise digital signal processing in-order to capture and then display the analogue signal. These are referred to as Digital Storage Oscilloscopes (DSOs). If the oscilloscope is able to accept digital signals, it is categorised as a mixed-signal oscilloscope (MSO). The other type is a mixed-domain oscilloscope (MDO). These have an integrated spectrum analyser feature.',
          },
          {
            text: 'Oscilloscopes are used across numerous industries for a range of applications, including validation, characterisation, troubleshooting, and manufacturing testing. They are extremely helpful for observing, analysing, and recording the behaviour of an electrical signal.',
          },
          {
            text: 'Here at Distrelec, we offer a comprehensive range of oscilloscopes, including a number of benchtop Oscilloscopes from leading manufacturers such as Keysight, Tektronix, Rohde & Schwarz and Peaktech. These are available in a wide variety of specifications.',
          },
          {
            text: 'When it comes to getting your items, we appreciate that you may need them as soon as possible. Our standard delivery turnaround is 1-2 days, and we also offer a super-express freight delivery option on some items/locations. Shipping costs are calculated and displayed at checkout.',
          },
        ],
        url: '/test-measurement/oscilloscopes/benchtop-oscilloscopes/c/cat-DNAV_PL_140903',
      },
    ],
    code: '30376587',
    codeErpRelevant: '30376587',
    configurable: false,
    customsCode: '9030.2000',
    dimensions: '503 x 385 x 400 MM',
    distManufacturer: {
      code: 'man_lcr',
      name: 'Teledyne LeCroy',
      nameSeo: 'teledyne-lecroy',
      urlId: '/manufacturer/teledyne-lecroy/man_lcr',
      emailAddresses: [],
      image: [
        {
          key: 'landscape_large',
          value: {
            format: 'landscape_large',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/_t/if/lecroy_logo_cmyk.jpg',
          },
        },
        {
          key: 'landscape_medium',
          value: {
            format: 'landscape_medium',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/_t/if/lecroy_logo_cmyk.jpg',
          },
        },
        {
          key: 'landscape_small',
          value: {
            format: 'landscape_small',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/_t/if/lecroy_logo_cmyk.jpg',
          },
        },
        {
          key: 'portrait_small',
          value: {
            format: 'portrait_small',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/_t/if/lecroy_logo_cmyk.jpg',
          },
        },
        {
          key: 'brand_logo',
          value: {
            format: 'brand_logo',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/_t/if/lecroy_logo_cmyk.jpg',
          },
        },
        {
          key: 'portrait_medium',
          value: {
            format: 'portrait_medium',
            url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/_t/if/lecroy_logo_cmyk.jpg',
          },
        },
      ],
      phoneNumbers: [],
      productGroups: [],
      promotionText: '',
      seoMetaDescription:
        'Shop 626 Teledyne LeCroy products at ${siteName}. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
      seoMetaTitle: 'Teledyne LeCroy Distributor | ${siteName}',
      webDescription: '',
      websites: [],
    },
    ean: '',
    elfaArticleNumber: '30376587',
    eligibleForReevoo: true,
    enumber: '',
    formattedSvhcReviewDate: '17/01/2023',
    grossWeight: 5300,
    grossWeightUnit: 'Gram',
    hasSvhc: false,
    images: [
      {
        format: 'landscape_small',
        imageType: 'PRIMARY',
        url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/-0/01/Teledyne LeCroy-WaveSurfer 4024HD-30159888-001.jpg',
      },
      {
        format: 'landscape_medium',
        imageType: 'PRIMARY',
        url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/-0/01/Teledyne LeCroy-WaveSurfer 4024HD-30159888-001.jpg',
      },
      {
        format: 'landscape_large',
        imageType: 'PRIMARY',
        url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/-0/01/Teledyne LeCroy-WaveSurfer 4024HD-30159888-001.jpg',
      },
      {
        format: 'portrait_small',
        imageType: 'PRIMARY',
        url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/-0/01/Teledyne LeCroy-WaveSurfer 4024HD-30159888-001.jpg',
      },
      {
        format: 'portrait_medium',
        imageType: 'PRIMARY',
        url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/-0/01/Teledyne LeCroy-WaveSurfer 4024HD-30159888-001.jpg',
      },
    ],
    inShoppingList: false,
    itemCategoryGroup: 'NORM',
    movexArticleNumber: '512377',
    name: 'FULLY LOADED Oscilloscope Bundle WaveSurfer 4000HD MSO 4x 500MHz 2.5GSPS HDMI / LXI / Micro SD / RJ45 / USB',
    nameEN:
      'FULLY LOADED Oscilloscope Bundle WaveSurfer 4000HD MSO 4x 500MHz 2.5GSPS HDMI / LXI / Micro SD / RJ45 / USB',
    navisionArticleNumber: '512377',
    orderQuantityMinimum: 1,
    orderQuantityStep: 1,
    productFamilyName: 'Oscilloscopes, WaveSurfer 4000HD',
    productFamilyUrl: '/en/oscilloscopes-wavesurfer-4000hd-teledyne-lecroy/pf/2127036',
    productImages: [{}, {}, {}, {}],
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
    svhcReviewDate: '2023-01-17T00:00:00+0000',
    transportGroupData: {
      bulky: false,
      code: '1000',
      dangerous: false,
      nameErp: 'Std / Non DG / No Calibration',
      relevantName: 'Std / Non DG / No Calibration',
    },
    typeName: 'WAVESURFER 4054HD PROMO1',
    url: '/fully-loaded-oscilloscope-bundle-wavesurfer-4000hd-mso-4x-500mhz-5gsps-hdmi-lxi-micro-sd-rj45-usb-teledyne-lecroy-wavesurfer-4054hd-promo1/p/30376587',
  },
  quantity: 1,
  returnableQuantity: 0,
  totalPrice: {
    currencyIso: 'CHF',
    priceType: 'BUY',
    value: 13917.75,
  },
  moqAdjusted: false,
  stepAdjusted: false,
};

export const MOCK_PRICE_OBJECT: Price = {
  basePrice: 13917.75,
  currencyIso: 'CHF',
  formattedValue: '13’917.75',
  minQuantity: 1,
  originalValue: 32943.75,
  pricePerX: 0,
  pricePerXBaseQty: 0,
  pricePerXUOM: 'PC',
  pricePerXUOMDesc: 'piece',
  pricePerXUOMQty: 0,
  priceType: PriceType.BUY,
  priceWithVat: 15045.088,
  saving: 16,
  value: 13917.75,
  vatPercentage: 8.1,
  vatValue: 1127.338,
};

export const MOCK_CART_STORE = {
  code: 's2b00009O1Y',
  guid: 'adb85811-4f80-4e6d-b386-7efb167f877d',
  entries: [
    {
      entryNumber: 1,
      product: {
        code: '30376587',
      },
      quantity: 1,
    },
  ],
};

export class MockCartStoreService {
  store$ = new BehaviorSubject<any>(MOCK_CART_STORE);

  getCartEntries() {
    return this.store$.getValue().entries;
  }
}

export class MockDistCartService {
  getCartDataFromStore() {
    return of(MOCK_CART_STORE);
  }
}
