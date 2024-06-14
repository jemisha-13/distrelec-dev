package com.distrelec.webpimmedias.filters;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import de.hybris.platform.core.Registry;
import de.hybris.platform.media.exceptions.MediaInvalidLocationException;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.url.impl.LocalMediaWebURLStrategy;
import de.hybris.platform.media.web.MediaFilterLogicContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.web.WebAppMediaFilter;
import de.hybris.platform.util.MediaUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BlobStorageStaticMediaFilter extends WebAppMediaFilter {
    private static final Logger LOG = LoggerFactory.getLogger(BlobStorageStaticMediaFilter.class);

    private static final String ALLOWED_Folders_KEY = "distrelecstorefront.static-cloud-content.allowed-folders";

    private static final String PIM_MEDIA_CONTAINER_NAME = "stibopim.media.container";

    private static final String EXPORTS_CONTAINER_NAME = "exports.container";

    private static final String BLOB_PREFIX_KEY = "blob-path-prefix";

    private static final String SITEMAP_PATH = "export.batch.sitemap.products.folder.target";

    private MediaStorageConfigService mediaStorageConfigService;

    private ConfigurationService configurationService;

    private final Supplier<Set<String>> allowedFolder = Suppliers.memoizeWithExpiration(() -> {
        final String[] names = StringUtils.split(getConfig().getString(ALLOWED_Folders_KEY, null), " ,;");

        return names != null ? Sets.newHashSet(names) : Collections.emptySet();
    }, 1, TimeUnit.MINUTES);

    @Override
    protected void loadFromMediaStorage(final HttpServletResponse httpResponse, final Iterable<String> mediaContext) {
        final String folderQualifier = getContextPart(ContextPart.FOLDER, mediaContext);
        final String location = prepareLocation(mediaContext);

        LOG.debug("Loading resource [location: '{}'] from blob storage.", location);

        try (InputStream inputStream = getMediaAsStreamWithSize(folderQualifier, location).getInputStream()) {
            LOG.debug("Loaded resource [location: '{}'] from blob storage.", location);
            IOUtils.copy(inputStream, httpResponse.getOutputStream());
        } catch (final MediaInvalidLocationException e) {
            LOG.debug("Invalid media location [location: '{}']", location);
            sendForbiddenResponseStatus(httpResponse, e);
        } catch (final MediaNotFoundException e) {
            LOG.debug("Media not found [location: '{}']", location);
            sendResourceNotFoundResponseStatus(httpResponse, e);
        } catch (final Exception e) {
            LOG.warn("Unexpected error while fetching blob storage medias: {}", e.getMessage());

            if (LOG.isDebugEnabled()) {
                LOG.debug("Error details", e);
            }

            sendBadRequestResponseStatus(httpResponse, e);
        }
    }

    private String prepareLocation(final Iterable<String> mediaContext) {
        String rawLocation = getContextPart(ContextPart.LOCATION, mediaContext);
        String locationWithPlusEncoded = rawLocation.replaceAll("\\+", "%2B");
        return URLDecoder.decode(locationWithPlusEncoded, StandardCharsets.UTF_8);
    }

    @Override
    protected Iterable<String> getMediaContext(HttpServletRequest httpRequest) {
        final String resourcePath = MediaUtil.addLeadingFileSepIfNeeded(getLegacyResourcePath(httpRequest));
        final String[] parts = StringUtils.split(resourcePath, "/", 2);

        if (ArrayUtils.getLength(parts) != 2) {
            throw new IllegalArgumentException("Invalid media path in URL");
        }

        final String container = parts[0];
        String mediaPath = parts[1];

        if (!isAllowedFolderRequest(container)) {
            throw new IllegalArgumentException("Cannot access blob container requested in URL");
        }

        String sitemapPath = configurationService.getConfiguration().getString(SITEMAP_PATH, "sitemap");

        if (sitemapPath.equals(container)) {
            return getSitemapContext(container, mediaPath);
        } else {
            return getMediaContext(container, mediaPath);
        }

    }

    private Iterable<String> getMediaContext(String mediaContainer, String mediaPath) {

        final MediaStorageConfigService.MediaFolderConfig mediaFolderConfig = mediaStorageConfigService.getConfigForFolder(mediaContainer);
        if (mediaFolderConfig != null) {
            String blobPrefix = mediaFolderConfig.getParameter(BLOB_PREFIX_KEY);

            if (StringUtils.isNotEmpty(blobPrefix)) {
                blobPrefix = MediaUtil.removeLeadingFileSepIfNeeded(blobPrefix);
                blobPrefix = MediaUtil.addTrailingFileSepIfNeeded(blobPrefix);

                mediaPath = blobPrefix + mediaPath;
            }
        }

        final List<String> result = new ArrayList<>(6);
        result.add(getTenantId());
        result.add(getConfig().getString(PIM_MEDIA_CONTAINER_NAME, null));
        result.add(null);
        StringBuilder sb = new StringBuilder();
        if ("WebShopImages".equals(mediaContainer)) {
            if (StringUtils.substringAfterLast(mediaPath, ".").equals("jpg") || !mediaPath.contains(".")) { // Temporary condition for HDLS-1804
                result.add("image/jpeg");
            } else if (StringUtils.substringAfterLast(mediaPath, ".").equals("tif")) {
                result.add("image/tiff");
            } else if (StringUtils.substringAfterLast(mediaPath, ".").equals("webp")) {
                result.add("image/webp");
            }
            sb.append("images/");
        } else if ("Downloads".equals(mediaContainer)) {
            if (StringUtils.substringAfterLast(mediaPath, ".").equals("pdf")) {
                result.add("application/pdf");
            } else if (StringUtils.substringAfterLast(mediaPath, ".").equals("zip")) {
                result.add("application/zip");
            }
            sb.append("documents/");
        } else if ("Streams".equals(mediaContainer)) {
            if (StringUtils.substringAfterLast(mediaPath, ".").equals("wmv")) {
                result.add("video/x-ms-wmv");
            } else if (StringUtils.substringAfterLast(mediaPath, ".").equals("mpg")) {
                result.add("video/mpeg");
            } else {
                result.add("application/octet-stream");
            }
            sb.append("videos/");
        } else if ("Audio".equals(mediaContainer)) {
            if (StringUtils.substringAfterLast(mediaPath, ".").equals("mp3")) {
                result.add("audio/mpeg");
            }
            sb.append("audios/");
        }
        sb.append(mediaPath);
        mediaPath = sb.toString();
        result.add(mediaPath);
        result.add(LocalMediaWebURLStrategy.NO_CTX_PART_MARKER);
        result.add("");
        result.add("pimmedias");

        return result;
    }

    private Iterable<String> getSitemapContext(String sitemapContainer, String mediaPath) {
        final List<String> result = new ArrayList<>(6);
        result.add(getTenantId());
        result.add(getConfig().getString(EXPORTS_CONTAINER_NAME, null));
        result.add(null);

        if (StringUtils.substringAfterLast(mediaPath, ".").equals("xml")) {
            result.add("application/xml");
        } else if (StringUtils.substringAfterLast(mediaPath, ".").equals("gz")) {
            result.add("application/gzip");
        }
        result.add(sitemapContainer + "/" + mediaPath);
        result.add(LocalMediaWebURLStrategy.NO_CTX_PART_MARKER);
        result.add("");

        return result;
    }

    @Override
    protected void verifyHash(MediaFilterLogicContext ctx) {
        // not needed
    }

    private boolean isAllowedFolderRequest(String container) {
        return container != null && allowedFolder.get().contains(StringUtils.lowerCase(container));
    }

    private String getTenantId() {
        return Registry.getCurrentTenantNoFallback().getTenantID();
    }

    @Override
    protected boolean isMedia(String resourcePath) {
        return true;
    }

    @Override
    protected boolean isSecureMedia(String resourcePath) {
        return false;
    }

    @Override
    protected boolean isResourceFromClassLoader(String resourcePath) {
        return false;
    }

    @Required
    public void setMediaStorageConfigService(MediaStorageConfigService mediaStorageConfigService) {
        this.mediaStorageConfigService = mediaStorageConfigService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
