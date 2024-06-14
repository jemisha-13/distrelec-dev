/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2011 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.facades.newsletter.converter;

import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.newsletter.model.DistNewsletterProfileExtModel;
import com.namics.distrelec.b2b.facades.newsletter.data.DistNewsletterData;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistConsentStatus;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Converter implementation for a {@link DistNewsletterData} object.
 *
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 *
 */
public class DistNewsletterDataConverter extends AbstractPopulatingConverter<DistConsentData, DistNewsletterProfileExtModel> {

    @Autowired
    private DistrelecCodelistService codelistService;

    @Override
    protected DistNewsletterProfileExtModel createTarget() {
        return new DistNewsletterProfileExtModel();
    }

    @Override
    public void populate(final DistConsentData source, final DistNewsletterProfileExtModel target) {
        super.populate(source, target);
        target.setEmail(source.getUid());
        target.setTitleCode(source.getTitleCode());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setDivision(source.getDivision());
        target.setRole(source.getRole());
        target.setSegmentedTopic(source.isSegmentedTopic());
        target.setActiveSubscription(source.isActiveSubscription());
        target.setNpsSubscription(source.isNpsSubscription());
        target.setAnonymousUser(source.isIsAnonymousUser());
        target.setModifiedTime(source.getModifiedTime());
        target.setErpContactId(source.getErpContactId());
        target.setIsRegistration(source.isIsRegistration());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setMobileNumber(source.getMobileNumber());
        target.setConsentStatus(null != source.getStatus() ? source.getStatus().name() : DistConsentStatus.ERROR_FETCHING_DETAILS.name());
        target.setSendWelcomeEmail(source.isSendNewsletterWelcomeEmail());
        target.setErpContactId(source.getErpContactId());
        target.setPhonePermission(source.isPhonePermission());
        target.setSmsPermissions(source.isSmsPermissions());
        target.setPaperPermission(source.isPaperPermission());
        target.setPersonalisationSubscription(source.isPersonalisationSubscription());
        target.setProfilingSubscription(source.isProfilingSubscription());
    }

    // BEGIN GENERATED CODE

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(final DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    // END GENERATED CODE
}
