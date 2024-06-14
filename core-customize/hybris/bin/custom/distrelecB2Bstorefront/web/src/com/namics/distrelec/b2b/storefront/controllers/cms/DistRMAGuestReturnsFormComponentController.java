package com.namics.distrelec.b2b.storefront.controllers.cms;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.model.cms2.components.DistRMAGuestReturnsFormComponentModel;
import com.namics.distrelec.b2b.facades.rma.DistReturnRequestFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

@Controller("DistRMAGuestReturnsFormComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistRMAGuestReturnsFormComponent)
public class DistRMAGuestReturnsFormComponentController extends AbstractDistCMSComponentController<DistRMAGuestReturnsFormComponentModel> {

    @Autowired
    private DistReturnRequestFacade distReturnRequestFacade;

    @Override
    protected void fillModel(HttpServletRequest request, Model model, DistRMAGuestReturnsFormComponentModel component) {
        model.addAttribute("returnReasons", distReturnRequestFacade.getReturnReasons());
    }

}
