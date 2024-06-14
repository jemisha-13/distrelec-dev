/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.newsletter.impl;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.obsolescence.service.DistObsolescenceService;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.*;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Default implementation of {@link DistNewsletterFacade}.
 *
 * @author pnueesch, Namics AG, Abhinay Jadhav
 * @since Distrelec 1.0, v8.7
 *
 */
public class DefaultDistNewsletterFacade implements DistNewsletterFacade {

    private static final Logger LOG = LogManager.getLogger(DefaultDistNewsletterFacade.class);

    @Autowired
    private DistrelecCodelistService codelistService;

    @Autowired
    private Converter<DistFunctionModel, DistFunctionData> functionConverter;

    @Autowired
    private Converter<DistDepartmentModel, DistDepartmentData> departmentConverter;

    @Autowired
    private ModelService modelService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private DistCustomerFacade b2bCustomerFacade;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    @Qualifier("defaultDistUserService")
    private UserService userService;

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Autowired
    @Qualifier("userFacade")
    protected DistUserFacade userFacade;

    @Autowired
    private DistBloomreachFacade distBloomreachFacade;

    @Autowired
    private DistObsolescenceService distObsolescenceService;

    @Override
    public List<DistFunctionData> getRoles() {
        return Converters.convertAll(codelistService.getAllDistFunctions(), functionConverter);
    }

    @Override
    public List<DistDepartmentData> getDivisions() {
        return Converters.convertAll(codelistService.getAllDistDepartments(), departmentConverter);
    }

    @Override
    public void optInForAllObsolescenceEmailsForCurrentUser() {
        final CustomerModel customer = (CustomerModel) userService.getCurrentUser();

        customer.setAllObsolCatSelected(true);
        customer.setOptedForObsolescence(true);
        modelService.save(customer);
    }

    @Override
    public void optInForAllObsolescenceEmailsForExistingUser(final String uid) {
        final UserModel user = getUserWithUid(uid);
        if (user instanceof CustomerModel) {
            CustomerModel customer = (CustomerModel) user;
            distObsolescenceService.saveObsolescenceCategoriesForCustomer(customer, true);
        }
    }

    private UserModel getUserWithUid(final String uid) {
        try {
            return userService.getUserForUID(uid);
        } catch (final UnknownIdentifierException e) {
            return null;
        }
    }

    @Override
    public boolean handleBloomreachNewsletterSubscription(final DistConsentData consentData) {
        try {
            final String subscriptionRequest = distBloomreachFacade.createBloomreachSubscriptionRequest(consentData);
            distBloomreachFacade.sendBatchRequestToBloomreach(subscriptionRequest);
            return true;
        } catch (IOException | DistBloomreachBatchException e) {
            LOG.error("Error subscribing newsletter to Bloomreach.", e);
            return false;
        }
    }

    // BEGIN GENERATED CODE

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(final DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    public Converter<DistFunctionModel, DistFunctionData> getFunctionConverter() {
        return functionConverter;
    }

    public void setFunctionConverter(final Converter<DistFunctionModel, DistFunctionData> functionConverter) {
        this.functionConverter = functionConverter;
    }

    public Converter<DistDepartmentModel, DistDepartmentData> getDepartmentConverter() {
        return departmentConverter;
    }

    public void setDepartmentConverter(final Converter<DistDepartmentModel, DistDepartmentData> departmentConverter) {
        this.departmentConverter = departmentConverter;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistCustomerFacade getB2bCustomerFacade() {
        return b2bCustomerFacade;
    }

    public void setB2bCustomerFacade(final DistCustomerFacade b2bCustomerFacade) {
        this.b2bCustomerFacade = b2bCustomerFacade;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public DistCustomerAccountService getDistCustomerAccountService() {
        return distCustomerAccountService;
    }

    public void setDistCustomerAccountService(final DistCustomerAccountService distCustomerAccountService) {
        this.distCustomerAccountService = distCustomerAccountService;
    }

    // END GENERATED CODE

}
