/* eslint-disable */

export const mockFusionSearchResponse = {
  fusion: {
    facet_labels: [
      'categoryHierarchy_en_ss:Filter By Category,manufacturerName:Manufacturer,productStatus:Product Status',
    ],
  },
  response: {
    query: {
      q: 'bulb',
      autocorrected: false,
    },
    start: 0,
    maxScore: 218496.22,
    fusionQueryId: 'OJLbaoUqW8',
    docs: [
      {
        inStock: false,
        productNumber: '30225676',
        eligibleForReevoo: false,
        id: '30225676-SE',
        description: 'Product Name: Halogen Bulb',
        thumbnail:
          'https://dev.media.distrelec.com/Web/WebShopImages/landscape_small/5-/01/Bailey-143239-30225675-01.jpg',
        displayFields:
          '[{"code":"dispowernum","attributeName":"Power","value":"150","unit":"W","fieldType":"double"},{"code":"disbulbbasetxt","attributeName":"Bulb Base","value":"E27","fieldType":"string"},{"code":"disvoltagenum","attributeName":"Voltage","value":"240","unit":"V","fieldType":"double"},{"code":"discolourtemperaturenum","attributeName":"Colour Temperature","value":"2800","unit":"K","fieldType":"double"},{"code":"dislightcolourtxt","attributeName":"Light Colour","value":"Warm White","fieldType":"string"},{"code":"energyclasseslov","attributeName":"Energy Class","value":"C","fieldType":"string"},{"code":"disbulbsurfacetxt","attributeName":"Bulb Surface","value":"Clear","fieldType":"string"},{"code":"disservicelifetxt","attributeName":"Service Life","value":"2000 h","fieldType":"string"},{"code":"dislengthnum","attributeName":"Length","value":"115","unit":"mm","fieldType":"double"},{"code":"disdiameternum","attributeName":"Diameter","value":"32","unit":"mm","fieldType":"double"},{"code":"disluminousfluxnum","attributeName":"Luminous Flux","value":"2780","unit":"lm","fieldType":"double"},{"code":"disseriesnav007001005000txt","attributeName":"Series","value":"ECO","fieldType":"string"}]',
        energyEfficiency: '{"Energyclasses_LOV":"C","Leistung_W":"150"}',
        movexArticleNumber: '448460',
        elfaArticleNumber: '30225676',
        alternativeAliasMPN: '',
        ean: '8714681432839',
        normalizedAlternativesMPN: '',
        normalizedAlternativeAliasMPN: '',
        sapMPN: '143283',
        typeNameNormalized: '143283',
        typeName: '143283',
        category1Name: 'Lighting',
        category3Code: 'cat-DNAV_PL_110204',
        category2Code: 'cat-L3D_531399',
        category1Code: 'cat-L2D_379658',
        category3: 'cat-DNAV_PL_110204|Halogen Bulbs',
        category2: 'cat-L3D_531399|Bulbs & Tubes',
        category1: 'cat-L2D_379658|Lighting',
        categoryCodePath: 'cat-L2D_379658/cat-L3D_531399/cat-DNAV_PL_110204',
        sapPlantProfile: '4',
        isRndBrand: false,
        activePromotionLabels: 'new|New to us',
        currency: 'SEK',
        scalePricesGross: '{"1":212.5,"5":191.25,"10":170.0}',
        scalePricesNet: '{"1":170.0,"5":153.0,"10":136.0}',
        itemMin: 1,
        itemStep: 1,
        salesUnit: 'piece',
        orderQuantityMinimum: 1,
        singleMinPriceNet: 170,
        singleMinPriceGross: 212.5,
        salesStatus: 21,
        imageURL_landscape_medium: '/Web/WebShopImages/landscape_medium/5-/01/Bailey-143239-30225675-01.jpg',
        imageURL_landscape_small: '/Web/WebShopImages/landscape_small/5-/01/Bailey-143239-30225675-01.jpg',
        manufacturerImageUrl: '/Web/WebShopImages/manufacturer_logo/cm/yk/bailey_logo_cmyk.jpg',
        manufacturerUrl: '/manufacturer/bailey-lights/man_bly',
        productFamilyCode: '3570130',
        imageURL: '/Web/WebShopImages/portrait_small/5-/01/Bailey-143239-30225675-01.jpg',
        codeErpRelevant: '30225676',
        visibleInChannels: ['B2B', 'B2C'],
        visibleInShop: true,
        buyable: true,
        productFamilyUrl: '/halogen-bulbs-eco-halogen-classic-bailey/pf/3570130',
        distManufacturer: 'Bailey Lights',
        url: '/halogen-bulb-150w-240v-2800k-warm-white-2780lm-e27-bailey-lights-143283/p/30225676',
        title: 'Halogen Bulb 150W 240V 2800K Warm White 2780lm E27',
        code: '30225676-SE',
        score: 218496.22,
      },
    ],
    numFound: 1297,
    rulesTriggered: false,
    punchOutFilter: {},
    totalPages: 26,
    'search.spelling.corrected': false,
    'search.keyword': 'bulb',
    numFoundExact: true,
    'search.keyword.query': 'bulb',
  },
  responseHeader: {
    zkConnected: true,
    QTime: 37,
    totalTime: 840,
    params: {
      fusionQueryId: 'OJLbaoUqW8',
    },
    status: 0,
  },
  facets: [
    {
      code: 'categoryCodes',
      field: 'categoryHierarchy_en_ss',
      name: 'Filter By Category',
      type: 'string',
      multiselect: false,
      values: [
        {
          value: 'cat-L2D_379624',
          filter: '&fq=category2Code:cat-L2D_379624',
          path: '1||cat-L1D_379516|Automation||cat-L2D_379624|Automation Signalling',
          level: 2,
          count: 29,
          name: 'Automation Signalling',
          selected: false,
        },
      ],
    },
    {
      code: 'pimWebUse_disbulbbasetxt_en_ss',
      field: 'pimWebUse_disbulbbasetxt_en_ss',
      multiselect: true,
      name: 'Bulb Base',
      type: 'string',
      unit: '',
      baseUnit: '',
      values: [
        {
          value: '2G11',
          name: '2G11',
          count: 29,
        },
        {
          value: '2G10',
          name: '2G10',
          count: 5,
        },
      ],
    },
    {
      code: 'pimWebUse_dislengthnum_en_ds',
      field: 'pimWebUse_dislengthnum_en_ds',
      multiselect: true,
      name: 'Length',
      type: 'number',
      unit: 'mm',
      baseUnit: '',
      values: [
        {
          value: 0.028,
          name: '28 mm',
          count: 42,
        },
        {
          value: 0.026,
          name: '26 mm',
          count: 34,
        },
        {
          value: 0.045,
          name: '45 mm',
          count: 33,
        },
      ],
    },
  ],
  request: {
    q: 'bulb',
    sid: 'gXdlIkfR4o',
    start: 0,
    rows: 50,
    sort: '',
    country: 'SE',
    language: 'en',
    channel: 'B2B',
  },
};

