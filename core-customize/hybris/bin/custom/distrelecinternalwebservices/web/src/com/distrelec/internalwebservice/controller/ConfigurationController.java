package com.distrelec.internalwebservice.controller;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Cache.SSR_CACHE_CONTAINER_NAME;
import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Environment.AZURE_HOT_FOLDER_BLOB_CONNECTION_STRING_CONFIG;

import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.distrelec.internalwebservice.dto.SsrCacheConfig;
import com.namics.distrelec.b2b.core.deployment.DeploymentTimestampService;

import de.hybris.platform.servicelayer.config.ConfigurationService;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DeploymentTimestampService deploymentTimestampService;

    @GetMapping("/ssrCache")
    public SsrCacheConfig ssrCache() {
        Configuration config = configurationService.getConfiguration();
        String connString = config.getString(AZURE_HOT_FOLDER_BLOB_CONNECTION_STRING_CONFIG);
        String container = config.getString(SSR_CACHE_CONTAINER_NAME);

        Date deploymentTimestamp = deploymentTimestampService.getTimestamp();

        SsrCacheConfig ssrCacheConfig = new SsrCacheConfig();
        ssrCacheConfig.setBlobConnectionString(connString);
        ssrCacheConfig.setBlobContainerName(container);
        ssrCacheConfig.setDeploymentTimestamp(deploymentTimestamp);

        return ssrCacheConfig;
    }
}
