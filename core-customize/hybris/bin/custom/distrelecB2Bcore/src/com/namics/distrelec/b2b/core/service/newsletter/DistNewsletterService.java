/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter;

import com.namics.distrelec.b2b.core.service.exception.NotFoundException;
import com.namics.distrelec.b2b.core.service.newsletter.exception.InvalidTokenException;
import com.namics.distrelec.b2b.core.service.newsletter.model.DistNewsletterProfileExtModel;

/**
 * Interface for newsletter service.
 * 
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 * 
 */
public interface DistNewsletterService {

    /**
     * Subscribes a specific newsletter. To update an existing anonymous profile you need an update token.
     * 
     * @param profile
     *            data to subscribe the newsletter profile
     * @param updateToken
     *            <code>null</code> or update token
     * @return <code>true</code> if successful
     * @throws InvalidTokenException
     *             if token is wrong
     */
    boolean subscribeSpecificNewsletter(DistNewsletterProfileExtModel profile, String updateToken) throws InvalidTokenException;

    /**
     * Unsubscribes a specific newsletter for a specific email address.
     * 
     * @param email
     *            email of the profile
     * @param deleteToken
     *            delete token
     * @return <code>true</code> if successful
     * @throws InvalidTokenException
     *             if token is wrong
     */
    boolean unsubscribeSpecificNewsletter(String email, String deleteToken) throws InvalidTokenException;

    /**
     * Returns the newsletter information for an existing profile. To read an existing anonymous profile you need an update token.
     * 
     * @param email
     *            newsletter profile id
     * @param readToken
     *            update token to read the profile
     * @return current newsletter data
     * @throws NotFoundException
     *             if profile is not found
     * @throws InvalidTokenException
     *             if token is wrong
     */
    DistNewsletterProfileExtModel getSpecificNewsletterProfile(String email, String readToken) throws NotFoundException, InvalidTokenException;

    /**
     * Activates the segmented newsletter for a specific email address. If the user is already registered for a specific newsletter, the
     * account will be migrated.
     * 
     * @param profile
     *            profile data to subscribe the newsletter
     * @return <code>true</code> if successful
     * 
     */
    boolean activateSegmentedNewsletter(DistNewsletterProfileExtModel profile);

    /**
     * Deactivates the segmented newsletter for a specific email address.
     * 
     * @param email
     *            email of the profile
     * @return <code>true</code> if successful
     */
    boolean deactivateSegmentedNewsletter(String email);

    /**
     * Returns the profile data for a specific email address.
     * 
     * @param email
     *            email of the profile
     * @return current newsletter data
     * @throws NotFoundException
     *             if profile is not found
     */
    DistNewsletterProfileExtModel getSegmentedNewsletterProfile(String email) throws NotFoundException;

    /**
     * Updates the profile data for a specific email address. Also to change the email address itself. For this case, you have to set the
     * new email address in the newsletter data object.
     * 
     * @param currentEmail
     *            current email of the profile
     * @param newProfile
     *            the new profile data of the newsletter
     * @return <code>true</code> if successful
     * @throws NotFoundException
     *             if profile is not found
     */
    boolean updateSegmentedNewsletterProfile(String currentEmail, DistNewsletterProfileExtModel newProfile) throws NotFoundException;

    /**
     * Removes the specific email address completely from the newsletter system.
     * 
     * @param email
     *            email of the profile
     * @return <code>true</code> if successful
     */
    boolean removeFromNewsletter(String email);

    /**
     * Get the newsletter status (activation flag) from the newsletter system and store it at the given user.
     * 
     * @param email
     *            email of the profile
     * @return <code>true</code> if successful
     */
    boolean updateNewsletterStatusForUser(String email);

}
