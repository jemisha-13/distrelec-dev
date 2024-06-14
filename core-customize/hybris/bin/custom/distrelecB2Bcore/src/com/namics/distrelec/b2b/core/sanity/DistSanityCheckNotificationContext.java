/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.sanity;

import de.hybris.platform.cronjob.jalo.CronJobNotificationTemplateContext;

/**
 * This interface defines the context attributes which can be used in a sanity check notification template.
 * 
 * @author pforster, Namics AG
 * @version 3.0.0
 * 
 */
public interface DistSanityCheckNotificationContext extends CronJobNotificationTemplateContext {

    public String getType();

    public String getBaseQuery();

    public String getCheckQuery();

    public String getThreshold();

    public String getErrorMessage();

    public String getLastResultBaseQuery();

    public String getLastResultCheckQuery();

}
