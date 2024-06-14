package com.distrelec.webexports.filters;

import com.namics.distrelec.b2b.core.constants.DistConstants.AzureMediaFolder;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.media.exceptions.MediaInvalidLocationException;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.media.url.impl.LocalMediaWebURLStrategy;
import de.hybris.platform.media.web.MediaFilterLogicContext;
import de.hybris.platform.servicelayer.web.WebAppMediaFilter;
import de.hybris.platform.util.MediaUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BlobStorageExportMediaFilter extends WebAppMediaFilter {
    private static final Logger LOG = LoggerFactory.getLogger(BlobStorageExportMediaFilter.class);

    @Override
    protected void doFilterMedia(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            Iterable<String> mediaContext) throws IOException, ServletException {
        processStandardResponse(httpRequest, httpResponse, mediaContext);
    }

    @Override
    protected void loadFromMediaStorage(final HttpServletResponse httpResponse, final Iterable<String> mediaContext) {
        final String folderQualifier = getContextPart(ContextPart.FOLDER, mediaContext);
        final String location = WebConstants.EXPORT_MEDIA_URL + "/" + getContextPart(ContextPart.LOCATION, mediaContext);

        try (InputStream inputStream = getMediaAsStreamWithSize(folderQualifier, location).getInputStream()) {
            LOG.debug("Loading resource [location: '{}'] from blob storage.", location);

            IOUtils.copy(inputStream, httpResponse.getOutputStream());
        } catch (final MediaInvalidLocationException e) {
            sendForbiddenResponseStatus(httpResponse, e);
        } catch (final MediaNotFoundException e) {
            sendResourceNotFoundResponseStatus(httpResponse, e);
        } catch (final Exception e) {
            LOG.warn("Unexpected error while fetching blob storage medias: {}", e.getMessage());

            if (LOG.isDebugEnabled()) {
                LOG.debug("Error details", e);
            }

            sendBadRequestResponseStatus(httpResponse, e);
        }
    }

    @Override
    protected Iterable<String> getMediaContext(HttpServletRequest httpRequest) {
        final String resourcePath = MediaUtil.addLeadingFileSepIfNeeded(getLegacyResourcePath(httpRequest));

        final List<String> result = new ArrayList<>(6);
        result.add(getTenantId());
        result.add(AzureMediaFolder.EXPORTS);
        result.add(null);
        result.add(LocalMediaWebURLStrategy.NO_CTX_PART_MARKER);
        result.add(resourcePath.substring(1));
        result.add(LocalMediaWebURLStrategy.NO_CTX_PART_MARKER);
        result.add("");

        return result;
    }

    @Override
    protected void verifyHash(MediaFilterLogicContext ctx) {
        // not needed
    }

    @Override
    protected boolean isMedia(String resourcePath) {
        return !"/".equals(resourcePath);
    }

    @Override
    protected boolean isSecureMedia(String resourcePath) {
        return false;
    }

    private String getTenantId() {
        return Registry.getCurrentTenantNoFallback().getTenantID();
    }
}
