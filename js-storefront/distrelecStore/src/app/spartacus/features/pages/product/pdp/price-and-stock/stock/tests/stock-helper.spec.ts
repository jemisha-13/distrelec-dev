import { MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_SCNORM } from '@features/mocks/mock-pdp-availability-SC-NORMAL';
import { processStockData } from '@features/pages/product/pdp/price-and-stock/stock/stock-helper';

describe('Stock Worker testing', () => {
  it('should return correct data for status 30 CH product', (done) => {
    const stockWorkerData = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_SCNORM);

    const stockLevels = MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_SCNORM.res.stockLevels;

    expect(stockWorkerData.stockLevelAvailable).toEqual(stockLevels[1].available);
    expect(stockWorkerData.furtherStockAvailable).toEqual(stockLevels[0].available);

    expect(stockWorkerData.stockLevelTotal).toEqual(MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_SCNORM.res.stockLevelTotal);
    expect(stockWorkerData.stockLevelTotal).toEqual(stockLevels[1].available + stockLevels[0].available);

    done();
  });
});
