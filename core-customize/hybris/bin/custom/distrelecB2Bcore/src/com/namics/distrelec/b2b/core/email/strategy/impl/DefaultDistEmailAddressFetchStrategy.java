package com.namics.distrelec.b2b.core.email.strategy.impl;

import de.hybris.platform.acceleratorservices.email.strategy.impl.DefaultEmailAddressFetchStrategy;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import org.apache.commons.lang3.StringUtils;

public class DefaultDistEmailAddressFetchStrategy extends DefaultEmailAddressFetchStrategy {

    @Override
    public EmailAddressModel fetch(final String emailAddress, final String displayName) {
        String trimmedEmailAddress = StringUtils.trim(emailAddress);
        String trimmedDisplayName = StringUtils.trim(displayName);
        return super.fetch(trimmedEmailAddress, trimmedDisplayName);
    }
}
