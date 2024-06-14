import { ProductReference as BaseProductReference } from '@spartacus/core/src/model/product.model';
import { Product } from '@spartacus/core';
import { ICustomProduct } from '@model/product.model';

export type ProductAccessoryType = 'optional' | 'compatible' | 'mandatory' | 'alternatives';

export enum ProductReferenceType {
  DIST_ACCESSORY = 'DIST_ACCESSORY',
  DIST_SIMILAR = 'DIST_SIMILAR',
  DIS_ALTERNATIVE_UPGRADE = 'DIS_ALTERNATIVE_UPGRADE',
  DIS_ALTERNATIVE_CALIBRATED = 'DIS_ALTERNATIVE_CALIBRATED',
  DIS_ALTERNATIVE_DE = 'DIS_ALTERNATIVE_DE',
  DIS_ALTERNATIVE_SIMILAR = 'DIS_ALTERNATIVE_SIMILAR',
  DIS_ALTERNATIVE_BETTERVALUE = 'DIS_ALTERNATIVE_BETTERVALUE',
}

export interface ProductReferences {
  accessories: CarouselProducts;
  alternatives: CarouselProducts;
  crosselling: CarouselProducts;
  mandatory: CarouselProducts;
}

export interface CarouselProducts {
  carouselProducts: Product[];
}

export interface ProductReference extends Omit<BaseProductReference, 'target'> {
  target?: ICustomProduct;
}
