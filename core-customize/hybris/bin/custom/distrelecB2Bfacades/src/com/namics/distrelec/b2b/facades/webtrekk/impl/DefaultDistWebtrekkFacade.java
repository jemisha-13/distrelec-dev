/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.util.UriUtils;

import com.namics.distrelec.b2b.core.model.WtAdvancedParameterModel;
import com.namics.distrelec.b2b.core.model.WtBasicParameterModel;
import com.namics.distrelec.b2b.facades.cms.converters.WtAdvancedParameterDataPopulator;
import com.namics.distrelec.b2b.facades.cms.converters.WtBasicParameterDataPopulator;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.product.data.WtAdvancedParameterData;
import com.namics.distrelec.b2b.facades.product.data.WtBasicParameterData;
import com.namics.distrelec.b2b.facades.product.data.WtProductParamsData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Default implementation of {@link DistWebtrekkFacade}.
 *
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 *
 */
public class DefaultDistWebtrekkFacade implements DistWebtrekkFacade {

    private static final String ONS_MNV = "ons.mnv";

    private static final String FAF_S_FCT = "faf-s.fct";

    private static final String ONS_ICO = "ons.ico";

    private static final String ONS_LNV = "ons.lnv";

    private static final Logger LOG = LogManager.getLogger(DefaultDistWebtrekkFacade.class);

    private static final String WT_JS_MEDIA = "webtrekk.javascript.media";

    private static final String WT_JS_MEDIA_TEST = "webtrekk.javascript.media.test";

    private static final String WT_TEASER_TRACKING_ID = "wtTeaserTrackingId"; // will not in use anymore from now onward

    private static final String WT_TEASER_TRACKING_ID_LNV = "wtTeaserTrackingIdLnv"; // for tracking Left-side navigation

    private static final String WT_TEASER_TRACKING_ID_ICO = "wtTeaserTrackingIdIco"; // for tracking Icons

    private static final String WT_TEASER_TRACKING_ID_FAF = "wtTeaserTrackingIdFaf"; // for tracking Facetted search

    private static final String WT_TEASER_TRACKING_ID_MNV = "wtTeaserTrackingIdMnv"; // for tracking Main navigation

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade cartFacade;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    @Qualifier("wtBasicParamPopulator")
    private WtBasicParameterDataPopulator wtBasicParamPopulator;

    @Autowired
    @Qualifier("wtAdvancedParamPopulator")
    private WtAdvancedParameterDataPopulator wtAdvancedParamPopulator;

    @Override
    public String getWebtrekkJsFileUrl(final HttpServletRequest request) {
        final StringBuilder urlBuilder = new StringBuilder();
        final String serverName = request.getServerName();
        final String wtMediaName = isNotLiveEnv(serverName) ? getConfigurationService().getConfiguration().getString(WT_JS_MEDIA_TEST)
                                                            : getConfigurationService().getConfiguration().getString(WT_JS_MEDIA);
        try {
            final CatalogVersionModel catalogVersion = getCmsSiteService().getCurrentSite().getContentCatalogs().get(0).getActiveCatalogVersion();
            final MediaModel wtMedia = getMediaService().getMedia(catalogVersion, wtMediaName);
            if (wtMedia == null) {
                LOG.warn("Webtrekk javascript media is null !! please check the configuration");
                return "";
            }
            final String protocol = request.isSecure() ? "https" : "http";
            urlBuilder.append(
                              getConfigurationService().getConfiguration()
                                                       .getString("website." + getBaseSiteService().getCurrentBaseSite().getUid() + "." + protocol));
            urlBuilder.append(wtMedia.getURL());
        } catch (final UnknownIdentifierException e) {
            LOG.error("Webtrekk javascript media cant be found by name " + wtMediaName, e);
            return "";
        } catch (final AmbiguousIdentifierException e) {
            LOG.error("Webtrekk - more than on media found for " + wtMediaName, e);
            return "";
        }
        return urlBuilder.toString();
    }

    /**
     * Returns true if the current URL is a live URL, otherwise false
     *
     * @param serverName
     *            like prod.distrelec.ch or test.distrelec.ch
     * @return true if the current URL is a live URL, otherwise false
     */
    private boolean isNotLiveEnv(final String serverName) {
        return serverName.toLowerCase().contains("prod.") || serverName.toLowerCase().contains("test.") || serverName.toLowerCase().contains(".local")
                || serverName.toLowerCase().contains("localhost");
    }

