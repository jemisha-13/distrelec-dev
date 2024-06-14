import { Translatable, TranslatableParams } from '@spartacus/core';
import { StockComponent } from '../stock.component';

export function getAvailabilityInStockLabels(fixture) {
  const stockLevelAvailable = fixture.debugElement.nativeElement.querySelector('#pdp_stock_headline');

  const withStockIconCheck = fixture.debugElement.nativeElement.querySelector('#pdp_stock_headline_check_icon');
  const withStockIconTimes = fixture.debugElement.nativeElement.querySelector('#pdp_stock_headline_times_icon');
  const withStockIconClock = fixture.debugElement.nativeElement.querySelector('#pdp_stock_headline_clock_icon');
  const withStockNotifyButton = fixture.debugElement.nativeElement.querySelector('#pdp_stock_notify_btn');
  const withStockShippingAvailable = fixture.debugElement.nativeElement.querySelector('#shippingAvailableInFuture');
  const noStockForBtrDir = fixture.debugElement.nativeElement.querySelector('#outOfStockMessageBtoDir');
  const withStockAvailableCHText = fixture.debugElement.nativeElement.querySelector('#pdp_stock_available_ch_text');
  const withStockAvailableText = fixture.debugElement.nativeElement.querySelector('#pdp_stock_available_text');
  const withStockAvailableToOrder = fixture.debugElement.nativeElement.querySelector('#availableToOrder');
  const withStockAvailableToBackOrder = fixture.debugElement.nativeElement.querySelector('#availableToBackOrder');
  const withStockAvailableToOrderCDC = fixture.debugElement.nativeElement.querySelector('#availableToOrderForCDC');
  const withoutStockButFurtherStock = fixture.debugElement.nativeElement.querySelector('#outOfStockMessage');
  const salesDataBTOText = fixture.debugElement.nativeElement.querySelector('#pdp_stock_btoText');

  const waldomDeliveryTime = fixture.debugElement.nativeElement.querySelector('#pdp_stock_waldomDeliveryTimeText');

  const waldomNextDayDelivery = fixture.debugElement.nativeElement.querySelector(
    '#pdp_stock_waldomNextDayDeliveryTimeText',
  );
  const moreStockAvailable = fixture.debugElement.nativeElement.querySelector('#pdp_stock_moreStockAvailableText');

  const sameDayOffer = fixture.debugElement.nativeElement.querySelector('#lc-dis126-pdp-modal');
  const pickUpLabel = fixture.debugElement.nativeElement.querySelector('#pdp_stock_pickup_label');
  const waldomStockStatusBelow60 = fixture.debugElement.nativeElement.querySelector('#pdp_stock_furtherTextWaldom');
  const additionalStock7371 = fixture.debugElement.nativeElement.querySelector(
    '#pdp_stock_replenishDeliveryTextPickup',
  );
  const additionalStock7374 = fixture.debugElement.nativeElement.querySelector('#pdp_stock_replenishDeliveryText');

  return {
    stockLevelAvailable,
    withStockIconCheck,
    withStockIconTimes,
    withStockIconClock,
    withStockNotifyButton,
    withStockShippingAvailable,
    noStockForBtrDir,
    withStockAvailableToOrder,
    withStockAvailableToBackOrder,
    withStockAvailableToOrderCDC,
    withoutStockButFurtherStock,
    salesDataBTOText,
    waldomDeliveryTime,
    moreStockAvailable,
    withStockAvailableCHText,
    withStockAvailableText,
    waldomNextDayDelivery,
    sameDayOffer,
    pickUpLabel,
    waldomStockStatusBelow60,
    additionalStock7371,
    additionalStock7374,
  };
}

