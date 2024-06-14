package com.namics.distrelec.b2b.core.bloomreach.client.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.namics.distrelec.b2b.core.bloomreach.client.DistBloomreachAPIClient;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachExportException;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DistBloomreachAPIClientImpl implements DistBloomreachAPIClient {

    private static final Logger LOG = LogManager.getLogger(DistBloomreachAPIClientImpl.class);

    private static final String BATCH_API_URL = "batch";

    private static final String CUSTOMER_ATTRIBUTES_URL = "customers/attributes";

    private static final String API_AUTHORIZATION_HEADER = "Authorization";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void callBatchRequest(String requestString) throws DistBloomreachBatchException {

        HttpClient client = HttpClient.newBuilder().build();

        String originalInput = getOriginalInput();

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(configurationService.getConfiguration().getString("bloomreach.api.url") + "/"
                                                         + configurationService.getConfiguration().getString("bloomreach.api.track.url") + "/" +
                                                         configurationService.getConfiguration().getString("bloomreach.api.project.token") + "/"
                                                         + BATCH_API_URL + ""))
                                         .POST(HttpRequest.BodyPublishers.ofString(requestString))
                                         .header(API_AUTHORIZATION_HEADER, "Basic " + Base64.getEncoder().encodeToString(originalInput.getBytes()))
                                         .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logResponse(response);
            if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                throw new DistBloomreachBatchException("Batch request could not be sent.");
            }
        } catch (IOException e) {
            LOG.error("Error occurred during sending or receiving.", e);
            throw new DistBloomreachBatchException("Error occurred during sending or receiving.", e);
        } catch (InterruptedException e) {
            LOG.error("Sending was interrupted.", e);
            throw new DistBloomreachBatchException("Sending was interrupted.", e);
        }

    }

    @Override
    public String exportCustomerConsents(String requestString) throws DistBloomreachExportException {
        HttpClient client = HttpClient.newBuilder().build();

        String originalInput = getOriginalInput();

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(configurationService.getConfiguration().getString("bloomreach.api.url") + "/"
                                                         + configurationService.getConfiguration().getString("bloomreach.api.data.url") + "/" +
                                                         configurationService.getConfiguration().getString("bloomreach.api.project.token") + "/"
                                                         + CUSTOMER_ATTRIBUTES_URL + ""))
                                         .POST(HttpRequest.BodyPublishers.ofString(requestString))
                                         .header(API_AUTHORIZATION_HEADER, "Basic " + Base64.getEncoder().encodeToString(originalInput.getBytes()))
                                         .header(CONTENT_TYPE_HEADER, "application/json")
                                         .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logResponse(response);
            if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                return response.body();
            } else {
                throw new DistBloomreachExportException("Customer consents could not be exported.");
            }
        } catch (IOException e) {
            LOG.error("Error occurred during sending or receiving.", e);
            throw new DistBloomreachExportException("Error occurred during sending or receiving.", e);
        } catch (InterruptedException e) {
            LOG.error("Sending was interrupted.", e);
            throw new DistBloomreachExportException("Sending was interrupted.", e);
        }
    }

    private String getOriginalInput() {
        return configurationService.getConfiguration().getString("bloomreach.api.key.id") + ":"
               + configurationService.getConfiguration().getString("bloomreach.api.key.secret");
    }

    private void logResponse(HttpResponse<String> response) {
        if (LOG.isDebugEnabled()) {
            HttpHeaders headers = response.headers();
            headers.map()
                   .forEach((k, v) -> LOG.debug(k + ":" + v));

            LOG.debug("Bloomreach response status: {}", response.statusCode());
            LOG.debug("Bloomreach response body: {}", response.body());
        }
    }

    /**
     * @return the configurationService
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * @param configurationService
     *            the configurationService to set
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
