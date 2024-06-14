/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.classification;

import de.hybris.platform.catalog.jalo.ProductFeature;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.imp.DefaultImportProcessor;
import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.jalo.Item;

/**
 * <p>
 * Extending the <code>DefaultImportProcessor</code> to correctly set the qualifier of a {@link ProductFeature}.
 * </p>
 * 
 * <p>
 * The qualifier in hybris is set in form of <code>/catalog/catalogversion/classificationClass.classificationAttribute</code> eg.
 * 
 * <pre>
 * NamicsClassification / 1.0 / ClsfHardware.marke
 * </pre>
 * 
 * </p>
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 * @deprecated Finally, I found out that you can do the same with impex "qualifier[default=,virtual=true,allowNull=true]" :)
 * 
 */
public class ProductFeatureQualifiertImportProcessor extends DefaultImportProcessor {

    @Override
    public Item processItemData(final ValueLine valueLine) throws ImpExException {
        final Item item = super.processItemData(valueLine);
        final ProductFeature productFeature = (ProductFeature) item;
        final String qualifier = productFeature.getQualifier();
        if (qualifier == null || "".equals(qualifier)) {
            productFeature.setProperty(ProductFeature.QUALIFIER, FeatureContainer.createUniqueKey(productFeature.getClassificationAttributeAssignment()));
        }
        return item;
    }

}
