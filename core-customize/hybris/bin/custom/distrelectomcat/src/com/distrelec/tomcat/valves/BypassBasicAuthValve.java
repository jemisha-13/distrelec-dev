package com.distrelec.tomcat.valves;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.valves.RequestFilterValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class BypassBasicAuthValve extends RequestFilterValve {

    private static final Log log = LogFactory.getLog(BypassBasicAuthValve.class);

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (!"/authorizationserver/oauth/token".equals(request.getRequestURI())) {
            boolean internalUser = false;

            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null) {
                String[] splittedXForwardedFor = xForwardedFor.split(", *");
                for (String xForwardedForIp : splittedXForwardedFor) {
                    if (isAllowed(xForwardedForIp)) {
                        internalUser = true;
                        break;
                    }
                }
            }
            if (!internalUser) {
                String property = request.getRequest().getRemoteAddr();
                if (isAllowed(property)) {
                    internalUser = true;
                }
            }

            if (internalUser) {
                setInternalUser(request);
            }
        }

        getNext().invoke(request, response);
    }

    @Override
    protected Log getLog() {
        return log;
    }

    protected void setInternalUser(Request request) {
        final String username = "distrelec";
        final String credentials = "credentials";
        final List<String> roles = new ArrayList<>();
        roles.add("tomcat");

        final Principal principal = new GenericPrincipal(username,
                credentials, roles);
        request.setUserPrincipal(principal);

        MimeHeaders mimeHeaders = request.getCoyoteRequest().getMimeHeaders();
        MessageBytes authorization = mimeHeaders.getValue("authorization");
        if (authorization != null && !authorization.startsWithIgnoreCase("bearer", 0)) {
            mimeHeaders.removeHeader("authorization");
        }
    }
}
