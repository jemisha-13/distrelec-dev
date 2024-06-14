package com.distrelec.tomcat.valves;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.RequestFilterValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.ServletException;
import java.io.IOException;

public class HacRemoteAddrValve extends RequestFilterValve {

    public static final String HAC = "/hac";

    private static final Log log = LogFactory.getLog(HacRemoteAddrValve.class);

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String contextPath = request.getContextPath();
        if (contextPath.startsWith(HAC)) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null) {
                String[] splittedXForwardedFor = xForwardedFor.split(", *");
                for (String xForwardedForIp : splittedXForwardedFor) {
                    if (isAllowed(xForwardedForIp)) {
                        getNext().invoke(request, response);
                        return;
                    }
                }
            }
            String property;
            if (getAddConnectorPort()) {
                property = request.getRequest().getRemoteAddr() + ";" + request.getConnector().getPort();
            } else {
                property = request.getRequest().getRemoteAddr();
            }

            process(property, request, response);
        } else {
            getNext().invoke(request, response);
        }
    }

    @Override
    protected Log getLog() {
        return log;
    }
}
