import { Injectable } from '@angular/core';
import { CountryCodesEnum } from '@context-services/country.service';
import {
  SALES_STATUS_PURCHASING_BLOCKED_IDENTIFIER,
  SALES_STATUS_PURCHASING_INACTIVE_IDENTIFIER,
  SALES_STATUS_PURCHASING_PHASEOUT_IDENTIFIER,
  SALES_STATUS_PURCHASING_SUSPENDED_IDENTIFIER,
} from '@helpers/constants';

export interface SalesStatusConfiguration {
  isDisabled: boolean;
  isEligibleForBackorder: boolean;
  isNotifyMeBackInStock: boolean;
  isStockable?: boolean;
  messagePdP: string;
  messagePdPCh: string;
  outOfStockMessage: string;
  outOfStockHeadlineMessage?: string;
  headlineMessagePDP?: string;
  icon: string;
  waldomDeliveryTimeText: string;
  waldomNextDayDeliveryTimeText: string;
  furtherTextAdditional: string;
  furtherTextWaldom: string;
  moreStockAvailableText: string;
  pickUp: string;
  btoText: string;
  outOfStockMessageBtoDir?: string;
}

@Injectable({
  providedIn: 'root',
})
export class SalesStatusService {
  getSalesStatusConfiguration(salesStatus: string): SalesStatusConfiguration {
    switch (salesStatus) {
      // 20 - New not ordered
      // Used when a new product has been confirmed for the assortment,
      // however no stock has been purchased yet and a date for stocking isn't yet known.
      //technically not out of stock but its treated like thatt
      case '20':
        return {
          messagePdP: 'salesStatus.pdp.status_20',
          messagePdPCh: 'salesStatus.pdp.status_20_CH',
          outOfStockMessage: 'salesStatus.pdp.status_20',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_20',
          isNotifyMeBackInStock: true,
          icon: 'clock',
          isDisabled: true,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 21 - New PO confirmed
      // New product not yet in Warehouse but a date is set for arrival.
      // We can start to take back-orders.
      case '21':
        return {
          isStockable: false,
          outOfStockMessage: 'salesStatus.pdp.status_21',
          messagePdP: 'salesStatus.pdp.status_21',
          messagePdPCh: 'salesStatus.pdp.status_21_CH',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_21',
          isNotifyMeBackInStock: true,
          icon: 'clock',
          isDisabled: false,
          isEligibleForBackorder: true,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 30 - Active
      // Product is active and no phase out is planned currently.
      // New products automatically move to this status from status 21 when stock is received in the Warehouse.
      case '30':
        return {
          messagePdP: 'salesStatus.pdp.status_30',
          messagePdPCh: 'salesStatus.pdp.status_30_CH',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.available_to_back_order',
          isNotifyMeBackInStock: true,
          icon: 'check',
          isDisabled: false,
          isEligibleForBackorder: true,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 31 - Phase out soon
      // We plan to phase out a product at a set date. It will behave as active until this time,
      // then it will automatically move to one of the phase out statuses.
      case '31':
        return {
          messagePdP: 'salesStatus.pdp.status_31',
          messagePdPCh: 'salesStatus.pdp.status_31_CH',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.available_to_back_order',
          isNotifyMeBackInStock: true,
          icon: 'check',
          isDisabled: false,
          isEligibleForBackorder: true,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 40 - No longer stocked/offered
      // The product is in a phase out stage due to a decision Distrelec has made.
      // No more stock will be purchased; it can be bought for as long as current stocks last.
      case '40':
        return {
          messagePdP: 'salesStatus.pdp.status_40',
          messagePdPCh: 'salesStatus.pdp.status_40_CH',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_40',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_not_available',
          outOfStockMessage: 'salesStatus.no_stock.available_to_back_order',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.headline_no_longer_available',
          isNotifyMeBackInStock: false,
          icon: 'check',
          isDisabled: false,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 41 - No longer manufactured
      // The product is in a phase out stage due to the manufacturer no longer making it.
      //  No more stock will be purchased; it can be bought for as long as current stocks last.
      case '41':
        return {
          messagePdP: 'salesStatus.pdp.status_41',
          messagePdPCh: 'salesStatus.pdp.status_41_CH',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_41',
          outOfStockMessage: 'salesStatus.no_stock.headline_no_longer_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.headline_no_longer_available',
          isNotifyMeBackInStock: false,
          icon: 'check',
          isDisabled: false,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 50 - Purchasing suspended MOQ or MOV
      // Applied when there is an issue preventing the Purchasing Team from placing a PO.
      // Orders can be placed with no back-order until stock runs out.
      case '50':
        return {
          messagePdP: 'salesStatus.pdp.status_50',
          messagePdPCh: 'salesStatus.pdp.status_50_CH',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_50',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_not_available',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.not_available',
          isNotifyMeBackInStock: false,
          icon: 'check',
          isDisabled: false,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 51 - EOL at Vendor
      // Applied when the supplier informs us that the product is EOL, may still be available from another supplier.
      case '51':
        return {
          messagePdP: 'salesStatus.pdp.status_51',
          messagePdPCh: 'salesStatus.pdp.status_51_CH',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_51',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_not_available',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.not_available',
          isNotifyMeBackInStock: false,
          icon: 'check',
          isDisabled: false,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 52 - Purchasing suspended temporary unavailable
      // Automatically applied when material is temporarily unavailable from our vendor.
      case '52':
        return {
          messagePdP: 'salesStatus.pdp.status_52',
          messagePdPCh: 'salesStatus.pdp.status_52_CH',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_52',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_not_available',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.not_available',
          isNotifyMeBackInStock: false,
          icon: 'check',
          isDisabled: false,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 53 - Purchasing suspended PCN
      // The product specification is changing so the product is temporarily blocked for replenishment.
      case '53':
        return {
          messagePdP: 'salesStatus.pdp.status_53',
          messagePdPCh: 'salesStatus.pdp.status_53_CH',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_53',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_not_available',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.not_available',
          isNotifyMeBackInStock: false,
          icon: 'check',
          isDisabled: false,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 60 - No longer stocked (commercial)
      // The product is no longer stocked due to a Distrelec commercial decision.
      // Automatically moved to this status, usually from status 40, when stock depleted.
      case '60':
        return {
          messagePdP: 'salesStatus.pdp.status_60',
          messagePdPCh: 'salesStatus.pdp.status_60_CH',
          headlineMessagePDP: 'salesStatus.no_stock.headline_not_stocked',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_no_longer_available',
          outOfStockMessage: 'salesStatus.no_stock.headline_no_longer_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.headline_no_longer_available',
          isNotifyMeBackInStock: false,
          icon: 'times',
          isDisabled: true,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 61 - No longer manufactured
      // The product is no longer stocked due to the manufacturer no longer making it.
      // Automatically moved to this status, usually from status 41, when stock depleted.
      case '61':
        return {
          messagePdP: 'salesStatus.pdp.status_61',
          messagePdPCh: 'salesStatus.pdp.status_61_CH',
          headlineMessagePDP: 'salesStatus.no_stock.headline_not_stocked',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_no_longer_available',
          outOfStockMessage: 'salesStatus.no_stock.headline_no_longer_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.headline_no_longer_available',
          isNotifyMeBackInStock: false,
          icon: 'times',
          isDisabled: true,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 62 - No longer available at our suppliers
      // The product is no longer stocked due to us no longer being able to get it from our suppliers.
      // Automatically moved to this status when material is not available for more than 25 days with our vendors.
      case '62':
        return {
          messagePdP: 'salesStatus.pdp.status_61',
          messagePdPCh: 'salesStatus.pdp.status_62_CH',
          headlineMessagePDP: 'salesStatus.no_stock.headline_not_stocked',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_no_longer_available',
          outOfStockMessage: 'salesStatus.no_stock.headline_no_longer_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.headline_no_longer_available',
          isNotifyMeBackInStock: false,
          icon: 'times',
          isDisabled: true,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 90 - Temporary quality block
      // Product Managers have concerns around quality and have temporarily withdrawn the product while it can be investigated.
      case '90':
        return {
          messagePdP: 'salesStatus.no_stock.not_available',
          messagePdPCh: 'salesStatus.no_stock.not_available',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_90',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.not_available',
          icon: 'times',
          isDisabled: true,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
          isNotifyMeBackInStock: false,
        };

      // 91 - Product recall
      // Manufacturer has issued a product recall.
      case '91':
        return {
          messagePdP: 'salesStatus.pdp.status_91',
          messagePdPCh: 'salesStatus.pdp.status_91',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_91',
          outOfStockHeadlineMessage: 'salesStatus.no_stock.headline_not_available',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.not_available',
          isNotifyMeBackInStock: false,
          icon: 'times',
          isDisabled: true,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      // 99 - Sales Block
      // This is used when we want to block a product in a specific Sales Org, not globally.
      // (Although the above Sales Statuses are already Sales Org specific, the ERP process will work by setting them globally and then sends for all Sales Org to the Web.
      // Status 99 allows the ERP to send a status to the Web for one or a sub-set of the Sales Orgs, not all globally.)
      case '99':
        return {
          messagePdP: 'salesStatus.pdp.status_99',
          messagePdPCh: 'salesStatus.pdp.status_99',
          headlineMessagePDP: 'salesStatus.pdp.headline_status_99',
          outOfStockMessage: 'salesStatus.no_stock.not_available',
          outOfStockMessageBtoDir: 'salesStatus.no_stock.not_available',
          isNotifyMeBackInStock: false,
          icon: '',
          isDisabled: true,
          isEligibleForBackorder: false,
          waldomDeliveryTimeText: 'salesStatus.pdp.waldom_delivery',
          waldomNextDayDeliveryTimeText: 'salesStatus.pdp.waldom_next_day_delivery',
          furtherTextAdditional: 'salesStatus.pdp.furtherTextAdditional',
          furtherTextWaldom: 'salesStatus.pdp.furtherTextWaldom',
          moreStockAvailableText: 'salesStatus.pdp.moreStockAvailableText',
          pickUp: 'salesStatus.pdp.pickUp',
          btoText: 'salesStatus.pdp.btoText',
        };

      default: {
        throw new Error(`Unknown sales status configuration requested: ${salesStatus}`);
      }
    }
  }

  endOfStockSalesStatus(salesStatus: string): boolean {
    return (
      salesStatus?.startsWith(SALES_STATUS_PURCHASING_PHASEOUT_IDENTIFIER) ||
      salesStatus?.startsWith(SALES_STATUS_PURCHASING_SUSPENDED_IDENTIFIER)
    );
  }

  alwaysBlockedSalesStatus(salesStatus: string): boolean {
    return (
      salesStatus?.startsWith(SALES_STATUS_PURCHASING_INACTIVE_IDENTIFIER) ||
      salesStatus?.startsWith(SALES_STATUS_PURCHASING_BLOCKED_IDENTIFIER)
    );
  }
}
