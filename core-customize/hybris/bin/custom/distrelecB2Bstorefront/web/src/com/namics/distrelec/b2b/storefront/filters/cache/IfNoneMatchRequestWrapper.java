package com.namics.distrelec.b2b.storefront.filters.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Removes gzip suffix added by apache deflate, to be able to match etags.
 */
class IfNoneMatchRequestWrapper extends HttpServletRequestWrapper {

    public static final String IF_NONE_MATCH_HEADER = "If-None-Match";
    public static final String GZIP_SUFFIX = "-gzip\"";

    public IfNoneMatchRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> originHeaders = super.getHeaders(name);

        if (IF_NONE_MATCH_HEADER.equalsIgnoreCase(name) && originHeaders.hasMoreElements()) {
            return removeGzipSuffixes(originHeaders);
        } else {
            return originHeaders;
        }
    }

    protected Enumeration<String> removeGzipSuffixes(Enumeration<String> headers) {
        List<String> filteredHeaders = new ArrayList<>();
        headers.asIterator().forEachRemaining(header -> filteredHeaders.add(removeGzipSuffix(header)));
        return Collections.enumeration(filteredHeaders);
    }

    protected String removeGzipSuffix(String header) {
        if (header.endsWith(GZIP_SUFFIX)) {
            return header.substring(0, header.length() - GZIP_SUFFIX.length()) + "\"";
        } else {
            return header;
        }
    }
}
