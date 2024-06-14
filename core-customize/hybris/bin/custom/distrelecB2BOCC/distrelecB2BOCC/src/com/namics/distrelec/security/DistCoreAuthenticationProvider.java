package com.namics.distrelec.security;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.spring.security.CoreAuthenticationProvider;

public class DistCoreAuthenticationProvider extends CoreAuthenticationProvider {

    @Autowired
    private DistUserService userService;

    @Autowired
    private DistCustomerFacade b2bCustomerFacade;

    @Override
    protected void additionalAuthenticationChecks(UserDetails details, AbstractAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(details, authentication);

        UserModel user = userService.getUserForUID(details.getUsername());

        if (isUserValid(user)) {
            B2BCustomerModel customer = (B2BCustomerModel)user;
            syncCustomerErpData(customer);

            if(!customer.getActive()){
                throw new DisabledException("User is disabled");
            }
        }
    }

    private void syncCustomerErpData(B2BCustomerModel user) {
        userService.setCurrentUser(user);
        b2bCustomerFacade.checkAndUpdateCustomer();
    }

    private boolean isUserValid(UserModel user) {
        return Objects.nonNull(user) && isNotGuest(user);
    }

    private boolean isNotGuest(UserModel user) {
        return user instanceof B2BCustomerModel && !CustomerType.GUEST.equals(((B2BCustomerModel) user).getCustomerType());
    }

}
