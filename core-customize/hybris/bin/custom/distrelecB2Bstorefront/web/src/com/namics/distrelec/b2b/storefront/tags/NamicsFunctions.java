/*
 * Copyright 2000-2011 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.tags;

import com.namics.hybris.toolbox.spring.SpringUtil;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.commercefacades.product.data.PriceData;
import org.apache.log4j.Logger;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * NamicsCMSComponentTag
 * 
 * @author rhusi, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class NamicsFunctions {
    private final static Logger LOG = Logger.getLogger(NamicsFunctions.class.getName());

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

        final CMSContentSlotService contentSlotService = SpringUtil.getBean("cmsContentSlotService", CMSContentSlotService.class);
        final List<SimpleCMSComponentModel> components = contentSlotService.getSimpleCMSComponents(contentSlot, false,
                (HttpServletRequest) pageContext.getRequest());

        if (LOG.isDebugEnabled() && (components == null || components.isEmpty())) {
            LOG.debug("Components were null or empty. Components: " + components);
        }

        return Boolean.valueOf(components != null && !components.isEmpty());
    }

    public static String encodeURI(final String input) throws UnsupportedEncodingException {
        return UriUtils.encodeFragment(input == null ? "" : input, "UTF-8");
    }

    public static String formatWebtrekkPrice(final PriceData priceData) {
        final DecimalFormat formatter = new DecimalFormat("0.00");
        return priceData == null ? "0.00" : formatter.format(priceData.getValue());
    }

    public static String formatAnalyticsPrice(final PriceData priceData) {
        final DecimalFormat formatter = new DecimalFormat("0.00");
        return priceData == null ? "0.00" : formatter.format(priceData.getValue());
    }

    public static boolean isZero(final BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static int getScale(final BigDecimal value) {
        return value == null ? 0 : value.scale();
    }
}
