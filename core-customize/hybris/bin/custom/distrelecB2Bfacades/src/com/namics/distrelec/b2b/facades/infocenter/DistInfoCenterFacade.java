/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.infocenter;

import com.namics.distrelec.b2b.core.event.DistAddressChangeEvent;
import com.namics.distrelec.b2b.core.event.DistCatalogOrderEvent;
import com.namics.distrelec.b2b.core.event.DistSeminarRegistrationRequestEvent;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;

public interface DistInfoCenterFacade {

    public void changeAddress(final DistAddressChangeEvent addressChangeEvent);

    public void registerForSeminar(final DistSeminarRegistrationData registration);

    public void registerForSeminar(final String seminarUID, final DistSeminarRegistrationData registration);

    public void registerForSeminar(final DistSeminarRegistrationRequestEvent registrationEvent);

    public void registerForSeminar(final String seminarUID, final DistSeminarRegistrationRequestEvent registrationEvent);

    public void orderCatalog(final DistCatalogOrderEvent catalogOrderEvent);

    public DistSeminarData getSeminarForUID(final String uid);

    public void createSeminar(final DistSeminarData seminarData);
}
