package com.namics.distrelec.b2b.core.mock.sap;

import com.distrelec.webservice.sap.v1.CustomerPriceArticles;
import com.distrelec.webservice.sap.v1.CustomerPriceRequest;
import com.distrelec.webservice.sap.v1.CustomerPriceResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Assert;
import org.junit.Test;

import static com.namics.distrelec.b2b.core.mock.sap.SIHybrisV1OutMockBuilder.customerPrice;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class SIHybrisV1OutMockBuilderTest {

    @Test
    public void testMockBuilder() throws Exception {
        String customerId = "customerId";
        String productCode0 = "productCode0";
        String productCode1 = "productCode1";

        ProductModel product0 = mockProduct(productCode0);
        ProductModel product1 = mockProduct(productCode1);

        SIHybrisV1Out siHybrisV1OutMock =
                new SIHybrisV1OutMockBuilder()
                        .expectIf07CustomerPrice(customerId, customerPrice(product0, 1L, 20))
                        .expectIf07CustomerPrice(customerId, customerPrice(product1, 1L, 18))
                        .build();

        CustomerPriceRequest request = new CustomerPriceRequest();
        request.setCustomerId(customerId);

        CustomerPriceArticles article1 = new CustomerPriceArticles();
        article1.setArticleNumber(productCode0);
        article1.setQuantity(1L);
        request.getArticles().add(article1);

        CustomerPriceResponse response = siHybrisV1OutMock.if07CustomerPrice(request);
        Assert.assertNotNull(response.getArticles());
        Assert.assertEquals(1, response.getArticles().size());
        Assert.assertEquals(productCode0, response.getArticles().get(0).getArticleNumber());
    }

    private ProductModel mockProduct(String productCode) {
        ProductModel product = mock(ProductModel.class);
        when(product.getCode()).thenAnswer(invocationOnMock -> productCode);
        return product;
    }
}
