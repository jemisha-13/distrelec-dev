/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.mail.internal;

import java.util.Arrays;
import java.util.List;

import com.namics.distrelec.b2b.core.mail.internal.data.MissingOrder;
import com.namics.distrelec.b2b.core.model.jobs.DistMissingOrdersCronjobModel;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * {@code DistMissingOrdersJob}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.10
 */
public class DistMissingOrdersJob extends AbstractDistInternalMailJob<DistMissingOrdersCronjobModel, List<String>, MissingOrder> {

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.mail.internal.AbstractDistInternalMailJob#getQuery()
     */
    @Override
    public FlexibleSearchQuery getQuery() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(getCronjob().getSqlQuery());
        query.addQueryParameter("endDate", getYesterdayDate());
        query.setResultClassList(Arrays.asList(String.class, String.class, String.class));
        return query;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.mail.internal.AbstractDistInternalMailJob#getEmailSubject()
     */
    @Override
    public String getEmailSubject() {
        return super.getEmailSubject() != null ? super.getEmailSubject() : "AutoEmail : Missing orders in SAP placed in Hybris report for Date: {date}";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.mail.internal.AbstractDistInternalMailJob#convert(java.lang.Object)
     */
    @Override
    public MissingOrder convert(final List<String> source) {
        final MissingOrder m = new MissingOrder();
        m.setCode(source.get(0));
        m.setCreated(source.get(1));
        m.setSalesOrg(source.get(2));
        return m;
    }
}
