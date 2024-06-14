/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.security;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.storefront.security.exceptions.DuplicateEmailAuthenticationException;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

/**
 * {@code DistAcceleratorAuthenticationProvider}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.5
 */
public class DistAcceleratorAuthenticationProvider extends AcceleratorAuthenticationProvider {

    private DistCustomerAccountService customerAccountService;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.spring.security.CoreAuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        try {
            return super.authenticate(authentication);
        } catch (final AuthenticationException authExp) {
            // DISTRELEC-8285 Try to authenticate the customer by his email
            final String username = (String) authentication.getPrincipal();
            // Validate user name as email address.
            if (StringUtils.isBlank(username) || !(new EmailValidator()).isValid(username, null)) {
                throw authExp;
            }

            final List<B2BCustomerModel> contacts = getCustomerAccountService().getCustomersByEmail(username);
            if (CollectionUtils.isEmpty(contacts)) {
                throw authExp;
            } else if (contacts.size() > 1) {
                // If multiple contacts uses the same email address
                throw new DuplicateEmailAuthenticationException("Multiple contacts are using the same email address.", authExp);
            } else {
                final String contactUID = contacts.get(0).getUid();
                return super.authenticate(new UsernamePasswordAuthenticationToken(contactUID, authentication.getCredentials()));
            }
        }
    }

    /**
     * @return the customerAccountService
     */
    public DistCustomerAccountService getCustomerAccountService() {
        return customerAccountService;
    }

    /**
     * @param customerAccountService
     *            the customerAccountService to set
     */
    public void setCustomerAccountService(final DistCustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }
}
