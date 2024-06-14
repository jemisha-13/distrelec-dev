package com.namics.distrelec.occ.core.v2.aop;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import de.hybris.platform.servicelayer.user.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.ForbiddenException;

@Aspect
@Component
public class B2ERestrictedAspect {

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Before("@annotation(com.namics.distrelec.occ.core.v2.annotations.B2ERestricted)")
    public void checkUser(JoinPoint jp) {
        if (getUserService().isMemberOfGroup(getUserService().getCurrentUser(), getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID))) {
            throw new ForbiddenException("B2E user is not allowed to access");
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
