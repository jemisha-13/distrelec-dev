/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.converters.response;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.webservice.if11.v3.OpenOrderCalculationResponse;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code OpenOrderCalculationResponsePopulator}
 * 
 * @param <TARGET>
 *
 * @author fbersani, Distrelec
 * @since Distrelec 2.0
 */
public class OpenOrderCalculationResponsePopulator<TARGET extends AbstractOrderModel> implements Populator<OpenOrderCalculationResponse, TARGET> {

    private static final Logger LOG = LogManager.getLogger(OpenOrderCalculationResponsePopulator.class);

    private ModelService modelService;
    private OrderCalculationResponseCommonMapping orderCalculationCommonMapping;

    @Override
    public void populate(final OpenOrderCalculationResponse source, final AbstractOrderModel target) throws ConversionException {
        try {
            // map the order number
            if (StringUtils.isNotBlank(source.getOrderId())) {
                target.setErpOpenOrderCode(source.getOrderId());
            }

            // map the order header
            target.setPaymentCost(source.getPaymentPrice());
            target.setDeliveryCost(source.getShippingPrice());
            target.setNetSubTotal(new Double(source.getSubtotal2()));
            target.setPaymentCost(source.getPaymentPrice());
            target.setDeliveryCost(source.getShippingPrice());
            target.setTotalTax(new Double(source.getTax()));
            target.setTotalPrice(new Double(source.getTotal()));
            target.setSubtotal(Double.valueOf(source.getSubtotal2()));

            // target.setGlobalDiscountValues(null);
            target.setSubtotal(Double.valueOf(source.getSubtotal2()));

            target.setTotalDiscounts(getOrderCalculationCommonMapping().getTotalDiscountsValue(source.getDiscounts()).doubleValue());

            // map the order entries
            getOrderCalculationCommonMapping().fillOrderEntries(source.getNewOrderEntries(), target);
            if (target instanceof de.hybris.platform.core.model.order.CartModel) {
                getOrderCalculationCommonMapping().fillConfirmedOrderEntries(source.getConfirmedOrderEntries(), target);
            }

            modelService.save(target);

            // set calculation flags
            getOrderCalculationCommonMapping().setCalculatedStatus(target);

        } catch (final CalculationException e) {
        	LOG.error("{} Error during SAP order calculation. Order: {}", ErrorLogCode.ORDER_RELATED_ERROR.getCode(), target.getCode(), e);
        	throw new ConversionException(e.getMessage(), e);
        }
    }

    // spring

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public OrderCalculationResponseCommonMapping getOrderCalculationCommonMapping() {
        return orderCalculationCommonMapping;
    }

    public void setOrderCalculationCommonMapping(final OrderCalculationResponseCommonMapping orderCalculationCommonMapping) {
        this.orderCalculationCommonMapping = orderCalculationCommonMapping;
    }

}
