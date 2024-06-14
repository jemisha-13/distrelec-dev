/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.eprocurement.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.data.ariba.DistAribaDataFactory;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.cxml.generated.*;
import com.namics.distrelec.b2b.facades.eprocurement.DistAribaFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Ariba.SetupRequestParams.PRODUCT_CODE;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Ariba.*;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.EMPTY;

/**
 * Default Distrelec implementation for the {@link DistAribaFacade}.
 */
public class DefaultDistAribaFacade implements DistAribaFacade {
    private static final Logger LOG = Logger.getLogger(DefaultDistAribaFacade.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistAribaService distAribaService;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistCartFacade cartFacade;

    private final DistAribaDataFactory dataFactory = new DistAribaDataFactory();

    @Override
    public Map<String, String> getCredentialsFromDistAribaOutSetupRequest(final String aribaSetupRequest) {
        final Map<String, String> credentials = new HashMap<>();
        credentials.put(URL_PARAM_KEY_USERNAME, EMPTY);
        credentials.put(URL_PARAM_KEY_PASSWORD, EMPTY);
        credentials.put(URL_PARAM_KEY_PAYLOADID, EMPTY);

        final CXML cXmlPunchOutSetupRequest = distAribaService.parseAribaSetupRequest(aribaSetupRequest);
        if (cXmlPunchOutSetupRequest != null) {
            final Header header = dataFactory.getHeader(cXmlPunchOutSetupRequest);

            final boolean useOldAribaAuthentication = getConfigurationService().getConfiguration().getBoolean("ariba.authentication.old", false);
            if (useOldAribaAuthentication) {

                final Identity identity = getCredential(header.getFrom().getCredential()).getIdentity();
                credentials.put(URL_PARAM_KEY_USERNAME, identity == null ? EMPTY : getContent(identity.getContent()));

                final SharedSecret sharedSecret = dataFactory.getSharedSecret(header.getSender().getCredential());
                credentials.put(URL_PARAM_KEY_PASSWORD, sharedSecret == null ? EMPTY : getContent(sharedSecret.getContent()));

            } else {
                final Credential fromCredential = getCredential(header.getFrom().getCredential());
                final Credential senderCredential = getCredential(header.getSender().getCredential());

                final Identity fromIdentity = fromCredential.getIdentity();

                credentials.put(URL_PARAM_KEY_USERNAME, getContent(fromIdentity.getContent()));

                final SharedSecret sharedSecret = dataFactory.getSharedSecret(senderCredential);
                credentials.put(URL_PARAM_KEY_PASSWORD, sharedSecret == null ? EMPTY : getContent(sharedSecret.getContent()));

            }

            final PunchOutSetupRequest punchOutSetupRequest = dataFactory.getPunchOutSetupRequest(cXmlPunchOutSetupRequest);
            credentials.put(PRODUCT_CODE,
                    (null != punchOutSetupRequest.getSelectedItem() && null != punchOutSetupRequest.getSelectedItem().getItemID())
                            ? punchOutSetupRequest.getSelectedItem().getItemID().getSupplierPartID()
                            : EMPTY);
        }
        return credentials;
    }

    private String getContent(final List<Object> contents) {
        if (contents != null) {
            if (CollectionUtils.isNotEmpty(contents)) {
                return contents.get(0).toString();
            }
        }
        return EMPTY;
    }

    @Override
    public CXML parseAribaSetupRequest() {
        return distAribaService.parseAribaSetupRequest();
    }

    @Override
    public Map<String, String> getAribaSetupRequestParameters(final CXML cXmlPunchOutSetupRequest) {
        return distAribaService.getAribaSetupRequestParameters(cXmlPunchOutSetupRequest);
    }

    @Override
    public String getAribaToken(final String userId, final String password, final String setupRequest) {
        return distAribaService.getAribaToken(userId, password, setupRequest);
    }

    @Override
    public void parseAribaSetupResponse(final HttpStatus status, final String url, final String payloadId, final Writer writer) {
        distAribaService.parseAribaSetupResponse(status, url, payloadId, writer);
    }

    @Override
    public String setUpAribaCart(final CXML cXmlPunchOutSetupRequest, final boolean useBasketFromCustomer, final String customerCartCode) {
        // create Ariba cart with cartCode from punchOutSetupRequest
        boolean loadAribaCartFailed = false;
        if (StringUtils.isNotBlank(customerCartCode)) {
            try {
                cartFacade.loadCart(customerCartCode);
                distAribaService.updateAribaCart(cXmlPunchOutSetupRequest);
                return customerCartCode;
            } catch (final UnknownIdentifierException e) {
                loadAribaCartFailed = true;
                sessionService.setAttribute(DistConstants.Ariba.Session.ARIBA_CART_NOT_LOADED, Boolean.TRUE);
                LOG.info("Create new Ariba cart because ariba customer cart could not be found", e);
            }
        }

        // create Ariba cart with product information from punchOutSetupRequest
        if (useBasketFromCustomer) {
            final String aribaCartCode = distAribaService.setUpAribaCart(cXmlPunchOutSetupRequest);
            distAribaService.updateAribaCart(cXmlPunchOutSetupRequest);
            return aribaCartCode;
        }
        // create empty Ariba cart
        final String newAribaCartCode = distAribaService.setUpAribaCart();
        distAribaService.updateAribaCart(cXmlPunchOutSetupRequest);

        // code of new empty Ariba cart will only be returned if loading
        // existing card failed!
        return loadAribaCartFailed ? newAribaCartCode : EMPTY;
    }

    private Credential getCredential(final List<Credential> credentials) {
        return CollectionUtils.isEmpty(credentials) ? null
                : credentials.stream().filter(Objects::nonNull)
                        .filter(credential -> (StringUtils.equalsIgnoreCase(credential.getDomain(), URL_PARAM_KEY_NETWORK_ID)
                                || StringUtils.equalsIgnoreCase(credential.getDomain(), URL_PARAM_KEY_ARIBA_NETWORK_ID)))
                        .findFirst().orElse(null);
    }

    @Override
    public void setUpAribaLanguage(final String isocode) {
        distAribaService.setUpAribaLanguage(isocode);
    }

    @Override
    public boolean openInNewWindow() {
        return distAribaService.openInNewWindow();
    }

    @Override
    public boolean haveItemOutData(final CXML cXmlPunchOutSetupRequest) {
        return distAribaService.haveItemOutData(cXmlPunchOutSetupRequest);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
