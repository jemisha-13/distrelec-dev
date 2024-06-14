package com.namics.distrelec.b2b.core.jalo.media;

import java.io.InputStream;
import java.net.URL;

import com.google.common.base.Preconditions;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.media.MediaSource;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.MediaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 * Extended because some medias are served directly from tomcat, not from hybris. Only {@link #getUrl(MediaSource)}
 * method was introduced.
 */
public class DistMediaManager extends MediaManager {

    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;
    private ConfigurationService configurationService;

    private static final String PIM_MEDIA_CONTAINER_NAME = "stibopim.media.container";

    @Override
    public InputStream getMediaAsStream(MediaSource mediaSource) {
        if (this.hasData(mediaSource)) {
            return this.getMediaAsStream(mediaSource.getFolderQualifier(), mediaSource.getLocation());
        } else {
            final String[] parts = StringUtils.split(StringUtils.substringAfterLast(mediaSource.getInternalUrl(), "/Web"), "/", 2);
            final String folderQualifier = configurationService.getConfiguration().getString(PIM_MEDIA_CONTAINER_NAME);
            StringBuilder sb = new StringBuilder();
            if (mediaSource.getInternalUrl().contains("WebShopImages")) {
                sb.append("images/");
            } else if (mediaSource.getInternalUrl().contains("Downloads")) {
                sb.append("documents/");
            } else if (mediaSource.getInternalUrl().contains("Streams")) {
                sb.append("videos/");
            } else if (mediaSource.getInternalUrl().contains("Audio")) {
                sb.append("audios/");
            }
            sb.append(parts[1]);
            final String location = sb.toString();
            return getMediaAsStream(folderQualifier, location);
        }
    }

    private InputStream tryToGetStreamFromUrl(MediaSource mediaSource) {
        Preconditions.checkState(StringUtils.isNotEmpty(mediaSource.getInternalUrl()), "media does not have URL, cannot get the stream");

        try {
            URL url = null;
            String localMediaRootUrl = MediaUtil.getLocalMediaWebRootUrl();
            String resource = mediaSource.getInternalUrl();
            if (resource.startsWith(localMediaRootUrl)) {
                resource = resource.substring(localMediaRootUrl.length());
                if (resource.charAt(0) == '/') {
                    resource = resource.substring(1);
                }

                if (resource.startsWith("fromjar")) {
                    resource = resource.substring("fromjar".length());
                    url = this.getClass().getResource(resource);
                }
            }

            if (url == null) {
                url = new URL(getUrl(mediaSource));
            }

            return url.openStream();
        } catch (Exception var5) {
            throw new IllegalStateException(var5.getMessage(), var5);
        }
    }

    private String getUrl(MediaSource mediaSource) {
        String internalUrl = mediaSource.getInternalUrl();

        if (internalUrl.startsWith("/")) {
            UriComponents uriComponents = ServletUriComponentsBuilder
                .fromUriString(getDistSiteBaseUrlResolutionService().getLocalMediaUrl(true))
                .path(internalUrl)
                .build();

            return uriComponents.toUriString();
        } else {
            return internalUrl;
        }
    }

    protected DistSiteBaseUrlResolutionService getDistSiteBaseUrlResolutionService() {
        return distSiteBaseUrlResolutionService;
    }

    @Required
    public void setDistSiteBaseUrlResolutionService(DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService) {
        this.distSiteBaseUrlResolutionService = distSiteBaseUrlResolutionService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
