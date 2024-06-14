/**
 * 
 */
package com.namics.hybris.toolbox.impex.classification;

import de.hybris.platform.catalog.jalo.classification.impex.ProductFeatureValueTranslator;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.jalo.Item;

import org.springframework.util.StringUtils;

/**
 * StringProductFeatureValueTranslator adds the defined type to the value expression.
 * 
 * @author rhusi, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class StringProductFeatureValueTranslator extends ProductFeatureValueTranslator {

    private char collectionValueDelimiter = ',';

    @Override
    public void init(final StandardColumnDescriptor columnDescriptor) {
        super.init(columnDescriptor);
        final String customDelimiter = columnDescriptor.getDescriptorData().getModifier("collection-delimiter");
        if ((customDelimiter != null) && (customDelimiter.length() > 0)) {
            this.collectionValueDelimiter = customDelimiter.charAt(0);
        }
    }

    @Override
    protected Object convertToJalo(final String valueExpr, final Item forItem) {
        if (StringUtils.hasText(valueExpr)) {
            final StandardColumnDescriptor standardColumnDescriptor = getColumnDescriptor();
            final String type = standardColumnDescriptor.getDescriptorData().getModifier("type");
            if (type != null) {
                return super.convertToJalo(type + collectionValueDelimiter + valueExpr, forItem);
            } else {
                throw new IllegalArgumentException("No 'type' attribute found in header for StringProductFeatureValueTranslator");
            }
        } else {
            return super.convertToJalo(valueExpr, forItem);
        }
    }
}
