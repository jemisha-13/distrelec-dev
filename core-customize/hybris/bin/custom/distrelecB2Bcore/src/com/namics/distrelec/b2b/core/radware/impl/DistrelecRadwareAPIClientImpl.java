package com.namics.distrelec.b2b.core.radware.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.namics.distrelec.b2b.core.radware.exception.DistRadwareAPIException;
import com.namics.distrelec.b2b.core.radware.DistrelecRadwareAPIClient;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DistrelecRadwareAPIClientImpl implements DistrelecRadwareAPIClient {

    private static final Logger LOG = LogManager.getLogger(DistrelecRadwareAPIClientImpl.class);

    private static final String API_COOKIE_HEADER = "Cookie";

    private static final String API_CONTENT_TYPE_HEADER = "Content-Type";

    private static final String API_AUTHORIZATION_HEADER = "Authorization";

    private static final String API_REQUESTED_ENTITY_HEADER = "requestEntityIds";

    private static final String JSON_CONTENT_TYPE = "application/json";

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public String getRadwareSessionToken(String requestString) throws DistRadwareAPIException {

        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(configurationService.getConfiguration().getString("radware.public.api.host.url") + "/"
                                                         + configurationService.getConfiguration().getString("radware.api.authentication.url")))
                                         .POST(HttpRequest.BodyPublishers.ofString(requestString))
                                         .header(API_COOKIE_HEADER, getAuthenticationCookieHeader())
                                         .header(API_CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
                                         .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logResponse(response);
            if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                throw new DistRadwareAPIException("Couldn't fetch Radware Token");
            }
        } catch (IOException e) {
            LOG.error("Couldn't fetch Radware Token", e);
            throw new DistRadwareAPIException("Couldn't fetch Radware Token", e);
        } catch (InterruptedException e) {
            LOG.error("Sending was interrupted.", e);
            throw new DistRadwareAPIException("Sending was interrupted.", e);
        }

        return response.body();

    }

    private String getAuthenticationCookieHeader() {
        return configurationService.getConfiguration().getString("radware.api.cookie.header");
    }

    private void logResponse(HttpResponse<String> response) {
        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> LOG.info(k + ":" + v));
        LOG.debug("Radware response status: {}", response.statusCode());
        LOG.debug("Radware response body: {}", response.body());
    }

    @Override
    public String getAuthTokenResponse(String sessionToken) throws DistRadwareAPIException {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(configurationService.getConfiguration().getString("radware.public.api.host.url") + "/"
                                                         + configurationService.getConfiguration().getString("radware.api.authorization.url") + "?client_id="
                                                         + configurationService.getConfiguration().getString("radware.api.authorization.client_id") + "&nonce="
                                                         + configurationService.getConfiguration().getString("radware.api.authorization.nonce")
                                                         + "&prompt=none&redirect_uri=https%3A%2F%2Fportal.radwarecloud.com&response_mode=form_post&response_type=token&scope=api_scope&sessionToken="
                                                         + sessionToken + "&state="
                                                         + configurationService.getConfiguration().getString("radware.api.authorization.state") + ""))
                                         .GET().header(API_COOKIE_HEADER, getAuthenticationCookieHeader())
                                         .header(API_CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
                                         .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logResponse(response);
            if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                throw new DistRadwareAPIException("Couldn't get Radware Authentication Token");
            }
        } catch (IOException e) {
            LOG.error("Couldn't get Radware Authentication Token", e);
            throw new DistRadwareAPIException("Couldn't get Radware Authentication Token", e);
        } catch (InterruptedException e) {
            LOG.error("Sending was interrupted.", e);
            throw new DistRadwareAPIException("Sending was interrupted.", e);
        }

        return response.body();
    }

    @Override
    public String getRadwareAppListResponse(String authToken) throws DistRadwareAPIException {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(configurationService.getConfiguration().getString("radware.portal.api.host.url") + "/"
                                                         + configurationService.getConfiguration().getString("radware.api.applistapi.url") + ""))
                                         .GET().header(API_COOKIE_HEADER, getAuthenticationCookieHeader())
                                         .header(API_CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
                                         .header(API_REQUESTED_ENTITY_HEADER,
                                                 configurationService.getConfiguration().getString("radware.api.applist.requestEntityIds"))
                                         .header(API_AUTHORIZATION_HEADER, "Bearer " + authToken)
                                         .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logResponse(response);
            if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                throw new DistRadwareAPIException("Couldn't get Radware App List");
            }
        } catch (IOException e) {
            LOG.error("Couldn't get Radware App List", e);
            throw new DistRadwareAPIException("Couldn't get Radware App List", e);
        } catch (InterruptedException e) {
            LOG.error("Sending was interrupted.", e);
            throw new DistRadwareAPIException("Sending was interrupted.", e);
        }

        return response.body();
    }

    @Override
    public void clearCDNCacheOnRadware(String applicationId, String authToken) throws DistRadwareAPIException {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(configurationService.getConfiguration().getString("radware.portal.api.host.url") + "/"
                                                         + configurationService.getConfiguration().getString("radware.api.clearcdncache.url.part1") + "/"
                                                         + applicationId + "/"
                                                         + configurationService.getConfiguration().getString("radware.api.clearcdncache.url.part2")))
                                         .POST(HttpRequest.BodyPublishers.ofString(getClearCDNCacheAPIBody()))
                                         .header(API_CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE)
                                         .header(API_REQUESTED_ENTITY_HEADER,
                                                 configurationService.getConfiguration().getString("radware.api.applist.requestEntityIds"))
                                         .header(API_AUTHORIZATION_HEADER, "Bearer " + authToken)
                                         .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logResponse(response);
            if (!HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                throw new DistRadwareAPIException("Couldn't clear Radware CDN cache for application " + applicationId);
            }
        } catch (IOException e) {
            LOG.error("Couldn't clear Radware CDN cache for application" + applicationId, e);
            throw new DistRadwareAPIException("Couldn't clear Radware CDN cache for application" + applicationId, e);
        } catch (InterruptedException e) {
            LOG.error("Sending was interrupted.", e);
            throw new DistRadwareAPIException("Sending was interrupted.", e);
        }

    }

    private String getClearCDNCacheAPIBody() {
        return "{\n"
               + "    \"paths\": \"/*\"\n"
               + "}";
    }

}