    @Override
    public void prepareWebtrekkProductParams(final Model model, final CartData cartData) {

        if (cartData != null || (getCartFacade().hasSessionCart() && CollectionUtils.isNotEmpty(getCartFacade().getSessionCart().getEntries()))) {
            final StringBuilder wtProduct = new StringBuilder();
            final StringBuilder wtProductCost = new StringBuilder();
            final StringBuilder wtProductCategory = new StringBuilder();
            final StringBuilder wtProductQuantity = new StringBuilder();

            final CartData data = (cartData == null) ? getCartFacade().getSessionCart() : cartData;

            if (data != null && CollectionUtils.isNotEmpty(data.getEntries())) {
                for (final OrderEntryData entry : data.getEntries()) {
                    // WT Product
                    if (wtProduct.length() > 0 && !wtProduct.toString().endsWith(";")) {
                        wtProduct.append(";");
                    }
                    wtProduct.append(encodeToUTF8(entry.getProduct().getCode()));
                    wtProduct.append(" / ");
                    wtProduct.append(encodeToUTF8(entry.getProduct().getName()));

                    // WT Product Cost
                    if (wtProductCost.length() > 0 && !wtProductCost.toString().endsWith(";")) {
                        wtProductCost.append(";");
                    }
                    wtProductCost.append(formatPriceValue(entry.getTotalPrice()));

                    // WT Product Category
                    if (wtProductCategory.length() > 0 && !wtProductCategory.toString().endsWith(";")) {
                        wtProductCategory.append(";");
                    }

                    final DistManufacturerData manufacturerData = entry.getProduct().getDistManufacturer();
                    wtProductCategory.append(encodeToUTF8(manufacturerData != null ? manufacturerData.getName() : ""));

                    // WT Product Quantity
                    if (wtProductQuantity.length() > 0 && !wtProductQuantity.toString().endsWith(";")) {
                        wtProductQuantity.append(";");
                    }
                    wtProductQuantity.append(entry.getQuantity());
                }

                final WtProductParamsData productData = new WtProductParamsData();
                productData.setProduct(wtProduct.toString());
                productData.setProductCategory(wtProductCategory.toString());
                productData.setProductCost(wtProductCost.toString());
                productData.setProductCurrency(getStoreSessionFacade().getCurrentCurrency().getIsocode());
                productData.setProductOrderId(data.getCode());
                productData.setProductOrderValue(formatPriceValue(data.getSubTotal()));
                productData.setProductQuantity(wtProductQuantity.toString());
                productData.setProductStatus("conf");
                productData.setDiscountValue(calculateDiscountValue(data));
                productData.setShippingCosts(formatPriceValue(data.getDeliveryCost()));
                productData.setPaymentFee(formatPriceValue(data.getPaymentCost()));
                productData.setVat(formatPriceValue(data.getTotalTax()));
                productData.setTotalValue(formatPriceValue(data.getTotalPrice()));
                if (data.getErpVoucherInfoData() != null) {// Setting up Voucher value
                    productData.setVoucherValue(String.valueOf(data.getErpVoucherInfoData().getFixedValue().getValue().doubleValue()));
                }
                model.addAttribute("wtProductData", productData);
            }
        }
    }

    @Override
    public String encodeToUTF8(final String value) {
        return UriUtils.encodeFragment(value == null ? "" : value, "UTF-8");
    }

    @Override
    public List<WtBasicParameterData> getWtBasicParameters(final AbstractPageModel page) {
        final List<WtBasicParameterModel> params = new ArrayList<>();
        final List<WtBasicParameterData> parameters = new ArrayList<>();
        final CMSSiteModel site = getCmsSiteService().getCurrentSite();
        final List<WtBasicParameterModel> siteParams = site.getWtBasicParameters();
        final List<WtBasicParameterModel> pageParams = page.getWtBasicParameters();
        final HashMap<String, String> pageParamMap = new HashMap<>();

        for (final Iterator<WtBasicParameterModel> i = pageParams.iterator(); i.hasNext();) {
            final WtBasicParameterModel param = i.next();
            pageParamMap.put(param.getKey(), param.getValue());
        }
        for (final Iterator<WtBasicParameterModel> i = siteParams.iterator(); i.hasNext();) {
            final WtBasicParameterModel param = i.next();
            if (!pageParamMap.containsKey(param.getKey())) {
                params.add(param);
            }
        }
        params.addAll(pageParams);
        for (final Iterator<WtBasicParameterModel> i = params.iterator(); i.hasNext();) {
            final WtBasicParameterModel source = i.next();
            final WtBasicParameterData target = new WtBasicParameterData();
            getWtBasicParamPopulator().populate(source, target);
            parameters.add(target);
        }
        return parameters;
    }

