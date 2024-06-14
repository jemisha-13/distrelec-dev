package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.OrderCancellationEmailProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.List;

public class OrderCancellationEmailContext extends CustomerEmailContext {

    private String orderNumber;

    private List<String> articleNumbers;

    private List<String> productNames;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof OrderCancellationEmailProcessModel) {
            final OrderCancellationEmailProcessModel orderCancellation = (OrderCancellationEmailProcessModel) businessProcessModel;
            setOrderNumber(orderCancellation.getOrderNumber());
            setArticleNumbers(orderCancellation.getArticleNumbers());
            setProductNames(orderCancellation.getProductNames());
        }
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<String> getArticleNumbers() {
        return articleNumbers;
    }

    public void setArticleNumbers(List<String> articleNumbers) {
        this.articleNumbers = articleNumbers;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }
}
