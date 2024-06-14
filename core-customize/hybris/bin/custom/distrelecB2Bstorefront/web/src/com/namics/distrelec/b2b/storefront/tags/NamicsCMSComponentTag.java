/*
 * Copyright 2000-2011 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.tags;

import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.util.List;

/**
 * NamicsCMSComponentTag
 * 
 * @author rhusi, Namics AG
 * @since Freitag 1.0
 * 
 */
public class NamicsCMSComponentTag {
    private final static Logger LOG = Logger.getLogger(NamicsCMSComponentTag.class.getName());

    /**
     * Checks if the content slots has valid components.
     * 
     * @param contentSlot
     *            The content slot
     * @param pageContext
     *            The current pageContext
     * @return true if there are any components, otherwise false
     */
    public static Boolean hasComponents(final ContentSlotModel contentSlot, final PageContext pageContext) {
        if (contentSlot == null || pageContext == null || pageContext.getRequest() == null) {
            LOG.info("Paramters are not valid: ContentSlot -> " + contentSlot + ", PageContext ->" + pageContext);
            return Boolean.FALSE;
        }

        final CMSContentSlotService contentSlotService = getContentSlotService((HttpServletRequest) pageContext.getRequest());
        final List<SimpleCMSComponentModel> components = contentSlotService.getSimpleCMSComponents(contentSlot, false,
                (HttpServletRequest) pageContext.getRequest());

        if (LOG.isDebugEnabled() && (components == null || components.isEmpty())) {
            LOG.debug("Components were null or empty. Components: " + components);
        }

        return Boolean.valueOf(components != null && !components.isEmpty());
    }

    protected static CMSContentSlotService getContentSlotService(final HttpServletRequest httpRequest) {
        return getSpringBean(httpRequest, "cmsContentSlotService", CMSContentSlotService.class);
    }

    /**
     * Returns the Spring bean with name <code>beanName</code> and of type <code>beanClass</code>.
     * 
     * @param <T>
     *            type of the bean
     * @param httpRequest
     *            the http request
     * @param beanName
     *            name of the bean
     * @param beanClass
     *            expected type of the bean
     * @return the bean matching the given arguments or <code>null</code> if no bean could be resolved
     */
    public static <T> T getSpringBean(final HttpServletRequest httpRequest, final String beanName, final Class<T> beanClass) {
        return RequestContextUtils.findWebApplicationContext(httpRequest, httpRequest.getSession().getServletContext()).getBean(beanName, beanClass);
    }
}
