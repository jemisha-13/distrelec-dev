package com.namics.distrelec.b2b.core.mock.sap;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.distrelec.webservice.sap.v1.CustomerPriceArticles;
import com.distrelec.webservice.sap.v1.CustomerPriceArticlesResponse;
import com.distrelec.webservice.sap.v1.CustomerPriceRequest;
import com.distrelec.webservice.sap.v1.CustomerPriceResponse;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class If07CustomerPriceSapCallBuilder extends AbstractSapCallBuilder<SIHybrisV1Out, If07CustomerPrices, CustomerPriceRequest, CustomerPriceResponse> {

    @Override
    protected OngoingStubbing<CustomerPriceResponse> buildStubbing(SIHybrisV1Out mock, CustomerPriceRequest request) {
        try {
            return when(mock.if07CustomerPrice(request));
        } catch (P1FaultMessage e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean matchRequest(If07CustomerPrices call, CustomerPriceRequest request) {
        if (!call.getCustomerId().equals(request.getCustomerId())) {
            return false;
        }


        Iterator<If07CustomerPrice> priceEntryIt = call.getCustomerPrices().iterator();
        Iterator<CustomerPriceArticles> priceArticlesIt = request.getArticles().iterator();

        while (true) {
            if (priceEntryIt.hasNext() && priceArticlesIt.hasNext()) {
                If07CustomerPrice priceEntry = priceEntryIt.next();
                CustomerPriceArticles priceArticle = priceArticlesIt.next();

                // compare product codes and quantities
                return priceArticle.getArticleNumber().equals(priceEntry.getProductCode())
                               && priceArticle.getQuantity() == priceEntry.getQuantity();
            }

            boolean hasNext = !priceEntryIt.hasNext() && !priceArticlesIt.hasNext();
            if (hasNext) {
                return true;
            } else {
                break;
            }
        }
        return false;
    }

    @Override
    protected CustomerPriceResponse buildResponse(If07CustomerPrices call) {
        List<CustomerPriceArticlesResponse> articlesResponse =
                call.getCustomerPrices().stream()
                        .map(this::mockCustomerPriceArticlesResponse)
                        .collect(Collectors.toList());

        CustomerPriceResponse response = mock(CustomerPriceResponse.class);
        when(response.getArticles()).thenReturn(articlesResponse);
        return response;
    }

    private CustomerPriceArticlesResponse mockCustomerPriceArticlesResponse(If07CustomerPrice priceEntry) {
        CustomerPriceArticlesResponse response = mock(CustomerPriceArticlesResponse.class);
        when(response.getArticleNumber()).thenReturn(priceEntry.getProductCode());
        when(response.getPriceWithoutVat()).thenReturn(priceEntry.getPrice());
        when(response.getQuantity()).thenReturn(priceEntry.getQuantity());
        when(response.getPRICEPERX())
                .thenReturn(BigDecimal.ONE);
        when(response.getPRICEPERXBASEQT())
                .thenReturn(BigDecimal.ONE);
        when(response.getPRICEPERXUOMQT())
                .thenReturn(BigDecimal.ONE);
        return response;
    }
}
