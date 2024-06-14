/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.support;

import com.namics.distrelec.b2b.core.event.DistSupportEvent;

import java.util.Collection;

public interface DistSupportFacade {

    public void sendSupport(final DistSupportEvent support);

    public Collection<String> getContactByValues();
}
