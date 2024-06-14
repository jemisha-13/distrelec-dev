/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.namics.distrelec.b2b.storefront.security;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.eprocurement.security.exceptions.EProcurementCustomerException;
import com.namics.distrelec.b2b.storefront.security.exceptions.CompanyCountryException;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.security.CoreAuthenticationProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Derived authentication provider supporting additional authentication checks.
 * <ul>
 * <li>prevent login without password for users created via CSCockpit</li>
 * <li>prevent login as user anonymous</li>
 * <li>prevent login as user admin</li>
 * <li>prevent login as user in group admingroup</li>
 * <li>prevent login as user if not authorised for B2B</li>
 * <li>prevent login as user if not authorised for B2B</li>
 * </ul>
 * any login as admin disables SearchRestrictions and therefore no page can be viewed correctly
 */
public class AcceleratorAuthenticationProvider extends CoreAuthenticationProvider {
    private static final String ROLE_ADMIN_GROUP = "ROLE_" + Constants.USER.ADMIN_USERGROUP.toUpperCase();

    private GrantedAuthority adminAuthority = new SimpleGrantedAuthority(ROLE_ADMIN_GROUP);
    private B2BUserGroupProvider b2bUserGroupProvider;
    private UserService userService;
    private SessionService sessionService;
    private DistrelecCMSSiteService cmsSiteService;

    @Override
    protected void additionalAuthenticationChecks(final UserDetails details, final AbstractAuthenticationToken authentication) {
        super.additionalAuthenticationChecks(details, authentication);

        // Check if user is anonymous
        if (Constants.USER.ANONYMOUS_CUSTOMER.equalsIgnoreCase(details.getUsername())) {
            throw new LockedException(
                    ErrorLogCode.INVALID_USER_ERROR.getCode() + ErrorSource.HYBRIS + "Login attempt as " + Constants.USER.ANONYMOUS_CUSTOMER + " is rejected");
        }

        // Check if user is admin
        if (Constants.USER.ADMIN_EMPLOYEE.equalsIgnoreCase(details.getUsername())) {
            throw new LockedException(
                    ErrorLogCode.INVALID_USER_GROUP_ERROR.getCode() + ErrorSource.HYBRIS + "Login attempt as " + Constants.USER.ADMIN_EMPLOYEE
                            + " is rejected");
        }

        // Check if user has supplied no password
        if (StringUtils.isEmpty((String) authentication.getCredentials())) {
            throw new BadCredentialsException(ErrorLogCode.INVALID_USER_ERROR.getCode() + ErrorSource.HYBRIS + "Login without password");
        }

        // Check if the user is in role admingroup
        if (getAdminAuthority() != null && details.getAuthorities().contains(getAdminAuthority())) {
            throw new LockedException(ErrorLogCode.INVALID_USER_GROUP_ERROR.getCode() + ErrorSource.HYBRIS + "Login attempt as "
                    + Constants.USER.ADMIN_USERGROUP + " is rejected");
        }

        // Check if the customer is B2B type or B2C Type
        // TODO check if B2C or B2B customer
        // if (!getB2bUserGroupProvider().isUserAuthorized(details.getUsername())) {
        // throw new InsufficientAuthenticationException(messages.getMessage("checkout.error.invalid.accountType",
        // "You are not allowed to login"));
        // }
        //
        // if (!getB2bUserGroupProvider().isUserEnabled(details.getUsername())) {
        // throw new DisabledException("User " + details.getUsername() + " is disabled... " +
        // messages.getMessage("CoreAuthenticationProvider.disabled"));
        // }

        // Check if the user is an e-procurement customer
        final B2BCustomerModel b2bCustomerModel = getUserService().getUserForUID(details.getUsername(), B2BCustomerModel.class);
        if (getUserService().isMemberOfGroup(b2bCustomerModel, getUserService().getUserGroupForUID(DistConstants.User.EPROCUREMENTGROUP_UID))) {
            throw new EProcurementCustomerException(ErrorLogCode.INVALID_USER_ERROR.getCode() + ErrorSource.CXML + "Login attempt as "
                    + DistConstants.User.EPROCUREMENTGROUP_UID + " is rejected");
        }

        // Check if user is allowed to login to current site
        final CMSSiteModel customerBaseSite = (CMSSiteModel) b2bCustomerModel.getCustomersBaseSite();
        if (customerBaseSite != null && !customerBaseSite.equals(getCmsSiteService().getCurrentSite())) {
            final CountryModel country = customerBaseSite.getCountry();
            getSessionService().setAttribute(WebConstants.LOGIN_WRONG_COUNTRY, Boolean.TRUE);
            getSessionService().setAttribute(WebConstants.REDIRECT_COUNTRY, country);
            getSessionService().setAttribute(WebConstants.REDIRECT_SITE, customerBaseSite.getUid());
            throw new CompanyCountryException(ErrorLogCode.INVALID_USER_ERROR.getCode() + ErrorSource.HYBRIS + "User " + details.getUsername()
                    + " tries to login into the wrong sales org.");
        }

    }

    protected B2BUserGroupProvider getB2bUserGroupProvider() {
        return b2bUserGroupProvider;
    }

    public void setB2bUserGroupProvider(final B2BUserGroupProvider b2bUserGroupProvider) {
        this.b2bUserGroupProvider = b2bUserGroupProvider;
    }

    public void setAdminGroup(final String adminGroup) {
        if (StringUtils.isNotBlank(adminGroup)) {
            adminAuthority = new SimpleGrantedAuthority(adminGroup);
        }
    }

    protected GrantedAuthority getAdminAuthority() {
        return adminAuthority;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public DistrelecCMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final DistrelecCMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

}
