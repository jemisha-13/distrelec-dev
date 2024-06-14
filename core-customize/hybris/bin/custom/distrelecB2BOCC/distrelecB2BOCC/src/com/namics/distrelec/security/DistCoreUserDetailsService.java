package com.namics.distrelec.security;

import java.time.Instant;
import java.util.*;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import de.hybris.platform.b2b.jalo.B2BCustomer;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.security.PrincipalGroup;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.spring.security.CoreUserDetails;
import de.hybris.platform.spring.security.CoreUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DistCoreUserDetailsService extends CoreUserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Override
    public CoreUserDetails loadUserByUsername(String username) {
        if (username == null) {
            return null;
        } else {
            User user;
            try {
                user = UserManager.getInstance().getUserByLogin(username.toLowerCase());
            } catch (JaloItemNotFoundException ex) {
                String uid;
                try {
                    uid = getUidByCustomerEmail(username.toLowerCase());
                } catch (DuplicateUidException e) {
                    throw new UsernameNotFoundException("User not found!", e);
                }
                user = getUserByUid(uid);
            }

            boolean enabled = this.isNotAnonymousOrAnonymousLoginIsAllowed(user) && !user.isLoginDisabledAsPrimitive() && isAccountActive(user);
            String password = user.getEncodedPassword(JaloSession.getCurrentSession().getSessionContext());
            if (password == null) {
                password = "";
            }

            return new CoreUserDetails(user.getLogin(), password, enabled, true, true, true, this.getAuthorities(user),
                                       JaloSession.getCurrentSession().getSessionContext().getLanguage().getIsoCode());
        }
    }

    private User getUserByUid(String uid) {
        try {
            return UserManager.getInstance().getUserByLogin(uid);
        } catch (JaloItemNotFoundException ex) {
            throw new UsernameNotFoundException("User not found!");
        }
    }

    private String getUidByCustomerEmail(String email) throws DuplicateUidException {
        if (StringUtils.isBlank(email) || !(new EmailValidator()).isValid(email, null)) {
            throw new UsernameNotFoundException("User not found!");
        }

        final List<B2BCustomerModel> contacts = distCustomerAccountService.getCustomersByEmail(email);
        if (CollectionUtils.isEmpty(contacts)) {
            throw new UsernameNotFoundException("User not found!");
        } else if (contacts.size() > 1) {
            throw new DuplicateUidException("Multiple contacts are using the same email address.");
        } else {
            return contacts.iterator().hasNext() ? contacts.iterator().next().getUid() : null;
        }
    }

    private boolean isAccountActive(User user) {
        return user.getDeactivationDate() == null || user.getDeactivationDate().toInstant().isAfter(Instant.now());
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        Set<PrincipalGroup> groups = user.getGroups();
        Collection<GrantedAuthority> authorities = new ArrayList<>(groups.size());

        for (PrincipalGroup group : groups) {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + group.getUID().toUpperCase()));
            for (PrincipalGroup gr : group.getAllGroups()) {
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + gr.getUID().toUpperCase()));
            }
        }

        return authorities;
    }
}
