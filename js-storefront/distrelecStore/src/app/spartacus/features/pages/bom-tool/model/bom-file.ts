import { ICustomProduct } from '@model/product.model';

export interface BomFileEntry {
  position: number;
  productCode: string;
  quantity: number;
  reference: string;
  salesStatus: string;
  searchTerm: string;
  product?: ICustomProduct;
  selectedAlternative?: ICustomProduct;
  isSelected: boolean;
  isAddedToCart: boolean;
  isValid: boolean;
}

export interface BomFileMpnEntry extends BomFileEntry {
  duplicateMpnProducts: ICustomProduct[];
  mpn: string;
}

export interface BomFile {
  customerId: string;
  fileName: string;
  errorMessages: string[];
  matchingProducts: BomFileEntry[];
  notMatchingProductCodes: {
    position: number;
    productCode: string;
    quantity: number;
    quantityRaw: string;
    reference: string;
    searchTerm: string;
  }[];
  punchedOutProducts: BomFileEntry[];
  quantityAdjustedProducts: BomFileEntry[];
  quantityAdjustedProductsCount: number;
  unavailableProducts: BomFileEntry[];
  duplicateMpnProducts: BomFileMpnEntry[];
}
