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
package com.namics.distrelec.b2b.storefront.controllers.misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.product.data.PickupStockLevelData;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageController;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Controller for checking the availability of products.
 */
@Controller
public class AvailabilityController extends AbstractPageController {

    protected static final Logger LOG = LogManager.getLogger(AvailabilityController.class);

    public static final String PARAM_PRODUCT_CODES = "productCodes";

    public static final String PARAM_DETAIL_INFO = "detailInfo";

    public static final String PARAM_USE_CACHE = "useCache";

    public static final String ATTR_AVAILABILITY_DATA = "availabilityData";

    private static final String PARAM_WAREHOUSE_CODE = "warehouseCode";

    public static final String ATTR_AVAILABILITY_STATE = "availabilityState";

    public static final String ATTR_AVAILABILITY_DATE = "availabilityDate";

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    @Qualifier("erp.availabilityService")
    private AvailabilityService availabilityService;

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(value = "/availability", method = RequestMethod.GET, produces = "application/json")
    public String getAvailabilityForProductCode(@RequestParam(PARAM_PRODUCT_CODES) final List<String> productCodes,
                                                @RequestParam(value = PARAM_DETAIL_INFO, required = false, defaultValue = "false") final Boolean detailInfo,
                                                @RequestParam(value = PARAM_USE_CACHE, required = false, defaultValue = "true") final Boolean useCache,
                                                final Model model) {

        // Limit call to availability, truncate list to limit.availability.to
        final int limitAvailabilityTo = Math.max(DEFAULT_SEARCH_MAX_SIZE,
                                                 configurationService.getConfiguration().getInt("limit.availability.to", DEFAULT_SEARCH_MAX_SIZE));
        if (productCodes.size() > limitAvailabilityTo) {
            productCodes.subList(limitAvailabilityTo, productCodes.size()).clear();
        }
        final Map<String, Integer> requestedQuantities = new LinkedHashMap<>();
        String[] tab = null;
        for (final String productCodeQuantity : productCodes) {
            tab = productCodeQuantity.split(";");
            final String productCode = normalizeProductCode(tab[0]);
            final Integer productRequestedQuantity = tab.length == 2 && tab[1] != null ? Integer.valueOf(tab[1]) : Integer.valueOf(1);
            if (null != productCode) {
                requestedQuantities.put(productCode, productRequestedQuantity);
            }
        }

        final List<ProductAvailabilityData> availabilityData = getProductFacade().getAvailability(requestedQuantities, detailInfo, useCache);

        model.addAttribute(ATTR_AVAILABILITY_DATA, availabilityData);
        return ControllerConstants.Views.Fragments.Product.Availability;
    }

    @RequestMapping(value = "/availability/pickup", method = RequestMethod.GET, produces = "application/json")
    public String getAvailabilityDateForWarehouseCode(@RequestParam(PARAM_WAREHOUSE_CODE) final String warehouseCode, final Model model) {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        Boolean orderAvailabilityState = Boolean.TRUE;
        Date orderAvailabilityDate = new Date(); // Today

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // 10 days is the latest date
        calendar.add(Calendar.DATE, 10);
        final Date latestDate = calendar.getTime();

        final Map<String, Integer> productCodesQuantity = new LinkedHashMap<>();

        // add all products
        for (final OrderEntryData orderEntryData : cartData.getEntries()) {
            productCodesQuantity.put(orderEntryData.getProduct().getCode(), Integer.valueOf(orderEntryData.getQuantity().intValue()));
        }

        final List<ProductAvailabilityData> availabilities = getProductFacade().getAvailability(productCodesQuantity, Boolean.TRUE);

        // For each product
        for (final ProductAvailabilityData availability : availabilities) {
            int neededQuantity = availability.getRequestedQuantity().intValue();

            // Enough stock for imediate pickup
            if (availability.getStockLevelPickup() != null) {
                int pickUpAvailable = 0;
                for (final PickupStockLevelData pickupStockLevelData : availability.getStockLevelPickup()) {
                    if (warehouseCode.equals(pickupStockLevelData.getWarehouseCode())) {
                        pickUpAvailable = pickupStockLevelData.getStockLevel().intValue();
                        continue;
                    }
                }
                if (pickUpAvailable >= neededQuantity) {
                    // we don't change the AvailabilityState
                    continue;
                }
                neededQuantity -= pickUpAvailable;
            }

            // NOT available immediately
            orderAvailabilityState = Boolean.FALSE;

            // Reorder
            if (availability.getBackorderQuantity() != null && availability.getBackorderQuantity().intValue() >= neededQuantity) {
                // Is it the latest availability?
                if (orderAvailabilityDate.before(availability.getBackorderDeliveryDate())) {
                    orderAvailabilityDate = availability.getBackorderDeliveryDate();
                    continue;
                }
            }

            // It is the latest date we ca have (10 days)
            orderAvailabilityDate = latestDate;
            break;
        }

        // DISTRELEC-8303
        final Calendar c = Calendar.getInstance();
        c.setTime(orderAvailabilityDate);
        final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SATURDAY) {
            c.add(Calendar.DATE, 2);
            orderAvailabilityDate = c.getTime();
        }

        // Set the right format
        final DateFormat dateFormat = new SimpleDateFormat(getMessageSource().getMessage("text.store.dateformat", null, getI18nService().getCurrentLocale()),
                                                           getI18nService().getCurrentLocale());

        // Add attributes to model
        model.addAttribute(ATTR_AVAILABILITY_STATE, orderAvailabilityState);
        model.addAttribute(ATTR_AVAILABILITY_DATE, dateFormat.format(orderAvailabilityDate));

        return ControllerConstants.Views.Fragments.Checkout.PickupAvailability;
    }

    public DistCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(final DistCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public AvailabilityService getAvailabilityService() {
        return availabilityService;
    }

    public void setAvailabilityService(final AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

}
