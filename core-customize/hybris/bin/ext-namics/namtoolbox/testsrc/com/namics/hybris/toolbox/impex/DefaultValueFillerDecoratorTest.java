/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class DefaultValueFillerDecoratorTest {

    @Test
    public void testCreateValueMap() {
        final DefaultValueFillerDecorator decorator = new DefaultValueFillerDecorator();
        final Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(DefaultValueFillerDecorator.MODIFIER_PREFIX + "2", "test");
        parameterMap.put(DefaultValueFillerDecorator.MODIFIER_PREFIX + "3", "other");
        final Map<Integer, String> fillerMap = decorator.createValueMap(parameterMap);
        Assert.assertNull(fillerMap.get(Integer.valueOf(1)));
        Assert.assertEquals("test", fillerMap.get(Integer.valueOf(2)));
        Assert.assertEquals("other", fillerMap.get(Integer.valueOf(3)));
        Assert.assertNull(fillerMap.get(Integer.valueOf(4)));

    }

    @Test
    public void testCreateResultValue() {
        final DefaultValueFillerDecorator decorator = new DefaultValueFillerDecorator();
        final Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(DefaultValueFillerDecorator.MODIFIER_PREFIX + "1", "test");
        parameterMap.put(DefaultValueFillerDecorator.MODIFIER_PREFIX + "2", "other");
        final Map<Integer, String> fillerMap = decorator.createValueMap(parameterMap);

        Assert.assertEquals("first:test:other:third", decorator.createResultValue("first:third", fillerMap, ":"));
    }

    @Test
    public void testCreateResultValueWithPrefixAndSuffix() {
        final DefaultValueFillerDecorator decorator = new DefaultValueFillerDecorator();
        final Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(DefaultValueFillerDecorator.MODIFIER_PREFIX + "0", "zero");
        parameterMap.put(DefaultValueFillerDecorator.MODIFIER_PREFIX + "2", "test");
        parameterMap.put(DefaultValueFillerDecorator.MODIFIER_PREFIX + "3", "other");
        parameterMap.put(DefaultValueFillerDecorator.MODIFIER_PREFIX + "5", "last");
        final Map<Integer, String> fillerMap = decorator.createValueMap(parameterMap);

        Assert.assertEquals("zero:first:test:other:fourth:last", decorator.createResultValue("first:fourth", fillerMap, ":"));
    }

}
