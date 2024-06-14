package com.namics.distrelec.b2b.storefront.controllers.pages;

import static com.namics.distrelec.b2b.storefront.eprocurement.ariba.security.AribaAuthenticationSuccessHandler.ARIBA_LOGIN_REDIRECT;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.facades.eprocurement.DistAribaFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.distrelecoci.exception.OciException;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

@Controller
public class AribaPostAuthenticationController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(AribaPostAuthenticationController.class);

    @Autowired
    private DistAribaFacade distAribaFacade;

    @RequestMapping(value = "/aribaSuccess", method = RequestMethod.GET)
    public String aribaEntrySuccess(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException, OciException {
        addGlobalModelAttributes(model, request);
        String aribaLoginRedirectTo = getRedirectionUrl(request);
        final boolean openInNewWindow = getDistAribaFacade().openInNewWindow();
        if (openInNewWindow) {
            LOG.info("openInNewWindow target set for customer ");
        }
        model.addAttribute("openInNewWindow", Boolean.valueOf(openInNewWindow));
        model.addAttribute("aribaFormPost", aribaLoginRedirectTo);
        model.addAttribute("form_method", "GET");
        model.addAttribute("sendForm", isCurrentEnvironmentDev());
        return ControllerConstants.Views.Pages.EProcurement.Ariba.AribaPage;
    }

    private String getRedirectionUrl(final HttpServletRequest request) {
        String aribaLoginRedirect = "/";
        if (request.getSession().getAttribute(ARIBA_LOGIN_REDIRECT) != null) {
            aribaLoginRedirect = request.getSession().getAttribute(ARIBA_LOGIN_REDIRECT).toString();
            request.getSession().removeAttribute(ARIBA_LOGIN_REDIRECT);
        }

        LOG.info("get redirect after loing to url " + aribaLoginRedirect);
        return aribaLoginRedirect;
    }

    public DistAribaFacade getDistAribaFacade() {
        return distAribaFacade;
    }

    public void setDistAribaFacade(DistAribaFacade distAribaFacade) {
        this.distAribaFacade = distAribaFacade;
    }

}
