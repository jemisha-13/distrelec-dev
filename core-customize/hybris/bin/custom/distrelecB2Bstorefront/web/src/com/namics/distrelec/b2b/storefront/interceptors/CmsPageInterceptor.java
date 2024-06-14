/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.interceptors;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageController;
import com.namics.distrelec.b2b.storefront.filters.cms.CMSSiteFilter;

import de.hybris.platform.cms2.jalo.preview.PreviewData;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

/**
 * Filter to load the appropriate Cms page slots into the model.
 */
public class CmsPageInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = Logger.getLogger(CmsPageInterceptor.class);

    public static final String SLOTS_MODEL = "slots";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private CMSPageService cmsPageService;

    @Autowired
    private CMSPreviewService cmsPreviewService;

    @Resource(name = "cmsPageContextService")
    private CMSPageContextService cmsPageContextService;

    @Resource(name = "requestContextRestrictionConverter")
    private Converter<RequestContextData, RestrictionData> requestContextRestrictionConverter;

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) {
        if (modelAndView != null) {
            modelAndView.addObject("cmsSite", cmsSiteService.getCurrentSite());

            // Look for the page in the model
            AbstractPageModel page = (AbstractPageModel) modelAndView.getModel().get(AbstractPageController.CMS_PAGE_MODEL);
            if (page != null) {
                final AbstractPageModel previewPage = lookupPreviewPage(request);
                if (previewPage != null && !previewPage.equals(page)) {
                    // Have a preview page that overrides the current page

                    // Check that the preview page is the same type as the expected page
                    if (!page.getClass().isInstance(previewPage)) {
                        LOG.error(
                                "Preview page is of type [" + previewPage.getClass().getName() + "] expected page of type [" + page.getClass().getName() + "]");
                    } else {
                        // Push the preview page into the model
                        LOG.info("Replaced page [" + page + "] with preview page [" + previewPage + "]");
                        modelAndView.addObject(AbstractPageController.CMS_PAGE_MODEL, previewPage);

                        // Check to see if we are using the default view for the page
                        if (modelAndView.getViewName() != null && modelAndView.getViewName().equals(getViewForPage(page))) {
                            final String viewForPreviewPage = getViewForPage(previewPage);
                            if (viewForPreviewPage != null && !viewForPreviewPage.equals(modelAndView.getViewName())) {
                                // Change the view name
                                LOG.info("Changing view from [" + modelAndView.getViewName() + "] to preview view [" + viewForPreviewPage + "]");
                                modelAndView.setViewName(viewForPreviewPage);
                            }
                        }

                        page = previewPage;
                    }
                }

                // Add the page to the request attribute "currentPage" which is then picked up by the CMSBodyTag, and then passed over to
                // the CMS Cockpit
                request.setAttribute("currentPage", page);

                // Add the content slots to the page as a map of slots - keyed by position
                modelAndView.addObject(SLOTS_MODEL, getContentSlotsForPageAsMap(page));

                updateCmsPageContextForPage(request, modelAndView, page);
            }
        }
    }

    /**
     * Brought from CmsPageBeforeViewHandler.
     */
    protected void updateCmsPageContextForPage(final HttpServletRequest request, final ModelAndView modelAndView,
                                               final AbstractPageModel page) {
        // Create the restriction data
        RequestContextData requestContextData = SpringHelper.getSpringBean(request, "requestContextData",
            RequestContextData.class, true);
        RestrictionData restrictionData = requestContextRestrictionConverter.convert(requestContextData);

        // Initialise CMS support
        CmsPageRequestContextData cmsPageRequestContextData = cmsPageContextService.updateCmsPageContextForPage(request,
            page, restrictionData);
        modelAndView.addObject("cmsPageRequestContextData", cmsPageRequestContextData);
    }

    /**
     * Retrieve all content slots for the page and return them in a map
     * 
     * @param page
     * @return map with content slots assigned to positions
     */
    protected Map<String, ContentSlotModel> getContentSlotsForPageAsMap(final AbstractPageModel page) {
        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }

        final Collection<ContentSlotData> slotModels = cmsPageService.getContentSlotsForPage(page, cmsPreviewService.getPagePreviewCriteria());
        final Map<String, ContentSlotModel> slots = new TreeMapWrapper<String, ContentSlotModel>();

        for (final ContentSlotData contentSlot : slotModels) {
            slots.put(contentSlot.getPosition(), contentSlot.getContentSlot());
        }

        return slots;
    }

    /**
     * Retrieves a preview ticket, if available and retrieves the preview page from the {@link PreviewData}
     * 
     * @param request
     * @return preview page
     */
    protected AbstractPageModel lookupPreviewPage(final HttpServletRequest request) {
        final String previewTicketId = request.getParameter(CMSSiteFilter.PREVIEW_TICKET_ID_PARAM);
        if (previewTicketId != null && !previewTicketId.isEmpty()) {
            final CMSPreviewTicketModel previewTicket = cmsPreviewService.getPreviewTicket(previewTicketId);
            if (previewTicket != null) {
                final PreviewDataModel previewData = previewTicket.getPreviewData();
                if (previewData != null) {
                    return previewData.getPage();
                }
            }
        }
        return null;
    }

    /**
     * Returns ths view name for the page by retrieving the frontendTemplateName from the masterTemplate of the page
     * 
     * @param page
     * @return view name or null, if the view name cannot retrieved from the masterTemplate
     */
    protected String getViewForPage(final AbstractPageModel page) {
        if (page != null) {
            final PageTemplateModel masterTemplate = page.getMasterTemplate();
            if (masterTemplate != null) {
                final String targetPage = cmsPageService.getFrontendTemplateName(masterTemplate);
                if (targetPage != null && !targetPage.isEmpty()) {
                    return AbstractPageController.PAGE_ROOT + targetPage;
                }
            }
        }
        return null;
    }

    /**
     * {@code TreeMapWrapper}
     * <p>
     * A wrapper class for the {@link TreeMap} as a sub-class of {@link HashMap}. This class is created because the JSTL implementation
     * supports only instances of {@link HashMap} for its {@code forEach} loop. Since we would like to preserve a certain order, i.e.,
     * alpha-numeric order, we wrap the {@link TreeMap} into a {@link HashMap}.
     * </p>
     * 
     * @param <K>
     * @param <V>
     *
     * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
     * @since Distrelec 5.11
     */
    protected static class TreeMapWrapper<K, V> extends HashMap<K, V> {

        final TreeMap<K, V> source;

        /**
         * Create a new instance of {@code TreeMapWrapper}
         */
        public TreeMapWrapper() {
            this.source = new TreeMap<K, V>();
        }

        /**
         * Create a new instance of {@code TreeMapWrapper}
         * 
         * @param m
         */
        public TreeMapWrapper(final Map<? extends K, ? extends V> m) {
            this.source = new TreeMap<K, V>(m);
        }

        /**
         * Create a new instance of {@code TreeMapWrapper}
         * 
         * @param comparator
         */
        public TreeMapWrapper(final Comparator<? super K> comparator) {
            this.source = new TreeMap<K, V>(comparator);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#size()
         */
        @Override
        public int size() {
            return source.size();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#isEmpty()
         */
        @Override
        public boolean isEmpty() {
            return source.isEmpty();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#containsKey(java.lang.Object)
         */
        @Override
        public boolean containsKey(final Object key) {
            return source.containsKey(key);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#containsValue(java.lang.Object)
         */
        @Override
        public boolean containsValue(final Object value) {
            return source.containsValue(value);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#get(java.lang.Object)
         */
        @Override
        public V get(final Object key) {
            return source.get(key);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
         */
        @Override
        public V put(final K key, final V value) {
            return source.put(key, value);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#remove(java.lang.Object)
         */
        @Override
        public V remove(final Object key) {
            return source.remove(key);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#putAll(java.util.Map)
         */
        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            source.putAll(map);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#clear()
         */
        @Override
        public void clear() {
            source.clear();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#keySet()
         */
        @Override
        public Set<K> keySet() {
            return source.keySet();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#values()
         */
        @Override
        public Collection<V> values() {
            return source.values();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.HashMap#entrySet()
         */
        @Override
        public Set<java.util.Map.Entry<K, V>> entrySet() {
            return source.entrySet();
        }
    }
}
