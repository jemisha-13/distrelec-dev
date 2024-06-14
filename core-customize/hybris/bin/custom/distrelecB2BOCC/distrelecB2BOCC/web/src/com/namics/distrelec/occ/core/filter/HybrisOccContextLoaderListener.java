package com.namics.distrelec.occ.core.filter;

import de.hybris.platform.spring.HybrisContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.List;

public class HybrisOccContextLoaderListener extends HybrisContextLoaderListener {

    public static final String POST_PROCESS_CONFIG_LOCATION_PARAM = "postProcessContextConfigLocation";

    private String postProcessContextConfigLocation;

    @Override
    public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
        postProcessContextConfigLocation = servletContext.getInitParameter(POST_PROCESS_CONFIG_LOCATION_PARAM);

        return super.initWebApplicationContext(servletContext);
    }

    @Override
    protected void fillConfigLocations(final String appName, final List<String> locations) {
        // Get the default config
        super.fillConfigLocations(appName, locations);

        if (postProcessContextConfigLocation != null) {
            locations.add(postProcessContextConfigLocation);
        }
    }
}
