/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.cxml.CxmlException;
import com.namics.distrelec.b2b.core.eprocurement.service.cxml.impl.CxmlOutboundSection;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.storefront.annotations.RequireHardLogin;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.CartPageController;
import com.namics.distrelec.distrelecoci.exception.OciException;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.order.exceptions.CalculationException;

/**
 * Controller for checkout process.
 */
@Controller
@RequestMapping(value = "/checkout")
public class DistCheckoutController extends AbstractDistCheckoutController {
    protected static final Logger LOG = LogManager.getLogger(DistCheckoutController.class);

    private static final String PAGE_OCI = "ociPage";

    private static final String PAGE_CXML = "cxmlPage";

    private static final String PAGE_ARIBA = "aribaPage";

    @RequireHardLogin
    @RequestMapping(method = RequestMethod.GET)
    public String checkout(final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request)
                                                                                                                        throws CMSItemNotFoundException,
                                                                                                                        OciException, CalculationException,
                                                                                                                        CxmlException {

        if (!getDistCheckoutFacade().hasCheckoutCart() || !hasItemsInCart()) {
            // no items in the cart
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CartPageController.PAGE_CART);
        }
        long time = System.currentTimeMillis();
        if (getDistCheckoutFacade().hasUnallowedBackorders()) {
            if (LOG.isDebugEnabled()) {
                time = System.currentTimeMillis() - time;
                final String methodName = getClass().getSimpleName() + ".makeObject()";
                LOG.debug("Time to check backorder entry in cart [{}]: {}ms", methodName, time);
            }
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_BACK_ORDER_DETAILS);
        }

        // check for e-procurement
        // OCI case
        if (getDistOciService().isOciCustomer()) {
            model.addAttribute("ociForm", getDistOciService().generateOciForm());
            model.addAttribute("sendForm", isCurrentEnvironmentDev());

            // remove the basket
            getB2bCartFacade().removeSessionCart();
            addGlobalModelAttributes(model, request);
            return setUpPage(model, PAGE_OCI, ControllerConstants.Views.Pages.EProcurement.OCI.OciPage);
        }

        // check for e-procurement
        // CXML case
        if (getDistCxmlService().isCxmlCustomer()) {

            final String cxmlDocument = getDistCxmlService().createCxmlOrderMessage();
            final CxmlOutboundSection cxmlOutboundSection = getDistCxmlService().getOutboundSection();

            model.addAttribute("cxmlDocument", cxmlDocument);
            model.addAttribute("cxmlParams", cxmlOutboundSection.getFields());
            model.addAttribute("cxmlFormPost", cxmlOutboundSection.getField(cxmlOutboundSection.getHookURLFieldName()));
            model.addAttribute("sendForm", isCurrentEnvironmentDev());

            // remove the basket
            getB2bCartFacade().removeSessionCart();

            addGlobalModelAttributes(model, request);
            return setUpPage(model, PAGE_CXML, ControllerConstants.Views.Pages.EProcurement.CXML.CXmlPage);

        }

        // check for e-procurement
        // Ariba case
        if (getDistAribaService().isAribaCustomer()) {
            model.addAttribute("aribaCXml", getDistAribaService().parseAribaOrderMessage());
            model.addAttribute("aribaFormPost", getSessionService().getAttribute(DistConstants.Ariba.Session.BROWSER_FORM_POST));
            getSessionService().removeAttribute(DistConstants.Ariba.Session.BROWSER_FORM_POST);
            model.addAttribute("sendForm", isCurrentEnvironmentDev());

            // remove the basket
            getB2bCartFacade().removeSessionCart();

            addGlobalModelAttributes(model, request);
            return setUpPage(model, PAGE_ARIBA, ControllerConstants.Views.Pages.EProcurement.Ariba.AribaPage);
        }

        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageCategoryAndPageType(digitalDatalayer,
                                                                        DigitalDatalayerConstants.PageName.Checkout.CHECKOUT,
                                                                        DigitalDatalayerConstants.PageName.Checkout.ORDERDETAIL,
                                                                        DigitalDatalayerConstants.PageType.CHECKOUTPAGE);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        String message = request.getParameter(DistConstants.Checkout.MESSAGE_PARAMETER);

        if (!StringUtils.isEmpty(message)) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_DELIVERY + DistConstants.Punctuation.QUESTION_MARK
                                                     + DistConstants.Checkout.MESSAGE_PARAMETER + "=" + message);
        }
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_DELIVERY);
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public String hasValidCart(final Model model, final RedirectAttributes redirectModel) {
        model.addAttribute("redirectPage", validateCheckout(model, redirectModel));
        return ControllerConstants.Views.Fragments.Checkout.ValidateCheckout;
    }

    protected String setUpPage(final Model model, final String pageId, final String pagePath) throws CMSItemNotFoundException {
        ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(pageId);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);
        return pagePath;
    }

    @Override
    protected boolean recalculateCartBeforePage() {
        return false;
    }

    public DistUserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(final DistUserFacade userFacade) {
        this.userFacade = userFacade;
    }
}
