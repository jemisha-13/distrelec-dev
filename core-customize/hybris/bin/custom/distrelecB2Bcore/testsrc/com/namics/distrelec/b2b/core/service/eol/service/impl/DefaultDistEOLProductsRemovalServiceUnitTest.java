package com.namics.distrelec.b2b.core.service.eol.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.StockService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistEOLProductsRemovalServiceUnitTest {

    @InjectMocks
    private DefaultDistEOLProductsRemovalService distEOLProductsRemovalService;

    @Mock
    private DistProductDao distProductDao;

    @Mock
    private ModelService modelService;

    @Mock
    private StockService stockService;

    private ProductModel product1;

    private ProductModel product2;

    private List<ProductModel> products;

    @Before
    public void setUp() {
        product1 = mock(ProductModel.class);
        product2 = mock(ProductModel.class);
        products = Arrays.asList(product1, product2);

        when(distProductDao.findEOLProductsForRemoval(any(Date.class), any(Date.class), any(Integer.class))).thenReturn(products);
    }

    @Test
    public void testRemoveEOLProducts_productsWithoutReferences() {
        distEOLProductsRemovalService.removeEOLProducts(anyInt(), anyInt(), anyInt());

        verify(distProductDao, times(1)).findEOLProductsForRemoval(any(Date.class), any(Date.class), any(Integer.class));
        verify(modelService, times(8)).removeAll(anyListOf(ProductModel.class));
        verify(stockService, times(2)).getAllStockLevels(any(ProductModel.class));
    }

    @Test
    public void testRemoveEOLProducts_productsWithReferences() {
        ProductModel product3 = mock(ProductModel.class);
        DistSalesStatusModel salesStatus = mock(DistSalesStatusModel.class);
        DistSalesOrgProductModel salesOrgProduct1 = mock(DistSalesOrgProductModel.class);
        DistSalesOrgProductModel salesOrgProduct2 = mock(DistSalesOrgProductModel.class);
        DistSalesOrgProductModel salesOrgProduct3 = mock(DistSalesOrgProductModel.class);
        ProductReferenceModel productReference1 = mock(ProductReferenceModel.class);
        ProductReferenceModel productReference2 = mock(ProductReferenceModel.class);
        ProductReferenceModel productReference3 = mock(ProductReferenceModel.class);
        List<ProductReferenceModel> references1 = Collections.singletonList(productReference1);
        List<ProductReferenceModel> references2 = Collections.singletonList(productReference2);
        List<ProductReferenceModel> references3 = Collections.singletonList(productReference2);
        Calendar calendar = Calendar.getInstance();
        calendar.set(9999, Calendar.DECEMBER, 31);
        Date futureDate = calendar.getTime();
        calendar.set(1000, Calendar.DECEMBER, 31);
        Date pastDate = calendar.getTime();

        when(salesOrgProduct1.getLastModifiedErp()).thenReturn(futureDate);
        when(salesOrgProduct2.getLastModifiedErp()).thenReturn(pastDate);
        when(salesOrgProduct3.getLastModifiedErp()).thenReturn(futureDate);
        when(salesStatus.getCode()).thenReturn("60");
        when(salesOrgProduct1.getSalesStatus()).thenReturn(salesStatus);
        when(salesStatus.getCode()).thenReturn("61");
        when(salesOrgProduct2.getSalesStatus()).thenReturn(salesStatus);
        when(salesStatus.getCode()).thenReturn("62");
        when(salesOrgProduct3.getSalesStatus()).thenReturn(salesStatus);
        when(product1.getProductReferences()).thenReturn(references1);
        when(product1.getSalesOrgSpecificProductAttributes()).thenReturn(Set.of(salesOrgProduct1));
        when(product2.getProductReferences()).thenReturn(references2);
        when(product2.getSalesOrgSpecificProductAttributes()).thenReturn(Set.of(salesOrgProduct2));
        when(product3.getProductReferences()).thenReturn(references3);
        when(product3.getSalesOrgSpecificProductAttributes()).thenReturn(Set.of(salesOrgProduct3));

        distEOLProductsRemovalService.removeEOLProducts(anyInt(), anyInt(), anyInt());

        verify(distProductDao, times(1)).findEOLProductsForRemoval(any(Date.class), any(Date.class), any(Integer.class));
        verify(modelService, times(5)).removeAll(anyListOf(ProductModel.class));
        verify(stockService, times(1)).getAllStockLevels(any(ProductModel.class));
    }

}
