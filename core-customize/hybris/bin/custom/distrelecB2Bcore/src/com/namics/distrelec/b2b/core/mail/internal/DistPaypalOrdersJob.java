/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.mail.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.namics.distrelec.b2b.core.util.PaymentUtils;
import org.apache.commons.collections.CollectionUtils;

import com.namics.distrelec.b2b.core.constants.GeneratedNamb2bacceleratorCoreConstants.Enumerations.PaymentTransactionType;
import com.namics.distrelec.b2b.core.mail.internal.data.PaypalOrder;
import com.namics.distrelec.b2b.core.model.jobs.DistPaypalOrdersCronjobModel;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * {@code DistPaypalOrdersJob}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.10
 */
public class DistPaypalOrdersJob extends AbstractDistInternalMailJob<DistPaypalOrdersCronjobModel, CartModel, PaypalOrder> {

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.mail.internal.AbstractDistInternalMailJob#processQueryResults(java.util.List)
     */
    @SuppressWarnings("deprecation")
    @Override
    protected List<CartModel> processQueryResults(List<CartModel> results) {
        final List<CartModel> processQueryResults = new ArrayList<CartModel>();
        final Iterator<CartModel> it = results.iterator();

        while (it.hasNext()) {

            final CartModel cart = it.next();
            final List<PaymentTransactionModel> paymentTransactions = cart.getPaymentTransactions();

            if (CollectionUtils.isNotEmpty(paymentTransactions)) {
                final Iterator<PaymentTransactionModel> paymentTransactionModelIterator = paymentTransactions.iterator();

                while (paymentTransactionModelIterator.hasNext()) {
                    final List<PaymentTransactionEntryModel> entries = paymentTransactionModelIterator.next().getEntries();
                    if (CollectionUtils.isNotEmpty(entries)) {

                        final Iterator<PaymentTransactionEntryModel> entriesiterator = entries.iterator();
                        boolean success = false;
                        boolean notify = false;
                        boolean failure = false;

                        while (entriesiterator.hasNext()) {

                            final PaymentTransactionEntryModel ccEntry = entriesiterator.next();
                            if (PaymentTransactionType.SUCCESS_RESPONSE.equals(ccEntry.getType().getCode())) {
                                success = PaymentUtils.isCorrectPaymentTransaction(ccEntry);
                            }
                            if (PaymentTransactionType.NOTIFY.equals(ccEntry.getType().getCode())) {
                                notify = PaymentUtils.isCorrectPaymentTransaction(ccEntry);
                            }
                            if (PaymentTransactionType.FAILED_RESPONSE.equals(ccEntry.getType().getCode())) {
                                failure = true;
                            }

                        }

                        if (notify && !success && !failure) {
                            processQueryResults.add(cart);
                        }
                    }
                }

            }
        }

        Collections.sort(processQueryResults, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 != null && o2 != null) {
                    CartModel c1 = (CartModel) o1;
                    CartModel c2 = (CartModel) o2;
                    return c1.getModifiedtime().compareTo(c2.getModifiedtime());
                }
                return 0;
            }
        });
        return processQueryResults;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.mail.internal.AbstractDistInternalMailJob#getEmailSubject()
     */
    @Override
    public String getEmailSubject() {
        return super.getEmailSubject() != null ? super.getEmailSubject()
                : "AutoEmail : Missing Orders from executed payments (PayPal, CC, iDEAL) report for Date: {date}";
    }

    /**
     * @return the query to be used for fetching the data from the database.
     */
    @Override
    public FlexibleSearchQuery getQuery() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(getCronjob().getSqlQuery());
        query.addQueryParameter("startDate", getYesterdayDate());
        return query;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.mail.internal.AbstractDistInternalMailJob#convert(java.lang.Object)
     */
    @Override
    public PaypalOrder convert(final CartModel source) {
        final PaypalOrder p = new PaypalOrder();
        p.setCode(source.getCode());
        p.setModifiedTime(source.getModifiedtime().toString());
        p.setPaymentType(source.getPaymentMode().getCode());
        p.setShopId(source.getSite().getUid());
        p.setUserId(source.getUser().getUid());
        return p;
    }

}
