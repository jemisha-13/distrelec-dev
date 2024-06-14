/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BeanUtil {

    public static Map<String, Object> stringPropertiesToMap(final Object bean) {
        final Map<String, Object> properties = new LinkedHashMap<String, Object>();
        for (final Method getter : getAllStringGetters(bean)) {
            final String result = getStringValueFromGetter(bean, getter);
            if (result != null) {
                properties.put(getStringNameFromGetterMethod(getter), result);
            }
        }
        return properties;
    }

    /**
     * Retrieves all string getter methods from a given object (bean).
     * 
     * @param obj
     *            the bean
     * @return list of getter methods
     */
    public static List<Method> getAllStringGetters(final Object obj) {
        final Method[] methods = obj.getClass().getMethods();
        final List<Method> stringGetters = new ArrayList<Method>();

        for (final Method m : methods) {
            if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 && String.class.equals(m.getReturnType())) {
                stringGetters.add(m);
            }
        }
        return stringGetters;
    }

    /**
     * Get the name of a getter method in lower case.
     * 
     * @param getter
     *            method
     * @return name
     */
    public static String getStringNameFromGetterMethod(final Method getter) {
        return getter.getName().substring(3).toLowerCase();
    }

    /**
     * Invokes getter method on object and returns result string.
     * 
     * @param obj
     *            the bean
     * @param getter
     *            the method
     * @return method return
     */
    public static String getStringValueFromGetter(final Object obj, final Method getter) {
        try {
            return getter.invoke(obj, (Object[]) null).toString();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
