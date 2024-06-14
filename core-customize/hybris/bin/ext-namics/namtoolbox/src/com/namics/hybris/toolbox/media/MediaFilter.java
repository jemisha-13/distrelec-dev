/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.media;

import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.util.MediaUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;

/**
 * This MediaFilter adds caching properties to the given response header so that it improves the performance, because the server has not to
 * deliver the medias for each request.
 * 
 * This MediaFilter can be used if the corresponding web.xml of the mediaweb extension is overwritten. The best practice is to do that over
 * the environment and the given configure scripts. You have to overwrite the configuration with the following settings:
 * 
 * ########################################### <filter> <filter-name>MediaFilter</filter-name>
 * <filter-class>com.namics.hybris.toolbox.media.MediaFilter</filter-class> <init-param>
 * <param-name>allowedExtensionsWhenUsingClassLoader</param-name>
 * <param-value>jpeg,jpg,gif,bmp,tiff,vcard,templ,tif,csv,eps,pdf,png</param-value> <!-- <description>if using /fromjar/.. to load files
 * from the appserver classloader, allow only the given extensions, separated by comma </description> --> </init-param> <init-param>
 * <param-name>cacheTTL</param-name> <param-value>86400</param-value> </init-param> </filter>
 * 
 * <filter-mapping> <filter-name>MediaFilter</filter-name> <url-pattern>/*</url-pattern> </filter-mapping>
 * ###########################################
 * 
 * @author rhusi, namics ag
 * @since MGB MEL 1.2
 * 
 */
