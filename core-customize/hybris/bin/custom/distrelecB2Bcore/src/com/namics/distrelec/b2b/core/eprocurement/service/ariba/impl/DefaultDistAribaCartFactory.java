/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.ariba.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.model.eprocurement.AribaCartModel;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Additional CartFactory to create AribaCart.
 */
public class DefaultDistAribaCartFactory implements CartFactory {

    private KeyGenerator keyGenerator;
    private ModelService modelService;
    private UserService userService;
    private CommonI18NService commonI18NService;
    private BaseSiteService baseSiteService;
    private BaseStoreService baseStoreService;

    @Override
    public CartModel createCart() {
        final AribaCartModel cart = createAribaCartInternal();
        modelService.save(cart);
        return cart;
    }

    /**
     * Creates a new {@link AribaCartModel} instance without persisting it.
     * 
     * @return {@link AribaCartModel} - a fully initialized, not persisted {@link AribaCartModel} instance
     */
    protected AribaCartModel createAribaCartInternal() {
        final UserModel user = userService.getCurrentUser();
        final CurrencyModel currency = commonI18NService.getCurrentCurrency();
        final AribaCartModel cart = modelService.create(AribaCartModel.class);
        cart.setCode(String.valueOf(keyGenerator.generate()));
        // DISTRELEC-CHANGE: set orderCode on cart
        // cart.setOrderCode(String.valueOf(keyGenerator.generate()));
        cart.setUser(user);
        cart.setCurrency(currency);
        cart.setDate(new Date());
        cart.setNet(isNetUser(user));
        cart.setAllowEditBasket(Boolean.FALSE);
        cart.setSite(getBaseSiteService().getCurrentBaseSite());
        cart.setStore(getBaseStoreService().getCurrentBaseStore());
        return cart;
    }

    private Boolean isNetUser(final UserModel user) {
        final User userItem = modelService.getSource(user);
        final boolean result = OrderManager.getInstance().getPriceFactory().isNetUser(userItem);
        return Boolean.valueOf(result);
    }

    @Required
    public void setKeyGenerator(final KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService siteService) {
        this.baseSiteService = siteService;
    }

    protected BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public void setBaseStoreService(final BaseStoreService service) {
        this.baseStoreService = service;
    }
}