export function getAvailabilityOutOfStockLabels(fixture) {
  const noStockHeadline = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_headline');
  const iconStatus20or21 = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_status_21_20_icon');
  const iconStatus30or31 = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_status_30_31_icon');
  const iconComingSoon = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_coming_soon_icon');
  const noStockStatus20sText = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_status20s_text');

  const noBTOStockHeadline = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_headline_bto_dir');
  const noBTODirStockHeadline = fixture.debugElement.nativeElement.querySelector(
    '#pdp_noStock_outOfStockMessageBtoDir',
  );
  const noStockBTOorDIR21or22 = fixture.debugElement.nativeElement.querySelector(
    '#pdp_noStock_bto_dir_status_21_20_icon',
  );
  const noStockBTOorDIR30or31 = fixture.debugElement.nativeElement.querySelector(
    '#pdp_noStock_bto_dir_status_30_31_icon',
  );
  const noStockStatus30sWaldomText = fixture.debugElement.nativeElement.querySelector(
    '#pdp_noStock_status30s_waldom_text',
  );
  const noStockStatus30sText = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_status30s_text');
  const noStockStatus30sBtoText = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_status30s_bto_text');

  const noStockBTOorDIRComingSoon = fixture.debugElement.nativeElement.querySelector(
    '#pdp_noStock_bto_dir_coming_soon_icon',
  );
  const noStockBTOtext = fixture.debugElement.nativeElement.querySelector('#pdp_noStock_btoText');
  const noStockFurtherAdditionaltext = fixture.debugElement.nativeElement.querySelector(
    '#pdp_noStock_furtherTextAdditional',
  );
  const noStockReplenishDeliveryText = fixture.debugElement.nativeElement.querySelector(
    '#pdp_noStock_replenishDeliveryText',
  );
  const noStockReplenishDeliveryPickupText = fixture.debugElement.nativeElement.querySelector(
    '#pdp_noStock_replenishDeliveryTextPickup',
  );
  const notifyStockButton = fixture.debugElement.nativeElement.querySelector('#pdp_stock_notify_btn');

  return {
    noStockHeadline,
    iconStatus20or21,
    iconStatus30or31,
    iconComingSoon,
    noBTOStockHeadline,
    noStockBTOorDIR21or22,
    noStockBTOorDIR30or31,
    noStockBTOorDIRComingSoon,
    noStockBTOtext,
    noStockFurtherAdditionaltext,
    noStockReplenishDeliveryText,
    noStockReplenishDeliveryPickupText,
    noStockStatus20sText,
    noStockStatus30sWaldomText,
    noStockStatus30sText,
    noStockStatus30sBtoText,
    notifyStockButton,
    noBTODirStockHeadline,
  };
}

export function assignWarehouseData(mockedAvailabilityData, component): StockComponent {
  component.isStockLevelWaldom = mockedAvailabilityData.isStockLevelWaldom;
  component.isWaldomDeliveryTime = mockedAvailabilityData.isWaldomDeliveryTime;
  component.replenishDeliveryText = mockedAvailabilityData.replenishDeliveryText;
  component.stockLevelAvailable = mockedAvailabilityData.stockLevelAvailable;
  component.isWaldomNextDayDeliveryTime = mockedAvailabilityData.isWaldomNextDayDeliveryTime;
  component.isBto = mockedAvailabilityData.isBto;
  component.isDir = mockedAvailabilityData.isDir;
  component.furtherStockWaldom = mockedAvailabilityData.furtherStockWaldom;
  component.replenishDeliveryTextPickup = mockedAvailabilityData.replenishDeliveryTextPickup;
  component.furtherStockAvailable = mockedAvailabilityData.furtherStockAvailable;
  component.furtherStockAdditional = mockedAvailabilityData.furtherStockAdditional;
  component.oosFurther = mockedAvailabilityData.oosFurther;
  component.pickUp = mockedAvailabilityData.pickUp;
  component.stockLevelPickup = mockedAvailabilityData.stockLevelPickup;
  component.furtherStock = mockedAvailabilityData.furtherStock;
  component.moreStockAvailableText = mockedAvailabilityData.moreStockAvailableText;
  component.isMoreStockAvailable = mockedAvailabilityData.isMoreStockAvailable;
  component.stockedCdcOnly = mockedAvailabilityData.stockedCdcOnly;

  return component;
}

