import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FusionProductPriceService } from './fusion-product-price.service';
import { PriceType } from '@spartacus/core';
import { VolumePriceType } from '@model/price.model';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { of } from 'rxjs';
import { mockVolumePrice } from '@features/mocks/mock-price-data';
import { mockFusionProductData } from '@features/mocks/mock-fusion-product-data';

describe('FusionProductPriceService', () => {
  let service: FusionProductPriceService;
  let mockChannelService;

  beforeEach(() => {
    mockChannelService = { getActive: jasmine.createSpy('getActive').and.returnValue(of('B2B')) };

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [FusionProductPriceService, { provide: ChannelService, useValue: mockChannelService }],
    });

    service = TestBed.inject(FusionProductPriceService);
  });

  it('should map single product price correctly for B2B', () => {
    const result = service.mapSinglePrice(mockFusionProductData);

    expect(result).toEqual({
      currencyIso: 'SEK',
      formattedValue: '90.1 SEK',
      maxQuantity: 2,
      minQuantity: 1,
      value: 90.1,
      priceType: PriceType.BUY,
      promoValue: 55.2,
      saving: 4,
    });
  });

  it('should map volume price', () => {
    const result = service.mapVolumePrice('{"1":842.5,"5":791.25}', '{"1":674.0,"5":633.0}', 'SEK');

    expect(result).toEqual([
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
    ]);
  });

  it('should normalize volume mapped prices based on channel', () => {
    const result = service.normalizeVolumePrices(mockVolumePrice);

    expect(result).toEqual([
      {
        currencyIso: 'SEK',
        formattedValue: '674.00 SEK',
        maxQuantity: 4,
        minQuantity: 1,
        priceType: PriceType.BUY,
        value: 674.0,
      },
      {
        currencyIso: 'SEK',
        formattedValue: '633.00 SEK',
        minQuantity: 5,
        priceType: PriceType.BUY,
        value: 633.0,
      },
    ]);
  });
});
