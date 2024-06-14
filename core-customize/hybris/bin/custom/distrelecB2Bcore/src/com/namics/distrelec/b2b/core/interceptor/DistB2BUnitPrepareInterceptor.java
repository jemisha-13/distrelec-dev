/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * {@code DistB2BUnitPrepareInterceptor}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public class DistB2BUnitPrepareInterceptor implements PrepareInterceptor<B2BUnitModel> {

    private UserService userService;
    private CommonI18NService commonI18NService;

    private static final Logger LOG = LoggerFactory.getLogger(DistB2BUnitPrepareInterceptor.class);

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.interceptor.PrepareInterceptor#onPrepare(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onPrepare(final B2BUnitModel unit, InterceptorContext ctx) throws InterceptorException {
        LOG.info("Preparing B2BUnitModel " + unit.getErpCustomerID());
        final Locale enLocale = getCommonI18NService().getLocaleForLanguage(getCommonI18NService().getLanguage("en"));
        if (StringUtils.isBlank(unit.getLocName(enLocale))) {
            unit.setLocName(StringUtils.isNotBlank(unit.getName()) ? unit.getName() : "Missing Company Name", enLocale);
        }


        if (unit.getApprovers() != null && !unit.getApprovers().isEmpty()) {
            removeContactsThatDoNotBelongToB2bApproverGroup(unit);
        }
    }

    private void removeContactsThatDoNotBelongToB2bApproverGroup(B2BUnitModel unit) {
        final UserGroupModel b2bApproverGroup = userService.getUserGroupForUID("b2bapprovergroup");
        final Collection<B2BCustomerModel> unitApprovers = new ConcurrentLinkedQueue<B2BCustomerModel>(unit.getApprovers());
        // Remove contacts that do not belong to the b2bapprovergroup user group.
        unitApprovers.removeIf(approver -> !userService.isMemberOfGroup(approver, b2bApproverGroup));
        unit.setApprovers(new HashSet<B2BCustomerModel>(unitApprovers));
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }
}
