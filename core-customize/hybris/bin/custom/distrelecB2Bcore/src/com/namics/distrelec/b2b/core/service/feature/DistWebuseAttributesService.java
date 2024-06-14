/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.feature;

import java.util.Map;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * 
 * Gets the WebUse-Attributes (technical Attributes) for a {@link ProductModel}.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20 (DISTRELEC-4581)
 * 
 */
public interface DistWebuseAttributesService {

    /**
     * returns a Map with the webUse-Attributes (technical attributes) of a {@link ProductModel}.
     * 
     * @param product
     *            product
     * @param language
     *            language (attention the product feature name will be retrieved in the session language)
     * @return a Map containing the webuse attributes (or empty)
     */
    Map<String, String> getWebuseAttributes(ProductModel product, LanguageModel language);
}
