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

import com.namics.distrelec.b2b.core.service.newsletter.model.DistNewsletterProfileExtModel;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistConsentStatus;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * Converter implementation for a {@link DistNewsletterProfileExtModel} object.
 *
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 *
 */
public class DistNewsletterProfilExtModelConverter extends AbstractPopulatingConverter<DistNewsletterProfileExtModel, DistConsentData> {

    @Override
    protected DistConsentData createTarget() {
        return new DistConsentData();
    }

    @Override
    public void populate(final DistNewsletterProfileExtModel source, final DistConsentData target) {
        super.populate(source, target);
        target.setUid(source.getEmail());
        target.setTitleCode(source.getTitleCode());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setDivision(source.getDivision());
        target.setRole(source.getRole());
        target.setSegmentedTopic(null != source.getSegmentedTopic() ? source.getSegmentedTopic() : false);
        target.setNpsSubscription(source.getNpsSubscription());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setActiveSubscription(null != source.getActiveSubscription() ? source.getActiveSubscription() : false);
        if (null != source.getConsentStatus()) {
            target.setStatus(DistConsentStatus.valueOf(source.getConsentStatus()));
        }
        target.setErpContactId(source.getErpContactId());
        target.setPhonePermission(source.isPhonePermission());
        target.setSmsPermissions(source.isSmsPermissions());
        target.setPaperPermission(source.isPaperPermission());
        target.setPersonalisationSubscription(source.isPersonalisationSubscription());
        target.setProfilingSubscription(source.isProfilingSubscription());
    }
}
