/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.core.service.user.daos.DistCustomerDao;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.user.impl.DefaultUserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class DefaultDistUserService extends DefaultUserService implements DistUserService {

    public static final String[] HEADERS_TO_TRY = { "X-Forwarded-For" };

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistCustomerDao distCustomerDao;

    private String internalIps;

    private Boolean isDevLocal;

    @Override
    public CustomerModel getCustomerByContactId(final String erpContactId) {
        return getDistCustomerDao().getCustomerByContactId(erpContactId);
    }

    @Override
    public <T extends CustomerModel> T getCustomerByContactId(final String erpContactId, final Class<T> clazz) {
        ServicesUtil.validateParameterNotNull(erpContactId, "The given erpContactId is null!");
        ServicesUtil.validateParameterNotNull(clazz, "The given clazz is null!");
        return this.validateType((T) this.getCustomerByContactId(erpContactId), clazz);
    }

    @Override
    public boolean accessFromInternalIp(HttpServletRequest request) {
        if (isDevLocal()) {
            return true;
        } else {
            String internalIps = getInternalIps();
            if (isNotBlank(internalIps)) {
                if (StringUtils.contains(internalIps, request.getRemoteAddr())) {
                    return true;
                }
                for (final String header : HEADERS_TO_TRY) {
                    final String headerIps = request.getHeader(header);
                    if (isNotBlank(headerIps)) {
                        String[] ips = headerIps.split(", *");
                        for (String ip : ips) {
                            if (isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip) && internalIps.contains(ip)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public CustomerType getCurrentCustomerType() {
        CustomerModel customer = (CustomerModel) getCurrentUser();
        return getCustomerType(customer);
    }

    @Override
    public CustomerType getCustomerType(CustomerModel customer) {
        if (isEShopGroup(customer)) {
            return CustomerType.B2E;
        }
        return customer.getCustomerType();
    }

    private boolean isEShopGroup(CustomerModel customer) {
        return isMemberOfGroup(customer, getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID));
    }

    /* Copypasted from superclass because it is private */
    private <T> T validateType(final T value, final Class type) {
        if (!type.isInstance(value)) {
            throw new ClassMismatchException(type, value.getClass());
        }
        return value;
    }

    protected boolean isDevLocal() {
        if (isDevLocal == null) {
            String hostname = getConfigurationService().getConfiguration().getString(DistConstants.PropKey.Environment.YMS_HOSTNAME, "");
            isDevLocal = "127.0.0.1".equals(hostname);
        }
        return isDevLocal;
    }

    @Override
    public String getInternalIps() {
        if (internalIps == null) {
            internalIps = getConfigurationService().getConfiguration().getString(DistConstants.PropKey.Environment.INTERNAL_IPS);
        }
        return internalIps;
    }

    @Override
    public boolean isCurrentCustomerErpSelected() {
        if (getCurrentUser() instanceof CustomerModel currentCustomer) {
            return currentCustomer.isErpSelectedCustomer();
        }
        return false;
    }

    public void setInternalIps(final String internalIps) {
        this.internalIps = internalIps;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistCustomerDao getDistCustomerDao() {
        return distCustomerDao;
    }

    public void setDistCustomerDao(final DistCustomerDao distCustomerDao) {
        this.distCustomerDao = distCustomerDao;
    }

}
