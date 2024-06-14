/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaContainerService;

/**
 * Converts a manufacturer "Product" XML element into a hybris {@link DistManufacturerModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ProductManufacturerElementConverter extends AbstractElementConverter implements PimImportElementConverter<DistManufacturerModel> {

    private static final Logger LOG = LogManager.getLogger(ProductManufacturerElementConverter.class);

    private static final String XP_NAME = "Name";
    private static final String XP_ASSET_ID = "AssetCrossReference[@Type='herstellerlogo1']/@AssetID";

    private static final String XP_PROMOTION_MANUFACTURER = "Values/Value[@AttributeID='promotionman_txt']";

    private static final String WEB_DESCRIPTION = "Values/Value[@AttributeID='manufacturer_web_descr']";
    private static final String XP_MANUFACTURER_META_DESC = "Values/ValueGroup[@AttributeID='DIS_MFTRMetaDesc']/Value";
    private static final String XP_MANUFACTURER_META_TITLE = "Values/ValueGroup[@AttributeID='DIS_MFTRMetaTitle']/Value";
    public static final int MAX_COLUMN_LENGTH_DEFAULT = 255;

    private MediaContainerService mediaContainerService;
    
    @Autowired
    private ConverterLanguageUtil converterLanguageUtil;

    @Override
    public String getId(final Element element) {
        return element.attributeValue(ATTRIBUTE_ID);
    }

    @Override
    public void convert(final Element source, final DistManufacturerModel target, final ImportContext importContext, final String hash) {
        final String manufacturerCode = target.getCode();
        final String name = source.elementTextTrim(XP_NAME);
            
        target.setName(name);
        target.setNameSeo(UrlResolverUtils.normalize(name, true));

        final String mediaContainerQualifier = source.valueOf(XP_ASSET_ID);
        if (StringUtils.isNotBlank(mediaContainerQualifier)) {
            try {
                target.setPrimaryImage(mediaContainerService.getMediaContainerForQualifier(mediaContainerQualifier));
            } catch (final UnknownIdentifierException e) {
                LOG.error("Could not find MediaContainer with qualifier {}", mediaContainerQualifier);
            }
        }

        Map<Locale, String> seoProductTitleValues = getConverterLanguageUtil().getLocalizedValues(source, XP_MANUFACTURER_META_TITLE);
        seoProductTitleValues.forEach((locale, value) -> {
            target.setSeoMetaTitle(truncate(value, MAX_COLUMN_LENGTH_DEFAULT, manufacturerCode, XP_MANUFACTURER_META_TITLE), locale);
        });

        Map<Locale, String> seoProductDescValues = getConverterLanguageUtil().getLocalizedValues(source, XP_MANUFACTURER_META_DESC);
        seoProductDescValues.forEach((locale, value) -> {
            target.setSeoMetaDescription(truncate(value, MAX_COLUMN_LENGTH_DEFAULT, manufacturerCode, XP_MANUFACTURER_META_DESC), locale);
        });

        Map<Locale, String> promotionTextValues = getConverterLanguageUtil().getLocalizedValues(source, XP_PROMOTION_MANUFACTURER);
        promotionTextValues.forEach(((locale, value) -> {
            target.setPromotionText(value, locale);
        }));

        Map<Locale, String> webDescriptionValues = getConverterLanguageUtil().getLocalizedValues(source, WEB_DESCRIPTION);
        webDescriptionValues.forEach(((locale, value) -> {
            target.setWebDescription(value, locale);
        }));

        target.setPimXmlHashMaster(hash);
        target.setPimHashTimestamp(new Date());
    }

    @Autowired
    public void setMediaContainerService(final MediaContainerService mediaContainerService) {
        this.mediaContainerService = mediaContainerService;
    }

    public ConverterLanguageUtil getConverterLanguageUtil() {
        return converterLanguageUtil;
    }

    public void setConverterLanguageUtil(ConverterLanguageUtil converterLanguageUtil) {
        this.converterLanguageUtil = converterLanguageUtil;
    }
    
    
}
