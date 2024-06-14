import { Observable, of } from 'rxjs';
import { Occ, PriceType, Product } from '@spartacus/core';
import { ICustomProduct } from '@model/product.model';
import { VolumePriceType } from '@model/price.model';

export const MOCK_PRODUCT_DATA: ICustomProduct = {
  code: '30383883',
  activePromotionLabels: [
    {
      code: 'top',
      label: 'New',
      nameEN: 'New',
      priority: 4,
      rank: 4,
    },
  ],
  allowBulk: false,
  alternativeAliasMPN: '',
  availableInSnapEda: false,
  availableToB2B: true,
  availableToB2C: true,
  baseOptions: [],
  batteryComplianceCode: 'Y',
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
      code: 'cat-L2-3D_527501',
      introText:
        'Diagnosing issues in your network is essential to maintaining the health of your systems. Decreases in efficiency and even downtime can be prevented through regular troubleshooting with the right testing equipment.',
      level: 2,
      name: 'Network, Data & Communications',
      nameEN: 'Network, Data & Communications',
      selected: false,
      seoMetaDescription:
        'Distrelec Switzerland stocks a wide range of Network, Data & Communications. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
      seoMetaTitle: 'Network, Data & Communications|$(siteName)',
      seoSections: [
        {
          header: 'Types of network, data and communications test equipment',
          text: 'At Distrelec we offer different types of equipment for maintaining your network:<br/><br/>• Fiber optic testing<br/>• Measure & control via RS-232 / RS-485 / USB<br/>• Network analyzers<br/>• PC and multimedia cable testers<br/>• Telecommunication measuring devices<br/><br/>All of these devices can play a role',
        },
        {
          header: 'What is fibre optic testing?',
          text: 'With fibre optic test equipment, you can test and run diagnostics on fibre optic wiring or devices that receive signals from it. By connecting your testing device to the outlet, you’ll run a series of diagnostic tests to check signal strength, stability and other factors.',
        },
        {
          header: 'Troubleshooting a WiFi network',
          text: 'There are many variables that lead to Wi-Fi complaints, ranging from network-based problems and configuration issues to environmental or client device misconfigurations. Collecting all the key pieces of information the very first time is key to every front-line IT responder to resolve any complaint.',
        },
      ],
      url: '/test-measurement/network-data-communications/c/cat-L2-3D_527501',
    },
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
      seoSections: [
        {
          text: 'We stock a range of network analysers from leading brands NetAlly, Trend Networks, Fluke Networks, Value, Beha-Amprobe, Intellinet, Kunbus and Teledyne LeCroy. Our comprehensive selection of network analysers includes devices that will enable you to run a variety of test functions including baud rate, event trigger, IP-address and live lists. The selection includes devices which can run cable tests such as fibre tests, hub blink, jitter, traffic performance tests and VoIP performance tests.',
        },
        {
          text: 'Whatever tests you wish to carry out on your home or office computer network, we are confident that we will stock the item that you need in order to conduct your testing quickly and easily. In addition to network analysers, we stock a wide range of accessories including CCTV camera testers, cable verifiers and network transmission testers. As with all of our electrical devices, our range of network analysers are compliant with the Restriction of Hazardous Substances Directive (RoHS) for your peace of mind and to comply with relevant safety legislation.',
        },
      ],
      url: '/test-measurement/network-data-communications/network-analysers/c/cat-DNAV_PL_140802',
    },
  ],
  businessOnlyProduct: false,
  buyable: true,
  buyablePhaseoutProduct: false,
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
      seoSections: [
        {
          text: 'We stock a range of network analysers from leading brands NetAlly, Trend Networks, Fluke Networks, Value, Beha-Amprobe, Intellinet, Kunbus and Teledyne LeCroy. Our comprehensive selection of network analysers includes devices that will enable you to run a variety of test functions including baud rate, event trigger, IP-address and live lists. The selection includes devices which can run cable tests such as fibre tests, hub blink, jitter, traffic performance tests and VoIP performance tests.',
        },
        {
          text: 'Whatever tests you wish to carry out on your home or office computer network, we are confident that we will stock the item that you need in order to conduct your testing quickly and easily. In addition to network analysers, we stock a wide range of accessories including CCTV camera testers, cable verifiers and network transmission testers. As with all of our electrical devices, our range of network analysers are compliant with the Restriction of Hazardous Substances Directive (RoHS) for your peace of mind and to comply with relevant safety legislation.',
        },
      ],
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
          position: 1010,
          range: false,
          searchable: true,
          type: 'string',
          visibility: 'a_visibility',
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
          position: 1020,
          range: false,
          searchable: true,
          type: 'string',
          visibility: 'a_visibility',
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
          position: 1030,
          range: false,
          searchable: true,
          type: 'number',
          visibility: 'a_visibility',
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
          position: 1040,
          range: false,
          searchable: true,
          type: 'string',
          visibility: 'a_visibility',
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
          position: 1060,
          range: false,
          searchable: true,
          type: 'string',
          visibility: 'a_visibility',
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
          position: 1070,
          range: false,
          searchable: true,
          type: 'string',
          visibility: 'a_visibility',
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
          position: 1080,
          range: false,
          searchable: true,
          type: 'string',
          visibility: 'a_visibility',
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
          position: 1090,
          range: false,
          searchable: true,
          type: 'string',
          visibility: 'a_visibility',
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
          position: 1100,
          range: false,
          searchable: true,
          type: 'number',
          visibility: 'a_visibility',
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
          position: 4110,
          range: false,
          searchable: false,
          type: 'string',
          visibility: 'd_visibility',
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
  configurable: false,
  countryOfOrigin: {
    european: false,
    isocode: 'US',
    name: 'United States',
    nameEN: 'United States',
  },
  customsCode: '9030.4000',
  description: 'Product Name: Industrial Ethernet Cable and Network Tester',
  dimensions: '435 x 200 x 148 MM',
  distManufacturer: {
    code: 'man_fln',
    name: 'Fluke Networks',
    nameSeo: 'fluke-networks',
    urlId: '/manufacturer/fluke-networks/man_fln',
    emailAddresses: [],
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
      alternativeDownloads: [
        {
          downloadUrl: '/Web/Downloads/_t/ds/Fluke_LinkIQ_ger_tds.pdf',
          languages: [
            {
              active: true,
              isocode: 'de',
              name: 'German',
              nativeName: 'Deutsch',
              rank: 0,
            },
          ],
          mimeType: 'PDF',
          name: 'Fluke_LinkIQ_ger_tds.pdf',
        },
      ],
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
              rank: 0,
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
  grossWeight: 2330,
  grossWeightUnit: 'Gram',
  hasSvhc: false,
  hazardStatements: [],
  images: [
    {
      format: 'landscape_small',
      imageType: 'PRIMARY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: 'PRIMARY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'landscape_large',
      imageType: 'PRIMARY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'portrait_small',
      imageType: 'PRIMARY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: 'PRIMARY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/01/Fluke-LIQ-100-IE-30383883-01.jpg',
    },
    {
      format: 'landscape_small',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'landscape_large',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'portrait_small',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/02/Fluke-LIQ-100-IE-30383883-02.jpg',
    },
    {
      format: 'landscape_small',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'landscape_large',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'portrait_small',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/03/Fluke-LIQ-100-IE-30383883-03.jpg',
    },
    {
      format: 'landscape_small',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'landscape_large',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'portrait_small',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/04/Fluke-LIQ-100-IE-30383883-04.jpg',
    },
    {
      format: 'landscape_small',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
    {
      format: 'landscape_medium',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
    {
      format: 'landscape_large',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
    {
      format: 'portrait_small',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
    {
      format: 'portrait_medium',
      imageType: 'GALLERY' as Occ.ImageType,
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/3-/05/Fluke-LIQ-100-IE-30383883-05.jpg',
    },
  ],
  images360: [],
  isDangerousGoods: false,
  isProductBatteryCompliant: true,
  isRNDProduct: false,
  isROHSComplaint: true,
  isROHSConform: true,
  isROHSValidForCountry: true,
  itemCategoryGroup: 'NORM',
  movexArticleNumber: '563012',
  name: 'Industrial Ethernet Cable and Network Tester, LinkIQ, 10Gbps, RJ45 / USB-C',
  nameEN: 'Industrial Ethernet Cable and Network Tester, LinkIQ, 10Gbps, RJ45 / USB-C',
  navisionArticleNumber: '563012',
  numberOfReviews: 0,
  orderQuantityMinimum: 1,
  orderQuantityStep: 1,
  productFamilyUrl: '/en/network-testers-linkiq-fluke-networks/pf/3428808',
  productImages: [{}, {}, {}, {}, {}],
  productInformation: {
    articleDescription: [],
    articleDescriptionBullets: [],
    deliveryNoteArticle:
      'Included:<br/>Cable and Network Tester, Multi-Connector adapter, magnetic Strap, RJ45 remote ID #1, quick reference guide, USB-C to USB-A cable, charging cable, CAT6a patch cord, RJ45/M12D patch cord, RJ45/M8D patch cord, RJ45/11 modular adapter, hanging strap with remote ID holder, carrying bag',
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
  summary: '',
  supplementalHazardInfos: [],
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
  typeName: 'LIQ-100-IE',
  unspsc5: '41113702',
  url: '/industrial-ethernet-cable-and-network-tester-linkiq-10gbps-rj45-usb-fluke-networks-liq-100-ie/p/30383883',
  slug: 'industrial-ethernet-cable-and-network-tester-linkiq-10gbps-rj45-usb-c',
  nameHtml: 'Industrial Ethernet Cable and Network Tester, LinkIQ, 10Gbps, RJ45 / USB-C',
  topLabel: {
    label: 'New',
    code: 'top',
  },
};

export const MOCK_PRODUCT_DATA$: Observable<ICustomProduct[]> = of([
  {
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
    buyable: true,
    categoryCodePath: 'cat-DNAV_PL_14030202',
    code: '30185066',
    codeErpRelevant: '30185066',
    description: 'Product Name: Battery Tester',
    distManufacturer: {
      name: 'RND Components',
      image: [
        {
          key: 'brand_logo',
          value: {
            url: '/Web/WebShopImages/manufacturer_logo/cm/yk/rnd_components_cmyk.jpg',
          },
        },
      ],
      url: '/en/manufacturer/rnd-components/man_rnp',
    },
    ean: '',
    elfaArticleNumber: '',
    eligibleForReevoo: true,
    energyEfficiencyLabelImage: [],
    images: [
      {
        format: 'landscape_medium',
        url: '/Web/WebShopImages/landscape_medium/6-/01/30185066-01.jpg',
      },
      {
        format: 'portrait_small',
        url: '/Web/WebShopImages/portrait_small/6-/01/30185066-01.jpg',
      },
      {
        format: 'landscape_small',
        url: '/Web/WebShopImages/landscape_small/6-/01/30185066-01.jpg',
      },
    ],
    inShoppingList: false,
    manufacturer: 'RND Components',
    movexArticleNumber: '',
    name: 'Battery Tester, 1.5 ... 9 VDC',
    orderQuantityMinimum: 1,
    orderQuantityStep: 1,
    origPosition: '1',
    price: {
      currencyIso: 'CHF',
      formattedValue: 'CHF 9,25',
      minQuantity: 1,
      priceType: 'BUY' as PriceType,
      value: 9.25,
    },
    productFamilyUrl: '/en/battery-testers-analogue/pf/1854138',
    productImages: [{}],
    salesStatus: '30',
    salesUnit: '1 piece',
    technicalAttributes: [
      {
        key: 'Suitable Battery Type',
        value: 'Alkaline',
      },
      {
        key: 'Suitable Battery Type',
        value: 'Button Cells',
      },
      {
        key: 'Suitable Battery Type',
        value: 'NiCd',
      },
      {
        key: 'Voltage Measuring Range',
        value: '1.5 ... 9 VDC',
      },
      {
        key: 'Display Type',
        value: 'Analogue',
      },
      {
        key: 'Battery Type',
        value: '1x 9 V',
      },
    ],
    typeName: 'RND 305-00004E+RND 305-00009',
    url: '/battery-tester-vdc-rnd-components-rnd-305-00004e-rnd-305-00009/p/30185066',
    volumePricesMap: [
      {
        key: 1,
        value: [
          {
            key: 'custom' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 9,25',
              minQuantity: 1,
              priceType: 'BUY' as PriceType,
              value: 9.25,
            },
          },
          {
            key: 'list' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 9,25',
              minQuantity: 1,
              priceType: 'BUY' as PriceType,
              value: 9.25,
            },
          },
        ],
      },
    ],
    slug: 'battery-tester-1.5-...-9-vdc',
    nameHtml: 'Battery Tester, 1.5 ... 9 VDC',
    topLabel: {
      label: 'Bundle',
      code: 'noMover',
    },
    volumePrices: [
      {
        currencyIso: 'CHF',
        formattedValue: 'CHF 9,25',
        minQuantity: 1,
        priceType: 'BUY' as PriceType,
        value: 9.25,
      },
    ],
  },
  {
    activePromotionLabels: [],
    availableInSnapEda: false,
    availableToB2B: true,
    availableToB2C: true,
    buyable: true,
    categoryCodePath: 'cat-DNAV_PL_14030202',
    code: '30113113',
    codeErpRelevant: '30113113',
    description: 'Product Name: Battery Tester',
    distManufacturer: {
      name: 'RND Components',
      image: [
        {
          key: 'brand_logo',
          value: {
            url: '/Web/WebShopImages/manufacturer_logo/cm/yk/rnd_components_cmyk.jpg',
          },
        },
      ],
      url: '/en/manufacturer/rnd-components/man_rnp',
    },
    ean: '653415246466',
    elfaArticleNumber: '30113113',
    eligibleForReevoo: true,
    energyEfficiencyLabelImage: [],
    images: [
      {
        format: 'landscape_medium',
        url: '/Web/WebShopImages/landscape_medium/3-/01/30113113-01.jpg',
      },
      {
        format: 'portrait_small',
        url: '/Web/WebShopImages/portrait_small/3-/01/30113113-01.jpg',
      },
      {
        format: 'landscape_small',
        url: '/Web/WebShopImages/landscape_small/3-/01/30113113-01.jpg',
      },
    ],
    manufacturer: 'RND Components',
    movexArticleNumber: '308429',
    name: 'Battery Tester, 1.5 ... 9 VDC',
    orderQuantityMinimum: 1,
    orderQuantityStep: 1,
    origPosition: '2',
    price: {
      currencyIso: 'CHF',
      formattedValue: 'CHF 9,25',
      maxQuantity: 2,
      minQuantity: 1,
      priceType: 'BUY' as PriceType,
      value: 9.25,
    },
    productFamilyUrl: '/en/battery-testers-analogue/pf/1854138',
    productImages: [{}],
    salesStatus: '30',
    salesUnit: '1 piece',
    technicalAttributes: [
      {
        key: 'Suitable Battery Type',
        value: 'Alkaline',
      },
      {
        key: 'Suitable Battery Type',
        value: 'Button Cells',
      },
      {
        key: 'Suitable Battery Type',
        value: 'NiCd',
      },
      {
        key: 'Voltage Measuring Range',
        value: '1.5 ... 9 VDC',
      },
      {
        key: 'Display Type',
        value: 'Analogue',
      },
      {
        key: 'Battery Type',
        value: '1x 9 V',
      },
    ],
    typeName: 'RND 305-00009',
    url: '/battery-tester-vdc-rnd-components-rnd-305-00009/p/30113113',
    volumePricesMap: [
      {
        key: 1,
        value: [
          {
            key: 'custom' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 9,25',
              maxQuantity: 2,
              minQuantity: 1,
              priceType: 'BUY' as PriceType,
              value: 9.25,
            },
          },
          {
            key: 'list' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 9,25',
              maxQuantity: 2,
              minQuantity: 1,
              priceType: 'BUY' as PriceType,
              value: 9.25,
            },
          },
        ],
      },
      {
        key: 3,
        value: [
          {
            key: 'custom' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 8,80',
              maxQuantity: 4,
              minQuantity: 3,
              priceType: 'BUY' as PriceType,
              value: 8.8,
            },
          },
          {
            key: 'list' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 9,25',
              maxQuantity: 4,
              minQuantity: 1,
              priceType: 'BUY' as PriceType,
              value: 9.25,
            },
          },
        ],
      },
      {
        key: 5,
        value: [
          {
            key: 'custom' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 8,25',
              maxQuantity: 9,
              minQuantity: 5,
              priceType: 'BUY' as PriceType,
              value: 8.25,
            },
          },
          {
            key: 'list' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 9,25',
              maxQuantity: 9,
              minQuantity: 1,
              priceType: 'BUY' as PriceType,
              value: 9.25,
            },
          },
        ],
      },
      {
        key: 10,
        value: [
          {
            key: 'custom' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 7,70',
              minQuantity: 10,
              priceType: 'BUY' as PriceType,
              value: 7.7,
            },
          },
          {
            key: 'list' as VolumePriceType,
            value: {
              currencyIso: 'CHF',
              formattedValue: 'CHF 9,25',
              minQuantity: 1,
              priceType: 'BUY' as PriceType,
              value: 9.25,
            },
          },
        ],
      },
    ],
    slug: 'battery-tester-1.5-...-9-vdc',
    nameHtml: 'Battery Tester, 1.5 ... 9 VDC',
    volumePrices: [
      {
        currencyIso: 'CHF',
        formattedValue: 'CHF 9,25',
        maxQuantity: 2,
        minQuantity: 1,
        priceType: 'BUY' as PriceType,
        value: 9.25,
      },
      {
        currencyIso: 'CHF',
        formattedValue: 'CHF 8,80',
        maxQuantity: 4,
        minQuantity: 3,
        priceType: 'BUY' as PriceType,
        value: 8.8,
      },
      {
        currencyIso: 'CHF',
        formattedValue: 'CHF 8,25',
        maxQuantity: 9,
        minQuantity: 5,
        priceType: 'BUY' as PriceType,
        value: 8.25,
      },
      {
        currencyIso: 'CHF',
        formattedValue: 'CHF 7,70',
        minQuantity: 10,
        priceType: 'BUY' as PriceType,
        value: 7.7,
      },
    ],
  },
]);

