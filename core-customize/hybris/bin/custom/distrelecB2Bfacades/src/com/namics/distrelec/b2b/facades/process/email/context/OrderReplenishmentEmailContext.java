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
package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Email context (velocity) for order replenishment.
 */
public class OrderReplenishmentEmailContext extends AbstractDistEmailContext {
    private Converter<CartToOrderCronJobModel, ScheduledCartData> scheduledCartConverter;
    private ScheduledCartData scheduledCartData;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof ReplenishmentProcessModel) {
            final CartToOrderCronJobModel cartToOrderCronJobModel = ((ReplenishmentProcessModel) businessProcessModel).getCartToOrderCronJob();
            this.setScheduledCartData(getScheduledCartConverter().convert(cartToOrderCronJobModel));
        }
    }

    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof ReplenishmentProcessModel) {
            return ((ReplenishmentProcessModel) businessProcessModel).getCartToOrderCronJob().getCart().getSite();
        }

        return null;
    }

    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof ReplenishmentProcessModel) {
            return (CustomerModel) ((ReplenishmentProcessModel) businessProcessModel).getCartToOrderCronJob().getCart().getUser();
        }

        return null;
    }

    @Override
    protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof StoreFrontCustomerProcessModel) {
            return ((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer().getSessionLanguage();
        }
        return null;
    }

    public ScheduledCartData getScheduledCartData() {
        return scheduledCartData;
    }

    public void setScheduledCartData(final ScheduledCartData scheduledCartData) {
        this.scheduledCartData = scheduledCartData;
    }

    public Converter<CartToOrderCronJobModel, ScheduledCartData> getScheduledCartConverter() {
        return scheduledCartConverter;
    }

    public void setScheduledCartConverter(final Converter<CartToOrderCronJobModel, ScheduledCartData> scheduledCartConverter) {
        this.scheduledCartConverter = scheduledCartConverter;
    }
}
