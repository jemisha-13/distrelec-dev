package com.namics.distrelec.b2b.facades.backorder.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.data.DistAvailabilityData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBackOrderFacadeImplTest {

    @Mock
    private DistB2BCartFacade cartFacade;

    @Mock
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Mock
    private DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver;

    @Mock
    private DistrelecProductFacade productFacade;

    @Mock
    private ProductService productService;

    @Mock
    private DistB2BCartFacade b2bCartFacade;

    @Mock
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private ModelService modelService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private I18NService i18nService;

    @Mock
    CartModel cart;

    @InjectMocks
    private DefaultBackOrderFacadeImpl facade;

    @Before
    public void setUp() throws CalculationException {
        doNothing().when(b2bCartFacade).recalculateCart();
        when(b2bCartFacade.getSessionCartModel()).thenReturn(cart);
    }

    @Test
    public void testNormalPath() throws CommerceCartModificationException, CalculationException {
        final List<OrderEntryData> backOrderItems = createCartEntries(false);
        facade.updateBackOrderItems(backOrderItems);

        verify(b2bCartFacade, times(1)).removeFromCart(anyString());
        verify(b2bCartFacade, times(1)).recalculateCart();

    }

    private List<OrderEntryData> createCartEntries(final boolean profitable) {
        final OrderEntryData data = new OrderEntryData();
        data.setProduct(createProduct());
        data.setBackOrderProfitable(profitable);
        data.setBackOrderedQuantity(0L);
        data.setQuantity(5L);
        data.setAvailabilities(createAvailabilities());
        data.setEntryNumber(1);
        return new ArrayList<>(Collections.singletonList(data));
    }

    private List<DistAvailabilityData> createAvailabilities() {
        final DistAvailabilityData data = new DistAvailabilityData();
        data.setEstimatedDate(calculateDate(5));
        return new ArrayList<>(Collections.singletonList(data));
    }

    private ProductData createProduct() {
        ProductData data = new ProductData();
        data.setCode("12345678");
        return data;
    }

    private Date calculateDate(int amountOfDays) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, amountOfDays);
        return calendar.getTime();
    }
}
