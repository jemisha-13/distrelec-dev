import { ImageType } from '@spartacus/core';

export const BLOOMREACH_DATA: any = {
  code: '30099461',
  activePromotionLabels: [
    {
      code: 'top',
      label: 'New',
    },
  ],
  allowBulk: false,
  availableInSnapEda: false,
  availableToB2B: true,
  availableToB2C: true,
  baseOptions: [],
  buyable: true,
  buyableReplacementProduct: false,
  categories: [
    {
      code: 'cat-DNAV_PL_140802',
      introText:
        'Network analysers are commonly used in home and business environments where testing and measurements of data and information communications systems are required. Network analysers are helpful electronic devices that can provide packet capture data which specifies who the devices are communicating with and via which port and protocol. They can identify devices or network issues which are causing traffic flow problems and detect unusual network traffic.',
      level: 3,
      name: 'Network Analysers',
      nameEN: 'Network Analysers',
      selected: false,
      seoMetaDescription:
        'Distrelec Switzerland stocks a wide range of Network Analysers. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
      seoMetaTitle: 'Network Analysers|$(siteName)',
      url: '/test-measurement/network-data-communications/network-analysers/c/cat-DNAV_PL_140802',
    },
  ],
  classifications: [
    {
      code: 'class-DNAV_PL_140802',
      features: [
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_equipmenttype_txt',
          comparable: true,
          featurePosition: 34,
          featureValues: [
            {
              value: 'Network Troubleshooter',
            },
            {
              value: 'Cable Tester Tone Generator',
            },
          ],
          name: 'Equipment Type',
          range: false,
          searchable: true,
          type: 'string',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_connectortype_txt',
          comparable: true,
          featurePosition: 45,
          featureValues: [
            {
              value: 'RJ45',
            },
            {
              value: 'USB-C',
            },
          ],
          name: 'Connector Type',
          range: false,
          searchable: true,
          type: 'string',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_networkspeed_num',
          comparable: true,
          featurePosition: 32,
          featureUnit: {
            name: 'Gbps',
            symbol: 'Gbps',
            unitType: 'std.unit.BPS',
          },
          featureValues: [
            {
              baseUnitValue: '1e+10',
              value: '10',
            },
          ],
          name: 'Network Speed',
          range: false,
          searchable: true,
          type: 'number',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_tone_txt',
          comparable: true,
          featurePosition: 31,
          featureValues: [
            {
              value: 'Yes',
            },
          ],
          name: 'Tone',
          range: false,
          searchable: true,
          type: 'string',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_cabletestfunction_txt',
          comparable: true,
          featurePosition: 33,
          featureValues: [
            {
              value: 'Cable Length Test',
            },
            {
              value: 'Wire Map',
            },
            {
              value: 'Crossed Pairs',
            },
            {
              value: 'Hub Blink',
            },
            {
              value: 'Miswire',
            },
            {
              value: 'PoE Testing',
            },
            {
              value: 'Reversed and Distance to Open',
            },
            {
              value: 'Short Circuit',
            },
            {
              value: 'Split Pairs',
            },
            {
              value: 'Tone Generator',
            },
            {
              value: 'Twisted Mapping',
            },
            {
              value: 'VLAN Support',
            },
          ],
          name: 'Cable Test Function',
          range: false,
          searchable: true,
          type: 'string',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_diagnosticfunctions_txt',
          comparable: true,
          featurePosition: 28,
          featureValues: [
            {
              value: 'Mode 10Base-Tx',
            },
            {
              value: 'Mode 100Base-Tx',
            },
            {
              value: 'Mode 1000Base-T',
            },
            {
              value: 'VLAN Detection',
            },
            {
              value: 'Port Finder',
            },
            {
              value: 'IP-Address',
            },
          ],
          name: 'Diagnostic Functions',
          range: false,
          searchable: true,
          type: 'string',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_displaytype_txt',
          comparable: true,
          featurePosition: 39,
          featureValues: [
            {
              value: 'Touchscreen',
            },
          ],
          name: 'Display Type',
          range: false,
          searchable: true,
          type: 'string',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_batterytype_txt',
          comparable: true,
          featurePosition: 29,
          featureValues: [
            {
              value: 'Rechargeable',
            },
          ],
          name: 'Battery Type',
          range: false,
          searchable: true,
          type: 'string',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_batterylife_num',
          comparable: true,
          featurePosition: 42,
          featureUnit: {
            name: 'h',
            symbol: 'h',
            unitType: 'unece.unit.SEC',
          },
          featureValues: [
            {
              baseUnitValue: '2.88e+4',
              value: '8',
            },
          ],
          name: 'Battery Life',
          range: false,
          searchable: true,
          type: 'number',
        },
        {
          code: 'DistrelecClassification/1.0/class-DNAV_PL_140802.dis_series_txt',
          comparable: true,
          featurePosition: 27,
          featureValues: [
            {
              value: 'LinkIQ',
            },
          ],
          name: 'Series',
          range: false,
          searchable: false,
          type: 'string',
        },
      ],
      name: 'Network Analysers',
    },
    {
      code: 'class-root',
      features: [],
    },
  ],
  codeErpRelevant: '30383883',
  countryOfOrigin: {
    isocode: 'US',
    name: 'United States',
  },
  customsCode: '9030.4000',
  description: 'Product Name: Industrial Ethernet Cable and Network Tester',
  dimensions: '435 x 200 x 148 MM',
  distManufacturer: {
    code: 'man_fln',
    name: 'RND Lab',
    nameSeo: 'fluke-networks',
    urlId: '/manufacturer/fluke-networks/man_fln',
    emailaddresses: [],
    image: [
      {
        key: 'landscape_large',
        value: {
          format: 'landscape_large',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/or/ks/Fluke_Networks.jpg',
        },
      },
      {
        key: 'landscape_medium',
        value: {
          format: 'landscape_medium',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/or/ks/Fluke_Networks.jpg',
        },
      },
      {
        key: 'portrait_small',
        value: {
          format: 'portrait_small',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/or/ks/Fluke_Networks.jpg',
        },
      },
      {
        key: 'landscape_small',
        value: {
          format: 'landscape_small',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/or/ks/Fluke_Networks.jpg',
        },
      },
      {
        key: 'brand_logo',
        value: {
          format: 'brand_logo',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/or/ks/Fluke_Networks.jpg',
        },
      },
      {
        key: 'portrait_medium',
        value: {
          format: 'portrait_medium',
          url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/or/ks/Fluke_Networks.jpg',
        },
      },
    ],
    phoneNumbers: [],
    productGroups: [],
    promotionText: '',
    seoMetaDescription:
      'Shop 253 Fluke Networks products at ${siteName}. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
    seoMetaTitle: 'Fluke Networks Distributor | ${siteName}',
    webDescription: '',
    websites: [],
  },
  downloads: [
    {
      code: 'datasheets',
      downloads: [
        {
          downloadUrl: '/Web/Downloads/_t/ds/Fluke_LinkIQ_eng_tds.pdf',
          languages: [
            {
              active: true,
              isocode: 'en',
              name: 'English',
              nativeName: 'English',
            },
          ],
          mimeType: 'PDF',
          name: 'Fluke_LinkIQ_eng_tds.pdf',
        },
      ],
      rank: 1,
      title: 'Datasheets',
    },
  ],
  ean: '',
  elfaArticleNumber: '30383883',
  eligibleForReevoo: true,
  enumber: '',
  formattedSvhcReviewDate: '17/01/2023',
  grossWeight: '2330',
  grossWeightUnit: 'Gram',
  hasSvhc: false,
  images: [
    {
      format: 'landscape_small',
      imageType: ImageType.PRIMARY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: ImageType.PRIMARY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'landscape_large',
      imageType: ImageType.PRIMARY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/1-/01/RND%20510-00003_30099461-01.jpg',
    },
    {
      format: 'portrait_small',
      imageType: ImageType.PRIMARY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: ImageType.PRIMARY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'landscape_small',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'landscape_large',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'portrait_small',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'landscape_small',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'landscape_large',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'portrait_small',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'landscape_small',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'landscape_large',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'portrait_small',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'landscape_small',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
    {
      format: 'landscape_large',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
    {
      format: 'portrait_small',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: ImageType.GALLERY,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
  ],
  isDangerousGoods: false,
  isProductBatteryCompliant: true,
  isRNDProduct: false,
  isROHSComplaint: true,
  itemCategoryGroup: 'NORM',
  movexArticleNumber: '563012',
  name: 'Industrial Ethernet Cable and Network Tester, LinkIQ, 10Gbps, RJ45 / USB-C',
  nameEN: 'Industrial Ethernet Cable and Network Tester, LinkIQ, 10Gbps, RJ45 / USB-C',
  numberOfReviews: 0,
  orderQuantityMinimum: 1,
  orderQuantityStep: 1,
  productFamilyUrl: '/en/network-testers-linkiq-fluke-networks/pf/3428808',
  productInformation: {
    articleDescription: [],
    articleDescriptionBullets: [],
    familyDescription: [],
    familyDescriptionBullets: [
      'Cable performance testing up to 10GBASE-T via frequency-based measurements ',
      'Network features include Nearest Switch Diagnostics including advertised data rate, switch name and port number, and VLAN',
      'Power over Ethernet verification - Detects the PoE class (1-8) and power, and performs a load test of available PoE from the connected switch ',
      'Displays cable length, wire map, and distance to open or short ',
      'Manage results and print reports from LinkWare™ PC',
    ],
    seriesDescription: [],
    seriesDescriptionBullets: [],
    usageNote: [],
  },
  purchasable: true,
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
  summary: '',
  svhcReviewDate: '2023-01-17T00:00:00+0000',
  technicalAttributes: [
    {
      key: 'Equipment Type',
      value: 'Network Troubleshooter',
    },
    {
      key: 'Equipment Type',
      value: 'Cable Tester Tone Generator',
    },
    {
      key: 'Connector Type',
      value: 'RJ45',
    },
    {
      key: 'Connector Type',
      value: 'USB-C',
    },
    {
      key: 'Network Speed',
      value: '10 Gbps',
    },
    {
      key: 'Tone',
      value: 'Yes',
    },
    {
      key: 'Cable Test Function',
      value: 'Cable Length Test',
    },
    {
      key: 'Cable Test Function',
      value: 'Wire Map',
    },
    {
      key: 'Cable Test Function',
      value: 'Crossed Pairs',
    },
    {
      key: 'Cable Test Function',
      value: 'Hub Blink',
    },
    {
      key: 'Cable Test Function',
      value: 'Miswire',
    },
    {
      key: 'Cable Test Function',
      value: 'PoE Testing',
    },
    {
      key: 'Cable Test Function',
      value: 'Reversed and Distance to Open',
    },
    {
      key: 'Cable Test Function',
      value: 'Short Circuit',
    },
    {
      key: 'Cable Test Function',
      value: 'Split Pairs',
    },
    {
      key: 'Cable Test Function',
      value: 'Tone Generator',
    },
    {
      key: 'Cable Test Function',
      value: 'Twisted Mapping',
    },
    {
      key: 'Cable Test Function',
      value: 'VLAN Support',
    },
    {
      key: 'Diagnostic Functions',
      value: 'Mode 10Base-Tx',
    },
    {
      key: 'Diagnostic Functions',
      value: 'Mode 100Base-Tx',
    },
    {
      key: 'Diagnostic Functions',
      value: 'Mode 1000Base-T',
    },
    {
      key: 'Diagnostic Functions',
      value: 'VLAN Detection',
    },
    {
      key: 'Diagnostic Functions',
      value: 'Port Finder',
    },
    {
      key: 'Diagnostic Functions',
      value: 'IP-Address',
    },
    {
      key: 'Display Type',
      value: 'Touchscreen',
    },
    {
      key: 'Battery Type',
      value: 'Rechargeable',
    },
    {
      key: 'Battery Life',
      value: '8 h',
    },
  ],
  transportGroupData: {
    bulky: false,
    code: '1000',
    dangerous: false,
    nameErp: 'Std / Non DG / No Calibration',
    relevantName: 'Std / Non DG / No Calibration',
  },
  typeName: 'RND 510-00003',
  unspsc5: '41113702',
  url: '/industrial-ethernet-cable-and-network-tester-linkiq-10gbps-rj45-usb-fluke-networks-liq-100-ie/p/30383883',
  slug: 'industrial-ethernet-cable-and-network-tester-linkiq-10gbps-rj45-usb-c',
  nameHtml: 'Industrial Ethernet Cable and Network Tester, LinkIQ, 10Gbps, RJ45 / USB-C',
  topLabel: {
    label: 'New',
    code: 'top',
  },
};
