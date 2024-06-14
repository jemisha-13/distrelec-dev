/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter.impl;


import org.apache.logging.log4j.spi.StandardLevel;

import com.namics.distrelec.b2b.core.annotations.LogInOut;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;
import com.namics.distrelec.b2b.core.service.newsletter.DistNewsletterService;
import com.namics.distrelec.b2b.core.service.newsletter.exception.InvalidTokenException;
import com.namics.distrelec.b2b.core.service.newsletter.model.DistNewsletterProfileExtModel;

public class MockNewsletterService implements DistNewsletterService {

    @Override
    @LogInOut(StandardLevel.INFO)
    public boolean subscribeSpecificNewsletter(final DistNewsletterProfileExtModel profile, final String updateToken) throws InvalidTokenException {
        return true;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public boolean unsubscribeSpecificNewsletter(final String email, final String deleteToken) throws InvalidTokenException {
        return true;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public DistNewsletterProfileExtModel getSpecificNewsletterProfile(final String email, final String readToken)
            throws NotFoundException, InvalidTokenException {
        throw new NotFoundException("No user with id [" + email + "] found!");
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public boolean activateSegmentedNewsletter(final DistNewsletterProfileExtModel profile) {
        return true;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public boolean deactivateSegmentedNewsletter(final String email) {
        return true;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public DistNewsletterProfileExtModel getSegmentedNewsletterProfile(final String email) throws NotFoundException {
        throw new NotFoundException("No user with id [" + email + "] found!");
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public boolean updateSegmentedNewsletterProfile(final String currentEmail, final DistNewsletterProfileExtModel newProfile) throws NotFoundException {
        return true;
    }

    @Override
    @LogInOut(StandardLevel.INFO)
    public boolean removeFromNewsletter(final String email) {
        return true;
    }

    @Override
    @LogInOut
    public boolean updateNewsletterStatusForUser(final String email) {
        return true;
    }

}
