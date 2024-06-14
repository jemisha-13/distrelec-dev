package com.namics.distrelec.b2b.core.security.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.namics.distrelec.b2b.core.security.IpAddressService;

@Service
public class IpAddressServiceImpl implements IpAddressService {

    private static final String[] REQUEST_HEADERS_TO_TRY = { "X-Client-IP", "X-Forwarded-By", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
                                                             "REMOTE_ADDR",
                                                             "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
                                                             "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA" };

    @Override
    public String getClientIpAddress() {
        return Arrays.stream(REQUEST_HEADERS_TO_TRY)
                     .map(header -> getIpWithHeader(header, getRequest()))
                     .filter(StringUtils::isNotEmpty)
                     .collect(Collectors.joining(" | "));
    }

    @Override
    public HttpServletRequest getRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                       .map(ServletRequestAttributes.class::cast)
                       .map(ServletRequestAttributes::getRequest)
                       .orElse(null);
    }

    private String getIpWithHeader(String header, HttpServletRequest request) {
        if (nonNull(request)) {
            String ip = Optional.ofNullable(request.getHeader(header)).orElse(EMPTY);
            return isIpValid(ip) ? header + " : " + ip : EMPTY;
        }
        return EMPTY;
    }

    private boolean isIpValid(String ip) {
        return StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip);
    }
}
