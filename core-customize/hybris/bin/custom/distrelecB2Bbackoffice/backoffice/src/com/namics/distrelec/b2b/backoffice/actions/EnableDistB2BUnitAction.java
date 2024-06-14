package com.namics.distrelec.b2b.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommerce.backoffice.actions.EnableB2BUnitAction;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class EnableDistB2BUnitAction extends EnableB2BUnitAction {

    @Autowired
    private UserService userService;

    @Override
    public boolean canPerform(ActionContext<B2BUnitModel> ctx) {
        boolean canPerform = false;
        B2BUnitModel b2BUnit = ctx.getData();
        if (b2BUnit != null) {
            UserModel currentUser = userService.getCurrentUser();
            UserGroupModel callCenterUserGroup = userService.getUserGroupForUID(DistConstants.User.DISTCALLCENTERGROUP_UID);
            boolean isUserMemberOfCallCenterGroup = userService.isMemberOfGroup(currentUser, callCenterUserGroup);
            canPerform = isUserMemberOfCallCenterGroup && !b2BUnit.getActive();
        }
        return canPerform ? canPerform : super.canPerform(ctx);
    }
}
