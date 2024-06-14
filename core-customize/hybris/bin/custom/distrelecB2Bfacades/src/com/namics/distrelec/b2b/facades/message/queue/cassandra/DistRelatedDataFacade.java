/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.message.queue.cassandra;

import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;

/**
 * {@code DistRelatedDataFacade}
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public interface DistRelatedDataFacade {

    /**
     * Fetch the related data of the product model.
     *
     * @param code
     *            the product model code.
     * @return the {@link RelatedData} relative to the product model given by its code.
     */
    public RelatedData findProductRelatedData(final String code);

    /**
     * Fetch the related data of the category model.
     *
     * @param code
     *            the category model code.
     * @return the {@link RelatedData} relative to the category model given by its code.
     */
    public RelatedData findCategoryRelatedData(final String code);

    /**
     * Fetch the related data of the manufacturer model.
     *
     * @param code
     *            the manufacturer model code.
     * @return the {@link RelatedData} relative to the manufacturer model given by its code.
     */
    public RelatedData findManufacturerRelatedData(final String code);
}
