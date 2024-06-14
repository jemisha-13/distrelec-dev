/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.cxml.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.eprocurement.service.cxml.CxmlException;
import com.namics.distrelec.distrelecoci.exception.OciException;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.JaloConnectException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.util.WebSessionFunctions;

public class CxmlManager {

    public static final String ATTR_IS_CXML_LOGIN = "IS_CXML_LOGIN";
    public static final String ATTR_OUTBOUND_SECTION_DATA = "OUTBOUND_SECTION_DATA";

    public static final String PARAM_HOOK_URL = "submiturl";

    public static boolean isCxmlSession(JaloSession jaloSession) {
        return jaloSession.getAttribute(ATTR_IS_CXML_LOGIN) != null && !(jaloSession.getAttribute(ATTR_IS_CXML_LOGIN).equals(Boolean.FALSE));
    }

    public static void cxmlLogin(HttpServletRequest request) throws CxmlException, OciException, IOException, JaloConnectException {

        JaloSession jalo = WebSessionFunctions.getSession(request);
        CxmlOutboundSection outboundSection = new CxmlOutboundSection(getRequestParameters(request), PARAM_HOOK_URL);

        if (StringUtils.isEmpty(outboundSection.getField(PARAM_HOOK_URL))) {
            throw new CxmlException("missing request parameter: " + PARAM_HOOK_URL);

        }

        login(request, outboundSection);

        jalo.setAttribute(ATTR_IS_CXML_LOGIN, Boolean.TRUE);
        jalo.setAttribute(ATTR_OUTBOUND_SECTION_DATA, outboundSection);

        return;
    }

    private static void login(final HttpServletRequest request, final CxmlOutboundSection outboundSection) throws CxmlException {
        HashMap loginprops = new HashMap();

        loginprops.put("user.principal", outboundSection.getField("USERNAME"));
        loginprops.put("user.credentials", outboundSection.getField("PASSWORD"));
        loginprops.put("user.pk", null);

        try {
            WebSessionFunctions.getSession(request).transfer(loginprops);
        } catch (JaloSecurityException localJaloSecurityException) {
            throw new CxmlException("Login failed.");
        } catch (JaloInvalidParameterException localJaloInvalidParameterException) {
            throw new CxmlException("Wrong parameter in Map");
        } catch (JaloConnectException localJaloConnectException) {
            throw new CxmlException("Unable to get jalo session!");
        }

    }

    private static Map getRequestParameters(HttpServletRequest request) {
        Map result = new HashMap();
        Map paramMap = request.getParameterMap();
        for (Iterator it = paramMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String[] values = (String[]) entry.getValue();
            if ((values == null) || (values.length <= 0)) {
                continue;
            }
            result.put(entry.getKey(), values[0]);

        }

        return result;
    }

    public static String createCxmlOrderMessage(final CartModel cart, CxmlOutboundSection outboundSection) throws CxmlException {
        if (!isCxmlSession(JaloSession.getCurrentSession())) {
            throw new CxmlException("The current jaloSession is not an Cxml Session");
        }

        return CxmlUtil.generateCXMLData(cart, outboundSection);
    }
}
