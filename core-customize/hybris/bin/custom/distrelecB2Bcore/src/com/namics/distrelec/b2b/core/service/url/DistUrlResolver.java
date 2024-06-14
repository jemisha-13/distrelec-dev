/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url;

import java.util.Locale;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code DistUrlResolver}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public interface DistUrlResolver<T> extends UrlResolver<T> {

    /**
     * Resolve the URL path for the source type.
     * 
     * @param source
     *            the source type.
     * @param baseSite
     *            the base site to which the URL will be resolved
     * @param language
     *            the language used to resolve the URL
     * @return the URL path
     */
    public String resolve(final T source, final BaseSiteModel baseSite, final String language);

    /**
     * Resolve the URL path for the source type.
     * 
     * @param source
     *            the source type.
     * @param baseSite
     *            the base site to which the URL will be resolved
     * @param locale
     *            the locale used to resolve the URL
     * @return the URL path
     */
    public String resolve(final T source, final BaseSiteModel baseSite, final Locale locale);
    
    /**
     * 
     * @param source
     * @param baseSite
     * @param locale
     * @param isCanonical
     * @return the URL path
     */
    public String resolve(final T source, final BaseSiteModel baseSite, final String locale, boolean isCanonical);

}
