package com.hybris.ymkt.sapymktsync.util;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

public class UserUtil {

    private UserUtil()  {}

    public static String getUserOriginalUid(final UserModel user)
    {
        if (user instanceof CustomerModel)
        {
            final String originalUid = ((CustomerModel) user).getOriginalUid();
            if (originalUid != null && !originalUid.isEmpty())
            {
                return originalUid;
            }
        }
        return user.getUid();
    }

}
