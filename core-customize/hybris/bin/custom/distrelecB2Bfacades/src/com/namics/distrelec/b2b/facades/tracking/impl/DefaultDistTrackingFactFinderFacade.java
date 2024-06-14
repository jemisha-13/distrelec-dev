/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.tracking.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.facades.reco.impl.DefaultDistRecommendationFacade;
import com.namics.distrelec.b2b.facades.tracking.DistTrackingFactFinderFacade;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.tracking.FactFinderTrackingData;
import com.namics.hybris.ffsearch.data.tracking.FactFinderTrackingEventEnum;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;
import com.namics.hybris.ffsearch.service.FactFinderTrackingService;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Session.IS_STOREFRONT_REQUEST;

/**
 * Default implementation of {@link DistTrackingFactFinderFacade}.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class DefaultDistTrackingFactFinderFacade implements DistTrackingFactFinderFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistTrackingFactFinderFacade.class);

    private static final char PATH_SEPERATOR = '~';

    private static final String BLANK_STRING = "";

    public static final String FF_QUERY_SESSION_PARAMETER = "ffSearchQuery";

    public static final String FF_TRACKING_COOKIE = "f_fid";

    public static final String OCC_FF_TRACKING_PARAM = "sid";

    public static final String OCC_TRACKING_FLAG = "occTracking";

    @Autowired
    @Qualifier("distff.trackingService")
    private FactFinderTrackingService trackingService;

    @Autowired
    @Qualifier("distff.channelService")
    private FactFinderChannelService channelService;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Override
    public void trackProductDetailClick(final HttpServletRequest request, final ProductData productData) {
        final FactFinderTrackingData trackingData = new FactFinderTrackingData();
        addGeneralData(request, productData, trackingData, FactFinderTrackingEventEnum.CLICK);
        addDetailPageData(request, trackingData, productData);
        trackDataAs(Collections.singletonList(trackingData));
    }

    @Override
    public void trackLogin(final HttpServletRequest request, final CustomerData customerData) {
        try {
            final FactFinderTrackingData trackingData = new FactFinderTrackingData();
            addLoginData(request, customerData, trackingData, FactFinderTrackingEventEnum.LOGIN);
            trackDataAs(Collections.singletonList(trackingData));
        } catch (final Exception ex) {
            LOG.error("Error while tracking login:", ex);
        }
    }

    @Override
    public void trackAddToCartClick(final HttpServletRequest request, final OrderEntryData cartEntry, final Long addedQuantity) {
        if (addedQuantity != null && addedQuantity > 0) {
            final List<FactFinderTrackingData> trackingDataList = getTrackingData(request, getPriceData(cartEntry, addedQuantity),
                                                                                  FactFinderTrackingEventEnum.CART);
            if (StringUtils.isNotEmpty(request.getParameter(DistrelecfactfindersearchConstants.PAGE_TYPE_PARAMETER))) {
                for (final FactFinderTrackingData trackingData : trackingDataList) {
                    addDetailPageData(request, trackingData, cartEntry.getProduct());
                }
            }
            setQueryInSession(request, cartEntry);
            trackDataAs(trackingDataList);
        }
    }

    private void setQueryInSession(final HttpServletRequest request, final OrderEntryData cartEntry) {
        final String query = request.getParameter(DistrelecfactfindersearchConstants.SEARCH_QUERY_PARAMETER_NAME);
        if (!StringUtils.isBlank(query)) {
            if (request.getSession() != null && request.getSession().getAttribute(FF_QUERY_SESSION_PARAMETER) == null) {
                final Map<String, String> ffQuery = new HashMap<>();
                ffQuery.put(cartEntry.getProduct().getCode(), query);
                request.getSession().setAttribute(FF_QUERY_SESSION_PARAMETER, ffQuery);
            } else if (request.getSession().getAttribute(FF_QUERY_SESSION_PARAMETER) != null) {
                final Map<String, String> ffQuery = (Map<String, String>) request.getSession().getAttribute(FF_QUERY_SESSION_PARAMETER);
                ffQuery.put(cartEntry.getProduct().getCode(), query);
            }
        }
    }

    private String getQueryFromSession(String code, HttpServletRequest request) {
        Map<String, String> ffQuery = getFFQueries(request);
        if (ffQuery == null) {
            return BLANK_STRING;
        }
        return ffQuery.getOrDefault(code, BLANK_STRING);
    }

    private Map<String, String> getFFQueries(HttpServletRequest request) {
        if (isStorefrontRequest() && request.getSession() != null) {
            return (Map<String, String>) request.getSession().getAttribute(FF_QUERY_SESSION_PARAMETER);
        } else {
            return sessionService.getAttribute(FF_QUERY_SESSION_PARAMETER);
        }
    }

    private void clearQueryFromSession(final HttpServletRequest request) {
        request.getSession().removeAttribute(FF_QUERY_SESSION_PARAMETER);
    }

    @Override
    public void trackAddBulkToCartClick(final HttpServletRequest request, final List<OrderEntryData> cartEntries) {
        if (!cartEntries.isEmpty()) {
            for (final OrderEntryData cartEntry : cartEntries) {
                final List<FactFinderTrackingData> trackingData = getTrackingData(request, getPriceData(cartEntry, cartEntry.getQuantity()),
                                                                                  FactFinderTrackingEventEnum.CART);
                trackDataAs(trackingData);
            }
        }
    }

    @Override
    public void trackRecommendedProductClick(final HttpServletRequest request, final ProductData productData) {
        final FactFinderTrackingData trackingData = new FactFinderTrackingData();
        addGeneralData(request, productData, trackingData, FactFinderTrackingEventEnum.RECOMMENDATION_CLICK);
        trackDataAs(Collections.singletonList(trackingData));
    }

    @Override
    public void trackOrderConfirmationPage(final HttpServletRequest request, final OrderData orderData) {
        final List<FactFinderTrackingData> trackingData = getTrackingData(request, getPriceData(orderData), FactFinderTrackingEventEnum.CHECKOUT);
        trackDataAs(trackingData);
        clearQueryFromSession(request);
    }

    @Override
    public void trackSearchFeedback(final HttpServletRequest request, final ProductData productData) {
        // currently not supported, would be a CR - checked with nando from spec
        // team
        // trackDataAs(trackingData, FactFinderTrackingEventEnum.FEEDBACK);
    }

    private void addLoginData(final HttpServletRequest request, final CustomerData customerData, final FactFinderTrackingData trackingData,
                              final FactFinderTrackingEventEnum eventType) {
        if (customerData == null) {
            LOG.error("Unable to send tracking data to FactFinder. No productData available.");
            return;
        }

        trackingData.setChannel(getChannelService().getCurrentFactFinderChannel());
        trackingData.setSid(getSid(request));
        if (!getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            final String userid = encodeData(getUserService().getCurrentUser().getUid());
            trackingData.setId(userid);
            trackingData.setUserId(userid);
        } else {
            trackingData.setUserId(encodeData((String) request.getSession().getAttribute("SPRING_SECURITY_LAST_USERNAME")));
        }
        trackingData.setMainId(request.getParameter(DefaultDistRecommendationFacade.MAIN_ID_PARAMETER_NAME));
        trackingData.setEvent(eventType);
    }

    private String encodeData(final String customerData) {
        return DistCryptography.toSHA256(customerData);
    }

    private void addGeneralData(final HttpServletRequest request, final ProductData productData, final FactFinderTrackingData trackingData,
                                final FactFinderTrackingEventEnum eventType) {
        if (productData == null) {
            LOG.error("Unable to send tracking data to FactFinder. No productData available.");
            return;
        }
        try {
            final String id = productData.getCode();
            trackingData.setId(id);
            trackingData.setChannel(getChannelService().getCurrentFactFinderChannel());
            trackingData.setSid(getSid(request));
            trackingData.setTitle(productData.getName());
            if (!getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
                trackingData.setUserId(encodeData(getUserService().getCurrentUser().getUid()));
            }
            trackingData.setMainId(request.getParameter(DefaultDistRecommendationFacade.MAIN_ID_PARAMETER_NAME));
            trackingData.setQuery(request.getParameter(DistrelecfactfindersearchConstants.SEARCH_QUERY_PARAMETER_NAME));
            if (StringUtils.isNotBlank(request.getParameter(DistrelecfactfindersearchConstants.ORIG_PAGE_SIZE))) {
                trackingData.setOrigPageSize(stringToInteger(request.getParameter(DistrelecfactfindersearchConstants.ORIG_PAGE_SIZE)));
            }
            if (StringUtils.isNotBlank(request.getParameter(DistrelecfactfindersearchConstants.POS_PARAMETER_NAME))) {
                trackingData.setPos(stringToInteger(request.getParameter(DistrelecfactfindersearchConstants.POS_PARAMETER_NAME)));
            }
            if (StringUtils.isNotBlank(request.getParameter(DistrelecfactfindersearchConstants.ORIG_PAGE_SIZE))) {
                trackingData.setOrigPageSize(stringToInteger(request.getParameter(DistrelecfactfindersearchConstants.ORIG_PAGE_SIZE)));
            }
            if (StringUtils.isNotBlank(request.getParameter(DistrelecfactfindersearchConstants.PRODUCT_PRICE))) {
                trackingData.setPrice(stringToDouble(request.getParameter(DistrelecfactfindersearchConstants.PRODUCT_PRICE)));
            }
            trackingData.setEvent(eventType);
        } catch (final Exception ex) {
            LOG.error("Error while tracking FF data:", ex);
        }
    }

    private String getSid(HttpServletRequest request) {
        return isStorefrontRequest() ? getSidFromCookie(request) : getSidFromParam(request);
    }

    private String getSidFromParam(HttpServletRequest request) {
        return request.getParameter(OCC_FF_TRACKING_PARAM);
    }

    private String getSidFromCookie(HttpServletRequest request) {
        String sid = null;
        if (request.getCookies() != null) {
            sid = Stream.of(request.getCookies())
                        .filter(cookie -> cookie.getName().equalsIgnoreCase(FF_TRACKING_COOKIE))
                        .map(cookie -> cookie.getValue())
                        .findFirst().orElse(null);
        }
        return StringUtils.isNotBlank(sid) ? sid :  request.getSession().getId();
    }

    private Integer stringToInteger(final String stringValue) {
        Integer intValue = null;
        try {
            intValue = Integer.valueOf(stringValue);
        } catch (final NumberFormatException ex) {
            LOG.error("NumberFormatException occurred while trying to convert String to Integer:", ex);
        }
        return intValue;
    }

    private Double stringToDouble(final String stringValue) {
        Double doubleValue = null;
        try {
            doubleValue = Double.valueOf(stringValue);
        } catch (final NumberFormatException ex) {
            LOG.error("NumberFormatException occurred while trying to convert String to Double:", ex);
        }
        return doubleValue;
    }

    private void addDetailPageData(final HttpServletRequest request, final FactFinderTrackingData trackingData, final ProductData productData) {
        String query = request.getParameter(DistrelecfactfindersearchConstants.SEARCH_QUERY_PARAMETER_NAME);
        final String productInfoQuery = request.getParameter(DistrelecfactfindersearchConstants.PRODUCT_INFORMATION_QUERY_PARAMETER_NAME);
        final String trackQuery = request.getParameter(DistrelecfactfindersearchConstants.TRACK_QUERY_PARAMETER_NAME);

        if (StringUtils.isNotBlank(trackQuery)) {
            query = trackQuery;
        } else if (StringUtils.isBlank(query) && StringUtils.isBlank(productInfoQuery)) {
            query = "*";
        } else if (StringUtils.isNotBlank(productInfoQuery) && (query != null && query.equals("*"))) {
            query = productInfoQuery;
        } else if (StringUtils.isBlank(query)) {
            query = productInfoQuery;
        }
        query = query.replace(PATH_SEPERATOR, '/');
        trackingData.setQuery(query);

        Integer pos = getRequestParamAsInt(DistrelecfactfindersearchConstants.POS_PARAMETER_NAME, request);
        if (pos == null || pos == 0) {
            pos = Integer.valueOf(1);
        }
        trackingData.setPos(pos);

        // Fallback: if origPos is not provided, pos is used instead.
        Integer origPos = getRequestParamAsInt(DistrelecfactfindersearchConstants.ORIG_POS_PARAMETER_NAME, request);
        if (origPos == null || origPos == 0) {
            origPos = pos;
        }
        trackingData.setOrigPos(origPos);

        Integer origPageSize = getRequestParamAsInt(DistrelecfactfindersearchConstants.ORIG_PAGE_SIZE_PARAMETER_NAME, request);
        if (origPageSize == null || origPageSize == 0) {
            origPageSize = Integer.valueOf(1);
        }
        trackingData.setOrigPageSize(origPageSize);

        Integer pageSize = getRequestParamAsInt(DistrelecfactfindersearchConstants.PAGE_SIZE_PARAMETER_NAME, request);
        if (pageSize == null || pageSize == 0) {
            pageSize = Integer.valueOf(1);
        }
        trackingData.setPageSize(pageSize);

        Integer page = getRequestParamAsInt(DistrelecfactfindersearchConstants.PAGE_PARAMETER_NAME, request);
        if (page != null && page != 0) {
            trackingData.setPage(page);
        }
        else if (origPageSize.intValue() != 0) {
            trackingData.setPage(Integer.valueOf((pos.intValue() / origPageSize.intValue()) + 1));
        } else {
            trackingData.setPage(1);
        }

        final String productPrice = request.getParameter(DistrelecfactfindersearchConstants.PRODUCT_PRICE);
        if (productPrice == null) {
            trackingData.setPrice(Optional.ofNullable(productData).map(ProductData::getPrice).map(PriceData::getValue).map(BigDecimal::doubleValue)
                                          .orElse(new Double("0.00")));
        } else {
            trackingData.setPrice(Double.valueOf(productPrice));
        }
        trackingData.setSimi(getRequestParamAsFloat(DistrelecfactfindersearchConstants.SIMI_PARAMETER_NAME, request));
        trackingData.setCampaign(StringUtils.isNotBlank(request.getParameter(DistrelecfactfindersearchConstants.CAMPAIGN_PARAMETER))
                                                                                                                                     ? request.getParameter(DistrelecfactfindersearchConstants.CAMPAIGN_PARAMETER)
                                                                                                                                     : BLANK_STRING);
    }

    private List<FactFinderTrackingData> getTrackingData(final HttpServletRequest request, final Map<ProductData, Map<Long, PriceData>> productPriceData,
                                                         final FactFinderTrackingEventEnum eventType) {
        final List<FactFinderTrackingData> trackingDataList = Lists.newArrayList();
        for (final Entry<ProductData, Map<Long, PriceData>> product : productPriceData.entrySet()) {
            if (MapUtils.isNotEmpty(product.getValue())) {
                final FactFinderTrackingData trackingData = new FactFinderTrackingData();
                final Entry<Long, PriceData> priceData = product.getValue().entrySet().iterator().next();
                addGeneralData(request, product.getKey(), trackingData, eventType);
                if (eventType.equals(FactFinderTrackingEventEnum.CHECKOUT) && product.getKey().getCode() != null) {
                    trackingData.setQuery(getQueryFromSession(product.getKey().getCode(), request));
                }
                // add count & price
                final Long count = priceData.getKey();
                final PriceData price = priceData.getValue();
                if (count != null && price != null) {
                    trackingData.setCount(Integer.valueOf(count.intValue()));
                    trackingData.setPrice(Double.valueOf(price.getValue().doubleValue()));
                    trackingDataList.add(trackingData);
                }
            }
        }
        return trackingDataList;
    }

    private void trackDataAs(final List<FactFinderTrackingData> trackingData) {
        if (isStorefrontRequest()) {
            trackDataAsync(trackingData);
        } else {
            trackDataBlocking(trackingData);
        }
    }

    private boolean isStorefrontRequest() {
        return BooleanUtils.toBoolean((Boolean) sessionService.getAttribute(IS_STOREFRONT_REQUEST));
    }

    private void trackDataAsync(final List<FactFinderTrackingData> trackingData) {
        for (final FactFinderTrackingData data : getValidTrackingData(trackingData)) {
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(createTrackingRunnable(data));
            executorService.shutdown();
        }
    }

    private void trackDataBlocking(final List<FactFinderTrackingData> trackingData) {
        for (final FactFinderTrackingData data : getValidTrackingData(trackingData)) {
            try {
                final boolean success = trackingService.trackData(data);
                if (!success) {
                    LOG.error("Tracking information was not successfully added for Shop event [" + data.getEvent() + "]");
                }
                // BEGIN SUPPRESS WARNING
            } catch (final Exception e) {
                // END SUPPRESS WARNING
                LOG.error("Failed sending tracking data [{}]", data, e);
            }
        }
    }

    private List<FactFinderTrackingData> getValidTrackingData(final List<FactFinderTrackingData> trackingData) {
        if (CollectionUtils.isEmpty(trackingData)) {
            return Collections.EMPTY_LIST;
        }

        final List<FactFinderTrackingData> validTrackingData = new ArrayList<>();
        for (final FactFinderTrackingData data : trackingData) {
            if (StringUtils.isBlank(data.getId())) {
                continue;
            }

            if (StringUtils.isBlank(data.getChannel())) {
                continue;
            }

            if (StringUtils.isBlank(data.getSid())) {
                continue;
            }

            if (data.getEvent() == null) {
                continue;
            }

            validTrackingData.add(data);
        }

        return validTrackingData;
    }

    private Map<ProductData, Map<Long, PriceData>> getPriceData(final OrderData order) {
        final Map<ProductData, Map<Long, PriceData>> orderedProducts = Maps.newHashMap();
        if (order != null && CollectionUtils.isNotEmpty(order.getEntries())) {
            for (final OrderEntryData orderEntry : order.getEntries()) {
                final ProductData product = orderEntry.getProduct();
                final Long count = orderEntry.getQuantity();
                final PriceData price = orderEntry.getTotalPrice();
                if (product != null && count != null && price != null) {
                    final Map<Long, PriceData> priceCount = Maps.newHashMap();
                    priceCount.put(count, price);
                    orderedProducts.put(product, priceCount);
                }
            }
        }
        return orderedProducts;
    }

    private Map<ProductData, Map<Long, PriceData>> getPriceData(final OrderEntryData entry, final Long addedQuantity) {
        final Map<ProductData, Map<Long, PriceData>> addedToCart = Maps.newHashMap();

        if (entry != null && addedQuantity != null) {
            final BigDecimal addedPriceValue = entry.getBasePrice().getValue().multiply(BigDecimal.valueOf(addedQuantity.longValue()));
            final PriceData price = getPriceDataFactory().create(entry.getBasePrice().getPriceType(), addedPriceValue, entry.getBasePrice().getCurrencyIso());

            if (price != null) {
                final Map<Long, PriceData> priceCount = Maps.newHashMap();
                priceCount.put(addedQuantity, price);
                addedToCart.put(entry.getProduct(), priceCount);
                return addedToCart;
            }
        }
        return addedToCart;
    }

    private OrderEntryData getOrderEntryForProductWithCode(final OrderData orderData, final String productCode) {
        for (final OrderEntryData entry : orderData.getEntries()) {
            if (entry.getProduct().getCode().equals(productCode)) {
                return entry;
            }
        }
        return null;
    }

    private Runnable createTrackingRunnable(final FactFinderTrackingData data) {
        final JaloSession session = JaloSession.getCurrentSession();
        final Tenant tenant = Registry.getCurrentTenant();
        final Runnable trackingRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    Registry.setCurrentTenant(tenant);
                    session.activate();
                    final boolean success = trackingService.trackData(data);
                    if (!success) {
                        LOG.error("Tracking information was not successfully added for Shop event [" + data.getEvent() + "]");
                    }
                    // BEGIN SUPPRESS WARNING
                } catch (final Exception e) {
                    // END SUPPRESS WARNING
                    LOG.error("Failed sending tracking data [{}]", data, e);
                } finally {
                    JaloSession.deactivate();
                    Registry.unsetCurrentTenant();
                }
            }
        };
        return trackingRunnable;
    }

    private Integer getRequestParamAsInt(final String param, final HttpServletRequest request) {
        return NumberUtils.createInteger(StringUtils.isNotBlank(request.getParameter(param)) ? request.getParameter(param) : "0");
    }

    private Float getRequestParamAsFloat(final String param, final HttpServletRequest request) {
        return NumberUtils.createFloat(StringUtils.isNotBlank(request.getParameter(param)) ? request.getParameter(param) : "0");
    }

    public FactFinderTrackingService getTrackingService() {
        return trackingService;
    }

    public void setTrackingService(final FactFinderTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public FactFinderChannelService getChannelService() {
        return channelService;
    }

    public void setChannelService(final FactFinderChannelService channelService) {
        this.channelService = channelService;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public UserService getUserService() {
        return userService;
    }

}
