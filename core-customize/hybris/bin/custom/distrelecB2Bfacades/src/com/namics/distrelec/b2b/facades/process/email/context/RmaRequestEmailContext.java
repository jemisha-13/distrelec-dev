/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Email;
import com.namics.distrelec.b2b.core.model.process.DistRmaRequestProcessEntryModel;
import com.namics.distrelec.b2b.core.model.process.DistRmaRequestProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Return request email context.
 *
 * @author lstuker, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelc 1.0
 */
public class RmaRequestEmailContext extends CustomerEmailContext {

    private static final String CUSTOMER_NUMBER = "customerNumber";

    private String rmaCode;
    private String orderCode;
    private String orderDate;
    private String siteUID;
    private String createdAt;
    private boolean guest;
    private String returnReason;
    private RmaRequestEmailContextGuestEntry guestEntry;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final List<RmaRequestEmailContextEntry> entries = new ArrayList<>();

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof DistRmaRequestProcessModel) {
            final DistRmaRequestProcessModel rmaRequestProcess = (DistRmaRequestProcessModel) businessProcessModel;
            setSiteUID(getSite(rmaRequestProcess).getUid());
            setOrderCode(rmaRequestProcess.getOrderCode());
            setRmaCode(rmaRequestProcess.getRmaCode());
            setCreatedAt(dateFormat.format(rmaRequestProcess.getCreatedAt()));

            put(EMAIL, getRmaRequestEmail());

            if (rmaRequestProcess.getPurchaseDate() != null) {
                setOrderDate(dateFormat.format(rmaRequestProcess.getPurchaseDate()));
            }

            if (rmaRequestProcess.getGuestEntry() != null) {
                populateGuestUserData(rmaRequestProcess);
            } else {
                populateRegisteredUserData(rmaRequestProcess);
            }

            if (CollectionUtils.isNotEmpty(rmaRequestProcess.getRmaRequestProcessEntries())) {
                for (final DistRmaRequestProcessEntryModel entry : rmaRequestProcess.getRmaRequestProcessEntries()) {
                    this.entries.add(new RmaRequestEmailContextEntry(entry.getAmount(), entry.getReturnReason(), entry.getReturnPackaging(), entry.getComment(),
                            entry.getSerialNumbers(), entry.getProductNumber(), entry.getProductName(), entry.getReplacementNote()));
                }
            }
        }
    }

    private void populateRegisteredUserData(DistRmaRequestProcessModel rmaRequestProcess) {
        setGuest(false);
        put(FROM_EMAIL, getSenderEmail(rmaRequestProcess.getCustomer().getContactEmail()));
        if (rmaRequestProcess.getCustomer() instanceof B2BCustomerModel) {
            B2BCustomerModel b2BCustomer = (B2BCustomerModel) rmaRequestProcess.getCustomer();
            put(CUSTOMER_NUMBER, b2BCustomer.getDefaultB2BUnit().getErpCustomerID());
        }
        put(FROM_DISPLAY_NAME, getSenderDisplayName(rmaRequestProcess.getCustomer().getName()));
    }

    private void populateGuestUserData(DistRmaRequestProcessModel rmaRequestProcess) {
        setGuest(true);
        final RmaRequestEmailContextGuestEntry rmaRequestEmailContextGuestEntry = new RmaRequestEmailContextGuestEntry();
        rmaRequestEmailContextGuestEntry.setCustomerName(rmaRequestProcess.getGuestEntry().getCustomerName());
        rmaRequestEmailContextGuestEntry.setEmailAddress(rmaRequestProcess.getGuestEntry().getEmailAddress());
        rmaRequestEmailContextGuestEntry.setPhoneNumber(rmaRequestProcess.getGuestEntry().getPhoneNumber());

        setGuestEntry(rmaRequestEmailContextGuestEntry);

        if (CollectionUtils.isNotEmpty(rmaRequestProcess.getRmaRequestProcessEntries())) {
            DistRmaRequestProcessEntryModel rmaRequestProcessEntry = rmaRequestProcess.getRmaRequestProcessEntries().iterator().next();
            returnReason = rmaRequestProcessEntry.getReturnReason();
        }

        put(FROM_EMAIL, getSenderEmail(rmaRequestProcess.getGuestEntry().getEmailAddress()));
        put(FROM_DISPLAY_NAME, getSenderDisplayName(rmaRequestProcess.getGuestEntry().getCustomerName()));
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(final String siteUID) {
        this.siteUID = siteUID;
    }

    public List<RmaRequestEmailContextEntry> getEntries() {
        return entries;
    }

    public String getRmaCode() {
        return rmaCode;
    }

    public void setRmaCode(final String rmaCode) {
        this.rmaCode = rmaCode;
    }

    public boolean getGuest() {
        return guest;
    }

    public void setGuest(final boolean guest) {
        this.guest = guest;
    }

    public RmaRequestEmailContextGuestEntry getGuestEntry() {
        return guestEntry;
    }

    public void setGuestEntry(final RmaRequestEmailContextGuestEntry guestEntry) {
        this.guestEntry = guestEntry;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(final String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    private String getRmaRequestEmail() {
        String configEmail = getEmail(Email.RMA_REQUEST_EMAIL_TO_PREFIX, Email.RMA_REQUEST_EMAIL_DEFAULT_TO);
        if (StringUtils.isNotEmpty(configEmail)) {
            return configEmail;
        } else {
            return (String) get(FROM_EMAIL);
        }
    }

    private String getSenderEmail(String defaultValue) {
        String configEmail = getEmail(Email.RMA_REQUEST_EMAIL_FROM_PREFIX, Email.RMA_REQUEST_EMAIL_DEFAULT_FROM);
        if (StringUtils.isNotEmpty(configEmail)) {
            return configEmail;
        } else {
            return defaultValue;
        }
    }

    private String getSenderDisplayName(String defaultValue) {
        String configEmail = getEmail(Email.RMA_REQUEST_EMAIL_DISPLAYNAME_PREFIX, Email.RMA_REQUEST_EMAIL_DEFAULT_DISPLAYNAME);
        if (StringUtils.isNotEmpty(configEmail)) {
            return configEmail;
        } else {
            return defaultValue;
        }
    }
}
