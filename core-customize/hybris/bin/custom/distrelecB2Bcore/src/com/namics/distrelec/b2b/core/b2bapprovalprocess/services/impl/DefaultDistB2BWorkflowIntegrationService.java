/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.b2bapprovalprocess.services.impl;

import de.hybris.platform.b2b.services.impl.DefaultB2BWorkflowIntegrationService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

/**
 * DefaultDistB2BWorkflowIntegrationService extends DefaultB2BWorkflowIntegrationService
 * 
 * Override the method decideAction because of a wrong session/user handling by hybris
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistB2BWorkflowIntegrationService extends DefaultB2BWorkflowIntegrationService {

    @Override
    protected void decideAction(final WorkflowActionModel workflowActionModel, final WorkflowDecisionModel workflowDecisionModel) {
        getSessionService().executeInLocalView(new SessionExecutionBody() {

            @Override
            public void executeWithoutResult() {
                getWorkflowProcessingService().decideAction(workflowActionModel, workflowDecisionModel);
            }

        }, getUserService().getAdminUser());
    }
}
