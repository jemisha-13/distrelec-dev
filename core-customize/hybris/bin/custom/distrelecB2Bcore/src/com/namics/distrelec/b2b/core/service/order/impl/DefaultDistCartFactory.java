package com.namics.distrelec.b2b.core.service.order.impl;

import java.util.Date;

import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import org.springframework.beans.factory.annotation.Autowired;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.order.impl.DefaultCartFactory;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

/**
 * Overwritten DefaultCartFactory (because of missing getter Methods for beans) to predefine orderCode on cart.
 */
public class DefaultDistCartFactory extends DefaultCartFactory {

    private KeyGenerator keyGenerator;
    private ModelService modelService;
    private UserService userService;
    private CommonI18NService commonI18NService;
    private DistEProcurementService distEProcurementService;
    private BaseSiteService baseSiteService;
    private BaseStoreService baseStoreService;
    private DistSalesOrgService distSalesOrgService;
    private KeyGenerator guidKeyGenerator;

    @Autowired
    private PaymentOptionService paymentOptionService;

    @Override
    public CartModel createCart() {
        final CartModel cart = createCartInternal();
        getModelService().save(cart);
        return cart;
    }

    /**
     * Creates a new {@link CartModel} instance without persisting it.
     * 
     * @return {@link CartModel} - a fully initialized, not persisted {@link CartModel} instance
     */
    @Override
    protected CartModel createCartInternal() {
        final UserModel user = getCartUser();
        final CurrencyModel currency = getCommonI18NService().getCurrentCurrency();
        final String cartModelTypeCode = Config.getString(JaloSession.CART_TYPE, "Cart");
        final CartModel cart = getModelService().create(cartModelTypeCode);
        cart.setCode(String.valueOf(getKeyGenerator().generate()));
        cart.setUser(user);
        cart.setCurrency(currency);
        cart.setDate(new Date());
        // cart.setNet(Boolean.FALSE);
        cart.setNet(isNetUser(user));
        cart.setSite(getBaseSiteService().getCurrentBaseSite());
        cart.setStore(getBaseStoreService().getCurrentBaseStore());
        cart.setLanguage(getCommonI18NService().getCurrentLanguage());
        setDefaultPaymentMethodIfSet(user, cart);
        cart.setGuid(guidKeyGenerator.generate().toString());
        return cart;
    }

    private void setDefaultPaymentMethodIfSet(UserModel user, CartModel cart) {
        if(user instanceof B2BCustomerModel && ((B2BCustomerModel) user).getDefaultPaymentMethod() != null){
            AbstractDistPaymentModeModel defaultPaymentMode = paymentOptionService.getDefaultPaymentOptionForUser((B2BCustomerModel) user);
            cart.setPaymentMode(defaultPaymentMode);
        }
    }

    private Boolean isNetUser(final UserModel user) {
        final User userItem = modelService.getSource(user);
        return OrderManager.getInstance().getPriceFactory().isNetUser(userItem);
    }

    protected UserModel getCartUser() {
        if (distEProcurementService.isEProcurementCustomer()) {
            // do not persist cart onto user if he is a eprocurement customer.
            return userService.getAnonymousUser();
        }
        return userService.getCurrentUser();
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    @Override
    @Required
    public void setKeyGenerator(final KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Override
    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistEProcurementService getDistEProcurementService() {
        return distEProcurementService;
    }

    @Required
    public void setDistEProcurementService(final DistEProcurementService distEProcurementService) {
        this.distEProcurementService = distEProcurementService;
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

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    @Required
    public void setDistSalesOrgService(DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public KeyGenerator getGuidKeyGenerator()
    {
        return guidKeyGenerator;
    }

    public void setGuidKeyGenerator(final KeyGenerator guidKeyGenerator)
    {
        this.guidKeyGenerator = guidKeyGenerator;
    }
}
