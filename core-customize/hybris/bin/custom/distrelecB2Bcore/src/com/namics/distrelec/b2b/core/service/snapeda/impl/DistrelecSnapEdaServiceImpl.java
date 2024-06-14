package com.namics.distrelec.b2b.core.service.snapeda.impl;

import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.namics.distrelec.b2b.core.service.snapeda.SnapEdaService;
import com.namics.distrelec.b2b.core.service.snapeda.exception.SnapEdaApiException;
import com.namics.distrelec.b2b.core.service.snapeda.response.SnapEdaResponse;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import static com.namics.distrelec.b2b.core.util.LocalDateUtil.convertDateToLocalDateTime;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DistrelecSnapEdaServiceImpl implements SnapEdaService {

    private static final Logger LOG = LoggerFactory.getLogger(DistrelecSnapEdaServiceImpl.class);

    private static final String URL = "snapeda.api.url";

    private static final String SNAPEDA_TOKEN = "snapeda.api.token";

    private static final String FOUND_PRODUCT_SNAPEDA_FLAGGING_TIMEOUT_DAYS = "snapeda.product.flagging.timeout.days.found"; // flagged as true

    private static final String NOT_FOUND_PRODUCT_SNAPEDA_FLAGGING_TIMEOUT_DAYS = "snapeda.product.flagging.timeout.days.not_found"; // flagged as false

    private static final int FOUND_PRODUCT_SNAPEDA_FLAGGING_TIMEOUT_DAYS_DEFAULT = 180;

    private static final int NOT_FOUND_PRODUCT_SNAPEDA_FLAGGING_TIMEOUT_DAYS_DEFAULT = 30;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ModelService modelService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void flagProduct(ProductModel product) {
        if (isFalse(isFlaggingNeeded(product))) {
            return;
        }

        SnapEdaResponse response = getResponse(product);
        if (response != null) {
            boolean flag = response.getStatus();

            product.setSnapEdaAvailabilityFlaggingDate(from(now().atZone(systemDefault()).toInstant())); // current date
            product.setAvailableInSnapEda(flag);
            modelService.save(product);
        }

    }

    private SnapEdaResponse getResponse(ProductModel product) {
        try {
            // Build URI
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(getSnapEdaApiURL())
                                                                  .queryParam("token", getSnapEdaApiToken())
                                                                  .queryParam("lang", "en");

            // Create HTTP entity with headers and body
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(getFormData(product), getHeaders());

            // POST to SnapEDA
            ResponseEntity<SnapEdaResponse> response = restTemplate.postForEntity(
              uriBuilder.toUriString(),
              request,
              SnapEdaResponse.class);

            if (isSuccessfulResponse(response)) {
                return response.getBody();
            } else {
                throw new SnapEdaApiException("Response was null.");
            }
        } catch (RestClientException | IllegalArgumentException | SnapEdaApiException e) {
            String message = String.format(
              "Error getting response from SnapEDA for product with code %s, manufacturer article number %s, and manufacturer name %s.",
              product.getCode(),
              product.getTypeName(),
              product.getManufacturer() != null ? product.getManufacturer().getName() : EMPTY);
            LOG.error(message);
            throw new SnapEdaApiException(message, e);
        }
    }

    private HttpHeaders getHeaders() {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Referer", "https://www.snapeda.com/");

        return headers;
    }

    private MultiValueMap<String, String> getFormData(ProductModel product) {
        // Set form data using
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("part", product.getTypeName());
        formData.add("manufacturer", product.getManufacturer().getName());

        return formData;
    }

    private boolean isFlaggingNeeded(ProductModel product) {
        if (product == null || isBlank(product.getTypeName())) {
            return false;
        }

        // If the flagging date was not set, flag this product and set the date, or
        // if the flagging date has passed (is after today), re-flag this product and re-set the date
        // otherwise, flagging is not needed
        // re-flagging for products not in SnapEDA (isAvailableInSnapEda == false) is 30 days
        // re-flagging for products in SnapEDA (isAvailableInSnapEda == true) is 180 days
        Date flaggingDate = product.getSnapEdaAvailabilityFlaggingDate();
        return flaggingDate == null ||
               isFlaggingTimeoutPeriodFinished(convertDateToLocalDateTime(flaggingDate), product.isAvailableInSnapEda());
    }

    private boolean isFlaggingTimeoutPeriodFinished(LocalDateTime lastFlaggingDate, boolean isAvailableInSnapEda) {
        LocalDateTime timeoutFinishedDateTime;
        if (isAvailableInSnapEda) {  // Products flagged with "true"
            timeoutFinishedDateTime = lastFlaggingDate.plusDays(getFoundSnapEdaProductFlaggingTimeoutInDays()); // 180 days
        } else {  // Products flagged with "false"
            timeoutFinishedDateTime = lastFlaggingDate.plusDays(getNotFoundSnapEdaProductFlaggingTimeoutInDays()); // 30 days
        }

        return now().isAfter(timeoutFinishedDateTime);
    }

    private boolean isSuccessfulResponse(ResponseEntity<SnapEdaResponse> response) {
        return response != null &&
               response.getStatusCode().is2xxSuccessful() &&
               response.hasBody();
    }

    private String getSnapEdaApiURL() {
        return configurationService.getConfiguration().getString(URL);
    }

    private String getSnapEdaApiToken() {
        return configurationService.getConfiguration().getString(SNAPEDA_TOKEN);
    }

    /**
     * Get timeout in days for products that are flagged as "true" in isAvailableInSnapEda.
     * <p>
     * For optimization purposes - we do not re-flag products already found for 180 days.
     * </p>
     */
    private int getFoundSnapEdaProductFlaggingTimeoutInDays() {
        return configurationService.getConfiguration()
                                   .getInt(FOUND_PRODUCT_SNAPEDA_FLAGGING_TIMEOUT_DAYS, FOUND_PRODUCT_SNAPEDA_FLAGGING_TIMEOUT_DAYS_DEFAULT);
    }

    /**
     * Get timeout in days for products that are flagged as "false" in isAvailableInSnapEda.
     * <p>
     * We do not re-flag products already found for 30 days.
     * </p>
     */
    private int getNotFoundSnapEdaProductFlaggingTimeoutInDays() {
        return configurationService.getConfiguration()
                                   .getInt(NOT_FOUND_PRODUCT_SNAPEDA_FLAGGING_TIMEOUT_DAYS, NOT_FOUND_PRODUCT_SNAPEDA_FLAGGING_TIMEOUT_DAYS_DEFAULT);
    }
}
