/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.Date;

import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.facades.customer.impl.DistCustomerUtilFacade;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;

import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.util.StandardDateRange;

/**
 * Additionally populates the list price of the product.
 *
 * @param <SOURCE> extends ProductModel
 * @param <TARGET> extends ProductData
 * @author daehusir, Distrelec
 */
public class DistProductPricePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends ProductPricePopulator<SOURCE, TARGET> {

    @Autowired
    private DistCustomerUtilFacade distCustomerUtilFacade;

    @Override
    public void populate(final SOURCE productModel, final TARGET productData) {
        final PriceInformation info;
        if (distCustomerUtilFacade.skipPrice()) {
            productData.setPrice(null);
        } else if ((info = getPriceInformation(productModel)) != null) {
            final PriceData priceData = ((DistPriceDataFactory) getPriceDataFactory()).create(PriceDataType.BUY, info);
            productData.setPrice(priceData);
            productData.setListPrice(priceData);

            // DISTRELEC-11256: Populating promotion end date
            if (((StandardDateRange) info.getQualifierValue(PriceRow.DATERANGE)) != null) {
                final StandardDateRange dateRange = (StandardDateRange) info.getQualifierValue(PriceRow.DATERANGE);

                final DistPriceRow erpPriceConditionType = (DistPriceRow) info.getQualifierValue(DistPriceRow.PRICEROW);
                if (null != erpPriceConditionType && null != erpPriceConditionType.getErpPriceConditionType()
                        && erpPriceConditionType.getErpPriceConditionType().getCode().equalsIgnoreCase("ZN00") && null != dateRange
                        && !dateRange.getEnd().before(new Date()) && !dateRange.getEnd().after(DateUtils.addYears(new Date(), 2))) {
                    productData.setPromotionEndDate(dateRange.getEnd());
                }
            }
        }
    }

    protected PriceInformation getPriceInformation(final ProductModel productModel) {
        return getCommercePriceService().getWebPriceForProduct(productModel);
    }

    @Override
    protected DistCommercePriceService getCommercePriceService() {
        return (DistCommercePriceService) super.getCommercePriceService();
    }

    public DistCustomerUtilFacade getDistCustomerUtilFacade() {
        return distCustomerUtilFacade;
    }

    public void setDistCustomerUtilFacade(final DistCustomerUtilFacade distCustomerUtilFacade) {
        this.distCustomerUtilFacade = distCustomerUtilFacade;
    }
}
