/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.newsletter;

import java.util.List;

import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;

/**
 * Interface for newsletter facade.
 *
 * @author pnueesch, Namics AG, Abhinay Jadhav
 * @since Distrelec 1.0, v8.7
 *
 */
public interface DistNewsletterFacade {

    /**
     * Returns all available newsletter roles.
     *
     * @return list of newsletter roles
     */
    List<DistFunctionData> getRoles();

    /**
     * Returns all available newsletter divisions.
     *
     * @return list of newsletter divisions
     */
    List<DistDepartmentData> getDivisions();

    void optInForAllObsolescenceEmailsForCurrentUser();

    void optInForAllObsolescenceEmailsForExistingUser(final String uid);

    boolean handleBloomreachNewsletterSubscription(final DistConsentData consentData);
}
