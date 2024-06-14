/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import java.util.HashSet;
import java.util.Set;

import com.namics.distrelec.b2b.core.model.process.DistrelecNewProductsNewsLetterEmailProcessModel;
import com.namics.distrelec.b2b.core.model.process.ProductsNewsLetterProcessEntryModel;

import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code DistNewProductsNewsLetterEventListener}
 *
 * @since Distrelec 5.10
 */
public class DistNewProductsNewsLetterEventListener
        extends AbstractDistEventListener<DistNewProductsNewsLetterEvent, DistrelecNewProductsNewsLetterEmailProcessModel> {

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.event.AbstractDistEventListener#createTarget()
     */
    @Override
    public DistrelecNewProductsNewsLetterEmailProcessModel createTarget() {
        return (DistrelecNewProductsNewsLetterEmailProcessModel) getBusinessProcessService()
                .createProcess("distrelecNewProductsNewsLetterEmailProcess" + System.currentTimeMillis(), "distrelecNewProductsNewsLetterEmailProcess");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#validate(de.hybris.platform.servicelayer.event.events.AbstractEvent)
     */
    @Override
    protected boolean validate(final DistNewProductsNewsLetterEvent event) {
        return event != null && event.getProducts() != null && !event.getProducts().isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#populate(de.hybris.platform.servicelayer.event.events.AbstractEvent,
     * de.hybris.platform.processengine.model.BusinessProcessModel)
     */
    @Override
    public void populate(final DistNewProductsNewsLetterEvent event, final DistrelecNewProductsNewsLetterEmailProcessModel target) {
        super.populate(event, target);

        final Set<ProductsNewsLetterProcessEntryModel> productItems = new HashSet<ProductsNewsLetterProcessEntryModel>();
        for (final ProductModel product : event.getProducts()) {
            final ProductsNewsLetterProcessEntryModel productItem = getModelServiceViaLookup().create(ProductsNewsLetterProcessEntryModel.class);
            productItem.setProduct(product);
            productItem.setProductNewsletterProcess(target);
            productItems.add(productItem);
        }

        target.setProductItems(productItems);
        target.setFromdate(event.getFromdate());
        target.setTodate(event.getTodate());
    }
}
