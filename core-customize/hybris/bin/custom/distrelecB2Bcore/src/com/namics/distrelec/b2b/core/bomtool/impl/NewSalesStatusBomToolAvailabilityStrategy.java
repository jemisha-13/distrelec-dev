package com.namics.distrelec.b2b.core.bomtool.impl;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NewSalesStatusBomToolAvailabilityStrategy extends AbstractBomToolAvailabilityStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(NewSalesStatusBomToolAvailabilityStrategy.class);

    @Autowired
    private ErpStatusUtil erpStatusUtil;

    @Override
    protected boolean isSalesStatusAvailableForSaleAfterStockIsDepleted(final DistSalesStatusModel salesStatus) {
        if(salesStatus == null ||StringUtils.isEmpty(salesStatus.getCode())){
            return false;
        }
        return !getErpStatusUtil().getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_IS_AVAILABLE_AFTER_STOCK_DEPLETION_IMPORTTOOL).contains(salesStatus.getCode());
    }

    @Override
    protected boolean isSalesStatusAvailableForSale(final DistSalesStatusModel salesStatus) {
        if (salesStatus == null || StringUtils.isEmpty(salesStatus.getCode())) {
                return false;
        }
        return !getErpStatusUtil().getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_IMPORTTOOL).contains(salesStatus.getCode());
    }

    public ErpStatusUtil getErpStatusUtil() {
        return erpStatusUtil;
    }

    public void setErpStatusUtil(final ErpStatusUtil erpStatusUtil) {
        this.erpStatusUtil = erpStatusUtil;
    }
}
