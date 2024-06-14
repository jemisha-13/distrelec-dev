package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.model.process.OrderCancellationEmailProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

public class OrderCancellationEventListener extends AbstractEventListener<OrderCancellationEvent> {

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    @Override
    protected void onEvent(OrderCancellationEvent event) {
        final OrderCancellationEmailProcessModel orderCancellationProcessModel = (OrderCancellationEmailProcessModel) getBusinessProcessService().createProcess(
                                                                                                                                                                "orderCancellationEmailProcess-"
                                                                                                                                                                + System.currentTimeMillis(),
                                                                                                                                                                "orderCancellationEmailProcess");
        orderCancellationProcessModel.setSite(event.getSite());
        orderCancellationProcessModel.setCustomer(event.getCustomer());
        orderCancellationProcessModel.setLanguage(event.getLanguage());
        orderCancellationProcessModel.setCurrency(event.getCurrency());
        orderCancellationProcessModel.setStore(event.getBaseStore());
        orderCancellationProcessModel.setOrderNumber(event.getOrderNumber());
        orderCancellationProcessModel.setArticleNumbers(event.getArticleNumbers());
        orderCancellationProcessModel.setProductNames(event.getProductNames());
        getModelServiceViaLookup().save(orderCancellationProcessModel);
        getBusinessProcessService().startProcess(orderCancellationProcessModel);
    }
}
