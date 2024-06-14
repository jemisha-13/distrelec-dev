import { TestBed } from '@angular/core/testing';
import { DistProductSuggestionFusionNormalizer } from './dist-fusion-product-suggestion-normalizer';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { FusionSuggestion } from '@model/fusion-suggestions.model';
describe('DistProductSuggestionFusionNormalizer', () => {
  let service: DistProductSuggestionFusionNormalizer;

  const mockSuggestions = {
    response: {
      categorySuggestions: [
        {
          url: '/semiconductors/discrete-semiconductors/diodes/esd-protection-diodes/c/cat-DNAV_PL_0702',
          name: 'ESD Protection Diodes',
          score: 1,
        },
        {
          url: '/optoelectronics/laser-equipment/laser-diodes/c/cat-DNAV_PL_020703',
          name: 'Laser Diodes',
          score: 1,
        },
      ],
      docs: [
        {
          id: '17002502-SE',
          mpnSearch: 'NONE',
          currency: 'SEK',
          scalePricesNet: '{"1":25.0,"3":23.5,"10":22.1,"25":21.3,"100":19.4,"500":16.6}',
          scalePricesGross: '{"1":31.25,"3":29.375,"10":27.625,"25":26.625,"100":24.25,"500":20.75}',
          singleMinPriceNet: 25,
          singleMinPriceGross: 31.25,
          itemStep: 1,
          itemMin: 1,
          titleShort: 'Rectifier Diode',
          manufacturer: 'Diotec',
          visibleInShop: true,
          productUrl: '/rectifier-diodepressfit200-60-diotec-byp60a2/p/17002502',
          imageURL: '/Web/WebShopImages/landscape_small/2-/75/Press-fit_12-75.jpg',
          title: 'Rectifier DiodePressfit200 V 60 A',
          typeName: 'BYP60A2',
          productNumber: '17002502',
          energyEffiency: '',
        },
      ],
      manufacturerSuggestions: [
        {
          url: '/manufacturer/diodes-incorporated/man_did',
          name: 'Diodes Incorporated',
          score: 1,
        },
      ],
    },
  } as FusionSuggestion;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [DistProductSuggestionFusionNormalizer, ChannelService],
    });
    service = TestBed.inject(DistProductSuggestionFusionNormalizer);
  });

  it('should return an empty array for termSuggestions if termSuggestions from typeahead api is undefined', () => {
    const result = service.convert(mockSuggestions, {});
    expect(result).toEqual({
      categorySuggestions: [
        {
          url: '/semiconductors/discrete-semiconductors/diodes/esd-protection-diodes/c/cat-DNAV_PL_0702',
          name: 'ESD Protection Diodes',
        },
        {
          url: '/optoelectronics/laser-equipment/laser-diodes/c/cat-DNAV_PL_020703',
          name: 'Laser Diodes',
        },
      ],
      products: [
        {
          name: 'Rectifier DiodePressfit200 V 60 A',
          code: '17002502',
          articleNr: '17002502',
          url: '/rectifier-diodepressfit200-60-diotec-byp60a2/p/17002502',
          image: '/Web/WebShopImages/landscape_small/2-/75/Press-fit_12-75.jpg',
          typeName: 'BYP60A2',
          itemMin: 1,
          itemStep: 1,
          priceData: {
            price: '25.00',
            currency: 'SEK',
          },
          energyEfficiency: '',
        },
      ],
      manufacturerSuggestions: [
        {
          url: '/manufacturer/diodes-incorporated/man_did',
          name: 'Diodes Incorporated',
        },
      ],
      termSuggestions: [],
    });
  });

  it('should return an array of categorySuggestions if categorySuggestions is in typeahead api', () => {
    const result = service.convert(mockSuggestions, {});
    expect(result).toEqual({
      categorySuggestions: [
        {
          url: '/semiconductors/discrete-semiconductors/diodes/esd-protection-diodes/c/cat-DNAV_PL_0702',
          name: 'ESD Protection Diodes',
        },
        {
          url: '/optoelectronics/laser-equipment/laser-diodes/c/cat-DNAV_PL_020703',
          name: 'Laser Diodes',
        },
      ],
      products: [
        {
          name: 'Rectifier DiodePressfit200 V 60 A',
          code: '17002502',
          articleNr: '17002502',
          url: '/rectifier-diodepressfit200-60-diotec-byp60a2/p/17002502',
          image: '/Web/WebShopImages/landscape_small/2-/75/Press-fit_12-75.jpg',
          typeName: 'BYP60A2',
          itemMin: 1,
          itemStep: 1,
          priceData: {
            price: '25.00',
            currency: 'SEK',
          },
          energyEfficiency: '',
        },
      ],
      manufacturerSuggestions: [
        {
          url: '/manufacturer/diodes-incorporated/man_did',
          name: 'Diodes Incorporated',
        },
      ],
      termSuggestions: [],
    });
  });

  it('should return an empty array for product if docs is an empty array in search api', () => {
    const mockSuggestionsNoDocs = {
      response: {
        categorySuggestions: [
          {
            url: '/semiconductors/discrete-semiconductors/diodes/esd-protection-diodes/c/cat-DNAV_PL_0702',
            name: 'ESD Protection Diodes',
            score: 1,
          },
          {
            url: '/optoelectronics/laser-equipment/laser-diodes/c/cat-DNAV_PL_020703',
            name: 'Laser Diodes',
            score: 1,
          },
        ],
        docs: [],
        manufacturerSuggestions: [
          {
            url: '/manufacturer/diodes-incorporated/man_did',
            name: 'Diodes Incorporated',
            score: 1,
          },
        ],
      },
    } as FusionSuggestion;

    const result = service.convert(mockSuggestionsNoDocs, {});
    expect(result).toEqual({
      categorySuggestions: [
        {
          url: '/semiconductors/discrete-semiconductors/diodes/esd-protection-diodes/c/cat-DNAV_PL_0702',
          name: 'ESD Protection Diodes',
        },
        {
          url: '/optoelectronics/laser-equipment/laser-diodes/c/cat-DNAV_PL_020703',
          name: 'Laser Diodes',
        },
      ],
      products: [],
      manufacturerSuggestions: [
        {
          url: '/manufacturer/diodes-incorporated/man_did',
          name: 'Diodes Incorporated',
        },
      ],
      termSuggestions: [],
    });
  });

  it('should handle undefined values for price conversion', () => {
    const mockSuggestionsPrice = {
      response: {
        categorySuggestions: [
          {
            url: '/semiconductors/discrete-semiconductors/diodes/esd-protection-diodes/c/cat-DNAV_PL_0702',
            name: 'ESD Protection Diodes',
            score: 1,
          },
          {
            url: '/optoelectronics/laser-equipment/laser-diodes/c/cat-DNAV_PL_020703',
            name: 'Laser Diodes',
            score: 1,
          },
        ],
        docs: [
          {
            id: '17002502-SE',
            mpnSearch: 'NONE',
            scalePricesNet: '{"1":25.0,"3":23.5,"10":22.1,"25":21.3,"100":19.4,"500":16.6}',
            scalePricesGross: '{"1":31.25,"3":29.375,"10":27.625,"25":26.625,"100":24.25,"500":20.75}',
            itemStep: 1,
            itemMin: 1,
            titleShort: 'Rectifier Diode',
            manufacturer: 'Diotec',
            visibleInShop: true,
            productUrl: '/rectifier-diodepressfit200-60-diotec-byp60a2/p/17002502',
            imageURL: '/Web/WebShopImages/landscape_small/2-/75/Press-fit_12-75.jpg',
            title: 'Rectifier DiodePressfit200 V 60 A',
            typeName: 'BYP60A2',
            productNumber: '17002502',
            energyEffiency: '',
          },
        ],
        manufacturerSuggestions: [
          {
            url: '/manufacturer/diodes-incorporated/man_did',
            name: 'Diodes Incorporated',
            score: 1,
          },
        ],
      },
    } as FusionSuggestion;

    const result = service.convert(mockSuggestionsPrice, {});
    expect(result).toEqual({
      categorySuggestions: [
        {
          url: '/semiconductors/discrete-semiconductors/diodes/esd-protection-diodes/c/cat-DNAV_PL_0702',
          name: 'ESD Protection Diodes',
        },
        {
          url: '/optoelectronics/laser-equipment/laser-diodes/c/cat-DNAV_PL_020703',
          name: 'Laser Diodes',
        },
      ],
      products: [
        {
          name: 'Rectifier DiodePressfit200 V 60 A',
          code: '17002502',
          articleNr: '17002502',
          url: '/rectifier-diodepressfit200-60-diotec-byp60a2/p/17002502',
          image: '/Web/WebShopImages/landscape_small/2-/75/Press-fit_12-75.jpg',
          typeName: 'BYP60A2',
          itemMin: 1,
          itemStep: 1,
          priceData: {
            price: '0.00',
            currency: '',
          },
          energyEfficiency: '',
        },
      ],
      manufacturerSuggestions: [
        {
          url: '/manufacturer/diodes-incorporated/man_did',
          name: 'Diodes Incorporated',
        },
      ],
      termSuggestions: [],
    });
  });
});
