/* eslint-disable @typescript-eslint/naming-convention */
/* eslint-disable max-len */
import { TestBed } from '@angular/core/testing';
import { DistFusionFeedbackCampaigns } from './dist-fusion-feedback-campaigns-normalizer';
import { FusionProductSearch } from '@model/fusion-product-search.model';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { ConverterService } from '@spartacus/core';

describe('DistFusionFeedbackCampaigns', () => {
  let service: DistFusionFeedbackCampaigns;

  let mockConverterService: jasmine.SpyObj<ConverterService>;

  const convertedData = {
    feedbackTexts: {
      entry: [],
    },
    pushedProducts: [
      {
        activePromotionLabels: [],
        availableInSnapEda: false,
        availableToB2B: true,
        availableToB2C: true,
        buyable: true,
        buyableReplacementProduct: 'none',
        categoryCodePath: undefined,
        codeErpRelevant: '13360355',
        commonAttrs: [],
        downloads: [],
        ean: '4250110809703',
        elfaArticleNumber: '3360355',
        eligibleForReevoo: true,
        endOfLifeDate: undefined,
        energyEfficiency: '',
        energyEfficiencyLabelImage: [],
        energyPower: '1.3',
        exclusionReason: 'abcd',
        itemCategoryGroup: '2',
        movexArticleNumber: '252950',
        nameEN: 'THT RGB-LED 5mm B 468nm, Gr 520nm, R 632nm T-1 3/4',
        orderQuantityMinimum: 50,
        orderQuantityStep: 1,
        origPosition: '1',
        otherAttributes: [],
        possibleOtherAttributes: { entry: [] },
        productCode: '30158768',
        productFamilyUrl: '/de/rgb-led-mm/pf/2245311',
        salesUnit: '1 Stück',
        svhcReviewDate: '10/10/2025',
        technicalAttributes: [],
        transportGroupData: {},
        typeName: 'RND 135-00193',
        volumePrices: [],
        volumePricesMap: [],
      },
    ],
  };

  beforeEach(() => {
    mockConverterService = jasmine.createSpyObj('ConverterService', ['convert']);

    TestBed.configureTestingModule({
      providers: [
        DistFusionFeedbackCampaigns,
        CommonTestingModule,
        { provide: ConverterService, useValue: mockConverterService },
      ],
    });
    service = TestBed.inject(DistFusionFeedbackCampaigns);
  });

  it('should map pushedProducts when product data is available', () => {
    const mockFeedbackCampaignData = {
      response: {
        carousel: [
          {
            productNumber: '13360355',
            eligibleForReevoo: true,
            id: '13360355-SE',
            description: 'Product Name: LED Bulb',
            thumbnail:
              'https://dev.media.distrelec.com/Web/WebShopImages/landscape_small/_t/if/summerkombination-auf-lampensockel-ba9s-2.jpg',
            displayFields:
              '[{"code":"dissupplycurrentnum","attributeName":"Supply Current","value":"20","unit":"mA","fieldType":"double"},{"code":"dislightcolourtxt","attributeName":"Light Colour","value":"White","fieldType":"string"},{"code":"disdiameternum","attributeName":"Diameter","value":"15","unit":"mm","fieldType":"double"},{"code":"disluminousintensitynum","attributeName":"Luminous Intensity","value":"9","unit":"cd","fieldType":"double"},{"code":"disbulbbasetxt","attributeName":"Bulb Base","value":"BA15d","fieldType":"string"},{"code":"dislengthnum","attributeName":"Length","value":"35","unit":"mm","fieldType":"double"},{"code":"dissupplyvoltageacnum","attributeName":"Supply Voltage - AC","value":"24","unit":"V","fieldType":"double"},{"code":"dissupplyvoltagedcnum","attributeName":"Supply Voltage - DC","value":"24","unit":"V","fieldType":"double"}]',
            energyEfficiency: '{}',
            movexArticleNumber: '252950',
            elfaArticleNumber: '3360355',
            alternativeAliasMPN: '',
            ean: '4250110809703',
            normalizedAlternativesMPN: '',
            normalizedAlternativeAliasMPN: '',
            sapMPN: '166K 13 2420 WS',
            typeNameNormalized: '166K132420WS',
            typeName: '166K 13 2420 WS',
            category2Code: 'cat-L3D_531399',
            category1Code: 'cat-L2D_379658',
            category2: 'cat-L3D_531399|Bulbs & Tubes',
            category1: 'cat-L2D_379658|Lighting',
            currency: 'SEK',
            scalePricesGross: '{"1":327.5,"10":306.25,"50":291.25}',
            scalePricesNet: '{"1":262.0,"10":245.0,"50":233.0}',
            itemMin: 1.0,
            itemStep: 1.0,
            salesUnit: 'piece',
            orderQuantityMinimum: 1.0,
            singleMinPriceNet: 262.0,
            singleMinPriceGross: 327.5,
            salesStatus: 30,
            imageURL_landscape_medium:
              '/Web/WebShopImages/landscape_medium/_t/if/summerkombination-auf-lampensockel-ba9s-2.jpg',
            imageURL_landscape_small:
              '/Web/WebShopImages/landscape_small/_t/if/summerkombination-auf-lampensockel-ba9s-2.jpg',
            manufacturerUrl: '/manufacturer/taunuslicht/man_tau',
            productFamilyCode: 'DC-86160',
            imageURL: '/Web/WebShopImages/portrait_small/_t/if/summerkombination-auf-lampensockel-ba9s-2.jpg',
            codeErpRelevant: '13360355',
            visibleInChannels: ['B2B', 'B2C'],
            visibleInShop: true,
            buyable: true,
            productFamilyUrl: '/led-indicator-lamps-ba15d/pf/DC-86160',
            productFamily: 'LED Indicator Lamps BA15d',
            distManufacturer: 'Taunuslicht',
            url: '/led-bulb-24v-20ma-ba15d-9cd-white-taunuslicht-166k-13-2420-ws/p/13360355',
            title: 'LED Bulb 24V 20mA BA15d 9cd White',
            code: '13360355-SE',
            score: 5.0426717,
          },
        ],
      },
    } as FusionProductSearch;

    mockConverterService.convert.and.returnValue(convertedData);
    const result = service.convert(mockFeedbackCampaignData, {});

    expect(mockConverterService.convert).toHaveBeenCalled();
    expect(result).toEqual({
      pushedProducts: [
        {
          feedbackTexts: {
            entry: [],
          },
          pushedProducts: [
            {
              activePromotionLabels: [],
              availableInSnapEda: false,
              availableToB2B: true,
              availableToB2C: true,
              buyable: true,
              buyableReplacementProduct: 'none',
              codeErpRelevant: '13360355',
              commonAttrs: [],
              downloads: [],
              categoryCodePath: undefined,
              endOfLifeDate: undefined,
              ean: '4250110809703',
              elfaArticleNumber: '3360355',
              eligibleForReevoo: true,
              energyEfficiency: '',
              energyEfficiencyLabelImage: [],
              energyPower: '1.3',
              exclusionReason: 'abcd',
              itemCategoryGroup: '2',
              movexArticleNumber: '252950',
              nameEN: 'THT RGB-LED 5mm B 468nm, Gr 520nm, R 632nm T-1 3/4',
              orderQuantityMinimum: 50,
              orderQuantityStep: 1,
              origPosition: '1',
              otherAttributes: [],
              possibleOtherAttributes: {
                entry: [],
              },
              productCode: '30158768',
              productFamilyUrl: '/de/rgb-led-mm/pf/2245311',
              salesUnit: '1 Stück',
              svhcReviewDate: '10/10/2025',
              technicalAttributes: [],
              transportGroupData: {},
              typeName: 'RND 135-00193',
              volumePrices: [],
              volumePricesMap: [],
            },
          ],
        },
      ],
      feedbackTexts: {
        entry: [],
      },
    } as any);
  });

  it('should set pushedProducts to an empty array when product data is not available', () => {
    const mockFeedbackCampaignData = {
      response: {
        carousel: [],
      },
    } as FusionProductSearch;

    const result = service.convert(mockFeedbackCampaignData, {});
    expect(result).toEqual({
      pushedProducts: [],
      feedbackTexts: { entry: [] },
    });
  });

  it('should map feedbackTexts when source data is available', () => {
    const mockFeedbackCampaignData = {
      fusion: {
        banner: [
          '{"url":"#","zone":"<div class=\'container\'><a href=\'/cms/single-board-computing?int_cid=1932search.bottom-SBCHUB\'> <img style=\'width:1296px; padding-bottom:20px; margin-left:-12px;\'src=\'https://www.distrelec.ch/medias/sbc-keywordbanner-SV.jpg?context=bWFzdGVyfHJvb3R8OTEwMzh8aW1hZ2UvanBlZ3xoM2EvaDdhLzk0NDAxOTg2ODg3OTguanBnfDY3MzdlZWU5MzM4MTI3NDRlNTZiM2FmYjRmYTM5YjQ4OGY2YmYxOWY5MjEwY2FhY2VkNWVkMmMwNGFjYTk1ZGI\'> </a></div>"}',
        ],
      },
    } as FusionProductSearch;

    const result = service.convert(mockFeedbackCampaignData, {});
    expect(result).toEqual({
      pushedProducts: [],
      feedbackTexts: {
        entry: [
          {
            key: 'SearchResult_top',
            value:
              "<div class='container'><a href='/cms/single-board-computing?int_cid=1932search.bottom-SBCHUB'> <img style='width:1296px; padding-bottom:20px; margin-left:-12px;'src='https://www.distrelec.ch/medias/sbc-keywordbanner-SV.jpg?context=bWFzdGVyfHJvb3R8OTEwMzh8aW1hZ2UvanBlZ3xoM2EvaDdhLzk0NDAxOTg2ODg3OTguanBnfDY3MzdlZWU5MzM4MTI3NDRlNTZiM2FmYjRmYTM5YjQ4OGY2YmYxOWY5MjEwY2FhY2VkNWVkMmMwNGFjYTk1ZGI'> </a></div>",
          },
        ],
      },
    });
  });

  it('should set feedbackTexts to an empty array when source data is not available', () => {
    const mockFeedbackCampaignData = {
      fusion: {},
    } as FusionProductSearch;

    const result = service.convert(mockFeedbackCampaignData, {});
    expect(result).toEqual({
      feedbackTexts: { entry: [] },
      pushedProducts: [],
    });
  });
});
