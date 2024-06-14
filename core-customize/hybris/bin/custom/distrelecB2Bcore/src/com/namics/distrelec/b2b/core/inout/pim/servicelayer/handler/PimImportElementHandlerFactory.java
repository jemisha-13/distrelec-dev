/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

/**
 * Factory interface to create all the element handler.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface PimImportElementHandlerFactory {

    PimImportElementHandler createPimImportElementHandler(String beanId);

}
