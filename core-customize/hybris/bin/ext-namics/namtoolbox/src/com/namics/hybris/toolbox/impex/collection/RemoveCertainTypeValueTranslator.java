/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.collection;

import de.hybris.platform.impex.jalo.header.AbstractDescriptor;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.CollectionType;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * <p>
 * Stellt sicher, dass in einer Collection Elemente eines bestimmten Typen gelöscht werden.
 * </p>
 * 
 * <p>
 * Beispiel:
 * 
 * <pre>
 * INSERT_UPDATE Company;uid[unique=true];@members[translator=com.namics.hybris.toolbox.impex.collection.RemoveCertainTypeValueTranslator][virtual=true][default=User];members(uid)[mode=append]
 * ;myCompany;newCreatedCustomer1;
 * 
 * </pre>
 * 
 * </p>
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class RemoveCertainTypeValueTranslator extends AbstractValueTranslator {

    private String typeValue;
    private ComposedType typeValueComposedType;
    private String attributeQualifier;

    private AbstractValueTranslator translator;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.AbstractValueTranslator#init(de.hybris.platform.impex.jalo.header.
     * StandardColumnDescriptor)
     */
    @Override
    public void init(final StandardColumnDescriptor descriptor) {
        super.init(descriptor);

        try {
            typeValue = descriptor.getDescriptorData().getModifier("itemtype");
            attributeQualifier = descriptor.getQualifier();

            if (!StringUtils.hasText(typeValue)) {
                throw new HeaderValidationException("You can't use " + RemoveCertainTypeValueTranslator.class + " with empty [itemtype] modifier.", -1);
            }

            typeValueComposedType = TypeManager.getInstance().getComposedType(typeValue);

            if (typeValueComposedType == null) {
                throw new HeaderValidationException("Composed Type '" + typeValue + "' doesn't exist.", -1);
            }

            final AttributeDescriptor ad = descriptor.getAttributeDescriptor();
            // Richtig ist 'uid'
            // descriptor.extractItemPathElements(descriptor.getDefinitionSrc())[0].get(0).getItemPatternLists();
            final String paramString = descriptor.getDefinitionSrc();
            CollectionType ct = null;
            if (ad.getAttributeType() instanceof CollectionType) {
                ct = (CollectionType) ad.getAttributeType();
            } else {
                throw new JaloInvalidParameterException("Attribute is not of type 'CollectionType'.", -1);
            }
            // List<AbstractDescriptor.ColumnParams>[] patternLists =
            // StandardColumnDescriptor.extractItemPathElements(paramString)[0].get(0).getItemPatternLists();
            final List<AbstractDescriptor.ColumnParams>[] patternLists = AbstractDescriptor.parseColumnDescriptor(paramString).getItemPatternLists();

            translator = AbstractValueTranslator.createTranslator(ad, ct, patternLists);
            translator.init(descriptor);

        } catch (final HeaderValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.AbstractValueTranslator#validate(de.hybris.platform.impex.jalo.header
     * .StandardColumnDescriptor)
     */
    @Override
    public void validate(final StandardColumnDescriptor cd) throws HeaderValidationException {
        translator.validate(cd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.AbstractValueTranslator#exportValue(java.lang.Object)
     */
    @Override
    public String exportValue(final Object paramObject) throws JaloInvalidParameterException {
        throw new JaloInvalidParameterException("Export is not supported.", -1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.AbstractValueTranslator#importValue(java.lang.String, de.hybris.platform.jalo.Item)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object importValue(final String paramString, final Item paramItem) throws JaloInvalidParameterException {
        try {

            final Collection<Object> collection = (Collection<Object>) paramItem.getAttribute(this.attributeQualifier);
            final Collection<Object> newcollection = new ArrayList<Object>();
            for (final Object object : collection) {
                if (object instanceof Item) {
                    final Item item = (Item) object;
                    if (item.getComposedType().getCode().equals(typeValueComposedType.getCode())) {
                        // we do not pass the object to the newcollection
                        continue;
                    }
                }
                newcollection.add(object);
            }

            paramItem.setAttribute(this.attributeQualifier, newcollection);

        } catch (final JaloSecurityException e) {
            throw new JaloInvalidParameterException(e, -1);
        } catch (final JaloBusinessException e) {
            throw new JaloInvalidParameterException(e, -1);
        }

        return translator.importValue(paramString, paramItem);
    }

}
