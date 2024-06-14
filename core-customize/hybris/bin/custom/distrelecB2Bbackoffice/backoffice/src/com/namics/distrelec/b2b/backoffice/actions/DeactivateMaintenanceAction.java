package com.namics.distrelec.b2b.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;

public class DeactivateMaintenanceAction extends BaseMaintenanceAction {

    @Override
    public ActionResult<Object> perform(ActionContext<Object> actionContext) {
        return setMaintenanceActive(actionContext, false);
    }

    @Override
    public boolean canPerform(ActionContext<Object> actionContext) {
        return isMaintenanceActive(actionContext);
    }

    @Override
    public String getConfirmationMessage(ActionContext<Object> ctx) {
        return ctx.getLabel("hmc.action.deactivatemaintenance.confirm");
    }
}
