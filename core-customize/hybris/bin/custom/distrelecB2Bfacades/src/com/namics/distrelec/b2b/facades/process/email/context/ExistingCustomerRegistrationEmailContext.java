/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.ExistingRegistrationEmailProcessModel;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

/**
 * CheckNewCustomerRegistrationEmailContext.
 *
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 */
public class ExistingCustomerRegistrationEmailContext extends CustomerEmailContext {

	public static final String REGISTERED_USER_NAME = "registeredUserName";
	public static final String COMPANY_NAME = "companyName";
	public static final String USER_MANAGEMENT_URL = "/my-account/company/user-management";
	public static final String CONTACT_US_URL = "/contact/cms/contact";

	private boolean storefrontRequest;

	@Autowired
	private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

	@Override
	public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
		super.init(businessProcessModel, emailPageModel);
		setLoginToken(((ExistingRegistrationEmailProcessModel) businessProcessModel).getCustomer().getToken());
		setStorefrontRequest(((ExistingRegistrationEmailProcessModel) businessProcessModel).getStorefrontRequest());
		put(EMAIL, ((ExistingRegistrationEmailProcessModel) businessProcessModel).getCustomer().getContactEmail());
		put(FROM_EMAIL, get(FROM_EMAIL));
		put(FROM_DISPLAY_NAME, get(FROM_DISPLAY_NAME));
        put(REGISTERED_USER_NAME, ((ExistingRegistrationEmailProcessModel) businessProcessModel).getRegisteredUserName());
		put(COMPANY_NAME, ((ExistingRegistrationEmailProcessModel) businessProcessModel).getCompanyName());
	}

	@Override
	protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
		if (businessProcessModel instanceof StoreFrontProcessModel) {
			return ((StoreFrontProcessModel) businessProcessModel).getSite();
		}
		return null;
	}

	@Override
	protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
		if (businessProcessModel instanceof ExistingRegistrationEmailProcessModel) {
			return ((ExistingRegistrationEmailProcessModel) businessProcessModel).getCustomer();
		}
		return null;
	}

	@Override
	protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
		if (businessProcessModel instanceof ExistingRegistrationEmailProcessModel) {
			final ExistingRegistrationEmailProcessModel storeFrontCustomerProcess = (ExistingRegistrationEmailProcessModel) businessProcessModel;
            if (getUserService().isAnonymousUser(storeFrontCustomerProcess.getCustomer()) && storeFrontCustomerProcess.getLanguage() != null) {
                // In case of anonymous user, we need the session language of the web site
				return storeFrontCustomerProcess.getLanguage();
			}

			return storeFrontCustomerProcess.getCustomer().getSessionLanguage();
		}
		return null;
	}

	public String getMyAccountUrl() throws UnsupportedEncodingException {
		return isStorefrontRequest() ? getUrlForStorefront(USER_MANAGEMENT_URL) : getUrlForHeadless(USER_MANAGEMENT_URL);
	}

	public String getContactUsUrl() throws UnsupportedEncodingException {
		return isStorefrontRequest() ? getUrlForStorefront(CONTACT_US_URL) : getUrlForHeadless(CONTACT_US_URL);
	}

	private String getUrlForHeadless(String path) {
		return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(getBaseSite(), true, path);
	}

	private String getUrlForStorefront(String path) {
		return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(getBaseSite(), true, path);
	}

	public boolean isStorefrontRequest() {
		return storefrontRequest;
	}

	public void setStorefrontRequest(boolean storefrontRequest) {
		this.storefrontRequest = storefrontRequest;
	}
}
