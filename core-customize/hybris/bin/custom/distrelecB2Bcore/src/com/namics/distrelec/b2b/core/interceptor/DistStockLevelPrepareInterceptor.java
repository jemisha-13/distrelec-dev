package com.namics.distrelec.b2b.core.interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.internal.model.impl.ModelValueHistory;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;

public class DistStockLevelPrepareInterceptor implements PrepareInterceptor<StockLevelModel> {

    private static final Logger LOG = LogManager.getLogger(DistStockLevelPrepareInterceptor.class);

    @Override
    public void onPrepare(StockLevelModel stockLevel, InterceptorContext interceptorContext) throws InterceptorException {

        if (interceptorContext.isNew(stockLevel)) {
            LOG.debug("First time stock is imported, InOutStockChange detected, for stockLevel[{},{}], flag is set", () -> stockLevel.getProductCode(),
                      () -> stockLevel.getWarehouse().getCode());
            stockLevel.setInOutStockChange(true);
            return;
        }

        if (!interceptorContext.getDirtyAttributes(stockLevel).containsKey(StockLevelModel.AVAILABLE)) {
            return;
        }

        final ItemModelContext itemModelContext = ModelContextUtils.getItemModelContext(stockLevel);

        if (itemModelContext != null) {
            ModelValueHistory valueHistory = ((ItemModelContextImpl) itemModelContext).getValueHistory();
            int oldValue = (int) valueHistory.getOriginalValue(StockLevelModel.AVAILABLE);

            int newValue = stockLevel.getAvailable();

            // if the product goes from in-stock to out-of-stock or vice versa, we want to set the flag
            if (oldValue > 0 && newValue <= 0 || oldValue <= 0 && newValue > 0) {
                LOG.debug("InOutStockChange detected, for stockLevel[{},{}], flag is set", () -> stockLevel.getProductCode(),
                          () -> stockLevel.getWarehouse().getCode());
                stockLevel.setInOutStockChange(true);
            } else {
                LOG.debug("No InOutStockChange detected, for stockLevel[{},{}], flag is reset ", () -> stockLevel.getProductCode(),
                          () -> stockLevel.getWarehouse().getCode());
                // if not, we want to remove the flag
                stockLevel.setInOutStockChange(false);
            }
        }
    }
}