export function resetProductAvailability(component: StockComponent): StockComponent {
  component.hasAvailabilityData = false;
  component.isWaldomDeliveryTime = false;
  component.isWaldomNextDayDeliveryTime = false;
  component.furtherStockWaldom = false;
  component.furtherStockAdditional = false;
  component.isStockLevelWaldom = false;
  component.furtherStock = false;
  component.replenishDeliveryText = '';
  component.replenishDeliveryTextPickup = '';
  component.cdcReplenishmentDeliveryTime = '';
  component.isoCode = '';
  component.stockLevelAvailable = 0;
  component.stockLevelTotal = 0;
  component.furtherStockAvailable = 0;
  component.isMoreStockAvailable = false;
  component.moreStockAvailableText = '';
  component.pickUp = false;
  component.stockLevelPickup = 0;
  component.isBto = false;
  component.isDir = false;
  component.oosFurther = false;
  component.stockedCdcOnly = false;

  return component;
}

export function returnSetOfTranslations(input: Translatable | string, options: TranslatableParams = {}) {
  switch (input) {
    case 'salesStatus.no_stock.cdc_only': {
      return `${options.stock} In stock - delivered in 3 business day(s)`;
    }
    case 'salesStatus.pdp.status_30': {
      return `${options.stock} In stock - delivered in 3 business day(s)`;
    }
    case 'salesStatus.pdp.status_30_CH': {
      return `${options.stock} In stock - delivered in 3 business day(s) or pick up in 2hrs`;
    }
    case 'salesStatus.pdp.waldom_next_day_delivery': {
      return 'The exact delivery date will be stated in the order confirmation';
    }
    case 'salesStatus.pdp.pickUp': {
      return `${options.stockLevel} Available for pickup 2h after receipt of order`;
    }
    case 'salesStatus.pdp.lc_next_day.CH': {
      return 'Same Day – Now offering same day delivery across Switzerland';
    }
    case 'salesStatus.pdp.furtherTextWaldom': {
      return `${options.available} Additional stock will be available in ${options.deliveryTime} days`;
    }
    case 'salesStatus.pdp.furtherTextAdditional': {
      return `Additional stock will be available in ${options.deliveryTime} days`;
    }
    case 'salesStatus.no_stock.available_to_order': {
      return 'Available to order';
    }
    case 'salesStatus.no_stock.available_to_back_order': {
      return 'Available to backorder';
    }
    case 'product.notify_me_section.notify_me_cta': {
      return 'Notify me';
    }
    case 'salesStatus.pdp.status_20': {
      return 'Coming soon';
    }
    case 'salesStatus.pdp.status_21': {
      return 'Pre-order now';
    }
    case 'salesStatus.pdp.status_31': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s)`;
    }
    case 'salesStatus.pdp.status_31_CH': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s) or pick up in 2hrs`;
    }
    case 'salesStatus.pdp.status_40': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s)`;
    }
    case 'salesStatus.pdp.status_40_CH': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s) or pick up in 2hrs`;
    }
    case 'salesStatus.pdp.status_41': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s)`;
    }
    case 'salesStatus.pdp.status_41_CH': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s) or pick up in 2hrs`;
    }
    case 'salesStatus.pdp.status_51': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s)`;
    }
    case 'salesStatus.pdp.status_51_CH': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s) or pick up in 2hrs`;
    }
    case 'salesStatus.pdp.status_52': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s)`;
    }
    case 'salesStatus.pdp.status_52_CH': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s) or pick up in 2hrs`;
    }
    case 'salesStatus.pdp.status_53': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s)`;
    }
    case 'salesStatus.pdp.status_53_CH': {
      return `${options.stock} In stock - delivered in ${options.deliveryTime} business day(s) or pick up in 2hrs`;
    }
    case 'salesStatus.pdp.status_60': {
      return `No longer available`;
    }
    case 'salesStatus.no_stock.headline_no_longer_available': {
      return 'No longer available';
    }
    case 'salesStatus.no_stock.not_manufactured': {
      return 'No longer manufactured';
    }
    case 'salesStatus.no_stock.not_available': {
      return 'Currently not available';
    }
    case 'salesStatus.pdp.btoText': {
      if (options.isNordics) {
        return 'Delivery times in accordance with supplier guidelines, subject to change.';
      }
      return 'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.';
    }
    case 'salesStatus.pdp.waldom_delivery': {
      return 'The exact delivery date will be stated in the order confirmation.';
    }
    case 'product.shipping.available.future': {
      return `${options.stock} Stock will be available in ${options.deliveryTime}* day(s)`;
    }
    default: {
      return input;
    }
  }
}
