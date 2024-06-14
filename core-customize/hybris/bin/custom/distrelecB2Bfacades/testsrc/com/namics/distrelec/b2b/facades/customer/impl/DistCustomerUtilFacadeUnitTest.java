package com.namics.distrelec.b2b.facades.customer.impl;

import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistCustomerUtilFacadeUnitTest {

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Mock
    private UserService userService;

    @InjectMocks
    private DistCustomerUtilFacade distCustomerUtilFacade;

    private static final String EXPORT_SHOP = "7801";

    private static final String US_ISO_CODE = "USD";

    @Test
    public void testSkipPriceTrueScenario() {
        // given
        boolean isExportShop = true;
        boolean skipPriceSetting = true;
        boolean isCurrencyUSD = true;
        boolean hasActiveOnlinePrice = false;

        // when
        prepareData(isExportShop, skipPriceSetting, isCurrencyUSD, hasActiveOnlinePrice);

        boolean result = distCustomerUtilFacade.skipPrice();

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testSkipPriceFalseScenarioShopNotExport() {
        // given
        boolean isExportShop = false;
        boolean skipPriceSetting = true;
        boolean isCurrencyUSD = true;
        boolean hasActiveOnlinePrice = false;

        // when
        prepareData(isExportShop, skipPriceSetting, isCurrencyUSD, hasActiveOnlinePrice);

        boolean result = distCustomerUtilFacade.skipPrice();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testSkipPriceFalseScenarioSkipPriceSettingFalse() {
        // given
        boolean isExportShop = true;
        boolean skipPriceSetting = false;
        boolean isCurrencyUSD = true;
        boolean hasActiveOnlinePrice = false;

        // when
        prepareData(isExportShop, skipPriceSetting, isCurrencyUSD, hasActiveOnlinePrice);

        boolean result = distCustomerUtilFacade.skipPrice();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testSkipPriceFalseScenarioCurrencyNotUSD() {
        // given
        boolean isExportShop = true;
        boolean skipPriceSetting = true;
        boolean isCurrencyUSD = false;
        boolean hasActiveOnlinePrice = false;

        // when
        prepareData(isExportShop, skipPriceSetting, isCurrencyUSD, hasActiveOnlinePrice);

        boolean result = distCustomerUtilFacade.skipPrice();

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testSkipPriceFalseScenarioHasActiveOnlinePrice() {
        // given
        boolean isExportShop = true;
        boolean skipPriceSetting = true;
        boolean isCurrencyUSD = true;
        boolean hasActiveOnlinePrice = true;

        // when
        prepareData(isExportShop, skipPriceSetting, isCurrencyUSD, hasActiveOnlinePrice);

        boolean result = distCustomerUtilFacade.skipPrice();

        // then
        assertThat(result, is(false));
    }

    private void prepareData(boolean isExportShop, boolean skipPriceSetting, boolean isCurrencyUSD, boolean hasActiveOnlinePrice) {
        SalesOrgData salesOrgData = mock(SalesOrgData.class);
        B2BCustomerModel currentUser = mock(B2BCustomerModel.class);
        Configuration configuration = mock(Configuration.class);
        CurrencyModel currencyModel = mock(CurrencyModel.class);
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        when(storeSessionFacade.getCurrentSalesOrg()).thenReturn(salesOrgData);
        when(salesOrgData.getCode()).thenReturn(isExportShop ? EXPORT_SHOP : "7370");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(skipPriceSetting);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(currencyModel.getIsocode()).thenReturn(isCurrencyUSD ? US_ISO_CODE : "EUR");
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(b2BUnitModel.getOnlinePriceCalculation()).thenReturn(true);
        when(userService.isAnonymousUser(currentUser)).thenReturn(!hasActiveOnlinePrice);
    }
}