    @Override
    public Collection<WtAdvancedParameterData> getWtAdvancedParameters(final AbstractPageModel page) {
        final List<WtAdvancedParameterModel> params = new ArrayList<>();
        final CMSSiteModel site = getCmsSiteService().getCurrentSite();
        final List<WtAdvancedParameterModel> siteParams = site.getWtAdvancedParameters();
        final List<WtAdvancedParameterModel> pageParams = page.getWtAdvancedParameters();
        final HashMap<String, String> pageParamMap = new HashMap<>();

        final HashMap<String, WtAdvancedParameterData> paramMap = new HashMap<>();

        for (final Iterator<WtAdvancedParameterModel> i = pageParams.iterator(); i.hasNext();) {
            final WtAdvancedParameterModel param = i.next();
            pageParamMap.put(param.getKey() + "_" + param.getId(), param.getValue());
        }
        for (final Iterator<WtAdvancedParameterModel> i = siteParams.iterator(); i.hasNext();) {
            final WtAdvancedParameterModel param = i.next();
            if (!pageParamMap.containsKey(param.getKey() + "_" + param.getId())) {
                params.add(param);
            }
        }
        params.addAll(pageParams);
        for (final Iterator<WtAdvancedParameterModel> i = params.iterator(); i.hasNext();) {
            final WtAdvancedParameterModel source = i.next();
            final WtAdvancedParameterData target = new WtAdvancedParameterData();
            getWtAdvancedParamPopulator().populate(source, target);
            if (paramMap.containsKey(target.getKey())) {
                paramMap.get(target.getKey()).addIdValue(source.getId(), source.getValue());
            } else {
                paramMap.put(target.getKey(), target);
            }
        }
        return paramMap.values();
    }

    @Override
    public void prepareWebtrekkOrderParams(final Model model, final OrderData data) {
        final StringBuilder wtProduct = new StringBuilder();
        final StringBuilder wtNewProduct = new StringBuilder();
        final StringBuilder wtProductCost = new StringBuilder();
        final StringBuilder wtProductCategory = new StringBuilder();
        final StringBuilder wtProductQuantity = new StringBuilder();

        if (data != null && CollectionUtils.isNotEmpty(data.getEntries())) {
            for (final OrderEntryData entry : data.getEntries()) {
                // WT Product
                if (wtProduct.length() > 0 && !wtProduct.toString().endsWith(";")) {
                    wtProduct.append(";");
                }
                wtProduct.append(encodeToUTF8(entry.getProduct().getCodeErpRelevant()));
                wtProduct.append(" / ");
                wtProduct.append(encodeToUTF8(entry.getProduct().getName()));

                // WT new product code
                if (wtNewProduct.length() > 0 && !wtNewProduct.toString().endsWith(";")) {
                    wtNewProduct.append(";");
                }
                wtNewProduct.append(encodeToUTF8(entry.getProduct().getCode()));

                // WT Product Cost
                if (wtProductCost.length() > 0 && !wtProductCost.toString().endsWith(";")) {
                    wtProductCost.append(";");
                }
                wtProductCost.append(formatPriceValue(entry.getTotalPrice()));

                // WT Product Category
                if (wtProductCategory.length() > 0 && !wtProductCategory.toString().endsWith(";")) {
                    wtProductCategory.append(";");
                }
                final DistManufacturerData manufacturerData = entry.getProduct().getDistManufacturer();
                wtProductCategory.append(encodeToUTF8(manufacturerData != null ? manufacturerData.getName() : ""));

                // WT Product Quantity
                if (wtProductQuantity.length() > 0 && !wtProductQuantity.toString().endsWith(";")) {
                    wtProductQuantity.append(";");
                }

                wtProductQuantity.append(entry.getQuantity());
            }

            final WtProductParamsData productData = new WtProductParamsData();
            productData.setProduct(wtProduct.toString());
            productData.setNewProduct(wtNewProduct.toString());
            productData.setProductCategory(wtProductCategory.toString());
            productData.setProductCost(wtProductCost.toString());
            productData.setProductCurrency(getStoreSessionFacade().getCurrentCurrency().getIsocode());
            productData.setProductOrderId(data.getCode());
            productData.setProductOrderValue(formatPriceValue(data.getSubTotal()));
            productData.setProductQuantity(wtProductQuantity.toString());
            productData.setProductStatus("conf");
            productData.setDiscountValue(calculateDiscountValue(data));
            productData.setShippingCosts(formatPriceValue(data.getDeliveryCost()));
            productData.setPaymentFee(formatPriceValue(data.getPaymentCost()));
            productData.setVat(formatPriceValue(data.getTotalTax()));
            productData.setTotalValue(formatPriceValue(data.getTotalPrice()));
            if (data.getErpVoucherInfoData() != null) {// Setting up Voucher value
                productData.setVoucherValue(String.valueOf(data.getErpVoucherInfoData().getFixedValue().getValue().doubleValue()));
            }
            model.addAttribute("wtProductData", productData);
        }
    }

