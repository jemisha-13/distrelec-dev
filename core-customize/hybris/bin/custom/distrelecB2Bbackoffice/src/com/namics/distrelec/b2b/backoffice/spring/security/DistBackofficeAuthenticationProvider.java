package com.namics.distrelec.b2b.backoffice.spring.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hybris.backoffice.spring.security.BackofficeAuthenticationProvider;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.DisabledException;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;

public class DistBackofficeAuthenticationProvider extends BackofficeAuthenticationProvider {

    private UserService userService;

    @Override
    protected void checkBackofficeAccess(EmployeeModel employee) throws DisabledException {
        Boolean backOfficeLoginDisabled = employee.getBackOfficeLoginDisabled();
        if (backOfficeLoginDisabled == null && isNotEmpty(employee.getGroups())) {
            backOfficeLoginDisabled = checkBackofficeAccessDisabledForGroups(employee.getGroups());
        }

        Boolean hmcLoginDisabled = employee.getHmcLoginDisabled();

        if (isTrue(backOfficeLoginDisabled) || isTrue(hmcLoginDisabled)) {
            throw new DisabledException("User access denied");
        }
    }

    private boolean checkBackofficeAccessDisabledForGroups(Collection<PrincipalGroupModel> groups) {
        Boolean disabled = null;
        Set<PrincipalGroupModel> parentGroups = new HashSet();
        Iterator var5 = groups.iterator();

        while(var5.hasNext()) {
            PrincipalGroupModel group = (PrincipalGroupModel)var5.next();
            if (group.getBackOfficeLoginDisabled() != null) {
                disabled = group.getBackOfficeLoginDisabled();
            }

            if (isTrue(disabled)) {
                break;
            }

            if (isNotEmpty(group.getGroups())) {
                parentGroups.addAll(group.getGroups());
            }
        }

        if (disabled == null && !parentGroups.isEmpty()) {
            disabled = checkBackofficeAccessDisabledForGroups(parentGroups);
        }

        return isTrue(disabled);
    }

    @Override
    @Required
    public void setUserService(UserService userService) {
        super.setUserService(userService);
        this.userService = userService;
    }
}
