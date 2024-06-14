/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;

/**
 * <p>
 * Allows the use of multiple cellDecorators on one cell in a specific order.
 * </p>
 * 
 * 
 * @author mwegener, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class MultipleDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(MultipleDecorator.class.getName());

    public static final String DECORATOR_MODIFIER = "decorator";

    private String decorator = "";
    private AbstractColumnDescriptor columnDescriptor;

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);
        columnDescriptor = column;
        decorator = column.getDescriptorData().getModifier(DECORATOR_MODIFIER);

        if (decorator == null) {
            decorator = "";
        }

        if ("".equals(decorator)) {
            throw new HeaderValidationException("The modifier [" + DECORATOR_MODIFIER + "] in " + MultipleDecorator.class + " must be set.", -1);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        String importValue = paramMap.get(Integer.valueOf(paramInt));
        final String[] decorators = decorator.split(" ");

        for (final String singleDecorator : decorators) {
            AbstractImpExCSVCellDecorator cellDecoratorInstance;

            try {
                final Class<AbstractImpExCSVCellDecorator> loadedClass = (Class<AbstractImpExCSVCellDecorator>) Class.forName(singleDecorator);
                cellDecoratorInstance = loadedClass.newInstance();

                // init
                cellDecoratorInstance.init(columnDescriptor);

                // decorate
                final Map<Integer, String> valueMap = new HashMap<Integer, String>();
                valueMap.put(Integer.valueOf(0), importValue);
                importValue = cellDecoratorInstance.decorate(0, valueMap);

            } catch (final ClassNotFoundException e) {
                LOG.error(e);
            } catch (final InstantiationException e) {
                LOG.error(e);
            } catch (final IllegalAccessException e) {
                LOG.error(e);
            } catch (final HeaderValidationException e) {
                LOG.error(e);
            }
        }

        final String resultValue = importValue;
        LOG.debug(resultValue);
        return resultValue;
    }

}
