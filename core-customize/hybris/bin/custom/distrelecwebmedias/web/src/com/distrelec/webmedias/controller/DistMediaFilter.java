package com.distrelec.webmedias.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.namics.distrelec.b2b.core.media.RedirectOnLocalStrategy;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.suspend.SystemIsSuspendedException;
import de.hybris.platform.core.threadregistry.OperationInfo;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.media.exceptions.MediaInvalidLocationException;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.media.services.MediaHeadersRegistry;
import de.hybris.platform.media.web.DefaultMediaFilterLogic;
import de.hybris.platform.media.web.MediaFilterLogicContext;
import de.hybris.platform.mediaweb.MediaFilter;
import de.hybris.platform.mediaweb.SecureResponseWrapper;

public class DistMediaFilter extends MediaFilter {

    private static final Logger LOG = LoggerFactory.getLogger(DistMediaFilter.class);

    private static final Splitter CTX_SPLITTER = Splitter.on("|");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = new SecureResponseWrapper((HttpServletResponse) response);

            try {
                RegistrableThread.registerThread(OperationInfo.builder().withCategory(OperationInfo.Category.WEB_REQUEST).asNotSuspendableOperation().build());
            } catch (SystemIsSuspendedException var13) {
                LOG.info("System is {}. Request will not be processed.", var13.getSystemStatus());
                httpResponse.sendError(503, "Application is in the SUSPENDED state");
                return;
            }

