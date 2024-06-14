package com.namics.distrelec.b2b.core.mail.internal.converter;

import com.namics.distrelec.b2b.core.mail.internal.data.MissingImageMediaFileData;
import com.namics.distrelec.b2b.core.util.DistMediaUtil;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class DistMissingImageMediaFileConverter extends AbstractPopulatingConverter<String, MissingImageMediaFileData> {

    @Override
    public void populate(String source, MissingImageMediaFileData target) {
        String[] parts = StringUtils.split(StringUtils.substringAfterLast(source, "/Web/"), "/", 2);
        if (ArrayUtils.getLength(parts) != 2) {
            throw new IllegalArgumentException("Invalid internal URL: " + source);
        }
        target.setMediaFolder(parts[0]);
        target.setMediaPath(parts[1]);
        target.setMediaLocation(getMediaLocation(parts[0], parts[1]));
        target.setMediaName(StringUtils.substringAfterLast(parts[1], "/"));
    }

    private String getMediaLocation(String mediaFolder, String mediaPath) {
        StringBuilder sb = new StringBuilder();
        sb.append(DistMediaUtil.replaceMediaFolder(mediaFolder));
        sb.append(StringUtils.substringBeforeLast(mediaPath, "/"));
        sb.append("/");
        return sb.toString();
    }
}
