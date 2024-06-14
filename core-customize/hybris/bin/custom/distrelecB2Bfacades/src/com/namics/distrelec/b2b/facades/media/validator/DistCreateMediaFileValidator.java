package com.namics.distrelec.b2b.facades.media.validator;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.media.validator.CreateMediaFileValidator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.validation.Errors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DistCreateMediaFileValidator extends CreateMediaFileValidator {

    protected final static String IMAGE_MEDIA_GROUP = "image";
    protected final static String MEDIA_IMAGE_UPLOAD_MAXSIZE_ERROR = "media.image.upload.maxsize.error";

    private ConfigurationService configurationService;

    @Override
    public void validate(Object obj, Errors errors) {
        super.validate(obj, errors);

        final MediaFileDto mediaFileDto = (MediaFileDto) obj;

        if (isImage(mediaFileDto.getMime())) {
            Long fileSize = mediaFileDto.getSize();
            Long maxSize = getImageUploadMaxSize();
            if (maxSize > 0L && fileSize > maxSize) {
                boolean newError = true;
                if (newError) {
                    errors.rejectValue(INPUT_STREAM, MEDIA_IMAGE_UPLOAD_MAXSIZE_ERROR);
                } else {
                    errors.rejectValue(INPUT_STREAM, CmsfacadesConstants.MEDIA_INPUT_STREAM_CLOSED);
                }
            }
        }
    }

    protected boolean isImage(String mime) {
        return isNotBlank(mime) && mime.startsWith(IMAGE_MEDIA_GROUP);
    }

    protected Long getImageUploadMaxSize() {
        return getConfigurationService().getConfiguration().getLong(DistConfigConstants.Media.MEDIA_IMAGE_UPLOAD_MAXSIZE, 0L);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
