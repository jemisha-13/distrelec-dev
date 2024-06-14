package com.namics.distrelec.occ.core.filter;

import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cmsoccaddon.redirect.suppliers.impl.ContentPageRedirectSupplier;
import org.apache.commons.lang3.BooleanUtils;

import javax.servlet.http.HttpServletRequest;

public class DistContentPageRedirectSupplier extends ContentPageRedirectSupplier {

    public static final String SMARTEDIT = "smartedit";

    @Override
    public boolean shouldRedirect(HttpServletRequest request, PreviewDataModel previewData) {
        boolean smartedit = BooleanUtils.toBoolean(request.getParameter(SMARTEDIT));
        return super.shouldRedirect(request, previewData) && smartedit;
    }
}
