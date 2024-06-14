package com.namics.distrelec.b2b.facades.pdf.impl;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.COMMA_COUNTRIES;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.pdf.data.DistPdfData;
import com.namics.distrelec.b2b.core.pdf.helper.DistPDFNumberFormatHelper;
import com.namics.distrelec.b2b.core.pdf.service.DistPDFService;
import com.namics.distrelec.b2b.facades.helper.DistLogoUrlHelper;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.pdf.DistPDFGenerationFacade;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

public class DefaultDistPDFGenerationFacade implements DistPDFGenerationFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistPDFGenerationFacade.class);

    private static final String CART_XSL_NAME = "cart.xsl";

    private static final String CART_VM_NAME = "pdf-cart.vm";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private CommerceCommonI18NService commerceCommonI18NService;

    @Autowired
    private DistPDFService distPDFService;

    @Autowired
    @Qualifier("defaultDistCartFacade")
    private DistCartFacade distCartFacade;

    @Autowired
    private DistLogoUrlHelper distLogoUrlHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public InputStream getPDFStreamForCart() {
        CMSSiteModel currentSite = cmsSiteService.getCurrentSite();
        LanguageModel currentLanguage = commerceCommonI18NService.getCurrentLanguage();
        Map<String, Object> params = new HashMap<>();
        CartData currentCart = distCartFacade.getSessionCart();
        params.put("cart", currentCart);
        if (currentSite == null || currentLanguage == null || currentCart == null) {
            LOG.debug("One or more arguments for generating the Cart PDF are null. Returning null");
            return null;
        }
        Locale currentLocale = commerceCommonI18NService.getCurrentLocale();
        String currencyIso = Optional.ofNullable(currentCart.getTotalPrice())
                                     .map(PriceData::getCurrencyIso)
                                     .orElseGet(this::getDefaultCurrencyIso);
        params.put("formatHelper", new DistPDFNumberFormatHelper(currentLocale, currencyIso,
                                                                 configurationService.getConfiguration().getString(COMMA_COUNTRIES, StringUtils.EMPTY)));
        params.put("currentSite", currentSite.getUid());

        DistPdfData data = getData(currentSite, currentLanguage, params);
        return distPDFService.generatePdfFromData(data);
    }

    @Override
    public String getCartPdfHeaderValue() {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return String.format("inline; filename=cart__%s.pdf", df.format(now));
    }

    private DistPdfData getData(CMSSiteModel site, LanguageModel language, Map<String, Object> params) {
        DistPdfData data = new DistPdfData(CART_VM_NAME, CART_XSL_NAME);
        data.setCmsSiteModel(site);
        data.setLanguage(language);
        data.setAdditionalParameters(params);
        data.setLogoUrl(distLogoUrlHelper.getLogoUrl(site));
        UserModel currentUser = userService.getCurrentUser();
        if (currentUser instanceof CustomerModel) {
            data.setCustomer((CustomerModel) currentUser);
        }
        return data;
    }

    private String getDefaultCurrencyIso() {
        CurrencyModel currency = commerceCommonI18NService.getCurrentCurrency();
        return currency == null ? null : currency.getIsocode();
    }
}
