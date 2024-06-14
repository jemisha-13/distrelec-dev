import { Address, PaymentDetails, Price, Product } from '@spartacus/core';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { CustomerType } from '@model/site-settings.model';
import { B2BCustomerData } from '@model/misc.model';
import { PaymentModes } from '@model/checkout.model';
import { DeliveryMode, OrderEntry, PaymentType } from '@spartacus/cart/base/root';
import { ErpVoucherInfo } from '@model/cart.model';

declare module '@spartacus/cart/base/root' {
  interface OrderEntry {
    addedFrom?: ItemListEntity;
    alternateAvailable: boolean;
    alternateQuantity: number;
    availabilities: OrderAvailability[];
    backOrderProfitable: boolean;
    backOrderedQuantity: number;
    basePrice?: Price;
    baseListPrice: Price;
    bom: boolean;
    cancellableQuantity?: number;
    configurationInfos: any[];
    customerReference: string;
    deliveryQuantity: number;
    dummyItem: boolean;
    entryNumber?: number;
    isBackOrder: boolean;
    isQuotation: boolean;
    mandatoryItem: boolean;
    moqAdjusted: boolean;
    mview?: string;
    pendingQuantity: number;
    product?: Product;
    quantity?: number;
    quotationId: string;
    quotationReference: string;
    returnableQuantity?: number;
    statusSummaryList: any[];
    stepAdjusted: boolean;
    taxValue: number;
    totalPrice?: Price;
    totalListPrice: Price;
    type: string;
  }

  interface PaymentType {
    creditCardPayment?: boolean;
    hop?: boolean;
    icons?: { url: string }[];
    iframe?: boolean;
    invoicePayment?: boolean;
    name?: string;
    selectable?: boolean;
    translationKey?: string;
    url?: string;
  }
}

declare module '@spartacus/order/root' {
  interface Order {
    b2bCustomerData: B2BCustomerData;
    billingAddress: Address;
    calculated?: boolean;
    cancellable?: boolean;
    canRequestInvoicePaymentMode: boolean;
    code?: string;
    created?: Date;
    customerType: CustomerType;
    guestCustomer?: boolean;
    deliveryAddress?: Address;
    deliveryCost?: Price;
    deliveryMode?: DeliveryMode;
    entries?: OrderEntry[];
    erpVoucherInfoData?: ErpVoucherInfo;
    invoicePaymentModeRequested: boolean;
    guid?: string;
    net?: boolean;
    openOrder: boolean;
    orderDate: Date;
    paymentCost: Price;
    paymentMode: PaymentType;
    paymentInfo?: PaymentDetails;
    returnable?: boolean;
    status?: string;
    salesApplication: string;
    subTotal?: Price;
    totalItems?: number;
    totalPrice?: Price;
    totalPriceWithTax?: Price;
    totalTax?: Price;
    type: 'orderWsDTO';
    pickupLocation?: Warehouse;
    exceededBudgetPrice?: Price;
  }
}

export interface OrderEntryList {
  orderEntries?: OrderEntry[];
}

export interface Warehouse {
  code: string;
  name: string;
  streetName?: string;
  streetNumber?: string;
  postalCode?: string;
  town?: string;
  phone: string;
  openingsHourMoFr?: string;
  openingsHourSa?: string;
}

export enum OrderStatus {
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  REJECTED = 'REJECTED',
  APPROVED = 'APPROVED',
  ERP_RECIEVED = 'ERP_STATUS_RECIEVED',
  ERP_PARTIALLY_SHIPPED = 'ERP_STATUS_PARTIALLY_SHIPPED',
  ERP_SHIPPED = 'ERP_STATUS_SHIPPED',
  ERP_CANCELLED = 'ERP_STATUS_CANCELLED',
  CREATED = 'CREATED',
}

export interface OrderAvailability {
  estimatedDate: Date;
  quantity: number;
}
