package com.namics.distrelec.b2b.core.media.url.impl;

import com.google.common.base.Preconditions;
import de.hybris.platform.media.MediaSource;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.url.PrettyUrlStrategy;
import de.hybris.platform.media.url.impl.LocalMediaWebURLStrategy;
import de.hybris.platform.media.url.impl.PrettyUrlStrategyFactory;
import de.hybris.platform.util.MediaUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;

import java.util.Objects;
import java.util.Optional;

/**
 * Reverts local media web url strategy to an older implementation. The main difference in the context calculation. The newer version encodes a location:
 * <pre>builder.append("|").append(this.encodeAsString(this.getCtxPartOrNullMarker(mediaSource.getLocation()).getBytes(StandardCharsets.UTF_8)));</pre>
 */
public class DistLocalMediaWebURLStrategy extends LocalMediaWebURLStrategy {

    @Override
    public String getUrlForMedia(MediaFolderConfig config, MediaSource mediaSource) {
        Preconditions.checkArgument(config != null, "Folder config is required to perform this operation");
        Preconditions.checkArgument(mediaSource != null, "MediaSource is required to perform this operation");
        return assembleUrl(config.getFolderQualifier(), mediaSource);
    }

    private String assembleUrl(String folderQualifier, MediaSource mediaSource) {
        if (!GenericValidator.isBlankOrNull(folderQualifier) && !GenericValidator.isBlankOrNull(mediaSource.getLocation())) {
            if (isPrettyUrlEnabled()) {
                Optional<String> prettyUrl = assembleLegacyURL(folderQualifier, mediaSource);
                if (prettyUrl.isPresent()) {
                    return (String) prettyUrl.get();
                }
            }

            return assembleURLWithMediaContext(folderQualifier, mediaSource);
        } else {
            return "";
        }
    }

    private Optional<String> assembleLegacyURL(String folderQualifier, MediaSource mediaSource) {
        StringBuilder sb = new StringBuilder(MediaUtil.addTrailingFileSepIfNeeded(getMediaWebRootContext()));
        PrettyUrlStrategy.MediaData mediaData = new PrettyUrlStrategy.MediaData(getTenantId(), folderQualifier, mediaSource.getLocation());
        mediaData.setRealFileName(mediaSource.getRealFileName());
        Optional<String> prettyPath = PrettyUrlStrategyFactory.getDefaultPrettyUrlStrategy().assemblePath(mediaData);
        Objects.requireNonNull(sb);
        return prettyPath.map(sb::append).map(Object::toString);
    }

    private String assembleURLWithMediaContext(String folderQualifier, MediaSource mediaSource) {
        StringBuilder builder = new StringBuilder(MediaUtil.addTrailingFileSepIfNeeded(getMediaWebRootContext()));
        String realFilename = getRealFileNameForMedia(mediaSource);
        if (realFilename != null) {
            builder.append(realFilename);
        }

        builder.append("?").append("context").append("=");
        builder.append(assembleMediaLocationContext(folderQualifier, mediaSource));
        return builder.toString();
    }

    private String getRealFileNameForMedia(MediaSource mediaSource) {
        String realFileName = mediaSource.getRealFileName();
        return StringUtils.isNotBlank(realFileName) ? MediaUtil.normalizeRealFileName(realFileName) : null;
    }

    private String assembleMediaLocationContext(String folderQualifier, MediaSource mediaSource) {
        StringBuilder builder = new StringBuilder(getTenantId());
        builder.append("|").append(folderQualifier.replace("|", ""));
        builder.append("|").append(mediaSource.getSize());
        builder.append("|").append(getCtxPartOrNullMarker(mediaSource.getMime()));
        builder.append("|").append(getCtxPartOrNullMarker(mediaSource.getLocation()));
        builder.append("|").append(getCtxPartOrNullMarker(mediaSource.getLocationHash()));
        return (new Base64(-1, (byte[]) null, true)).encodeAsString(builder.toString().getBytes());
    }

    private String getCtxPartOrNullMarker(String ctxPart) {
        return StringUtils.isNotBlank(ctxPart) ? ctxPart : "-";
    }
}
