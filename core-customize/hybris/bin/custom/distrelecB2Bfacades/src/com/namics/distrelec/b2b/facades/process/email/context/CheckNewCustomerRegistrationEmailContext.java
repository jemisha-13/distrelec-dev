/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * CheckNewCustomerRegistrationEmailContext.
 *
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 */
public class CheckNewCustomerRegistrationEmailContext extends CustomerEmailContext {

    private String hasAdmin;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);

        String toEmail = getConfigurationService().getConfiguration().getString(
                "offline.customer.registration.check.new.customer.registration." + (getBaseSite() != null ? getBaseSite().getUid() : "default"));
        if (StringUtils.isEmpty(toEmail)) {
            toEmail = getConfigurationService().getConfiguration().getString("offline.customer.registration.check.new.customer.registration.default");
        }

        final String fromEmail = toEmail;

        final B2BCustomerModel customer = (B2BCustomerModel) getCustomer(businessProcessModel);
        final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
        if (b2bUnit != null && b2bUnit.getSalesOrg() != null && b2bUnit.getSalesOrg().isAdminManagingSubUsers()
                && CollectionUtils.isNotEmpty(b2bUnit.getMembers())) {
            for (final Object o : b2bUnit.getMembers()) {
                if (o instanceof B2BCustomerModel) {
                    final B2BCustomerModel b2bCustomer = (B2BCustomerModel) o;
                    if (!b2bCustomer.getUid().equals(customer.getUid()) && isActiveB2BAdminUser(b2bCustomer)) {
                        setHasAdmin(Boolean.TRUE.toString());
                        break;
                    }
                }
            }
        }
        put(FROM_EMAIL, fromEmail);
        put(EMAIL, toEmail);
    }

    private boolean isActiveB2BAdminUser(final B2BCustomerModel b2bCustomer) {
        return getUserService().isMemberOfGroup(b2bCustomer, getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP)) //
                && BooleanUtils.isTrue(b2bCustomer.getActive());
    }

    @Override
    protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
        return getCommonI18NService().getLanguage("en");
    }

    public String getHasAdmin() {
        return hasAdmin;
    }

    public void setHasAdmin(final String hasAdmin) {
        this.hasAdmin = hasAdmin;
    }

}
