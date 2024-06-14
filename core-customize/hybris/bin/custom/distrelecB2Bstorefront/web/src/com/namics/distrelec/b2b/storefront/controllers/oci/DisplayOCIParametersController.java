/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.oci;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * {@code DisplayOCIParametersController}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.3
 */
@Controller
public class DisplayOCIParametersController {

    private static final String CRLF = "\r\n";

    @ResponseBody
    @RequestMapping(value = "/DisplayOCIParameters", method = { RequestMethod.POST }, produces = "text/plain;charset=UTF-8")
    public String displayParameters(final Model model, final HttpServletRequest request, final HttpServletResponse response) {
        final StringBuilder sb = new StringBuilder("[HTTP-POST]");
        final Enumeration<String> paramNames = request.getParameterNames();
        final List<String> meta_params = new ArrayList<String>();
        final List<String> c_params = new ArrayList<String>();

        while (paramNames.hasMoreElements()) {
            final String paramName = paramNames.nextElement();
            if (paramName.matches("submit.*")) {
                continue;
            }

            if (paramName.matches(".*((\\[\\d+\\])|(_\\d+\\:\\d+\\[\\]))")) {
                c_params.add(paramName);
            } else {
                meta_params.add(paramName);
            }
        }

        // Adding meta parameters
        if (!meta_params.isEmpty()) {
            for (final String p_name : meta_params) {
                sb.append(CRLF).append(p_name).append(' ').append(request.getParameter(p_name));
            }
        }
        // Adding Cart parameters
        for (final String p_name : c_params) {
            sb.append(CRLF).append(p_name).append(CRLF).append(request.getParameter(p_name));
        }

        return sb.toString();
    }
}
