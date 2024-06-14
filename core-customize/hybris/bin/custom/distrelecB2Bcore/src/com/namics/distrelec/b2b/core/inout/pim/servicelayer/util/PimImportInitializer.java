/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;

/**
 * Interface with methods to initialize an item during PIM data import.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 * 
 * @param <T>
 *            type of the object which has to be initialized
 */
public interface PimImportInitializer<T> {

    void initialize(T item, ImportContext importContext);

}
