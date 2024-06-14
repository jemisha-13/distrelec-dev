package com.namics.distrelec.b2b.backoffice.widgets.systembar;

import com.hybris.cockpitng.widgets.common.systembar.SystemBarController;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;

public class DistSystemBarController extends SystemBarController {

    private String ENVIRONMENT_NAME_CONFIG_KEY = "backoffice.environment.name";

    private Span envName;
    @Override
    public void initialize(Component comp) {
        super.initialize(comp);

        Label envLabel = new Label(getEnvName());
        envLabel.setParent(envName);
    }

    private String getEnvName() {
        ConfigurationService configurationService = Registry.getApplicationContext().getBean(ConfigurationService.class);
        Configuration configuration = configurationService.getConfiguration();

        return configuration.getString(ENVIRONMENT_NAME_CONFIG_KEY, "");
    }

    public void setEnvName(Span envName) {
        this.envName = envName;
    }
}
