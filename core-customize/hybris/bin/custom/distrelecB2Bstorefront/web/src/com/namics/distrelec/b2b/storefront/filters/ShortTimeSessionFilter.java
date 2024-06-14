package com.namics.distrelec.b2b.storefront.filters;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.util.SearchRobotDetector;
import de.hybris.platform.jalo.JaloSession;
import org.apache.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * A filter to create short time sessions. Search robots (recognized by HTTP header "User-Agent") are not dealing with cookies. Hence, a new
 * session will be created for each request. In order to save (memory) resources we reduce the session live time. The same behavior can also
 * be achieved by adding a special query parameter (?shortTimeSession=true).
 * 
 * @author ascherrer
 * @since Distrelec, v2.0.28
 */
public class ShortTimeSessionFilter extends GenericFilterBean {

    private static final Logger LOG = Logger.getLogger(ShortTimeSessionFilter.class.getName());

    private static final String HTTP_HEADER_USER_AGENT = "User-Agent";
    private static final int DEFAULT_TIMEOUT = 5 * 60; // 5min
    private static final int ADDITIONAL_TIMEOUT_FOR_JALO_SESSION = 60; // JaloSession has to outlive the http session

    private SearchRobotDetector searchRobotDetector;
    private int timeout = DEFAULT_TIMEOUT;

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException,
            ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            final HttpServletRequest request = (HttpServletRequest) servletRequest;
            final HttpSession session = request.getSession(false);

            if (session != null && session.isNew()) {
                if (isReduceTimeoutRequired(request)) {
                    final int timeoutInSeconds = getTimeout();
                    setSessionTimeout(session, timeoutInSeconds);
                }
                if (LOG.isDebugEnabled()) {
                    logSessionData(request);
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isReduceTimeoutRequired(final HttpServletRequest request) {
        final String timeoutParam = request.getParameter(WebConstants.SHORT_TIME_SESSION_PARAM);

        if (timeoutParam != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Short time session requested by query parameter [" + WebConstants.SHORT_TIME_SESSION_PARAM + "]");
            }
            return true;
        }

        return getSearchRobotDetector().isSearchRobot(request);
    }

    private int getSessionTimeout(final HttpSession session) {
        if (session != null) {
            return session.getMaxInactiveInterval();
        } else {
            return 0;
        }
    }

    private void setSessionTimeout(final HttpSession session, final int newTimeout) {
        if (session != null) {
            session.setMaxInactiveInterval(newTimeout);
            JaloSession.getCurrentSession().setTimeout(newTimeout + ADDITIONAL_TIMEOUT_FOR_JALO_SESSION);
        }
    }

    private void logSessionData(final HttpServletRequest request) {
        final int timeout = getSessionTimeout(request.getSession(false));
        final String userAgent = request.getHeader(HTTP_HEADER_USER_AGENT);
        final String serverName = request.getServerName();
        final int serverPort = request.getServerPort();
        final String requestURL = request.getRequestURL().toString();
        final String remoteAddr = request.getRemoteAddr();
        final String remoteHost = request.getRemoteHost();

        final StringBuffer buf = new StringBuffer(300);
        buf.append("New HTTP session created: ");
        buf.append("timeout [").append(timeout).append("] | ");
        buf.append("userAgent [").append(userAgent).append("] | ");
        buf.append("serverName [").append(serverName).append("] | ");
        buf.append("serverPort [").append(serverPort).append("] | ");
        buf.append("requestURL [").append(requestURL).append("] | ");
        buf.append("remoteAddr [").append(remoteAddr).append("] | ");
        buf.append("remoteHost [").append(remoteHost).append("].");

        LOG.debug(buf.toString());
    }

    public SearchRobotDetector getSearchRobotDetector() {
        return searchRobotDetector;
    }

    public void setSearchRobotDetector(final SearchRobotDetector searchRobotDetector) {
        this.searchRobotDetector = searchRobotDetector;
    }

    private int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

}
