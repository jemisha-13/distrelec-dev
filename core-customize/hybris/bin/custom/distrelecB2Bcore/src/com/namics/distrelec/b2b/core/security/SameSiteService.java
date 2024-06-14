package com.namics.distrelec.b2b.core.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SameSiteService {

    boolean is3rdPartyCookieAccessRequired();

    boolean isUserAgentSupported(HttpServletRequest request);

    void allow3rdPartyCookieAccess(HttpServletRequest request, HttpServletResponse response);

}
