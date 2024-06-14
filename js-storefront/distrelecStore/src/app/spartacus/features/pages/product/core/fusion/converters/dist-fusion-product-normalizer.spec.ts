/* eslint-disable max-len */
/* eslint-disable @typescript-eslint/naming-convention */
import { TestBed } from '@angular/core/testing';
import { DistFusionProductNormalizer } from './dist-fusion-product-normalizer';
import { FusionProduct } from '@model/fusion-product-search.model';
import { FusionProductPriceService } from '../services/fusion-product-price.service';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { PriceType } from '@spartacus/core';
import { VolumePriceType } from '@model/price.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { of } from 'rxjs';

describe('DistFusionProductNormalizer', () => {
  let service: DistFusionProductNormalizer;

  const mockProduct = {
    inStock: true,
    productNumber: '15246855',
    eligibleForReevoo: false,
    id: '15246855-SE',
    description: 'Product Name: Multipac 2U(HE)340 20860-212',
    thumbnail:
      'https://dev.media.distrelec.com/Web/WebShopImages/landscape_small/_t/if/19-einsatzgehause-multipac-1.jpg',
    displayFields:
      '[{"code":"diswidthhorizontalpitchnum","attributeName":"Width Horizontal Pitch","value":"84","fieldType":"double"},{"code":"disheightunitsnum","attributeName":"Height Units","value":"2","fieldType":"double"},{"code":"diswidthnum","attributeName":"Width","value":"403","unit":"mm","fieldType":"double"},{"code":"dissuitablefortxt","attributeName":"Suitable for","value":"Non-standardised assemblies","fieldType":"string"},{"code":"disdepthnum","attributeName":"Depth","value":"340","unit":"mm","fieldType":"double"},{"code":"dissubrackcolourtxt","attributeName":"Subrack Colour","value":"Grey","fieldType":"string"},{"code":"dissubrackmaterialtxt","attributeName":"Subrack Material","value":"Aluminium","fieldType":"string"}]',
    energyEfficiency: '{"Energyclasses_LOV":"E","Leistung_W":"2"}',
    energyEfficiencyLabelImageUrl: '/Web/WebShopImages/portrait_medium/32/75/EE_Label_963275.jpg',
    movexArticleNumber: '321503',
    elfaArticleNumber: '5246855',
    alternativeAliasMPN: '',
    ean: '',
    normalizedAlternativesMPN: '',
    normalizedAlternativeAliasMPN: '',
    sapMPN: '20860212',
    typeNameNormalized: '20860212',
    typeName: '20860-212',
    category1Name: 'Enclosures',
    category3Code: 'cat-DNAV_PL_060201',
    category2Code: 'cat-DNAV_0502',
    category1Code: 'cat-L2D_379531',
    category3: 'cat-DNAV_PL_060201|19" Subracks',
    category2: 'cat-DNAV_0502|19" Subracks & Parts',
    category1: 'cat-L2D_379531|Enclosures',
    categoryCodePath: 'cat-L2D_379531/cat-DNAV_0502/cat-DNAV_PL_060201',
    sapPlantProfile: 'FM00',
    isRndBrand: false,
    currency: 'SEK',
    scalePricesGross: '{"1":842.5,"5":791.25}',
    scalePricesNet: '{"1":674.0,"5":633.0}',
    itemMin: 1.0,
    itemStep: 1.0,
    salesUnit: 'piece',
    orderQuantityMinimum: 1.0,
    singleMinPriceNet: 674.0,
    singleMinPriceGross: 842.5,
    standardPriceNet: 55.2,
    percentageDiscount: 4,
    salesStatus: 30,
    imageURL_landscape_medium: '/Web/WebShopImages/landscape_medium/_t/if/19-einsatzgehause-multipac-1.jpg',
    imageURL_landscape_small: '/Web/WebShopImages/landscape_small/_t/if/19-einsatzgehause-multipac-1.jpg',
    manufacturerImageUrl: '/Web/WebShopImages/manufacturer_logo/tm/ap/schroff_logo_bitmap.jpg',
    manufacturerUrl: '/manufacturer/nvent-schroff/man_srf',
    productFamilyCode: '165765',
    imageURL: '/Web/WebShopImages/portrait_small/_t/if/19-einsatzgehause-multipac-1.jpg',
    codeErpRelevant: '15246855',
    visibleInChannels: ['B2B', 'B2C'],
    visibleInShop: true,
    buyable: true,
    productFamilyUrl: '/19-enclosure-multipac/pf/165765',
    productFamily: '19" Enclosure, Multipac',
    distManufacturer: 'nVent Schroff',
    url: '/multipac-2u-he-340-20860-212-nvent-schroff-20860-212/p/15246855',
    title: 'Multipac 2U(HE)340 20860-212',
    code: '15246855-SE',
    score: 1.0,
  } as FusionProduct;

  const emptyCustomProduct = {
    images: [],
    categoryCodePath: '',
    activePromotionLabels: [],
    availableToB2B: false,
    availableToB2C: false,
    buyable: false,
    buyableReplacementProduct: undefined,
    codeErpRelevant: '',
    commonAttrs: [],
    downloads: [],
    elfaArticleNumber: '',
    eligibleForReevoo: false,
    endOfLifeDate: undefined,
    energyEfficiency: '',
    energyEfficiencyLabelImage: undefined,
    energyPower: '',
    exclusionReason: '',
    itemCategoryGroup: '',
    movexArticleNumber: '',
    nameEN: '',
    orderQuantityMinimum: 0,
    orderQuantityStep: 0,
    origPosition: '',
    otherAttributes: [],
    possibleOtherAttributes: {
      entry: [],
    },
    productCode: '',
    salesUnit: '',
    svhcReviewDate: '',
    technicalAttributes: [],
    typeName: '',
  };

  const mockAllSiteSettingsService = {
    getMediaDomain: jasmine.createSpy('getMediaDomain').and.returnValue(of('https://dev.media.distrelec.com')),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [
        DistFusionProductNormalizer,
        FusionProductPriceService,
        ChannelService,
        { provide: AllsitesettingsService, useValue: mockAllSiteSettingsService },
      ],
    });
    service = TestBed.inject(DistFusionProductNormalizer);
  });

  it('should populate product when data is present', () => {
    const result = service.convert(mockProduct, emptyCustomProduct);
    expect(result).toEqual({
      activePromotionLabels: [],
      availableInSnapEda: false,
      availableToB2B: true,
      availableToB2C: true,
      buyable: true,
      buyableReplacementProduct: undefined,
      categoryCodePath: undefined,
      code: '15246855',
      codeErpRelevant: '15246855',
      commonAttrs: [],
      description: 'Product Name: Multipac 2U(HE)340 20860-212',
      distManufacturer: {
        name: 'nVent Schroff',
        image: undefined,
        url: '/manufacturer/nvent-schroff/man_srf',
      },
      downloads: [],
      ean: '',
      elfaArticleNumber: '5246855',
      eligibleForReevoo: false,
      endOfLifeDate: undefined,
      energyEfficiency: 'E',
      energyEfficiencyLabelImage: [
        {
          key: 'portrait_medium',
          value: {
            format: 'portrait_medium',
            url: 'https://dev.media.distrelec.com/Web/WebShopImages/portrait_medium/32/75/EE_Label_963275.jpg',
          },
        },
      ],
      energyPower: '1.3',
      exclusionReason: '',
      images: [
        {
          format: 'landscape_medium',
          url: '/Web/WebShopImages/landscape_medium/_t/if/19-einsatzgehause-multipac-1.jpg',
        },
        {
          format: 'landscape_small',
          url: '/Web/WebShopImages/landscape_small/_t/if/19-einsatzgehause-multipac-1.jpg',
        },
      ],
      itemCategoryGroup: '',
      manufacturer: 'nVent Schroff',
      movexArticleNumber: '321503',
      name: 'Multipac 2U(HE)340 20860-212',
      nameEN: '',
      orderQuantityMinimum: 1,
      orderQuantityStep: 1,
      origPosition: '5',
      otherAttributes: [],
      possibleOtherAttributes: { entry: [] },
      price: {
        currencyIso: 'SEK',
        formattedValue: '674 SEK',
        maxQuantity: 2,
        minQuantity: 1,
        priceType: PriceType.BUY,
        value: 674,
        promoValue: 55.2,
        saving: 4,
      },
      productCode: '',
      productFamilyUrl: '/19-enclosure-multipac/pf/165765',
      salesStatus: '30',
      salesUnit: 'piece',
      svhcReviewDate: '',
      technicalAttributes: [
        { key: 'Width Horizontal Pitch', value: '84' },
        { key: 'Height Units', value: '2' },
        { key: 'Width', value: '403 mm' },
        { key: 'Suitable for', value: 'Non-standardised assemblies' },
        { key: 'Depth', value: '340 mm' },
        { key: 'Subrack Colour', value: 'Grey' },
        { key: 'Subrack Material', value: 'Aluminium' },
      ],
      typeName: '20860-212',
      url: '/multipac-2u-he-340-20860-212-nvent-schroff-20860-212/p/15246855',
      volumePrices: [
        {
          currencyIso: 'SEK',
          formattedValue: '842.50 SEK',
          maxQuantity: 4,
          minQuantity: 1,
          priceType: PriceType.BUY,
          value: 842.5,
        },
        {
          currencyIso: 'SEK',
          formattedValue: '791.25 SEK',
          minQuantity: 5,
          priceType: PriceType.BUY,
          value: 791.25,
        },
      ],
      volumePricesMap: [
        {
          key: 1,
          value: [
            {
              key: VolumePriceType.CUSTOM,
              value: {
                currencyIso: 'SEK',
                formattedValue: '842.50 SEK',
                maxQuantity: 4,
                minQuantity: 1,
                priceType: PriceType.BUY,
                value: 842.5,
              },
            },
            {
              key: VolumePriceType.LIST,
              value: {
                currencyIso: 'SEK',
                formattedValue: '674.00 SEK',
                maxQuantity: 4,
                minQuantity: 1,
                priceType: PriceType.BUY,
                value: 674,
              },
            },
          ],
        },
        {
          key: 5,
          value: [
            {
              key: VolumePriceType.CUSTOM,
              value: {
                currencyIso: 'SEK',
                formattedValue: '791.25 SEK',
                minQuantity: 5,
                priceType: PriceType.BUY,
                value: 791.25,
              },
            },
            {
              key: VolumePriceType.LIST,
              value: {
                currencyIso: 'SEK',
                formattedValue: '633.00 SEK',
                minQuantity: 1,
                priceType: PriceType.BUY,
                value: 633,
              },
            },
          ],
        },
      ],
    });
  });

  it('should return true if the B2B channel is present in the visibleInChannels array', () => {
    const result = service.convert(mockProduct, emptyCustomProduct);

    expect(result.availableToB2B).toEqual(true);
  });

  it('should return true if the B2C channel is present in the visibleInChannels array', () => {
    const result = service.convert(mockProduct, emptyCustomProduct);

    expect(result.availableToB2C).toEqual(true);
  });

  it('should return false if the channel is empty in the visibleInChannels array', () => {
    const mockEmptyChannel = {
      inStock: true,
      productNumber: '15246855',
      eligibleForReevoo: false,
      id: '15246855-SE',
      description: 'Product Name: Multipac 2U(HE)340 20860-212',
      thumbnail:
        'https://dev.media.distrelec.com/Web/WebShopImages/landscape_small/_t/if/19-einsatzgehause-multipac-1.jpg',
      displayFields:
        '[{"code":"diswidthhorizontalpitchnum","attributeName":"Width Horizontal Pitch","value":"84","fieldType":"double"},{"code":"disheightunitsnum","attributeName":"Height Units","value":"2","fieldType":"double"},{"code":"diswidthnum","attributeName":"Width","value":"403","unit":"mm","fieldType":"double"},{"code":"dissuitablefortxt","attributeName":"Suitable for","value":"Non-standardised assemblies","fieldType":"string"},{"code":"disdepthnum","attributeName":"Depth","value":"340","unit":"mm","fieldType":"double"},{"code":"dissubrackcolourtxt","attributeName":"Subrack Colour","value":"Grey","fieldType":"string"},{"code":"dissubrackmaterialtxt","attributeName":"Subrack Material","value":"Aluminium","fieldType":"string"}]',
      energyEfficiency: '{}',
      movexArticleNumber: '321503',
      elfaArticleNumber: '5246855',
      alternativeAliasMPN: '',
      ean: '',
      normalizedAlternativesMPN: '',
      normalizedAlternativeAliasMPN: '',
      sapMPN: '20860212',
      typeNameNormalized: '20860212',
      typeName: '20860-212',
      category1Name: 'Enclosures',
      category3Code: 'cat-DNAV_PL_060201',
      category2Code: 'cat-DNAV_0502',
      category1Code: 'cat-L2D_379531',
      category3: 'cat-DNAV_PL_060201|19" Subracks',
      category2: 'cat-DNAV_0502|19" Subracks & Parts',
      category1: 'cat-L2D_379531|Enclosures',
      categoryCodePath: 'cat-L2D_379531/cat-DNAV_0502/cat-DNAV_PL_060201',
      sapPlantProfile: 'FM00',
      isRndBrand: false,
      currency: 'SEK',
      scalePricesGross: '{"1":842.5,"5":791.25}',
      scalePricesNet: '{"1":674.0,"5":633.0}',
      itemMin: 1.0,
      itemStep: 1.0,
      salesUnit: 'piece',
      orderQuantityMinimum: 1.0,
      singleMinPriceNet: 674.0,
      singleMinPriceGross: 842.5,
      salesStatus: 30,
      imageURL_landscape_medium: '/Web/WebShopImages/landscape_medium/_t/if/19-einsatzgehause-multipac-1.jpg',
      imageURL_landscape_small: '/Web/WebShopImages/landscape_small/_t/if/19-einsatzgehause-multipac-1.jpg',
      manufacturerImageUrl: '/Web/WebShopImages/manufacturer_logo/tm/ap/schroff_logo_bitmap.jpg',
      manufacturerUrl: '/manufacturer/nvent-schroff/man_srf',
      productFamilyCode: '165765',
      imageURL: '/Web/WebShopImages/portrait_small/_t/if/19-einsatzgehause-multipac-1.jpg',
      codeErpRelevant: '15246855',
      visibleInChannels: [],
      visibleInShop: true,
      buyable: true,
      productFamilyUrl: '/19-enclosure-multipac/pf/165765',
      productFamily: '19" Enclosure, Multipac',
      distManufacturer: 'nVent Schroff',
      url: '/multipac-2u-he-340-20860-212-nvent-schroff-20860-212/p/15246855',
      title: 'Multipac 2U(HE)340 20860-212',
      code: '15246855-SE',
      score: 1.0,
    } as FusionProduct;

    const result = service.convert(mockEmptyChannel, emptyCustomProduct);

    expect(result.availableToB2C).toEqual(false);
    expect(result.availableToB2B).toEqual(false);
  });

  it('should populate DistManufacturer if data is present', () => {
    const result = service.convert(mockProduct, emptyCustomProduct);

    expect(result.distManufacturer).toEqual({
      name: 'nVent Schroff',
      image: undefined,
      url: '/manufacturer/nvent-schroff/man_srf',
    });
  });

  it('should populate technical attributes if displayFields are present', () => {
    const result = service.convert(mockProduct, emptyCustomProduct);

    expect(result.technicalAttributes).toEqual([
      { key: 'Width Horizontal Pitch', value: '84' },
      { key: 'Height Units', value: '2' },
      { key: 'Width', value: '403 mm' },
      { key: 'Suitable for', value: 'Non-standardised assemblies' },
      { key: 'Depth', value: '340 mm' },
      { key: 'Subrack Colour', value: 'Grey' },
      { key: 'Subrack Material', value: 'Aluminium' },
    ]);
  });

  it('should populates images if image data is present', () => {
    const result = service.convert(mockProduct, emptyCustomProduct);

    expect(result.images).toEqual([
      {
        format: 'landscape_medium',
        url: '/Web/WebShopImages/landscape_medium/_t/if/19-einsatzgehause-multipac-1.jpg',
      },
      {
        format: 'landscape_small',
        url: '/Web/WebShopImages/landscape_small/_t/if/19-einsatzgehause-multipac-1.jpg',
      },
    ]);
  });

  it('should populate energy efficienty image and rating when energyEfficiency data is present', () => {
    const result = service.convert(mockProduct, emptyCustomProduct);

    expect(result.energyEfficiency).toEqual('E');

    expect(result.energyEfficiencyLabelImage).toEqual([
      {
        key: 'portrait_medium',
        value: {
          format: 'portrait_medium',
          url: 'https://dev.media.distrelec.com/Web/WebShopImages/portrait_medium/32/75/EE_Label_963275.jpg',
        },
      },
    ]);
  });
});
