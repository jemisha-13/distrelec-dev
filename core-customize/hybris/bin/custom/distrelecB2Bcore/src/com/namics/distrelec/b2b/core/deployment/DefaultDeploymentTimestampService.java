package com.namics.distrelec.b2b.core.deployment;

import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Environment.BUILD_DATE;

import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.deployment.DistDeploymentTimestampModel;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

class DefaultDeploymentTimestampService implements DeploymentTimestampService {

    private final String FIND_TIMESTAMP_QUERY = "SELECT {" + DistDeploymentTimestampModel.PK + "} FROM {" +
                                                DistDeploymentTimestampModel._TYPECODE + "}";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ModelService modelService;

    @Override
    public Date getTimestamp() {
        Configuration config = configurationService.getConfiguration();
        String buildDate = config.getString(BUILD_DATE);

        FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_TIMESTAMP_QUERY);
        DistDeploymentTimestampModel deploymentTimestamp;
        try {
            deploymentTimestamp = flexibleSearchService.searchUnique(query);
        } catch (ModelNotFoundException modelNotFoundException) {
            // create new
            deploymentTimestamp = new DistDeploymentTimestampModel();
        }

        boolean matchesBuild = buildDate.equals(deploymentTimestamp.getBuilddate());
        if (!matchesBuild) {
            // refresh timestamp
            updateDeploymentTimestamp(deploymentTimestamp, buildDate);
        }
        return deploymentTimestamp.getDeploymentTimestamp();
    }

    private void updateDeploymentTimestamp(DistDeploymentTimestampModel deploymentTimestamp, String buildDate) {
        deploymentTimestamp.setBuilddate(buildDate);
        deploymentTimestamp.setDeploymentTimestamp(new Date());
        modelService.save(deploymentTimestamp);
    }
}
