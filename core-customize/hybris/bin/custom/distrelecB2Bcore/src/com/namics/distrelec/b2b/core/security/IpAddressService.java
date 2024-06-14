package com.namics.distrelec.b2b.core.security;

import javax.servlet.http.HttpServletRequest;

public interface IpAddressService {
    HttpServletRequest getRequest();

    String getClientIpAddress();
}
