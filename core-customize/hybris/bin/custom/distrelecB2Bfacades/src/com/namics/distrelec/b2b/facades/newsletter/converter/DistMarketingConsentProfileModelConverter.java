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

import com.namics.distrelec.b2b.core.service.newsletter.model.DistMarketingConsentProfileModel;
import com.namics.distrelec.b2b.core.service.newsletter.model.DistNewsletterProfileExtModel;
import com.namics.distrelec.b2b.facades.user.data.DistConsentStatus;
import com.namics.distrelec.b2b.facades.user.data.DistMarketingConsentData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * Converter implementation for a {@link DistNewsletterProfileExtModel} object.
 *
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 *
 */
public class DistMarketingConsentProfileModelConverter extends AbstractPopulatingConverter<DistMarketingConsentProfileModel, DistMarketingConsentData> {

    @Override
    protected DistMarketingConsentData createTarget() {
        return new DistMarketingConsentData();
    }

    @Override
    public void populate(final DistMarketingConsentProfileModel source, final DistMarketingConsentData target) {
        super.populate(source, target);
        target.setUid(source.getEmail());
        target.setTitleCode(source.getTitleCode());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setDivision(source.getDivision());
        target.setRole(source.getRole());
        target.setSegmentedTopic(null != source.getSegmentedTopic() ? source.getSegmentedTopic() : false);
        target.setNpsSubscription(source.getNpsSubscription());
        target.setActiveSubscription(null != source.getActiveSubscription() ? source.getActiveSubscription() : false);
        if (null != source.getConsentStatus()) {
            target.setStatus(DistConsentStatus.valueOf(source.getConsentStatus()));
        }
        target.setErpContactId(source.getErpContactId());
        target.setEmailConsent(source.isEmailConsent());
        target.setSmsConsent(source.isSmsConsent());
        target.setPhoneConsent(source.isPhoneConsent());
        target.setPostConsent(source.isPostConsent());
        target.setProfilingConsent(source.isProfilingConsent());
        target.setKnowHowConsent(source.isKnowHowConsent());
        target.setPersonalisationConsent(source.isPersonalisationConsent());
        target.setSaleAndClearanceConsent(source.isSaleAndClearanceConsent());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setNewsLetterConsent(source.isNewsLetterConsent());
        target.setCustomerSurveysConsent(source.isCustomerSurveysConsent());
        target.setPersonalisedRecommendationConsent(source.isPersonalisedRecommendationConsent());
    }
}