            try {
                doFilterMedia(httpRequest, httpResponse, getMediaContext(httpRequest));
            } catch (IllegalArgumentException var11) {
                sendBadRequestResponseStatus(httpResponse, var11);
            } finally {
                RegistrableThread.unregisterThread();
            }
        } else {
            chain.doFilter(request, response);
        }

    }

    private void doFilterMedia(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Iterable<String> mediaContext) throws IOException,
                                                                                                                                ServletException {
        setCurretTenantByID(mediaContext);
        if (getMediaManager().isSecuredFolder(getContextPart(DistContextPart.FOLDER, mediaContext))) {
            httpResponse.setStatus(403);
        } else {
            String resourcePath = getResourcePath(httpRequest);
            if (isResourceFromClassLoader(resourcePath)) {
                loadFromClassLoader(httpResponse, resourcePath);
            } else {
                String responseETag = generateETag(getContextPart(DistContextPart.LOCATION, mediaContext));
                httpResponse.setHeader("ETag", responseETag);
                String requestETag = httpRequest.getHeader("If-None-Match");
                if (responseETag.equals(requestETag)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("ETag [{}] equal to If-None-Match, sending 304", responseETag);
                    }

                    readConfiguredHeaderParamsAndWriteToResponse(httpResponse);
                    modifyResponseWithConfiguredHeaders(httpResponse);
                    httpResponse.setStatus(304);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("ETag [{}] is not equal to If-None-Match, sending standard response", responseETag);
                    }

                    processStandardResponse(httpRequest, httpResponse, mediaContext);
                }
            }
        }

    }

    private Iterable<String> getMediaContext(HttpServletRequest httpRequest) {
        String encodedMediaCtx = getLocalMediaWebUrlContextParam(httpRequest);
        return encodedMediaCtx == null ? createLegacyLocalMediaWebUrlContext(httpRequest) : createLocalMediawebUrlContext(encodedMediaCtx);
    }

    private Iterable<String> createLegacyLocalMediaWebUrlContext(HttpServletRequest httpRequest) {
        String resourcePath = getLegacyResourcePath(httpRequest);
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        return splitLegacyPath(resourcePath);
    }

    private String getLegacyResourcePath(HttpServletRequest httpRequest) {
        String resourcePath = httpRequest.getServletPath();
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            String reqURI = httpRequest.getRequestURI();
            String ctxPath = httpRequest.getContextPath();
            resourcePath = reqURI.replace(ctxPath, "");
        }

        return resourcePath;
    }

    private Iterable<String> createLocalMediawebUrlContext(String encodedMediaCtx) {
        Preconditions.checkArgument(!GenericValidator.isBlankOrNull(encodedMediaCtx), "incorrect media context in request");
        Iterable<String> mediaContext = CTX_SPLITTER.split(decodeBase64(encodedMediaCtx));
        Preconditions.checkArgument(Iterables.size(mediaContext) == 6, "incorrect media context in request");
        List<String> mediaContextList = new ArrayList();
        Iterator var4 = mediaContext.iterator();

        while (var4.hasNext()) {
            String e = (String) var4.next();
            mediaContextList.add(e);
        }

        mediaContextList.set(DistContextPart.LOCATION.getPartNumber(), (String) mediaContextList.get(DistContextPart.LOCATION.getPartNumber()));
        return mediaContextList;
    }

    private String getLocalMediaWebUrlContextParam(HttpServletRequest httpRequest) {
        return httpRequest.getParameter("context");
    }

    private String generateETag(String location) {
        HashFunction md5 = Hashing.md5();
        return md5.hashUnencodedChars(location).toString();
    }

    private String getResourcePath(HttpServletRequest httpRequest) {
        String resourcePath = httpRequest.getServletPath();
        if (GenericValidator.isBlankOrNull(resourcePath)) {
            String reqURI = httpRequest.getRequestURI();
            String ctxPath = httpRequest.getContextPath();
            resourcePath = reqURI.replace(ctxPath, "");
        }

        return resourcePath;
    }

    private void processStandardResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Iterable<String> mediaContext) throws IOException,
                                                                                                                                          ServletException {
        String resourcePath = getResourcePath(httpRequest);
        MediaFilterLogicContext ctx = new MediaFilterLogicContext(mediaContext, resourcePath);

        try {
            verifyHash(ctx);
            readConfiguredHeaderParamsAndWriteToResponse(httpResponse);
            addContentDisposition(httpRequest, httpResponse, resourcePath);
            addContentType(httpResponse, ctx);
            loadFromMediaStorage(httpResponse, mediaContext);
        } catch (MediaInvalidLocationException var7) {
            sendForbiddenResponseStatus(httpResponse, var7);
        } catch (MediaNotFoundException var8) {
            sendResourceNotFoundResponseStatus(httpRequest, httpResponse, var8);
        } catch (Exception var9) {
            sendBadRequestResponseStatus(httpResponse, var9);
        }

    }

    private void readConfiguredHeaderParamsAndWriteToResponse(HttpServletResponse httpResponse) throws UnsupportedEncodingException {
        MediaHeadersRegistry mediaHeadersRegistry = getMediaManager().getMediaHeadersRegistry();
        if (mediaHeadersRegistry != null) {
            Map<String, String> headerParams = mediaHeadersRegistry.getHeaders();
            Iterator var4 = headerParams.entrySet().iterator();

            while (var4.hasNext()) {
                Map.Entry<String, String> me = (Map.Entry) var4.next();
                httpResponse.setHeader((String) me.getKey(), (String) me.getValue());
            }
        }

    }

    private void addContentDisposition(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String resourcePath) {
        if (isAddContentDisposition(httpRequest, resourcePath)) {
            String var10002 = getRealFileNameFromResource(resourcePath);
            httpResponse.addHeader("Content-Disposition", " attachment; filename=" + var10002);
        }

    }

    private boolean isAddContentDisposition(HttpServletRequest httpRequest, String resourcePath) {
        boolean addContentDisposition = Boolean.parseBoolean(httpRequest.getParameter("attachment"));
        if (!addContentDisposition) {
            Set<String> extensions = getAllowedExtensions("media.force.download.dialog.fileextensions");
            if (extensions != null && !extensions.isEmpty()) {
                String lowerCaseResource = resourcePath.toLowerCase();
                Iterator var6 = extensions.iterator();

                while (var6.hasNext()) {
                    String ext = (String) var6.next();
                    if (lowerCaseResource.endsWith(ext)) {
                        return true;
                    }
                }
            }
        }

        return addContentDisposition;
    }

    /**
     * @deprecated
     */
    @Deprecated(since = "2011", forRemoval = true)
    protected void addContentType(HttpServletResponse httpResponse, Iterable<String> mediaContext, String resourcePath) {
        addContentType(httpResponse, new MediaFilterLogicContext(mediaContext, resourcePath));
    }

    protected void addContentType(HttpServletResponse httpResponse, MediaFilterLogicContext ctx) {
        getMediaFilterLogic().addContentType(httpResponse, ctx);
        setXContentTypeOptionsHeader(httpResponse);
    }

    protected DefaultMediaFilterLogic getMediaFilterLogic() {
        return (DefaultMediaFilterLogic) Registry.getApplicationContext().getBean("mediaFilterLogic", DefaultMediaFilterLogic.class);
    }

    protected RedirectOnLocalStrategy getRedirectOnLocalStrategy() {
        return Registry.getApplicationContext().getBean("redirectOnLocalStrategy", RedirectOnLocalStrategy.class);
    }

    private boolean isResourceFromClassLoader(String resourcePath) {
        return resourcePath != null && resourcePath.contains("/fromjar");
    }

    private String getRealFileNameFromResource(String resourcePath) {
        int index = resourcePath.lastIndexOf("/");
        return index >= 0 ? resourcePath.substring(index + 1) : resourcePath;
    }

    private void loadFromClassLoader(HttpServletResponse httpResponse, String resourcePath) throws IOException {
        String resourceName = resourcePath.substring(resourcePath.indexOf("/fromjar") + 1 + "fromjar".length());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Trying to load resource '{}' from classloader.", resourceName);
        }

        PolicyFactory policy = (new HtmlPolicyBuilder()).toFactory();
        ServletOutputStream var10000;
        String var10001;
        if (isDeniedByExtensionForClassloader(resourceName)) {
            httpResponse.setContentType("text/plain");
            setXContentTypeOptionsHeader(httpResponse);
            modifyResponseWithConfiguredHeaders(httpResponse, "text/plain");
            var10000 = httpResponse.getOutputStream();
            var10001 = policy.sanitize(resourceName);
            var10000.println("not allowed to load media '" + var10001
                             + "' from classloader. Check parameter media.allowed.extensions.for.ClassLoader in advanced.properties to change the file extensions (e.g. *.gif) that are allowed to download.");
        } else {
            InputStream inputStream = null;

            try {
                inputStream = getResourceAsStream(resourceName);
                if (inputStream == null) {
                    httpResponse.setContentType("text/plain");
                    setXContentTypeOptionsHeader(httpResponse);
                    modifyResponseWithConfiguredHeaders(httpResponse, "text/plain");
                    var10000 = httpResponse.getOutputStream();
                    var10001 = policy.sanitize(resourceName);
                    var10000.println("file '" + var10001 + "' not found!");
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Loading resource '{}' from classloader.", resourceName);
                    }

                    modifyResponseWithConfiguredHeaders(httpResponse);
                    IOUtils.copy(inputStream, httpResponse.getOutputStream());
                }
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }

    }

    private boolean isDeniedByExtensionForClassloader(String resourceName) {
        Set<String> extensions = getAllowedExtensions("media.allowed.extensions.for.ClassLoader");
        if (extensions != null && !extensions.isEmpty()) {
            String check = resourceName.toLowerCase();
            Iterator var4 = extensions.iterator();

            String ext;
            do {
                if (!var4.hasNext()) {
                    return true;
                }

                ext = (String) var4.next();
            } while (!check.endsWith(ext));

            return false;
        } else {
            return false;
        }
    }

    private void loadFromMediaStorage(HttpServletResponse httpResponse, Iterable<String> mediaContext) throws MediaNotFoundException {
        InputStream inputStream = null;

        try {
            String folderQualifier = getContextPart(DistContextPart.FOLDER, mediaContext);
            String location = getContextPart(DistContextPart.LOCATION, mediaContext);
            String contentType = httpResponse.getContentType();
            verifyFolderIsNotSecured(folderQualifier);
            MediaManager.InputStreamWithSize inputStreamWithSize = getMediaAsStreamWithSize(folderQualifier, location);
            inputStream = inputStreamWithSize.getInputStream();
            httpResponse.setContentLengthLong(inputStreamWithSize.getSize());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Loading resource [location: {}] from media storage.", location);
            }

            modifyResponseWithConfiguredHeaders(httpResponse, contentType, folderQualifier);
            IOUtils.copy(inputStream, httpResponse.getOutputStream());
        } catch (MediaInvalidLocationException var13) {
            sendForbiddenResponseStatus(httpResponse, var13);
        } catch (MediaNotFoundException mediaNotFoundException) {
            throw mediaNotFoundException;
        } catch (Exception var15) {
            sendBadRequestResponseStatus(httpResponse, var15);
        } finally {
            IOUtils.closeQuietly(inputStream);
            unsetCurrentTenant();
        }

    }

    private void verifyFolderIsNotSecured(String folderQualifier) {
        if (getMediaManager().isFolderConfiguredAsSecured(folderQualifier)) {
            throw new MediaInvalidLocationException("Access to file denied");
        }
    }

    private void verifyHash(MediaFilterLogicContext ctx) {
        if (!isBasedOnPrettyUrl(ctx.getMediaContext())) {
            getMediaFilterLogic().verifyHash(ctx);
        }
    }

    private boolean isBasedOnPrettyUrl(Iterable<String> mediaContext) {
        return mediaContext != null && Iterables.size(mediaContext) > 6 && INTERNAL_PRETTY_URL_MARKER.equals(Iterables.get(mediaContext, 6));
    }

    private MediaManager.InputStreamWithSize getMediaAsStreamWithSize(String folderQualifier, String location) {
        return getMediaManager().getMediaAsStreamWithSize(folderQualifier, location);
    }

    private String decodeBase64(String value) {
        String decodedValue = "";
        if (StringUtils.isNotBlank(value)) {
            try {
                decodedValue = new String((new Base64(-1, (byte[]) null, true)).decode(value));
            } catch (Exception var4) {
                throw new RuntimeException("Cannot decode base32 coded string: " + value);
            }
        }

        return decodedValue;
    }

    private String getContextPart(DistContextPart contextPart, Iterable<String> mediaContext) {
        return (String) Iterables.get(mediaContext, contextPart.getPartNumber());
    }

    private void sendForbiddenResponseStatus(HttpServletResponse httpResponse, Exception exception) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Access forbidden for given media", exception);
        }

        httpResponse.setStatus(403);
    }

    private void sendBadRequestResponseStatus(HttpServletResponse httpResponse, Exception exception) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("The request sent by the client was syntactically incorrect", exception);
        }

        httpResponse.setStatus(400);
    }

    private void sendResourceNotFoundResponseStatus(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Exception exception) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Requested resource not found", exception);
        }

        boolean isRedirected = getRedirectOnLocalStrategy().redirectIfLocal(httpRequest, httpResponse);

        if (!isRedirected) {
            httpResponse.setStatus(404);
        }
    }

    private Set<String> getAllowedExtensions(String configParameter) {
        String str = getConfig().getParameter(configParameter);
        Set extensions;
        if (StringUtils.isBlank(str)) {
            extensions = Collections.emptySet();
        } else {
            Set<String> set = new LinkedHashSet();
            StringTokenizer tok = new StringTokenizer(str, ",;");

            while (tok.hasMoreTokens()) {
                String strElement = tok.nextToken().toLowerCase().trim();
                if (strElement.length() > 0) {
                    set.add(strElement);
                }
            }

            extensions = Collections.unmodifiableSet(set);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("{}: Supported media extensions: {}", configParameter, extensions.toString());
        }

        return extensions;
    }

    private enum DistContextPart {
        TENANT(0),
        FOLDER(1),
        SIZE(2),
        MIME(3),
        LOCATION(4),
        LOCATION_HASH(5);

        private final int partNumber;

        private DistContextPart(int partNumber) {
            this.partNumber = partNumber;
        }

        public int getPartNumber() {
            return partNumber;
        }
    }
}
