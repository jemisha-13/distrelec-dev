/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.education;

import com.namics.distrelec.b2b.core.education.data.DistEducationRegistrationData;

import java.util.List;

/**
 * {@code DistEducationFacade}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public interface DistEducationFacade {

    /**
     * Register for an education event
     * 
     * @param educationRegistrationData
     */
    public void register(final DistEducationRegistrationData educationRegistrationData);

    /**
     * Retrieve the list of all education registrations
     * 
     * @return a list of {@code DistEducationRegistrationData}
     */
    public List<DistEducationRegistrationData> getAll();

}
