package com.namics.distrelec.b2b.core.media;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Strategy for returning redirects to another environment if the current environment is a local development.
 */
public interface RedirectOnLocalStrategy {

    /**
     * Redirects to another environment if the current environment is a local development.
     * 
     * @return boolean indicating if a redirect is returned
     */
    boolean redirectIfLocal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException;
}
