package com.namics.distrelec.b2b.core.inout.erp.converters.response;

import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import com.namics.distrelec.b2b.core.inout.erp.exception.MoqConversionException;
import com.namics.distrelec.b2b.core.inout.erp.service.UpdateOrderEntryService;
import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class OrderCalculationResponseCommonMappingTest {
    @Mock
    private ModelService modelService;
    @Mock
    private ProductService productService;
    @Mock
    private Converter<OrderEntryResponse, AbstractOrderEntryModel> orderEntryResponseConverter;
    @Mock
    private Converter<OrderEntryResponse, SubOrderEntryModel> subOrderEntryResponseConverter;
    @InjectMocks
    private OrderCalculationResponseCommonMapping orderEntriesMapping;
    @Mock
    private UpdateOrderEntryService updateOrderEntryService;

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        doNothing().when(modelService).save(any(AbstractOrderEntryModel.class));
        doNothing().when(modelService).save(any(SubOrderEntryModel.class));
        doNothing().when(modelService).save(any(AbstractOrderModel.class));
        doNothing().when(modelService).saveAll(AbstractOrderEntryModel.class);
    }

    private AbstractOrderModel createBaseCart(){
        AbstractOrderModel cart =  mock(AbstractOrderModel.class);
        when(cart.getNewOrderEntries()).thenReturn(createBaseCartItems());
        when(cart.getPk()).thenReturn(PK.BIG_PK);
        return cart;
    }

    private List<AbstractOrderEntryModel> createBaseCartItems(){
        final ProductModel productModel = new ProductModel();
        productModel.setCode("123456");

        final AbstractOrderEntryModel orderItem = new OrderEntryModel();
        orderItem.setProduct(productModel);
        orderItem.setCustomerReference("Ref:001");
        orderItem.setQuotationId("QUO:001");
        orderItem.setLineNumber("000001");
        return Stream.of(orderItem).collect(Collectors.toList());
    }

    private OrderEntryResponse createBaseSAPOrderEntry(){
        OrderEntryResponse entry = new OrderEntryResponse();
        entry.setMoq(null);
        entry.setMaterialNumber("123456");
        entry.setQuotationId("QUO:001");
        entry.setQuotationItem("000001");
        entry.setHigherLevelItemReason("M");
        entry.setItemNumber("0001");
        //Skip IF11 call
        entry.setHigherLevelItem("000000");
        return entry;
    }

    @Test
    public void testDefaultNonParentOrderPath() throws CalculationException {
        final AbstractOrderModel baseCart = createBaseCart();
        final OrderEntryResponse orderEntry = createBaseSAPOrderEntry();
        final List<OrderEntryResponse> baseSAPOrderEntries = Stream.of(orderEntry).collect(Collectors.toList());
        orderEntriesMapping.fillOrderEntries(baseSAPOrderEntries, baseCart);
    }

    @Test
    public void throwExceptionWhenMoqIsNotNull() throws CalculationException {
        final AbstractOrderModel baseCart = createBaseCart();
        final OrderEntryResponse orderEntry = createBaseSAPOrderEntry();
        orderEntry.setMoq(10D);
        orderEntry.setOrderQuantity(5);
        orderEntry.setMaterialNumber("30115142");

        final List<OrderEntryResponse> baseSAPOrderEntries = Stream.of(orderEntry).collect(Collectors.toList());

        thrownException.expect(MoqConversionException.class);
        orderEntriesMapping.fillOrderEntries(baseSAPOrderEntries, baseCart);
    }
}