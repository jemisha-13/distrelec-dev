package com.distrelec.solrfacetsearch.setup;

import java.util.ArrayList;
import java.util.List;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;

public class DistrelecFusionIntegrationSystemSetup extends AbstractSystemSetup {
    public static final String IMPORT_BASE_DATA = "importBaseData";

    public static final String CREATE_INDEX_SETUP = "createIndexSetup";

    @SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
    public void createEssentialData() {
    }

    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
    public void createIndexSetup(SystemSetupContext context) {

        final boolean createIndexSetup = getBooleanSystemSetupParameter(context, CREATE_INDEX_SETUP);

        if (createIndexSetup) {
            importImpexFile(context, "/distrelecfusionintegration/import/createSolrSetup.impex");
        }
    }

    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
    public List<SystemSetupParameter> getInitializationOptions(final SystemSetupContext context) {
        return getInitializationOptions();
    }

    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {

        final List<SystemSetupParameter> params = new ArrayList<>();

        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_DATA,
                                                     "IMPORT BASE DATA like base, countries, erpcodes, delivery modes, payment modes theme usergroups", false));

        return params;
    }
}
