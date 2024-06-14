package com.namics.distrelec.b2b.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseMaintenanceAction implements CockpitAction<Object, Object> {

    @Autowired
    private ModelService modelService;

    protected ActionResult<Object> setMaintenanceActive(ActionContext<Object> actionContext, boolean maintenanceActive) {
        List<CMSSiteModel> cmsSites = getDataAsCollection(actionContext);
        for (CMSSiteModel cmsSiteModel : cmsSites) {
            cmsSiteModel.setMaintenanceActive(maintenanceActive);
            getModelService().save(cmsSiteModel);
        }
        return new ActionResult<>("success", cmsSites);
    }

    protected boolean isMaintenanceActive(ActionContext<Object> actionContext) {
        List<CMSSiteModel> cmsSites = getDataAsCollection(actionContext);
        return cmsSites.stream()
                .allMatch(cmsSiteModel -> cmsSiteModel.isMaintenanceActive());
    }

    @Override
    public boolean needsConfirmation(ActionContext<Object> actionContext) {
        return true;
    }

    protected List<CMSSiteModel> getDataAsCollection(ActionContext<Object> ctx) {
        List<CMSSiteModel> ctxObjects = new ArrayList();
        Object data = ctx.getData();
        if (data instanceof Collection) {
            ctxObjects.addAll((Collection)ctx.getData());
        } else if (data instanceof CMSSiteModel){
            ctxObjects.add((CMSSiteModel) ctx.getData());
        }

        return ctxObjects;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
