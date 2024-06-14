package com.namics.distrelec.b2b.core.media.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.media.DistMediaDao;
import com.namics.distrelec.b2b.core.media.DistMediaService;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaService;

public class DefaultDistMediaService extends DefaultMediaService implements DistMediaService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistMediaService.class);
    
    private static final String IMAGE_MIME_PATTERN = "image/";

    private static final String IMAGE_SVG_MIME = "image/svg";
    
    @Autowired
    private DistMediaDao distMediaDao;

    @Override
    public List<DistVideoMediaModel> searchVideoMedia(String term, int page, int pageSize, CatalogVersionModel catalogVersion) {
        return distMediaDao.searchVideoMedia(term, page, pageSize, catalogVersion);
    }

    @Override
    public DistVideoMediaModel findVideoMedia(String code, CatalogVersionModel catalogVersion) {
        try {
            return distMediaDao.findVideoMedia(code, catalogVersion);
        } catch (UnknownIdentifierException e) {
            return null;
        }
    }
    
    @Override
    public void setStreamForMedia(MediaModel mediaModel, InputStream data) {
        super.setStreamForMedia(mediaModel, data);
        updateImageDimensions(mediaModel);
    }
    
    @Override
    public void updateImageDimensions(MediaModel mediaModel) {
        if (StringUtils.isNotEmpty(mediaModel.getMime()) && (mediaModel.getMime().contains(IMAGE_MIME_PATTERN) && !mediaModel.getMime().startsWith(IMAGE_SVG_MIME))) {
            BufferedImage img = null;
            try {
                img = ImageIO.read(this.getStreamFromMedia(mediaModel));
                mediaModel.setHeight(img.getHeight());
                mediaModel.setWidth(img.getWidth());
                this.modelService.save(mediaModel);
            } catch (IOException e) {
                LOG.error("Cannot load Image" + e.getMessage());
            }
        }
    }

}
