package com.namics.distrelec.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;

import java.io.IOException;
import java.io.OutputStream;

class ServerHttpResponseWrapper implements ServerHttpResponse {

    private final ServerHttpResponse delegate;

    ServerHttpResponseWrapper(ServerHttpResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setStatusCode(HttpStatus httpStatus) {
        delegate.setStatusCode(httpStatus);
    }

    @Override
    public void flush() throws IOException {
        // intentionally do not delegate method call
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public OutputStream getBody() throws IOException {
        return delegate.getBody();
    }

    @Override
    public HttpHeaders getHeaders() {
        return delegate.getHeaders();
    }

    void flushDelegate() throws IOException {
        delegate.flush();
    }

    ServerHttpResponse getDelegate() {
        return delegate;
    }
}
