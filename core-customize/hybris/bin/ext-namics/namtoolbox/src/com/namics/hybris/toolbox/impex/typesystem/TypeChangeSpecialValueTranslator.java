/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.typesystem;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;

import org.springframework.util.StringUtils;

/**
 * <p>
 * Wechselt bei einem hybris item den Type, z.B. von MyProduct zu Product.
 * </p>
 * 
 * <p>
 * Beispiel:
 * 
 * <pre>
 * INSERT_UPDATE MyProduct;code[unique=true];catalogVersion(catalog(id),version);@type[translator=com.namics.hybris.toolbox.impex.typesystem.TypeChangeSpecialValueTranslator][virtual=true][default=Product]
 * ;testProduct;Default:staged;Product
 * </pre>
 * 
 * </p>
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class TypeChangeSpecialValueTranslator extends AbstractSpecialValueTranslator {

    private String attributeQualifier;
    private String defaultValue;
    private boolean isVirtual;

    /*
     * (non-Javadoc)
     * 
     * @seede.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator#init(de.hybris.platform.impex.jalo.header .
     * SpecialColumnDescriptor)
     */
    @Override
    public void init(final SpecialColumnDescriptor cd) throws HeaderValidationException {
        attributeQualifier = cd.getQualifier().replace("@", "");
        defaultValue = cd.getDescriptorData().getModifier("default");
        isVirtual = StringUtils.hasText(cd.getDescriptorData().getModifier("virtual"));

        if (!"type".equalsIgnoreCase(attributeQualifier)) {
            throw new HeaderValidationException("You can't use " + TypeChangeSpecialValueTranslator.class + " with another qualifier than 'type'.", -1);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator#performImport(java.lang.String,
     * de.hybris.platform.jalo.Item)
     */
    @Override
    public void performImport(final String cellValue, final Item processedItem) throws ImpExException {
        final String typeValue = StringUtils.hasText(cellValue) || isVirtual ? cellValue : defaultValue;

        final ComposedType typeValueComposedType = TypeManager.getInstance().getComposedType(typeValue);
        try {

            if (typeValueComposedType == null) {
                throw new ImpExException("Composed Type '" + typeValue + "' doesn't exist.");
            }

            if (!processedItem.isInstanceOf(typeValueComposedType)) {
                throw new ImpExException("Instance of type '" + processedItem.getComposedType().getCode() + "' is not assignable " + "to Composed Type '"
                        + typeValue + "'.");
            }

            processedItem.setComposedType(typeValueComposedType);

        } catch (final JaloInvalidParameterException e) {
            throw new ImpExException(e);
        } catch (final JaloBusinessException e) {
            throw new ImpExException(e);
        }
    }

}
