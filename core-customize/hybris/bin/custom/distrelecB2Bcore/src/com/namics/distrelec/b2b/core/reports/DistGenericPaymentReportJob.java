/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.reports;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.jobs.DistGenericSanityCheckCronJobModel;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

public class DistGenericPaymentReportJob extends AbstractJobPerformable<DistGenericSanityCheckCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistGenericPaymentReportJob.class);

    private static final String QUERY = "SELECT DISTINCT({ct.PK}) FROM {Cart AS ct JOIN PaymentTransaction AS pt ON {ct.PK} = {pt.ORDER}} WHERE {ct.PAYMENTMODE} IN ({{ SELECT {PK} FROM {PaymentMode} WHERE {CODE} IN ('PayPal', '004_CreditCard', 'CreditCard')}}) AND {ct.modifiedtime} > ?startDate";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public PerformResult perform(final DistGenericSanityCheckCronJobModel cronJob) {

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        final Date date = DateUtils.addDays(calendar.getTime(), -2);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY);
        query.addQueryParameter("startDate", date);

        final List<CartModel> result = flexibleSearchService.<CartModel> search(query).getResult();

        if (CollectionUtils.isEmpty(result)) {
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }

        final Map<CartModel, Date> failedPayments = new HashMap<CartModel, Date>();

        for (final CartModel ct : result) {
            final List<PaymentTransactionModel> paymentTransactions = ct.getPaymentTransactions();
            if (CollectionUtils.isEmpty(paymentTransactions)) {
                continue;
            }

            Date trialDate = null;

            for (final PaymentTransactionModel paymentTransactionModel : paymentTransactions) {
                final List<PaymentTransactionEntryModel> entries = paymentTransactionModel.getEntries();
                if (CollectionUtils.isEmpty(entries)) {
                    continue;
                }

                boolean success = false;
                boolean notify = false;
                boolean failure = false;

                for (final PaymentTransactionEntryModel ccEntry : entries) {

                    if (PaymentTransactionType.SUCCESS_RESPONSE.equals(ccEntry.getType())) {
                        success = "OK".equals(ccEntry.getTransactionStatus());
                    }
                    if (PaymentTransactionType.NOTIFY.equals(ccEntry.getType())) {
                        notify = "OK".equals(ccEntry.getTransactionStatus());
                    }
                    if (PaymentTransactionType.FAILED_RESPONSE.equals(ccEntry.getType())) {
                        failure = true;
                        trialDate = ccEntry.getModifiedtime();
                    }
                }

                if (notify && !success && !failure) {
                    failedPayments.put(ct, trialDate);
                }
            }
        }

        if (!failedPayments.isEmpty()) {
            // for (final CartModel ct : failedPayments.keySet()) {
            // TODO
            // }
        } else {
            LOG.info("No failed payment found");
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
}
