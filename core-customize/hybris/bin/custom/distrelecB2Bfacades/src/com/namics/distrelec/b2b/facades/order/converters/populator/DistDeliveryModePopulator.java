/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.converters.populator;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.cart.DeliveryCostModel;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.hybris.toolbox.items.SessionUtil;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.converters.populator.DeliveryModePopulator;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class DistDeliveryModePopulator extends DeliveryModePopulator {

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private CartService cartService;

    @Autowired
    private DistUserService userService;

    @Override
    public void populate(final DeliveryModeModel source, final DeliveryModeData target) {
        super.populate(source, target);

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        if (source instanceof AbstractDistDeliveryModeModel) {
            target.setTranslationKey(((AbstractDistDeliveryModeModel) source).getTranslationKey());
            target.setTranslation(((AbstractDistDeliveryModeModel) source).getTranslation());
        }
        target.setSelectable(isSelectable(source));

        if (userService.getCurrentUser() instanceof B2BCustomerModel) {
            target.setDefaultDeliveryMode(isDefaultDeliveryMode(source));
        }

        if (cartService.hasSessionCart()) {
            Optional<Double> shippingCost = cartService.getSessionCart().getDeliveryCosts().stream()
                                                       .filter(cost -> cost.getDeliveryModeCode().equals(source.getCode()))
                                                       .findFirst()
                                                       .map(DeliveryCostModel::getCost);

            shippingCost.ifPresent(cost -> target.setShippingCost(priceDataFactory.create(PriceDataType.BUY,
                                                                                          BigDecimal.valueOf(cost),
                                                                                          commonI18NService.getCurrentCurrency()
                                                                                                           .getIsocode())));
        }
    }

    private boolean isDefaultDeliveryMode(DeliveryModeModel source) {
        return StringUtils.equals(source.getCode(), ((B2BCustomerModel) userService.getCurrentUser()).getDefaultDeliveryMethod());
    }

    private boolean isSelectable(DeliveryModeModel source) {
        return null != source.getCode() && SessionUtil.isShippingModeSelectable(source.getCode());
    }
}