export const mockFusionSearchResult = {
  pagination: {
    pageSize: 50,
    currentPage: 1,
    totalPages: 26,
    totalResults: 1297,
  },
  currentQuery: {
    query: {
      value: 'bulb',
    },
    url: '/search?q=bulb',
  },
  freeTextSearch: 'bulb',
  keywordRedirectUrl: null,
  facets: [
    {
      code: 'filter_categoryCodes',
      values: [
        {
          code: 'cat-L2D_379624',
          name: 'Automation Signalling',
          count: 29,
          selected: false,
          propertyNameArgumentSeparator: '^',
          query: {
            query: {
              value: 'bulb&filter_categoryCodes=cat-L2D_379624',
            },
            url: '/search?q=bulb&filter_categoryCodes=cat-L2D_379624',
          },
          queryFilter: 'filter_categoryCodes=cat-L2D_379624',
        },
      ],
      name: 'Filter By Category',
      type: 'CHECKBOX',
      unit: undefined,
      hasSelectedElements: false,
      hasMinMaxFilters: false,
      minValue: undefined,
      maxValue: undefined,
    },
    {
      code: 'filter_disbulbbasetxt_en_ss',
      values: [
        {
          code: '2G10',
          name: '2G10',
          count: 5,
          selected: undefined,
          propertyNameArgumentSeparator: '^',
          query: {
            query: {
              value: 'bulb&filter_disbulbbasetxt_en_ss=2G10',
            },
            url: '/search?q=bulb&filter_disbulbbasetxt_en_ss=2G10',
          },
          queryFilter: 'filter_disbulbbasetxt_en_ss=2G10',
        },
        {
          code: '2G11',
          name: '2G11',
          count: 29,
          selected: undefined,
          propertyNameArgumentSeparator: '^',
          query: {
            query: {
              value: 'bulb&filter_disbulbbasetxt_en_ss=2G11',
            },
            url: '/search?q=bulb&filter_disbulbbasetxt_en_ss=2G11',
          },
          queryFilter: 'filter_disbulbbasetxt_en_ss=2G11',
        },
      ],
      name: 'Bulb Base',
      type: 'CHECKBOX',
      unit: '',
      hasSelectedElements: false,
      hasMinMaxFilters: false,
      minValue: undefined,
      maxValue: undefined,
    },
    {
      code: 'filter_dislengthnum_en_ds',
      values: [
        {
          code: 0.026,
          name: '26 mm',
          count: 34,
          selected: undefined,
          propertyNameArgumentSeparator: '^',
          query: {
            query: {
              value: 'bulb&filter_dislengthnum_en_ds=0.026',
            },
            url: '/search?q=bulb&filter_dislengthnum_en_ds=0.026',
          },
          queryFilter: 'filter_dislengthnum_en_ds=0.026',
        },
        {
          code: 0.028,
          name: '28 mm',
          count: 42,
          selected: undefined,
          propertyNameArgumentSeparator: '^',
          query: {
            query: {
              value: 'bulb&filter_dislengthnum_en_ds=0.028',
            },
            url: '/search?q=bulb&filter_dislengthnum_en_ds=0.028',
          },
          queryFilter: 'filter_dislengthnum_en_ds=0.028',
        },
        {
          code: 0.045,
          name: '45 mm',
          count: 33,
          selected: undefined,
          propertyNameArgumentSeparator: '^',
          query: {
            query: {
              value: 'bulb&filter_dislengthnum_en_ds=0.045',
            },
            url: '/search?q=bulb&filter_dislengthnum_en_ds=0.045',
          },
          queryFilter: 'filter_dislengthnum_en_ds=0.045',
        },
      ],
      name: 'Length',
      type: 'CHECKBOX',
      unit: 'mm',
      hasSelectedElements: false,
      hasMinMaxFilters: true,
      minValue: undefined,
      maxValue: undefined,
    },
  ],
  categoryFilters: [],
  categoryBreadcrumbs: [
    {
      name: undefined,
      url: '[object Object]',
    },
  ],
  products: [
    {
      inStock: false,
      productNumber: '30225676',
      eligibleForReevoo: false,
      id: '30225676-SE',
      description: 'Product Name: Halogen Bulb',
      thumbnail:
        'https://dev.media.distrelec.com/Web/WebShopImages/landscape_small/5-/01/Bailey-143239-30225675-01.jpg',
      displayFields:
        '[{"code":"dispowernum","attributeName":"Power","value":"150","unit":"W","fieldType":"double"},{"code":"disbulbbasetxt","attributeName":"Bulb Base","value":"E27","fieldType":"string"},{"code":"disvoltagenum","attributeName":"Voltage","value":"240","unit":"V","fieldType":"double"},{"code":"discolourtemperaturenum","attributeName":"Colour Temperature","value":"2800","unit":"K","fieldType":"double"},{"code":"dislightcolourtxt","attributeName":"Light Colour","value":"Warm White","fieldType":"string"},{"code":"energyclasseslov","attributeName":"Energy Class","value":"C","fieldType":"string"},{"code":"disbulbsurfacetxt","attributeName":"Bulb Surface","value":"Clear","fieldType":"string"},{"code":"disservicelifetxt","attributeName":"Service Life","value":"2000 h","fieldType":"string"},{"code":"dislengthnum","attributeName":"Length","value":"115","unit":"mm","fieldType":"double"},{"code":"disdiameternum","attributeName":"Diameter","value":"32","unit":"mm","fieldType":"double"},{"code":"disluminousfluxnum","attributeName":"Luminous Flux","value":"2780","unit":"lm","fieldType":"double"},{"code":"disseriesnav007001005000txt","attributeName":"Series","value":"ECO","fieldType":"string"}]',
      energyEfficiency: '{"Energyclasses_LOV":"C","Leistung_W":"150"}',
      movexArticleNumber: '448460',
      elfaArticleNumber: '30225676',
      alternativeAliasMPN: '',
      ean: '8714681432839',
      normalizedAlternativesMPN: '',
      normalizedAlternativeAliasMPN: '',
      sapMPN: '143283',
      typeNameNormalized: '143283',
      typeName: '143283',
      category1Name: 'Lighting',
      category3Code: 'cat-DNAV_PL_110204',
      category2Code: 'cat-L3D_531399',
      category1Code: 'cat-L2D_379658',
      category3: 'cat-DNAV_PL_110204|Halogen Bulbs',
      category2: 'cat-L3D_531399|Bulbs & Tubes',
      category1: 'cat-L2D_379658|Lighting',
      categoryCodePath: 'cat-L2D_379658/cat-L3D_531399/cat-DNAV_PL_110204',
      sapPlantProfile: '4',
      isRndBrand: false,
      activePromotionLabels: 'new|New to us',
      currency: 'SEK',
      scalePricesGross: '{"1":212.5,"5":191.25,"10":170.0}',
      scalePricesNet: '{"1":170.0,"5":153.0,"10":136.0}',
      itemMin: 1,
      itemStep: 1,
      salesUnit: 'piece',
      orderQuantityMinimum: 1,
      singleMinPriceNet: 170,
      singleMinPriceGross: 212.5,
      salesStatus: 21,
      imageURL_landscape_medium: '/Web/WebShopImages/landscape_medium/5-/01/Bailey-143239-30225675-01.jpg',
      imageURL_landscape_small: '/Web/WebShopImages/landscape_small/5-/01/Bailey-143239-30225675-01.jpg',
      manufacturerImageUrl: '/Web/WebShopImages/manufacturer_logo/cm/yk/bailey_logo_cmyk.jpg',
      manufacturerUrl: '/manufacturer/bailey-lights/man_bly',
      productFamilyCode: '3570130',
      imageURL: '/Web/WebShopImages/portrait_small/5-/01/Bailey-143239-30225675-01.jpg',
      codeErpRelevant: '30225676',
      visibleInChannels: ['B2B', 'B2C'],
      visibleInShop: true,
      buyable: true,
      productFamilyUrl: '/halogen-bulbs-eco-halogen-classic-bailey/pf/3570130',
      distManufacturer: 'Bailey Lights',
      url: '/halogen-bulb-150w-240v-2800k-warm-white-2780lm-e27-bailey-lights-143283/p/30225676',
      title: 'Halogen Bulb 150W 240V 2800K Warm White 2780lm E27',
      code: '30225676-SE',
      score: 218496.22,
    },
  ],
  punchedOut: false,
  url: 'en/search?q=bulb&sid=gXdlIkfR4o',
  noRelevantResultsBanner: false,
};
