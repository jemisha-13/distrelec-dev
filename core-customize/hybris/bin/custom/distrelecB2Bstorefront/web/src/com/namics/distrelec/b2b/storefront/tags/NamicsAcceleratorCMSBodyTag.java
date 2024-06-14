/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.tags;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2lib.cmstags.CMSBodyTag;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * NamicsAcceleratorCMSBodyTag extends AcceleratorCMSBodyTag.
 *
 * @author rhusi, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class NamicsAcceleratorCMSBodyTag extends CMSBodyTag {

    private static final Logger LOG = Logger.getLogger(NamicsAcceleratorCMSBodyTag.class);

    private String cssClass;
    private String style;
    private String liveEditCssPath;
    private String liveEditJsPath;

    @Override
    public int doStartTag() throws JspException {
        AbstractPageModel currentPage = (AbstractPageModel)this.pageContext.getRequest().getAttribute("currentPage");
        String currentPagePk = null;
        if (currentPage != null) {
            currentPagePk = currentPage.getPk().toString();
        }

        StringBuilder bodyTagBuilder = new StringBuilder();
        bodyTagBuilder.append("body");
        bodyTagBuilder.append(this.isPreviewDataModelValid() ? " onload=\"getCurrentPageLocation(window.location.href, '" + currentPagePk + "' , currentUserId " + ", currentJaloSessionId)\"" : "");
        bodyTagBuilder.append(this.isLiveEdit() ? " onclick=\"return getCMSElement(event)\"" : "");
        bodyTagBuilder.append(getBodyTagCssClass(currentPage));
        bodyTagBuilder.append(getBodyTagStyle(currentPage));

        try {
            this.pageContext.getOut().print("<" + bodyTagBuilder.toString() + ">\n");
            this.pageContext.getOut().print(this.isPreviewDataModelValid() ? "<script type=\"text/javascript\">\n var currentUserId;\n var currentJaloSessionId;\n function getCurrentPageLocation(url, pagePk, userUid, jaloSessionId){\n   if (url != \"\") {\n       \tparent.postMessage({eventName:notifyIframeAboutUrlChange, data: [url, pagePk, userUid, jaloSessionId]},'*');\n \t  \t\n   }\n}\n</script>\n" : "");
        } catch (IOException var5) {
            LOG.warn("Error processing tag", var5);
        }

        return 1;
    }

    protected String getBodyTagCssClass(final AbstractPageModel currentPage) {
        if (getCssClass() != null && !getCssClass().isEmpty()) {
            return " class=\"" + getCssClass() + "\" ";
        }
        return "";
    }

    protected String getBodyTagStyle(final AbstractPageModel currentPage) {
        if (getCssClass() != null && !getCssClass().isEmpty()) {
            return " style=\"" + getStyle() + "\" ";
        }
        return "";
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(final String cssClass) {
        this.cssClass = cssClass;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(final String style) {
        this.style = style;
    }

    public String getLiveEditCssPath() {
        return liveEditCssPath;
    }

    public void setLiveEditCssPath(final String liveEditCssPath) {
        this.liveEditCssPath = liveEditCssPath;
    }

    public String getLiveEditJsPath() {
        return liveEditJsPath;
    }

    public void setLiveEditJsPath(final String liveEditJsPath) {
        this.liveEditJsPath = liveEditJsPath;
    }
}