public class MediaFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(MediaFilter.class);
    private static final String MIME_PARAM = "mime";
    private static final String REALNAME_PARAM = "realname";
    private static final String ALLOWEDEXTENSIONS_PARAM = "allowedExtensionsWhenUsingClassLoader";
    private static final String CACHE_TTL = "cacheTTL";
    private FilterConfig filterConfig;
    private File mediaReadDir;
    private Set<String> allowedExtensions;
    private String cacheTTL = "86400";

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.mediaReadDir = MediaUtil.getMediaReadDir();

        if (LOG.isInfoEnabled()) {
            LOG.info("Using media.read.dir: " + getMediaReadDir());
        }

        final String str = getFilterConfig().getInitParameter(ALLOWEDEXTENSIONS_PARAM);
        if (str != null) {
            final Set<String> set = new LinkedHashSet<String>();
            for (final StringTokenizer tok = new StringTokenizer(str, ",;"); tok.hasMoreTokens();) {
                final String s = tok.nextToken().toLowerCase();
                if (s.length() <= 0) {
                    continue;
                }
                set.add(s);
            }

            this.allowedExtensions = Collections.unmodifiableSet(set);
            LOG.info("Allowed media extensions: " + getAllowedExtensions().toString());
        } else {
            this.allowedExtensions = null;
            LOG.info("Allowed media extensions: ALL");
        }

        cacheTTL = getFilterConfig().getInitParameter(CACHE_TTL) != null ? getFilterConfig().getInitParameter(CACHE_TTL) : cacheTTL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if ((!(request instanceof HttpServletRequest)) || (!(response instanceof HttpServletResponse))) {
            chain.doFilter(request, response);
            return;
        }

        final HttpServletResponse resp = (HttpServletResponse) response;
        final String real = request.getParameter(REALNAME_PARAM);
        final String mime = request.getParameter(MIME_PARAM);

        if ((real != null) && (mime != null)) {
            resp.setContentType(mime);
            resp.addHeader("Content-Disposition", mime + "; filename=" + real);
        }

        // Set caching
        setCachingSettings(resp);

        // Get real resource name
        final String resourceName = getResourceName(request);

        if (("".equals(resourceName)) || ("/".equals(resourceName)) || ("index.html".equalsIgnoreCase(resourceName))
                || ("/index.html".equalsIgnoreCase(resourceName))) {
            chain.doFilter(request, response);
        } else if (resourceName.contains("/fromjar")) {
            // Resource from jar
            getContentFromJar(resp, resourceName);
        } else {
            // Resource from file
            getContentFromFile(resp, resourceName);
        }
    }

    private void setCachingSettings(final HttpServletResponse resp) {
        final FastDateFormat fdFormat = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss z", TimeZone.getTimeZone("GMT"), Locale.US);
        final long millis = java.lang.System.currentTimeMillis();

        Date actDate = null;
        if (getCacheTTL() != null) {
            actDate = new Date(millis + Long.parseLong(getCacheTTL()) * 1000);
        } else {
            actDate = new Date(millis);
        }

        resp.setHeader("Expires", fdFormat.format(actDate));
        resp.setHeader("Cache-Control", "max-age=" + getCacheTTL());
    }

    private String getResourceName(final ServletRequest request) {
        String resourceName = ((HttpServletRequest) request).getServletPath();
        if ((resourceName == null) || (resourceName.trim().length() == 0)) {
            final String reqURI = ((HttpServletRequest) request).getRequestURI();
            final String ctxPath = ((HttpServletRequest) request).getContextPath();
            resourceName = reqURI.replace(ctxPath, "");
        }

        // Get real resource name
        // return MediaUtil.replaceToHumanReadable(resourceName);
        return MediaUtil.normalizeRealFileName(resourceName);
    }

    private void getContentFromJar(final HttpServletResponse resp, final String resourceName) throws IOException {
        final ServletOutputStream os = resp.getOutputStream();
        final String properResourceName = resourceName.substring(resourceName.indexOf("/fromjar") + 1 + "fromjar".length());

        if (isDeniedByExtension(properResourceName)) {
            resp.setContentType("text/plain");
            os.println("not allowed to load media '" + properResourceName
                    + "'. Check parameter allowedExtensionsWhenUsingClassLoader in web.xml of mediaweb to change the file extensions (e.g. *.gif) that are allowed to download.");
            return;
        }

        final InputStream is = Media.class.getResourceAsStream(properResourceName);
        if (is == null) {
            resp.setContentType("text/plain");
            resp.getOutputStream().println("File " + properResourceName + " not found!");
            return;
        }

        final byte[] buffer = new byte[4096];
        int r = 0;

        while ((r = is.read(buffer)) != -1) {
            os.write(buffer, 0, r);
        }
        os.flush();
        is.close();
    }

    private void getContentFromFile(final HttpServletResponse resp, final String resourceName) throws IOException {
        InputStream fis = null;
        InputStream bis = null;
        try {
            final File mediaFile = new File(getMediaReadDir(), resourceName);
            if (!(mediaFile.exists())) {
                LOG.warn("MediaFile '" + mediaFile.getAbsolutePath() + "' not found!");
                return;
            }
            if (!(mediaFile.canRead())) {
                LOG.error("MediaFile '" + mediaFile.getAbsolutePath() + "' not readable!");
                return;
            }

            fis = new FileInputStream(mediaFile);
            bis = new BufferedInputStream(fis);
            final OutputStream os = resp.getOutputStream();

            final Collection<?> mimeTypes = MimeUtil.getMimeTypes(mediaFile);
            final MimeType mimeType = MimeUtil.getMostSpecificMimeType(mimeTypes);
            resp.setContentType(mimeType.toString());

            final byte[] buffer = new byte[4096];
            int r = 0;
            while ((r = bis.read(buffer)) != -1) {
                os.write(buffer, 0, r);
            }
            os.flush();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    private boolean isDeniedByExtension(final String resourceName) {
        boolean denied = false;
        if (getAllowedExtensions() != null) {
            denied = true;
            final String check = resourceName.toLowerCase();
            for (final String t : getAllowedExtensions()) {
                if (!(check.endsWith(t))) {
                    continue;
                }
                denied = false;
                break;
            }
        }

        return denied;
    }

    private FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    private Set<String> getAllowedExtensions() {
        return this.allowedExtensions;
    }

    private String getCacheTTL() {
        return this.cacheTTL;
    }

    private File getMediaReadDir() {
        return this.mediaReadDir;
    }

}