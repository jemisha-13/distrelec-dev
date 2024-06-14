/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.education.service;

import com.namics.distrelec.b2b.core.education.data.DistEducationRegistrationData;

/**
 * {@code DistEducationService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public interface DistEducationService {

    /**
     * Register the education request
     * 
     * @param educationRegistrationData
     */
    public void register(final DistEducationRegistrationData educationRegistrationData);

}
