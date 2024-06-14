/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.core.event.DistCatalogOrderEvent;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.infocenter.DistInfoCenterFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.CatalogOrderForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;

@Controller
@RequestMapping("/**/catalog-order")
public class CatalogOrderPageController extends AbstractPageController {

    private static final String CATALOG_ORDER_PAGE = "catalogOrderPage";

    @Autowired
    private DistInfoCenterFacade distInfoCenterFacade;

    @Autowired
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;

    @ModelAttribute("countries")
    public Collection<CountryData> getCountries() {
        return getCheckoutFacade().getDeliveryCountriesAndRegions();
    }

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return getUserFacade().getTitles();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String registerPage(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        model.addAttribute(new CatalogOrderForm());
        populatePage(model, request);
        return ControllerConstants.Views.Pages.InfoCenter.catalogOrderPage;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String orderCatalog(@Valid
    final CatalogOrderForm catalogOrderForm, final BindingResult bindingResult, final Model model, final HttpServletRequest request)
            throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        boolean success = true;
        if (bindingResult.hasErrors()) {
            model.addAttribute(catalogOrderForm);
            GlobalMessages.addErrorMessage(model, "formCatalogOrder.failure");
            success = false;
        } else {
            getDistInfoCenterFacade().orderCatalog(createEvent(catalogOrderForm));
            GlobalMessages.addConfMessage(model, "formCatalogOrder.success");
            model.addAttribute(new CatalogOrderForm());
        }
        populatePage(model, request);
        return success ? addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/catalog-order/success") : ControllerConstants.Views.Pages.InfoCenter.catalogOrderPage;
    }

    protected void populatePage(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        final ContentPageModel contentPage = getContentPageForLabelOrId(CATALOG_ORDER_PAGE);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getContentPageBreadcrumbBuilder().getBreadcrumbs(contentPage, request));
    }

    protected DistCatalogOrderEvent createEvent(final CatalogOrderForm form) {
        final DistCatalogOrderEvent event = new DistCatalogOrderEvent();
        event.setEmailDisplayName(getMessageSource().getMessage("support.displayName", null, "CatalogOrder", getI18nService().getCurrentLocale()));
        BeanUtils.copyProperties(form, event);
        return event;
    }

    public DistInfoCenterFacade getDistInfoCenterFacade() {
        return distInfoCenterFacade;
    }

    public void setDistInfoCenterFacade(final DistInfoCenterFacade distInfoCenterFacade) {
        this.distInfoCenterFacade = distInfoCenterFacade;
    }

    public DistCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(final DistCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(final UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public ContentPageBreadcrumbBuilder getContentPageBreadcrumbBuilder() {
        return contentPageBreadcrumbBuilder;
    }

    public void setContentPageBreadcrumbBuilder(final ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder) {
        this.contentPageBreadcrumbBuilder = contentPageBreadcrumbBuilder;
    }

}
