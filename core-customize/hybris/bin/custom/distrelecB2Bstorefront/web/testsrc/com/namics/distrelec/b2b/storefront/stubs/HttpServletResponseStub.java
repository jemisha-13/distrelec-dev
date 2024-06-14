/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.stubs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseStub implements HttpServletResponse {
    private final List<Cookie> cookies;
    private boolean committed;

    public HttpServletResponseStub() {
        super();
        cookies = new ArrayList<Cookie>();
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public void flushBuffer() throws IOException {
        //
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    public void setCommitted(final boolean isCommitted) {
        this.committed = isCommitted;
    }

    @Override
    public void reset() {
        //
    }

    @Override
    public void resetBuffer() {
        //
    }

    @Override
    public void setBufferSize(final int arg0) {
        //
    }

    @Override
    public void setCharacterEncoding(final String arg0) {
        //
    }

    @Override
    public void setContentLength(final int arg0) {
        //
    }

    @Override
    public void setContentLengthLong(long l) {
        //
    }

    @Override
    public void setContentType(final String arg0) {
        //
    }

    @Override
    public void setLocale(final Locale arg0) {
        //
    }

    @Override
    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public void addDateHeader(final String arg0, final long arg1) {
        //
    }

    @Override
    public void addHeader(final String arg0, final String arg1) {
        //
    }

    @Override
    public void addIntHeader(final String arg0, final int arg1) {
        //
    }

    @Override
    public boolean containsHeader(final String arg0) {
        return false;
    }

    @Override
    public String encodeRedirectURL(final String arg0) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(final String arg0) {
        return null;
    }

    @Override
    public String encodeURL(final String arg0) {
        return null;
    }

    @Override
    public String encodeUrl(final String arg0) {
        return null;
    }

    @Override
    public void sendError(final int arg0) throws IOException {
        //
    }

    @Override
    public void sendError(final int arg0, final String arg1) throws IOException {
        //
    }

    @Override
    public void sendRedirect(final String arg0) throws IOException {
        //
    }

    @Override
    public void setDateHeader(final String arg0, final long arg1) {
        //
    }

    @Override
    public void setHeader(final String arg0, final String arg1) {
        //
    }

    @Override
    public void setIntHeader(final String arg0, final int arg1) {
        //
    }

    @Override
    public void setStatus(final int arg0) {
        //
    }

    @Override
    public void setStatus(final int arg0, final String arg1) {
        //
    }

    @Override
    public String getHeader(String arg0) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String arg0) {
        return null;
    }

    @Override
    public int getStatus() {
        return 0;
    }
}