export const MOCKED_RS_PRODUCT_AVAILABILITY = {
  backorderQuantity: 0,
  deliveryTimeBackorder: '> 10 d',
  detailInfo: true,
  leadTimeErp: 2,
  productCode: '30149810',
  requestedQuantity: 1,
  statusLabel: '',
  stockLevelPickup: [
    {
      stockLevel: 38,
      warehouseCode: '7374',
      warehouseName: 'Distrelec Schweiz AG',
    },
  ],
  stockLevelTotal: 5038,
  stockLevels: [
    {
      available: 5000,
      deliveryTime: '',
      external: false,
      fast: false,
      mview: 'RSP',
      replenishmentDeliveryTime: '3 ',
      replenishmentDeliveryTime2: '106 ',
      waldom: false,
      warehouseId: '7371',
    },
    {
      available: 38,
      deliveryTime: '',
      external: false,
      fast: false,
      mview: 'RSP',
      replenishmentDeliveryTime: '1 ',
      replenishmentDeliveryTime2: '0',
      waldom: false,
      warehouseId: '7374',
    },
  ],
};

export const MOCKED_DIR_PRODUCT_AVAILABILITY = {
  backorderQuantity: 0,
  deliveryTimeBackorder: '> 10 d',
  detailInfo: true,
  leadTimeErp: 2,
  productCode: '30149810',
  requestedQuantity: 1,
  statusLabel: '',
  stockLevelPickup: [
    {
      stockLevel: 38,
      warehouseCode: '7374',
      warehouseName: 'Distrelec Schweiz AG',
    },
  ],
  stockLevelTotal: 5038,
  stockLevels: [
    {
      available: 5000,
      deliveryTime: '',
      external: false,
      fast: false,
      mview: 'DIR',
      replenishmentDeliveryTime: '3 ',
      replenishmentDeliveryTime2: '106 ',
      waldom: false,
      warehouseId: '7371',
    },
    {
      available: 38,
      deliveryTime: '',
      external: false,
      fast: false,
      mview: 'DIR',
      replenishmentDeliveryTime: '1 ',
      replenishmentDeliveryTime2: '0',
      waldom: false,
      warehouseId: '7374',
    },
  ],
};

export const MOCK_IMAGES = [
  {
    key: 'landscape_large',
    value: {
      format: 'landscape_large',
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/cm/yk/rnd_components_cmyk.jpg',
    },
  },
  {
    key: 'landscape_medium',
    value: {
      format: 'landscape_medium',
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/cm/yk/rnd_components_cmyk.jpg',
    },
  },
  {
    key: 'landscape_small',
    value: {
      format: 'landscape_small',
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/cm/yk/rnd_components_cmyk.jpg',
    },
  },
  {
    key: 'portrait_small',
    value: {
      format: 'portrait_small',
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/cm/yk/rnd_components_cmyk.jpg',
    },
  },
  {
    key: 'brand_logo',
    value: {
      format: 'brand_logo',
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/manufacturer_logo/cm/yk/rnd_components_cmyk.jpg',
    },
  },
  {
    key: 'portrait_medium',
    value: {
      format: 'portrait_medium',
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/cm/yk/rnd_components_cmyk.jpg',
    },
  },
];
