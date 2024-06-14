/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.core.service.media.DistMediaContainerService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Converts a manufacturer "Product" XML element into a hybris {@link DistImage360Model}.
 * 
 * @author pforster, Namics AG
 * @since Distrelec 3.0
 */
public class ProductImage360ElementConverter implements PimImportElementConverter<DistImage360Model> {

    private static final Logger LOG = LogManager.getLogger(ProductImage360ElementConverter.class);

    private static final String XP_ROWS = "Values/Value[@AttributeID='robot05_row']";
    private static final String XP_COLUMNS = "Values/Value[@AttributeID='robot06_col']";
    private static final String XP_PATTERN = "Values/Value[@AttributeID='robot07_pattern']";
    private static final String XP_360_IMAGES = "AssetCrossReference[@Type='360_image']";
    private static final String XP_ASSET_ID = "@AssetID";

    @Autowired
    private DistMediaContainerService distMediaContainerService;

    @Override
    public String getId(final Element element) {
        return element.attributeValue(ATTRIBUTE_ID);
    }

    @Override
    public void convert(Element source, DistImage360Model target, ImportContext importContext, String hash) {
        try {
            target.setColumns(Integer.valueOf(source.valueOf(XP_COLUMNS)));
            target.setPattern(source.valueOf(XP_PATTERN));
            target.setPimXmlHashMaster(hash);
            target.setPimHashTimestamp(new Date());
            target.setRows(Integer.valueOf(source.valueOf(XP_ROWS)));

            List<Node> imageNodes = source.selectNodes(XP_360_IMAGES);
            if (imageNodes == null) {
                LOG.warn("Image 360 Element [" + target.getCode() + "] doesn't contain images");
            } else {
                target.setMediaContainers(getMediaContainers(importContext, imageNodes));
            }
        } catch(Exception e) {
            LOG.error("converter error:" + source, e);
        }
    }

    private Set<MediaContainerModel> getMediaContainers(ImportContext importContext, List<Node> imageNodes) {
        Set<MediaContainerModel> mediaContainers = new LinkedHashSet<>();
        for (Node imageNode : imageNodes) {
            String imageId = imageNode.valueOf(XP_ASSET_ID);
            try {
                MediaContainerModel mediaContainer = distMediaContainerService.getMediaContainerForQualifier(importContext.getProductCatalogVersion(), imageId);
                mediaContainers.add(mediaContainer);
            } catch (UnknownIdentifierException e) {
                LOG.warn("Could not find MediaContainer for qualifier [" + imageId + "] and catalog version [" + importContext.getProductCatalogVersion()
                        + "]: " + e.getMessage());
            }
        }
        return mediaContainers;
    }

    public DistMediaContainerService getDistMediaContainerService() {
        return distMediaContainerService;
    }

    public void setDistMediaContainerService(DistMediaContainerService distMediaContainerService) {
        this.distMediaContainerService = distMediaContainerService;
    }
}
