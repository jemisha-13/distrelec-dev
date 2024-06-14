package com.namics.distrelec.b2b.storefront.etag;

import javax.servlet.http.HttpServletResponse;

public interface ETagHeaderService {

    void setCacheControlHeader(HttpServletResponse response);

}
