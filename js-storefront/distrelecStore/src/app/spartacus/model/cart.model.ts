import { Address, PaymentDetails, Price } from '@spartacus/core';
import { CustomerType } from '@model/site-settings.model';
import { Warehouse } from '@model/order.model';
import { Cart, CartModification, DeliveryMode, OrderEntry } from '@spartacus/cart/base/root';
import { Product } from '@spartacus/core/src/model/product.model';

declare module '@spartacus/cart/base/root' {
  interface Cart {
    cartId?: string;
    billingAddress?: Address;
    codiceCIG?: string;
    codiceCUP?: string;
    completeDelivery?: boolean;
    contactAddress?: Address;
    creditBlocked: boolean;
    customerBlockedInErp?: boolean;
    customerReference?: string;
    eligibleForFastCheckout?: boolean;
    erpVoucherInfoData?: ErpVoucherInfo;
    hasUnallowedBackorder?: boolean;
    moq?: number;
    movCurrency?: string;
    movLimit?: number;
    paymentMode?: PaymentMode;
    pickupLocation?: Warehouse;
    productCodeMisalignment?: string;
    projectNumber?: string;
    punchedOutProducts?: string;
    blockedProducts?: string;
    reevooEligible?: boolean;
    scheduledDeliveryDate?: Date;
    type: string;
    statusCode?: string;
    calculationFailed?: boolean;
    updatedMOQProducts?: string[];
    endOfLifeProducts?: string[];
    phasedOutProducts?: string[];
    waldom?: boolean;
    error?: {
      message: string;
      type: string;
    }[];
  }

  interface CartModification {
    entry?: OrderEntry;
    statusCode?: string;
    quantityAdded?: number;
  }

  interface DeliveryMode {
    defaultDeliveryMode?: boolean;
    shippingCost?: Price;
    selectable?: boolean;
    translation?: string;
    translationKey?: string;
  }
}

declare module '@spartacus/core' {
  interface Principal {
    type: CustomerType;
  }

  interface PaymentDetails {
    isValid?: boolean;
  }
}

export interface DeliveryModes {
  deliveryModes: DeliveryMode[];
  warehouses: Warehouse[];
}

export interface PaymentMode {
  code: string;
  defaultPaymentMode: boolean;
  invoicePayment: boolean;
  paymentInfo: PaymentDetails;
}

export type BulkProducts = {
  itemNumber: string;
  quantity: number;
  reference: string;
  productCode: string;
}[];

export interface ErpVoucherInfo {
  calculatedInERP?: boolean;
  code?: string;
  fixedValue?: Price;
  returnERPCode?: string;
  valid?: boolean;
}

export interface AddressResponse {
  distAddresses: Address[];
}

export interface RequestQuote {
  code: string;
  message: string;
  name: string;
  status: string;
}

export interface AddBulkResponse {
  blockedProducts: Product[];
  cartModifications: CartModification[];
  errorProducts: Product[];
  phaseOutProducts: Product[];
  punchOutProducts: Product[];
}

export interface AddToCartQuotationRequest {
  quotationId: string;
  products?: AddToCartQuotationProduct[];
}

export interface AddToCartQuotationProduct {
  productCode: string;
  quantity: number;
  itemNumber: string;
  reference: string;
}

export interface CartQuotation {
  type: 'quotation';
  id: string;
  entries: OrderEntry[];
}

export interface ShareCartFormData {
  senderName: string;
  senderEmail: string;
  receiverName: string;
  receiverEmail: string;
  message?: string;
}

export enum CartTypeException {
  CART = 'CartError',
  CART_ENTRY = 'CartEntryError',
  CART_DISABLED = 'AddToCartDisabledError',
  CART_MODIFICATION = 'CommerceCartModificationError',
}

export enum CartSuppressedException {
  NOT_FOUND = 'notFound',
  INVALID = 'invalid',
  INVALID_MOV = 'checkout.invalid.mov',
  INVALID_BACKORDER = 'checkout.invalid.backorder',
  CANNOT_RESTORE = 'cannotRestore',
  CANNOT_MERGE = 'cannotMerge',
  CANNOT_RECALCULATE = 'cannotRecalculate',
  CANNOT_RESET_DELIVERY_MODE = 'cannotResetDeliveryMode',
  CANNOT_RESET_ADDRESS = 'cannotResetAddress',
  EXPIRED = 'expired',
  PURCHASE_BLOCK = 'PurchaseBlockedProducts',
  FORBIDDEN = 'checkout.error.invalid.accountType',
  VOUCHER_OPERATION_ERROR = 'VoucherOperationError',
}

export enum CartException {
  SAP_INVALID_CATALOG = 'sap.catalog.order.articles.error',
  PUNCHOUT_PRODUCTS = 'cart.punchout_error',
  ADD_TO_CART_DISABLED = 'add.to.cart.disabled',
}

export interface BulkProductData {
  productCode: string;
  quantity: number;
  itemNumber: string;
  reference: string;
}
