import { ProductAvailability } from '@model/product-availability.model';

export function processStockData(data: { isoCode: string; res: ProductAvailability; salesStatus: string }) {
  let stockLevelTotal = 0;
  let isStockLevelWaldom = false;
  let isWaldomDeliveryTime = false;
  let replenishDeliveryText = '';
  let stockLevelAvailable = 0;
  let isWaldomNextDayDeliveryTime = false;
  let isBto = false;
  let isDir = false;
  let furtherStockWaldom = false;
  let replenishDeliveryTextPickup = '';
  let furtherStockAvailable = 0;
  let furtherStockAdditional = false;
  let oosFurther = false;
  let pickUp = false;
  let stockLevelPickup = 0;
  let furtherStock = false;
  let isMoreStockAvailable = false;
  let moreStockAvailableText = '';
  let stockedCdcOnly = false;

  data?.res?.stockLevels.forEach((stockLevel) => {
    const stockLevelIsWaldom = stockLevel?.waldom && stockLevel?.mview === 'BTR';
    const stockLevelPickupItem =
      data?.res?.stockLevelPickup !== undefined && data?.res?.stockLevelPickup?.length > 0
        ? data?.res?.stockLevelPickup[0]
        : false;
    const isPickupStock = stockLevelPickupItem && stockLevelPickupItem?.warehouseCode === stockLevel?.warehouseId;
    const stockLevels = data?.res?.stockLevels;

    stockLevelTotal = data?.res?.stockLevelTotal;
    isStockLevelWaldom = stockLevelIsWaldom;

    if (stockLevelIsWaldom && stockLevelTotal > 0) {
      isWaldomDeliveryTime = true;
      replenishDeliveryText = stockLevel?.replenishmentDeliveryTime;
      stockLevelAvailable = stockLevel?.available;
    } else {
      if (
        stockLevelTotal > 0 &&
        stockLevel.available > 0 &&
        (isPickupStock || !stockLevelPickupItem) &&
        stockLevel.mview !== 'BTO' &&
        stockLevel.mview !== 'DIR'
      ) {
        if (data.isoCode === 'FR') {
          isWaldomDeliveryTime = true;
        } else if (Number(data.salesStatus) !== 20 && Number(data.salesStatus) !== 21) {
          isWaldomNextDayDeliveryTime = true;
        }
        replenishDeliveryText = stockLevel?.replenishmentDeliveryTime;
        stockLevelAvailable = stockLevel?.available;
      } else if (stockLevel.mview === 'BTO') {
        if (stockLevel?.available) {
          replenishDeliveryText = stockLevel?.replenishmentDeliveryTime2.replace(' ', '');
        }

        isBto = true;
      } else if (stockLevel.mview === 'DIR') {
        isDir = true;
      }
    }

    if (stockLevelPickupItem) {
      data?.res?.stockLevelPickup.forEach((stockPickup) => {
        if (
          stockLevel?.available > 0 &&
          stockPickup.warehouseCode !== stockLevel.warehouseId &&
          !stockLevelIsWaldom &&
          (data.isoCode === 'CH' || data.isoCode === 'LI') &&
          stockLevel.mview !== 'BTO'
        ) {
          if (Number(data.salesStatus) !== 20 && Number(data.salesStatus) !== 21) {
            furtherStockWaldom = true;
            replenishDeliveryTextPickup = stockLevel?.replenishmentDeliveryTime;
            furtherStockAvailable = stockLevel?.available;
          }
          if (stockLevels[1]?.replenishmentDeliveryTime2 !== '0') {
            furtherStockAdditional = true;
            replenishDeliveryTextPickup = stockLevels[1]?.replenishmentDeliveryTime2;
          }
        } else {
          if (
            stockLevels[1].warehouseId === '7374' &&
            stockLevels[1].available === 0 &&
            stockLevels[0].available > 0 &&
            (data.isoCode === 'CH' || data.isoCode === 'LI')
          ) {
            stockedCdcOnly = true;
          }
          if (
            stockPickup.warehouseCode !== stockLevel?.warehouseId &&
            Number(stockLevels[1].replenishmentDeliveryTime2) !== 0 &&
            data.salesStatus !== '50' &&
            data.salesStatus !== '51' &&
            !isBto
          ) {
            furtherStockAdditional = true;
            oosFurther = true;
            stockLevelAvailable = stockLevel?.available;
            replenishDeliveryText = stockLevels[1].replenishmentDeliveryTime2;
            replenishDeliveryTextPickup = stockLevels[1].replenishmentDeliveryTime2;
          }
        }

        if (
          (stockPickup?.stockLevel !== 0 || Number(data.salesStatus) < 40) &&
          stockLevel.mview !== 'BTO' &&
          stockPickup?.stockLevel !== 0
        ) {
          pickUp = true;
          stockLevelPickup = stockPickup?.stockLevel;
        }
      });
    } else {
      if (
        stockLevels.length === 1 &&
        (Number(stockLevels[0].replenishmentDeliveryTime2) > 0 ||
          Number(stockLevels[0].replenishmentDeliveryTime) > 0) &&
        !stockLevels[0].waldom
      ) {
        replenishDeliveryText =
          Number(stockLevels[0].replenishmentDeliveryTime2) > 0
            ? stockLevels[0].replenishmentDeliveryTime2
            : stockLevels[0].replenishmentDeliveryTime;
        furtherStockAdditional = true;
      }

      if (!stockLevelIsWaldom && (data.isoCode === 'CH' || data.isoCode === 'LI') && stockLevels.length > 1) {
        furtherStock = true;
        replenishDeliveryText = stockLevel.replenishmentDeliveryTime.replace(' ', '');
      } else {
        if (
          stockLevel.replenishmentDeliveryTime2 !== undefined &&
          stockLevel.replenishmentDeliveryTime2 !== '0' &&
          stockLevel.mview !== 'BTO' &&
          Number(data.salesStatus) !== 20 &&
          Number(data.salesStatus) !== 21 &&
          data.isoCode !== 'FR'
        ) {
          furtherStockAdditional = true;
          replenishDeliveryText = stockLevel.replenishmentDeliveryTime2;
          isWaldomNextDayDeliveryTime = true;
        }
      }
    }

    if (stockLevel.leadTime !== undefined && Number(stockLevel.leadTime) > 0 && Number(data.salesStatus) < 40) {
      isMoreStockAvailable = true;
      moreStockAvailableText = stockLevel.leadTime;
    }
  });
  return {
    stockLevelTotal,
    isStockLevelWaldom,
    isWaldomDeliveryTime,
    replenishDeliveryText,
    stockLevelAvailable,
    isWaldomNextDayDeliveryTime,
    isBto,
    isDir,
    furtherStockWaldom,
    replenishDeliveryTextPickup,
    furtherStockAvailable,
    furtherStockAdditional,
    oosFurther,
    pickUp,
    stockLevelPickup,
    furtherStock,
    isMoreStockAvailable,
    moreStockAvailableText,
    stockedCdcOnly,
  };
}
