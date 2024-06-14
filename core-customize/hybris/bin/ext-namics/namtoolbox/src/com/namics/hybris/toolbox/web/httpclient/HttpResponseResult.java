/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.web.httpclient;

public class HttpResponseResult {

    private String content;
    private int httpStatus;

    public HttpResponseResult(final String content, final int httpStatus) {
        super();
        this.content = content;
        this.httpStatus = httpStatus;
    }

    public HttpResponseResult() {
        super();
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public void setHttpStatus(final int httpStatus) {
        this.httpStatus = httpStatus;
    }

}
