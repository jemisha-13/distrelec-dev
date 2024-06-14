import { Price } from '@spartacus/core';

declare module '@spartacus/core' {
  interface Price {
    basePrice?: number;
    discountValue?: number;
    formattedPrice?: string;
    formattedPriceWithVat?: string;
    originalValue?: number;
    pricePerX?: number;
    pricePerXBaseQty?: number;
    pricePerXUOM?: string;
    pricePerXUOMDesc?: string;
    pricePerXUOMQty?: number;
    priceWithVat?: number;
    promoValue?: number;
    saving?: number;
    vatPercentage?: number;
    vatValue?: number;
    maxQuantity?: number;
    minQuantity?: number;
    currencyIso?: string;
    value?: number;
  }
}

export interface Prices {
  price: Price;
  volumePrices: Price[];
  volumePricesMap: VolumePriceMap[];
  promotionEndDate?: Date;
}

export interface VolumePriceMap {
  key: number;
  value: VolumePrice[];
}

export enum VolumePriceType {
  LIST = 'list',
  CUSTOM = 'custom',
}

export type VolumePrice = {
  key: VolumePriceType;
  value: Price;
};
