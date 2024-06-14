package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Environment;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.util.CaptchaUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({"/session-config"})
public class SessionConfigController extends AbstractController {

	private static final Logger LOG = Logger.getLogger(SessionConfigController.class);

	private static final String INTERNAL_USER = "internalUser";

	@Autowired
	private CaptchaUtil captchaUtil;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private DistUserService userService;

	@Autowired
	private StoreSessionFacade storeSessionFacade;

	@RequestMapping(value = "set", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public String setConfig(@RequestParam(value = "key", required = true) final String key,
							@RequestParam(value = "value", required = false) final String value,
							final HttpServletRequest request) throws CMSItemNotFoundException {

		if("skipCaptcha".equals(key))
		{
			getCaptchaUtil().setSkipCaptcha(request);
		}
		if (INTERNAL_USER.equals(key)) {
			setInternalUser(request);
		}
		return null;
	}


	@RequestMapping(value = "unset", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public String unsetConfig(@RequestParam(value = "key", required = true) final String key,
							@RequestParam(value = "value", required = false) final String value,
							final HttpServletRequest request) throws CMSItemNotFoundException {

		if (INTERNAL_USER.equals(key)) {
			unsetUserAsInternal();
		}
		return null;
	}

	private void setInternalUser(HttpServletRequest request) {
		if (getUserService().accessFromInternalIp(request)) {
			setUserAsInternal();
		}
	}

	private void setUserAsInternal() {
		getSessionService().setAttribute(Environment.INTERNAL_USER, true);
	}

	private void unsetUserAsInternal() {
		getSessionService().removeAttribute(Environment.INTERNAL_USER);

		// reset current language
		String currentLangIsocode = getStoreSessionFacade().getCurrentLanguage().getIsocode();
		getStoreSessionFacade().setCurrentLanguage(currentLangIsocode);
	}

	public CaptchaUtil getCaptchaUtil() {
		return captchaUtil;
	}

	public void setCaptchaUtil(CaptchaUtil captchaUtil) {
		this.captchaUtil = captchaUtil;
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public DistUserService getUserService() {
		return userService;
	}

	public void setUserService(DistUserService userService) {
		this.userService = userService;
	}

	public StoreSessionFacade getStoreSessionFacade() {
		return storeSessionFacade;
	}

	public void setStoreSessionFacade(StoreSessionFacade storeSessionFacade) {
		this.storeSessionFacade = storeSessionFacade;
	}
}
