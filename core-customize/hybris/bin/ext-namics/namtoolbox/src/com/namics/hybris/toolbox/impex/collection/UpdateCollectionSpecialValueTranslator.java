/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.collection;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/**
 * <p>
 * Stellt sicher, dass in einer Collection jedes Element nur einmal vorkommt.
 * </p>
 * 
 * <p>
 * Beispiel:
 * 
 * <pre>
 * INSERT_UPDATE Product;code[unique=true];supercategories(code,catalogVersion(catalog(id),version))[mode=append];catalogVersion(catalog(id),version);@supercategories[translator=com.namics.hybris.toolbox.impex.collection.UpdateCollectionSpecialValueTranslator][virtual=true][default=supercategories]
 * ;testProduct;testCategory1:Default:staged,testCategory2:Default:staged;Default:staged
 * </pre>
 * 
 * </p>
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class UpdateCollectionSpecialValueTranslator extends AbstractSpecialValueTranslator {
    private static final Logger log = Logger.getLogger(UpdateCollectionSpecialValueTranslator.class.getName());

    private String attributeQualifier;

    /*
     * (non-Javadoc)
     * 
     * @seede.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator#init(de.hybris.platform.impex.jalo.header .
     * SpecialColumnDescriptor)
     */
    @Override
    public void init(final SpecialColumnDescriptor cd) throws HeaderValidationException {
        attributeQualifier = cd.getQualifier().replace("@", "");

        if (cd.getDescriptorData().getModifier("default") == null) {
            throw new HeaderValidationException("You can't use " + UpdateCollectionSpecialValueTranslator.class + " without a [default] modifier.", -1);
        }
        if (cd.getDescriptorData().getModifier("virtual") == null) {
            throw new HeaderValidationException("You can't use " + UpdateCollectionSpecialValueTranslator.class + " without a [virtual] modifier.", -1);
        }

        if (!StringUtils.hasText(attributeQualifier)) {
            throw new HeaderValidationException("You can't use " + UpdateCollectionSpecialValueTranslator.class + " with empty qualifier.", -1);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator#performImport(java.lang.String,
     * de.hybris.platform.jalo.Item)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void performImport(final String cellValue, final Item processedItem) throws ImpExException {
        try {
            final Collection<Object> collection = (Collection<Object>) processedItem.getAttribute(this.attributeQualifier);
            final SortedSet<Object> newcollection = new TreeSet<Object>(collection);
            processedItem.setAttribute(this.attributeQualifier, newcollection);

        } catch (final JaloInvalidParameterException e) {
            throw new ImpExException(e);
        } catch (final JaloSecurityException e) {
            throw new ImpExException(e);
        } catch (final JaloBusinessException e) {
            throw new ImpExException(e);
        }
    }

}
