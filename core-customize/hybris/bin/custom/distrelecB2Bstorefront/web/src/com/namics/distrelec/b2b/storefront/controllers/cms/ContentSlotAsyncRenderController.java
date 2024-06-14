/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * {@code ContentSlotAsyncRenderController}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
@Controller("ContentSlotAsyncRenderController")
@RequestMapping(value = "/async-slot-load")
public class ContentSlotAsyncRenderController {

    protected static final Logger LOG = Logger.getLogger(ContentSlotAsyncRenderController.class);

    private static final String CMS_UID = "suid";

    @Autowired
    private CMSContentSlotService cmsContentSlotService;

    @RequestMapping
    public String get(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws Exception {
        String cmsUid = (String) request.getAttribute(CMS_UID);
        if (StringUtils.isEmpty(cmsUid)) {
            cmsUid = request.getParameter(CMS_UID);
        }

        if (StringUtils.isEmpty(cmsUid)) {
            LOG.error("No component specified in [" + CMS_UID + "]");
            throw new AbstractPageController.HttpNotFoundException();
        }

        try {
            final ContentSlotModel contentSlot = getCmsContentSlotService().getContentSlotForId(cmsUid);
            if (isVisible(contentSlot)) {
                model.addAttribute("asyncContentSlot", contentSlot);
            }
        } catch (final Exception e) {
            LOG.error("Could not find content slot with UID [" + cmsUid + "]", e);
            throw new AbstractPageController.HttpNotFoundException(e);
        }

        return getView();
    }

    protected boolean isVisible(final ContentSlotModel item) {
        final Date now = new Date();
        return BooleanUtils.isTrue(item.getActive()) && (item.getActiveFrom() == null || now.after(item.getActiveFrom()))
                && (item.getActiveUntil() == null || now.before(item.getActiveUntil()));
    }

    public String getView() {
        return ControllerConstants.Views.Cms.ComponentPrefix + "contentslot-async-renderer";
    }

    public CMSContentSlotService getCmsContentSlotService() {
        return cmsContentSlotService;
    }

    public void setCmsContentSlotService(CMSContentSlotService cmsContentSlotService) {
        this.cmsContentSlotService = cmsContentSlotService;
    }
}
