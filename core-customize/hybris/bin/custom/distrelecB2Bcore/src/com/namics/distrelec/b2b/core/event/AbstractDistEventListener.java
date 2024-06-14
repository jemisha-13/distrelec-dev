/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

/**
 * {@code AbstractDistEventListener}
 * <p>
 * An abstract class for business processes event listeners.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public abstract class AbstractDistEventListener<T extends AbstractEvent, E extends BusinessProcessModel> extends AbstractEventListener<T> {

    /**
     * Create the target business process model
     * 
     * @return the target business process model.
     */
    public abstract E createTarget();

    /**
     * Create the target business process model. The default implementation is nothing else than calling the method {@link #createTarget()}.
     * 
     * @param sourceEvent
     *            the origin event.
     * @return the target business process model.
     * @see #createTarget()
     */
    public E createTarget(final T sourceEvent) {
        return createTarget();
    }

    /**
     * Populate the target business process model with data from the source event.
     * 
     * @param event
     *            the source event
     * @param target
     *            the target business process model.
     */
    public void populate(final T event, final E target) {
        // Override this method
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.servicelayer.event.impl.AbstractEventListener#onEvent(de.hybris.platform.servicelayer.event.events.AbstractEvent)
     */
    @Override
    protected void onEvent(final T event) {
        if (!validate(event)) {
            return;
        }

        final E target = createTarget(event);
        // Populating the target business process model from the source event
        populate(event, target);
        // Save and start the process
        getModelServiceViaLookup().save(target);
        getBusinessProcessService().startProcess(target);
    }

    /**
     * Validates that the data in the source event is valid.
     * <p>
     * The default implementation returns always {@code true}
     * </p>
     * 
     * @param event
     *            the event to validate
     * @return {@code true} if the source event is valid, {@code false} otherwise.
     */
    protected boolean validate(final T event) {
        return true;
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    public BaseSiteService getBaseSiteServiceViaLookup(){
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getBaseSiteServiceViaLookup().");
    }
}
