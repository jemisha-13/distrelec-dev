/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.model.process.SapVoucherEmailProcessModel;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * {@code SapVoucherEventListener}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public class SapVoucherEventListener extends AbstractDistEventListener<SapVoucherEvent, SapVoucherEmailProcessModel> {

    private static final Logger LOG = LogManager.getLogger(SapVoucherEventListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.event.AbstractDistEventListener#createTarget()
     */
    @Override
    public SapVoucherEmailProcessModel createTarget() {
        return (SapVoucherEmailProcessModel) getBusinessProcessService().createProcess("sapVoucherEmailProcess_" + System.currentTimeMillis(),
                "sapVoucherEmailProcess");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#validate(de.hybris.platform.servicelayer.event.events.AbstractEvent)
     */
    @Override
    protected boolean validate(final SapVoucherEvent event) {
        if (event.getTargetOrder() == null) {
            LOG.error("The target order cannot be null!");
            return false;
        }
        if (event.getTargetOrder().getGeneratedVoucher() == null) {
            LOG.error("{} {} No generated SAP voucher linked to order {}", ErrorLogCode.ORDER_RELATED_ERROR, ErrorSource.HYBRIS,
                    event.getTargetOrder().getGeneratedVoucher().getCode());
        	return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#populate(de.hybris.platform.servicelayer.event.events.AbstractEvent,
     * de.hybris.platform.processengine.model.BusinessProcessModel)
     */
    @Override
    public void populate(final SapVoucherEvent event, final SapVoucherEmailProcessModel sapVoucherProcess) {
        // Setting the process language, this is important for anonymous users
        sapVoucherProcess.setCustomer(event.getCustomer());
        sapVoucherProcess.setSite(event.getSite());
        sapVoucherProcess.setStore(event.getBaseStore());

        final LanguageModel language = (event.getTargetOrder() instanceof OrderModel) ? ((OrderModel) event.getTargetOrder()).getLanguage()
                : ((event.getTargetOrder() instanceof CartModel) ? ((CartModel) event.getTargetOrder()).getLanguage() : null);

        sapVoucherProcess.setLanguage(language);
        final DateFormat DF = new SimpleDateFormat(event.getDatePattern());
        sapVoucherProcess.setVoucherCode(event.getTargetOrder().getGeneratedVoucher().getCode());
        sapVoucherProcess.setValidFrom(DF.format(event.getTargetOrder().getGeneratedVoucher().getValidFrom()));
        sapVoucherProcess.setValidUntil(DF.format(event.getTargetOrder().getGeneratedVoucher().getValidUntil()));
        // Setting the value
        final NumberFormat NF = NumberFormat.getCurrencyInstance(new Locale(language.getIsocode()));
        NF.setCurrency(Currency.getInstance(event.getTargetOrder().getCurrency().getIsocode()));
        sapVoucherProcess.setVoucherValue(new StringBuilder(NF.format(event.getTargetOrder().getGeneratedVoucher().getValue())).insert(3, ' ').toString());
    }
}
