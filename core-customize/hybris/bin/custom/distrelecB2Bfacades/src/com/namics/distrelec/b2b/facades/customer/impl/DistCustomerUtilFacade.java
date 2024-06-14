/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.customer.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author datneerajs, Namics AG
 * @since Distrelec 1.1 needed for DISTRELEC-7670
 */
public class DistCustomerUtilFacade {

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private UserService userService;

    private static final String EXPORT_SHOP = "7801";

    public boolean skipPrice() {
        return isCurrentShopisExportShop() && getSkipPriceSetting() && isCurrentCustomerCurrencyisUSD() && !hasActiveOnlinePrice();
    }

    private boolean getSkipPriceSetting() {
        return getConfigurationService().getConfiguration().getBoolean(DistConstants.PropKey.User.SKIP_PRICE, false);
    }

    private boolean isCurrentCustomerCurrencyisUSD() {
        return getCommonI18NService().getCurrentCurrency().getIsocode().equalsIgnoreCase("USD");
    }

    private boolean isCurrentShopisExportShop() {
        return (getStoreSessionFacade().getCurrentSalesOrg().getCode().equalsIgnoreCase(EXPORT_SHOP));
    }

    /**
     * Checks whether the customer has online price activated or not.
     *
     * @return {@code true} if the customer is not anonymous and has online price activated, otherwise {@code false}
     */
    private boolean hasActiveOnlinePrice() {
        final UserModel currentUser = getUserService().getCurrentUser();
        if (!getUserService().isAnonymousUser(currentUser)) {
            return BooleanUtils.isTrue(((B2BCustomerModel) currentUser).getDefaultB2BUnit().getOnlinePriceCalculation());
        }

        return false;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

}
