package com.namics.distrelec.occ.core.strategies.impl;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.oauth2.DefaultOauthUserAuditLoginStrategy;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class DistOauthUserAuditLoginStrategy extends DefaultOauthUserAuditLoginStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DistOauthUserAuditLoginStrategy.class);

    @Autowired
    private DistUserService userService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistCustomerFacade b2bCustomerFacade;

    @Override
    public void auditUserOnCorrectCredentials(final String uid) {
        super.auditUserOnCorrectCredentials(uid);

        UserModel user = null;
        try {
            user = userService.getUserForUID(uid);
        } catch (UnknownIdentifierException ex) {
            LOG.warn("Could not find user with uid: {}", uid);
        }

        if (isUserValid(user)) {
            B2BCustomerModel customer = (B2BCustomerModel) user;
            saveUserLoginTime(customer);
            b2bCustomerFacade.storeIPAddress();
        }
    }
    private boolean isUserValid(UserModel user) {
        return Objects.nonNull(user) && isNotGuest(user);
    }

    private boolean isNotGuest(UserModel user) {
        return user instanceof B2BCustomerModel && !CustomerType.GUEST.equals(((B2BCustomerModel) user).getCustomerType());
    }

    private void saveUserLoginTime(B2BCustomerModel user) {
        user.setLastLogin(new Date());
        modelService.save(user);
    }
}
