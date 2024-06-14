/* eslint-disable max-len */
/* eslint-disable @typescript-eslint/naming-convention */
import { TestBed } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { IFactFinderResults } from '@model/factfinder.model';
import { DefaultImageService } from '@services/default-image.service';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { DistProductSuggestionFactFinderNormalizer } from './dist-fact-finder-product-suggestion-normalizer';

describe('DistProductSuggestionFactFinderNormalizer', () => {
  let service: DistProductSuggestionFactFinderNormalizer;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [DistProductSuggestionFactFinderNormalizer, ChannelService, DefaultImageService],
    });
    service = TestBed.inject(DistProductSuggestionFactFinderNormalizer);
  });

  it('should normalise the fact finder data IFactFinderResults in to Suggestion model', () => {
    const mockFactFinderData = {
      suggestions: [
        {
          attributes: {
            ManufacturerUrl: '/en/manufacturer/hq/man_hq',
            sourceField: 'Manufacturer',
          },
          hitCount: 0,
          image: '',
          name: 'HQ',
          searchParams: '/FACT-Finder/Search.ff?query=HQ+*&filterManufacturer=HQ&channel=distrelec_7310_ch_en',
          type: 'brand',
        },
        {
          attributes: {
            sourceField: 'Category3',
            'categoryCodePathROOT/cat-L2D_379658': 'cat-L3D_531399',
            'categoryCodePathROOT/cat-L2D_379658/cat-L3D_531399': 'cat-DNAV_PL_110212',
            categoryCodePathROOT: 'cat-L2D_379658',
            CategoryExtensions:
              '[{"url":"/en/lighting/c/cat-L2D_379658","imageUrl":"/Web/WebShopImages/landscape_small/ho/ne/CL_353_iPhone.jpg"},{"url":"/en/lighting/bulbs-tubes/c/cat-L3D_531399","imageUrl":"/Web/WebShopImages/landscape_small/ho/ne/16339_iPhone.jpg"},{"url":"/en/lighting/bulbs-tubes/specialty-bulbs/c/cat-DNAV_PL_110212","imageUrl":"/Web/WebShopImages/landscape_small/98/04/gasurladdningslampa_159804.jpg"}]',
          },
          hitCount: 0,
          image: '',
          name: 'Specialty Bulbs',
          searchParams: '/FACT-Finder/Search.ff?query=*&filterCategory3=Specialty+Bulbs&channel=distrelec_7310_ch_en',
          type: 'category',
        },

        {
          attributes: {
            TypeName: 'DULUX T/E PLUS 26W/827',
            articleNr: '11082780',
            ItemsMin: '1',
            deeplink: '/en/fluorescent-bulb-26-5w-2700k-1800lm-gx24q-132mm-osram-dulux-plus-26w-827/p/11082780',
            'categoryCodePathROOT/cat-L2D_379658/cat-L3D_531399': 'cat-DNAV_PL_110202',
            SalesUnit: '1 piece',
            Title: 'Fluorescent Bulb 26.5W 2700K 1800lm GX24q-3 132mm',
            ProductNumber: '11082780',
            AdditionalImageURLs:
              '{"landscape_medium":"/Web/WebShopImages/landscape_medium/_t/if/leuchtstoffrohren-in-ringform-lumilux-t9c-13.jpg","landscape_small":"/Web/WebShopImages/landscape_small/_t/if/leuchtstoffrohren-in-ringform-lumilux-t9c-13.jpg"}',
            energyEfficiency: '{"Energyclasses_LOV":"G","Leistung_W":"26"}',
            PromotionLabels:
              '[{code:hotOffer,label:"Exclusive",priority:2,rank:2,active:false},{code:noMover,label:"Bundle",priority:1,rank:1,active:false},{code:top,label:"New",priority:4,rank:4,active:false},{code:hit,label:"New Low Price",priority:7,rank:3,active:false},{code:used,label:"used",priority:8,rank:8,active:false},{code:calibrationService,label:"+cal",priority:0,rank:9,active:false},{code:new,label:"New to us",priority:6,rank:7,active:false},{code:bestseller,label:"bestseller",priority:5,rank:6,active:false},{code:offer,label:"Offer",priority:3,rank:5,active:false}]',
            ProductURL: '/en/fluorescent-bulb-26-5w-2700k-1800lm-gx24q-132mm-osram-dulux-plus-26w-827/p/11082780',
            ItemsStep: '1',
            Price:
              '|CHF;Gross;1=7.05435|CHF;Net;1=6.55|CHF;Gross;Min=7.05435|CHF;Net;Min=6.55|CHF;Gross;10=6.5697|CHF;Net;10=6.1|CHF;Gross;25=6.3543|CHF;Net;25=5.9|',
            'categoryCodePathROOT/cat-L2D_379658': 'cat-L3D_531399',
            Manufacturer: 'Osram',
            Buyable: '1',
            id: '11082780',
            campaignProductNr: '11082780',
            categoryCodePathROOT: 'cat-L2D_379658',
          },
          hitCount: 0,
          image: '',
          name: '11082780',
          searchParams: '/FACT-Finder/Search.ff?query=11082780&channel=distrelec_7310_ch_en',
          type: 'productName',
        },
      ],
    } as IFactFinderResults;

    const expectedSuggestionData = {
      products: [
        {
          code: '11082780',
          name: 'Fluorescent Bulb 26.5W 2700K 1800lm GX24q-3 132mm',
          url: '/en/fluorescent-bulb-26-5w-2700k-1800lm-gx24q-132mm-osram-dulux-plus-26w-827/p/11082780',
          image: '/Web/WebShopImages/landscape_small/_t/if/leuchtstoffrohren-in-ringform-lumilux-t9c-13.jpg',
          articleNr: '11082780',
          typeName: 'DULUX T/E PLUS 26W/827',
          itemMin: '1',
          itemStep: '1',
          priceData: {
            price: '6.55',
            currency: 'CHF',
          },
          energyEfficiency: '{"Energyclasses_LOV":"G","Leistung_W":"26"}',
          energyEfficiencyLabelImageUrl: '',
        },
      ],
      termSuggestions: [],
      categorySuggestions: [
        {
          name: 'Specialty Bulbs',
          url: '/en/lighting/bulbs-tubes/specialty-bulbs/c/cat-DNAV_PL_110212',
        },
      ],
      manufacturerSuggestions: [
        {
          name: 'HQ',
          url: '/en/manufacturer/hq/man_hq',
        },
      ],
    };

    const result = service.convert(mockFactFinderData, {});
    expect(result).toEqual(expectedSuggestionData);
  });

  it('should return empty [] for suggestions if types are undefined and return empty products [] if type for product is not productName', () => {
    const mockFactFinderDataNegativeCase = {
      suggestions: [
        {
          attributes: {
            ManufacturerUrl: '/en/manufacturer/hq/man_hq',
            sourceField: 'Manufacturer',
          },
          hitCount: 0,
          image: '',
          name: 'HQ',
          searchParams: '/FACT-Finder/Search.ff?query=HQ+*&filterManufacturer=HQ&channel=distrelec_7310_ch_en',
          type: undefined,
        },
        {
          attributes: {
            sourceField: 'Category3',
            'categoryCodePathROOT/cat-L2D_379658': 'cat-L3D_531399',
            'categoryCodePathROOT/cat-L2D_379658/cat-L3D_531399': 'cat-DNAV_PL_110212',
            categoryCodePathROOT: 'cat-L2D_379658',
            CategoryExtensions:
              '[{"url":"/en/lighting/c/cat-L2D_379658","imageUrl":"/Web/WebShopImages/landscape_small/ho/ne/CL_353_iPhone.jpg"},{"url":"/en/lighting/bulbs-tubes/c/cat-L3D_531399","imageUrl":"/Web/WebShopImages/landscape_small/ho/ne/16339_iPhone.jpg"},{"url":"/en/lighting/bulbs-tubes/specialty-bulbs/c/cat-DNAV_PL_110212","imageUrl":"/Web/WebShopImages/landscape_small/98/04/gasurladdningslampa_159804.jpg"}]',
          },
          hitCount: 0,
          image: '',
          name: 'Specialty Bulbs',
          searchParams: '/FACT-Finder/Search.ff?query=*&filterCategory3=Specialty+Bulbs&channel=distrelec_7310_ch_en',
          type: undefined,
        },

        {
          attributes: {
            TypeName: 'DULUX T/E PLUS 26W/827',
            articleNr: '11082780',
            ItemsMin: '1',
            deeplink: '/en/fluorescent-bulb-26-5w-2700k-1800lm-gx24q-132mm-osram-dulux-plus-26w-827/p/11082780',
            'categoryCodePathROOT/cat-L2D_379658/cat-L3D_531399': 'cat-DNAV_PL_110202',
            SalesUnit: '1 piece',
            Title: 'Fluorescent Bulb 26.5W 2700K 1800lm GX24q-3 132mm',
            ProductNumber: '11082780',
            AdditionalImageURLs:
              '{"landscape_medium":"/Web/WebShopImages/landscape_medium/_t/if/leuchtstoffrohren-in-ringform-lumilux-t9c-13.jpg","landscape_small":"/Web/WebShopImages/landscape_small/_t/if/leuchtstoffrohren-in-ringform-lumilux-t9c-13.jpg"}',
            energyEfficiency: '{"Energyclasses_LOV":"G","Leistung_W":"26"}',
            PromotionLabels:
              '[{code:hotOffer,label:"Exclusive",priority:2,rank:2,active:false},{code:noMover,label:"Bundle",priority:1,rank:1,active:false},{code:top,label:"New",priority:4,rank:4,active:false},{code:hit,label:"New Low Price",priority:7,rank:3,active:false},{code:used,label:"used",priority:8,rank:8,active:false},{code:calibrationService,label:"+cal",priority:0,rank:9,active:false},{code:new,label:"New to us",priority:6,rank:7,active:false},{code:bestseller,label:"bestseller",priority:5,rank:6,active:false},{code:offer,label:"Offer",priority:3,rank:5,active:false}]',
            ProductURL: '/en/fluorescent-bulb-26-5w-2700k-1800lm-gx24q-132mm-osram-dulux-plus-26w-827/p/11082780',
            ItemsStep: '1',
            Price:
              '|CHF;Gross;1=7.05435|CHF;Net;1=6.55|CHF;Gross;Min=7.05435|CHF;Net;Min=6.55|CHF;Gross;10=6.5697|CHF;Net;10=6.1|CHF;Gross;25=6.3543|CHF;Net;25=5.9|',
            'categoryCodePathROOT/cat-L2D_379658': 'cat-L3D_531399',
            Manufacturer: 'Osram',
            Buyable: '1',
            id: '11082780',
            campaignProductNr: '11082780',
            categoryCodePathROOT: 'cat-L2D_379658',
          },
          hitCount: 0,
          image: '',
          name: '11082780',
          searchParams: '/FACT-Finder/Search.ff?query=11082780&channel=distrelec_7310_ch_en',
          type: 'randomTypeToCaptureFilter',
        },
      ],
    } as IFactFinderResults;

    const expectedSuggestionData = {
      products: [],
      termSuggestions: [],
      categorySuggestions: [],
      manufacturerSuggestions: [],
    };

    const result = service.convert(mockFactFinderDataNegativeCase, {});
    expect(result).toEqual(expectedSuggestionData);
  });
});
