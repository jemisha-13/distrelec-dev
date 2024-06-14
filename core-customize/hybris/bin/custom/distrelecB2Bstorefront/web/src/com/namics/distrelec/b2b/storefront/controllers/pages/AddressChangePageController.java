/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.core.event.DistAddressChangeEvent;
import com.namics.distrelec.b2b.core.event.DistAddressChangeEvent.Address;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.infocenter.DistInfoCenterFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.AddressChangeForm;
import com.namics.distrelec.b2b.storefront.forms.OfflineAddressForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;

@Controller
@RequestMapping("/**/address-change")
public class AddressChangePageController extends AbstractPageController {

    private static final String ADDRESSCHANGE_CMS_PAGE = "offlineAddressChangePage";

    @Autowired
    @Qualifier("contentPageUrlResolver")
    private DistUrlResolver<ContentPageModel> contentPageUrlResolver;

    @Autowired
    private DistInfoCenterFacade distInfoCenterFacade;

    @Autowired
    private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

    @RequestMapping(method = RequestMethod.GET)
    public String changeAddressPage(final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException, UnsupportedEncodingException {
        final String redirection = checkRequestUrl(request, response, getContentPageUrlResolver().resolve(getAddressChangePage()));
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

        addGlobalModelAttributes(model, request);
        model.addAttribute(new AddressChangeForm());
        populatePage(model, request);
        return ControllerConstants.Views.Pages.InfoCenter.AddressChangePage;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String changeAddress(@Valid
    final AddressChangeForm addressChangeForm, final BindingResult bindingResult, final Model model, final HttpServletRequest request)
            throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        boolean success = true;
        final boolean isCaptchaValid = getCaptchaUtil().validateReCaptcha(request);
        if (isCaptchaValid && !bindingResult.hasErrors()) {
            getDistInfoCenterFacade().changeAddress(createEvent(addressChangeForm));
            GlobalMessages.addConfMessage(model, "formOfflineAddressChange.success");
            model.addAttribute(new AddressChangeForm());
        } else {
            if (bindingResult.hasErrors()) {
                GlobalMessages.addErrorMessage(model, "formOfflineAddressChange.failed");
            } else if (!isCaptchaValid) {
                GlobalMessages.addErrorMessage(model, "form.captcha.error");
                model.addAttribute("captchaError", Boolean.TRUE);
            }
            success = false;
            model.addAttribute(addressChangeForm);
        }
        populatePage(model, request);
        return success ? addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/address-change/success") : ControllerConstants.Views.Pages.InfoCenter.AddressChangePage;
    }

    protected DistAddressChangeEvent createEvent(final AddressChangeForm form) {
        final DistAddressChangeEvent event = new DistAddressChangeEvent(form.getCustomerNumber(), form.getComment());
        event.setEmailDisplayName(getMessageSource().getMessage("support.displayName", null, "AddressChange", getI18nService().getCurrentLocale()));
        final Address oldAddress = event.new Address();
        final Address newAddress = event.new Address();

        final OfflineAddressForm oldAddressForm = form.getOldAddress();
        if (oldAddressForm != null) {
            BeanUtils.copyProperties(oldAddressForm, oldAddress);
        }
        event.setOldAddress(oldAddress);

        final OfflineAddressForm newAddressForm = form.getNewAddress();
        if (newAddressForm != null) {
            BeanUtils.copyProperties(newAddressForm, newAddress);
        }
        event.setNewAddress(newAddress);

        return event;
    }

    protected ContentPageModel getAddressChangePage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId(ADDRESSCHANGE_CMS_PAGE);
    }

    protected void populatePage(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        final ContentPageModel addressChangePage = getAddressChangePage();
        storeCmsPageInModel(model, addressChangePage);
        setUpMetaDataForContentPage(model, addressChangePage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getContentPageBreadcrumbBuilder().getBreadcrumbs(addressChangePage, request));
    }

    public DistUrlResolver<ContentPageModel> getContentPageUrlResolver() {
        return contentPageUrlResolver;
    }

    public void setContentPageUrlResolver(final DistUrlResolver<ContentPageModel> contentPageUrlResolver) {
        this.contentPageUrlResolver = contentPageUrlResolver;
    }

    public DistInfoCenterFacade getDistInfoCenterFacade() {
        return distInfoCenterFacade;
    }

    public void setDistInfoCenterFacade(final DistInfoCenterFacade distInfoCenterFacade) {
        this.distInfoCenterFacade = distInfoCenterFacade;
    }

    public ContentPageBreadcrumbBuilder getContentPageBreadcrumbBuilder() {
        return contentPageBreadcrumbBuilder;
    }

    public void setContentPageBreadcrumbBuilder(final ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder) {
        this.contentPageBreadcrumbBuilder = contentPageBreadcrumbBuilder;
    }

}
