/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.hybris.toolbox.spring;

import de.hybris.platform.core.Registry;

/**
 * Util for spring functions.
 * 
 * @author rhusi, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class SpringUtil {

    /**
     * Returns the bean from the application context.
     * 
     * @param beanName
     *            The name of the bean.
     * @return the bean as java.lang.Object. You have to cast this object.
     */
    public static Object getBean(final String beanName) {
        return Registry.getApplicationContext().getBean(beanName);
    }

    /**
     * Returns the bean from the application context.
     * 
     * @param <T>
     *            class type
     * @param beanName
     *            The name of the bean.
     * @param clazz
     *            The class name
     * @return the bean from the application context as its implementation. No cast is needed.
     */
    public static <T> T getBean(final String beanName, final Class<T> clazz) {
        return Registry.getApplicationContext().getBean(beanName, clazz);
    }

    /**
     * Returns the bean from the application context. Bean are defined in file '/resources/melcore-application-context.xml'.
     * 
     * @param beanClass
     *            The class name of the bean.
     * @return the bean as java.lang.Object. You have to cast this object.
     */
    public static Object getBean(final Class<?> beanClass) {
        return Registry.getApplicationContext().getBean(beanClass);
    }

}