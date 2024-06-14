/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.process.nps;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.model.process.DistNpsProcessModel;

import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.task.RetryLaterException;

/**
 * {@code NPSCheckValuesDecisionAction}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.5
 */
public class NPSCheckValuesDecisionAction extends AbstractSimpleDecisionAction<DistNpsProcessModel> {

    private static final Logger LOG = LogManager.getLogger(NPSCheckValuesDecisionAction.class);
    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.processengine.action.AbstractSimpleDecisionAction#executeAction(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    public Transition executeAction(final DistNpsProcessModel process) throws RetryLaterException, Exception {
        LOG.info("Checking NPS values for the NPS {}", process.getNpsCode());
        try {
            if (StringUtils.isNotBlank(process.getText()) || (process.getValue() >= 0 && process.getValue() <= 6)) {
                return Transition.OK;
            }
        } catch (final Exception exp) {
            LOG.error("Error occur while executing action NPSCheckValuesDecisionAction", exp);
        }
        return Transition.NOK;
    }
}
