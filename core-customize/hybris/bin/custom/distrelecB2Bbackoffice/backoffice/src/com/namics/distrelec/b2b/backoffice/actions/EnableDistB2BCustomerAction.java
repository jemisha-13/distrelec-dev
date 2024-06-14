package com.namics.distrelec.b2b.backoffice.actions;

import javax.annotation.Resource;

import com.hybris.cockpitng.actions.ActionContext;
import com.namics.distrelec.b2b.core.constants.DistConstants.User;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommerce.backoffice.actions.EnableB2BCustomerAction;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

public class EnableDistB2BCustomerAction extends EnableB2BCustomerAction {

    @Resource(name = "userService")
    private UserService distUserService;

    @Override
    public boolean canPerform(ActionContext<B2BCustomerModel> ctx) {
        boolean canPerform = false;
        B2BCustomerModel b2bCustomerModel = ctx.getData();
        if (b2bCustomerModel != null) {
            UserModel currentUser = distUserService.getCurrentUser();
            UserGroupModel callCenterUserGroup = distUserService.getUserGroupForUID(User.DISTCALLCENTERGROUP_UID);
            boolean isUserMemberOfCallCenterGroup = distUserService.isMemberOfGroup(currentUser, callCenterUserGroup);
            canPerform = isUserMemberOfCallCenterGroup && !b2bCustomerModel.getActive();
        }
        return canPerform ? canPerform : super.canPerform(ctx);
    }
}
