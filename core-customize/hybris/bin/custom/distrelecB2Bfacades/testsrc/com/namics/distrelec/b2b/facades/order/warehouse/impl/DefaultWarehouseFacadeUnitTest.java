package com.namics.distrelec.b2b.facades.order.warehouse.impl;

import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWarehouseFacadeUnitTest {

    @Mock
    private CMSSiteService cmsSiteService;

    @Mock
    private DistrelecProductFacade distrelecProductFacade;

    @Mock
    private DistCommerceCommonI18NService i18NService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Converter<WarehouseModel, WarehouseData> warehouseConverter;

    @Spy
    @InjectMocks
    private DefaultWarehouseFacade warehouseFacade;

    @Test
    public void testGetCheckoutPickupWarehousesForCurrentSite() {
        // given
        CMSSiteModel cmsSite = mock(CMSSiteModel.class);

        // when
        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSite);
        when(cmsSite.getCheckoutPickupWarehouses()).thenReturn(Set.of(mock(WarehouseModel.class), mock(WarehouseModel.class)));

        List<WarehouseData> result = warehouseFacade.getCheckoutPickupWarehousesForCurrSite();

        // then
        assertThat(result, hasSize(2));
    }

    @Test
    public void testGetPickupWarehousesAndCalculatePickupDate() {
        // given
        CartData cartData = mock(CartData.class);
        CMSSiteModel cmsSite = mock(CMSSiteModel.class);
        Locale locale = mock(Locale.class);

        WarehouseData warehouseData1 = new WarehouseData();
        WarehouseData warehouseData2 = new WarehouseData();
        warehouseData1.setCode("warehouse1");
        warehouseData2.setCode("warehouse2");
        CountryModel countryModel = mock(CountryModel.class);

        OrderEntryData orderEntry1 = new OrderEntryData();
        orderEntry1.setProduct(new ProductData());
        orderEntry1.getProduct().setCode("Product1");
        orderEntry1.setQuantity(2L);

        OrderEntryData orderEntry2 = new OrderEntryData();
        orderEntry2.setProduct(new ProductData());
        orderEntry2.getProduct().setCode("Product2");
        orderEntry2.setQuantity(3L);

        Map<String, Integer> productQuantityMap = new HashMap<>();
        productQuantityMap.put("Product1", 2);
        productQuantityMap.put("Product2", 3);

        Pair<Boolean, Date> booleanDatePair1 = Pair.of(Boolean.TRUE, new Date());
        Pair<Boolean, Date> booleanDatePair2 = Pair.of(Boolean.TRUE, new Date());

        // when
        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSite);
        when(cmsSite.getCountry()).thenReturn(countryModel);
        when(countryModel.getIsocode()).thenReturn("CH");
        doReturn(List.of(warehouseData1, warehouseData2)).when(warehouseFacade).getCheckoutPickupWarehousesForCurrSite();
        when(cartData.getEntries()).thenReturn(Arrays.asList(orderEntry1, orderEntry2));
        when(distrelecProductFacade.getPickupAvailabilityDate(productQuantityMap, "warehouse1")).thenReturn(booleanDatePair1);
        when(distrelecProductFacade.getPickupAvailabilityDate(productQuantityMap, "warehouse2")).thenReturn(booleanDatePair2);
        when(i18NService.getCurrentLocale()).thenReturn(locale);
        when(locale.getLanguage()).thenReturn("de");
        when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class))).thenReturn("MM/dd/yyyy");

        List<WarehouseData> result = warehouseFacade.getPickupWarehousesAndCalculatePickupDate(cartData);

        // then
        assertThat(result.get(0).getCode(), equalTo("warehouse1"));
        assertThat(result.get(1).getCode(), equalTo("warehouse2"));
        assertThat(result.get(0).getAvailableForImmediatePickup(), is(true));
        assertThat(result.get(1).getAvailableForImmediatePickup(), is(true));
    }
}