    @Override
    public void addTeaserTrackingId(final Model model, final String teaserTrakingInstrument) {
        final StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.lowerCase(getCmsSiteService().getCurrentSite().getCountry().getIsocode()));
        builder.append(".");
        model.addAttribute(WT_TEASER_TRACKING_ID, builder.toString() + teaserTrakingInstrument);
        model.addAttribute(WT_TEASER_TRACKING_ID_LNV, builder.toString() + ONS_LNV);
        model.addAttribute(WT_TEASER_TRACKING_ID_ICO, builder.toString() + ONS_ICO);
        model.addAttribute(WT_TEASER_TRACKING_ID_FAF, builder.toString() + FAF_S_FCT);
        model.addAttribute(WT_TEASER_TRACKING_ID_MNV, builder.toString() + ONS_MNV);
    }

    @Override
    public String prepareActivatedEventsParam(final String value) {
        if (value != null && value.length() > 0) {
            final StringBuilder builder = new StringBuilder();
            final List<String> items = Arrays.asList(value.split("\\s*,\\s*"));
            for (final Iterator<String> it = items.iterator(); it.hasNext();) {
                final String item = it.next();
                builder.append("'");
                builder.append(item);
                if (it.hasNext()) {
                    builder.append("', ");
                } else {
                    builder.append("'");
                }
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    protected String calculateDiscountValue(final AbstractOrderData data) {
        BigDecimal totalDiscount = new BigDecimal("0.0");
        for (final OrderEntryData entry : data.getEntries()) {
            final PriceData listPrice = entry.getTotalListPrice();
            final PriceData yourPrice = entry.getTotalPrice();
            if (listPrice != null && yourPrice != null) {
                totalDiscount = totalDiscount.add(listPrice.getValue().subtract(yourPrice.getValue()));
            }
        }
        return formatPriceBigDecimalValue(totalDiscount);

    }

    protected String formatPriceValue(final PriceData priceData) {
        final DecimalFormat formatter = new DecimalFormat("0.00");
        return priceData == null ? "0.00" : formatter.format(priceData.getValue());
    }

    protected String formatPriceBigDecimalValue(final BigDecimal value) {
        final DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(value);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public DistB2BCartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(final DistB2BCartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public WtBasicParameterDataPopulator getWtBasicParamPopulator() {
        return wtBasicParamPopulator;
    }

    public void setWtBasicParamPopulator(final WtBasicParameterDataPopulator wtBasicParamPopulator) {
        this.wtBasicParamPopulator = wtBasicParamPopulator;
    }

    public WtAdvancedParameterDataPopulator getWtAdvancedParamPopulator() {
        return wtAdvancedParamPopulator;
    }

    public void setWtAdvancedParamPopulator(final WtAdvancedParameterDataPopulator wtAdvancedParamPopulator) {
        this.wtAdvancedParamPopulator = wtAdvancedParamPopulator;
    }
}
