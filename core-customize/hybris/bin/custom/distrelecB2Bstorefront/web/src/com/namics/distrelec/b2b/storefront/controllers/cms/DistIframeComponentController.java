/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.model.cms2.components.DistIframeComponentModel;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import org.apache.commons.lang.StringUtils;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

/**
 * Controller for CMS DistIframeComponent.
 */
@Controller("DistIframeComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistIframeComponent)
public class DistIframeComponentController extends AbstractDistCMSComponentController<DistIframeComponentModel> {

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistIframeComponentModel component) {

        final String languageIsoCode = request.getLocale().getISO3Language();
        model.addAttribute(DistIframeComponentModel.LINKTOIFRAME, component.getLinkToIframe().replace("$lang", languageIsoCode).trim());

        final StringBuilder params = new StringBuilder();
        final Set<String> keys = request.getParameterMap().keySet();
        final String parameterEncoding = StringUtils.isEmpty(component.getParameterEncoding()) ? "UTF-8" : component.getParameterEncoding();
        for (final String key : keys) {
            try {
                if (!component.getLinkToIframe().contains("?") && params.length() == 0) {
                    params.append("?" + key + "=" + URLEncoder.encode(request.getParameter(key), parameterEncoding));
                } else {
                    params.append("&" + key + "=" + URLEncoder.encode(request.getParameter(key), parameterEncoding));
                }
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("Das Encoding '" + parameterEncoding + "' bei der Komponente '" + component.getUid() + "' wird nicht unterstützt.",
                        e);
            }
        }
        String encodeParams = Encode.forHtmlAttribute(params.toString());
        model.addAttribute("params", encodeParams);
    }
}
