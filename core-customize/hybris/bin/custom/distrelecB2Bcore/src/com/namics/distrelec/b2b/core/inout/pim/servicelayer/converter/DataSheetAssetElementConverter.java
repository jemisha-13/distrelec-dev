/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;
import com.namics.distrelec.b2b.core.model.DistAssetTypeModel;
import com.namics.distrelec.b2b.core.model.DistDocumentTypeModel;
import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;
import com.namics.distrelec.b2b.core.service.media.DistMediaFormatService;

/**
 * Converts a data sheet "Asset" XML element into a hybris {@link DistDownloadMediaModel}.
 * 
 * @author ceberle, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DataSheetAssetElementConverter extends AbstractMediaAssetElementConverter<DistDownloadMediaModel> {

    private static final Logger LOG = LogManager.getLogger(DataSheetAssetElementConverter.class);

    private static final String ASSET_TYPE = "UserTypeID";
    private static final String XP_DOCUMENT_TYPE = "Values/ValueGroup[@AttributeID='10_datasheetdocumenttype_lov']/Value[@LOVQualifierID='eng' or @LOVQualifierID='std.lang.all']";
    private static final String XP_URL = "AssetPushLocation[@ConfigurationID='downloads']"; // "AssetPushLocation[@ConfigurationID='streams']"

    @Autowired
    private DistrelecCodelistService codelistService;

    @Autowired
    private DistMediaFormatService distMediaFormatService;

    protected static final Map<String, String> DOCUMENT_TYPE_MAP = new HashMap<String, String>();
    static {
        DOCUMENT_TYPE_MAP.put("ATEX-certificate", "certificates");
        DOCUMENT_TYPE_MAP.put("Brochure", "brochures");
        DOCUMENT_TYPE_MAP.put("CE-certificate", "certificates");
        DOCUMENT_TYPE_MAP.put("Datasheet", "datasheets");
        DOCUMENT_TYPE_MAP.put("Manual", "manuals");
        DOCUMENT_TYPE_MAP.put("Upgrade", "software");
        DOCUMENT_TYPE_MAP.put("Material Datasheet", "datasheets");
        DOCUMENT_TYPE_MAP.put("Product Datasheet", "datasheets");
        DOCUMENT_TYPE_MAP.put("Test report template", "templates");
        DOCUMENT_TYPE_MAP.put("REACH Statement", "certificates");
        DOCUMENT_TYPE_MAP.put("Safety Datasheet", "datasheets");
        DOCUMENT_TYPE_MAP.put("Software", "software");
        DOCUMENT_TYPE_MAP.put("Driver", "software");
        DOCUMENT_TYPE_MAP.put("Certificate", "certificates");
        DOCUMENT_TYPE_MAP.put("Instruction manual", "manuals");
        // DISTRELEC-11385 Additional document types.
        DOCUMENT_TYPE_MAP.put("Excel", "brochures");
        DOCUMENT_TYPE_MAP.put("Energy label", "certificates");
        DOCUMENT_TYPE_MAP.put("CAD File", "cad_and_drawings");
        DOCUMENT_TYPE_MAP.put("Drawing", "cad_and_drawings");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.AbstractMediaAssetElementConverter#convertSpecialAttibutes(org.dom4j.
     * Element, de.hybris.platform.core.model.media.MediaModel, com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext,
     * java.lang.String)
     */
    @Override
    public void convertSpecialAttibutes(final Element source, final DistDownloadMediaModel target, final ImportContext importContext, final String hash) {
        // Set the document type.
        setDocumentType(source, target);
        // Set the asset type.
        setAssetType(source, target);
        // Set languages and media format.
        target.setLanguages(getLanguages(source));
        target.setMediaFormat(getDistMediaFormatService().getMediaFormatForQualifier(DistConstants.MediaFormat.PDF));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.AbstractMediaAssetElementConverter#setMediaURLs(org.dom4j.Element,
     * de.hybris.platform.core.model.media.MediaModel)
     */
    @Override
    public void setMediaURLs(final Element source, final DistDownloadMediaModel target) {
        final String pushLocation = source.valueOf(XP_URL);
        final String externalUrl = source.valueOf(XP_EXTERNAL_URL);
        if (StringUtils.isNotEmpty(pushLocation)) {
            target.setURL(ImageAssetElementConverter.URL_PREFIX + pushLocation);
        } else if (StringUtils.isNotEmpty(externalUrl)) {
            target.setURL(externalUrl);
        } else {
            LOG.error("Could not resolve push location for download media [{}]", new Object[] { getId(source) });
            throw new ElementConverterException("Error converting download media");
        }
    }

    /**
     * Set the media document type.
     * 
     * @param source
     * @param target
     */
    private void setDocumentType(final Element source, final DistDownloadMediaModel target) {
        final DistDocumentTypeModel documentType = getDocumentType(source);
        if (documentType != null) {
            target.setDocumentType(documentType);
        } else {
            LOG.error("Could not resolve document type for download media [{}]", new Object[] { getId(source) });
            throw new ElementConverterException("Error converting download media");
        }
    }

    /**
     * Set the media asset type.
     * 
     * @param source
     * @param target
     */
    private void setAssetType(final Element source, final DistDownloadMediaModel target) {
        final DistAssetTypeModel assetType = getAssetType(source);
        if (assetType != null) {
            target.setAssetType(assetType);
        } else {
            LOG.error("Could not resolve asset type for download media [{}]", new Object[] { getId(source) });
            throw new ElementConverterException("Error converting download media");
        }
    }

    /**
     * Retrieve the asset type from the XML element
     * 
     * @param source
     * @return the media asset type
     */
    private DistAssetTypeModel getAssetType(final Element source) {
        final String code = source.attributeValue(ASSET_TYPE).toLowerCase();
        if (StringUtils.isEmpty(code)) {
            LOG.debug("No asset type set on xml element {}", new Object[] { source.asXML() });
            return null;
        }

        try {
            return getCodelistService().getDistrelecAssetType(code);
        } catch (final NotFoundException e) {
            LOG.debug("Could not get asset type for code [{}]", new Object[] { code }, e);
            return null;
        }
    }

    /**
     * Retrieve the document type.
     * 
     * @param source
     * @return the media document type.
     */
    private DistDocumentTypeModel getDocumentType(final Element source) {

        final XPath xpath = source.createXPath(XP_DOCUMENT_TYPE);
        final List<Element> values = xpath.selectNodes(source);

        if (CollectionUtils.isEmpty(values)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No document type set on xml element {}", source.asXML());
            }
            return null;
        }

        final String value = values.get(0).getStringValue();
        if (StringUtils.isEmpty(value)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No document type set on xml element {}", source.asXML());
            }
            return null;
        }

        final String code = DOCUMENT_TYPE_MAP.get(value);
        if (code == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No document type code mapping for value [{}]", value);
            }
            return null;
        }

        try {
            return getCodelistService().getDistrelecDocumentType(code);
        } catch (final NotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Could not get document type for code [{}] and xml value [{}]", new Object[] { code, value }, e);
            }
            return null;
        }

    }

    // Getters & Setters

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    public DistMediaFormatService getDistMediaFormatService() {
        return distMediaFormatService;
    }

    public void setDistMediaFormatService(DistMediaFormatService distMediaFormatService) {
        this.distMediaFormatService = distMediaFormatService;
    }
}
