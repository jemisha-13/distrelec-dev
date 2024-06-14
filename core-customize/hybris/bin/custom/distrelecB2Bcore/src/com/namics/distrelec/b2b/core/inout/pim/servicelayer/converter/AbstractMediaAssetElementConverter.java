/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code AbstractMediaAssetElementConverter}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public abstract class AbstractMediaAssetElementConverter<T extends MediaModel> implements PimImportElementConverter<T> {

    private static final Logger LOG = LogManager.getLogger(AbstractMediaAssetElementConverter.class);

    protected static final String ASSET_TYPE = "UserTypeID";
    protected static final String XP_DESCRIPTION = "Values/ValueGroup[@AttributeID='bmecat_ds_description']/Value";
    protected static final String XP_EXTERNAL_URL = "Values/Value[@AttributeID='35_assetexternallyurl_txt']";
    protected static final String XP_NAME = "Name";
    protected static final String XP_LANGUAGE = "Values/MultiValue[@AttributeID='40_datasheetlanguages_lov']/Value";
    private static final String DEFAULT_LANGUAGE_CODE="EN";
    
    @Autowired
    @Qualifier("commonI18NService")
    private CommonI18NService commonI18NService;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Autowired
    private ModelService modelService;
    
    @Autowired
	private ConverterLanguageUtil converterLanguageUtil;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter#getId(org.dom4j.Element)
     */
    @Override
    public String getId(final Element element) {
        return element.attributeValue(ATTRIBUTE_ID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter#convert(org.dom4j.Element,
     * java.lang.Object, com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext, java.lang.String)
     */
    @Override
    public void convert(final Element source, final T target, final ImportContext importContext, final String hash) {

        // Set the media URLs
        setMediaURLs(source, target);
        // Convert media special attributes
        convertSpecialAttibutes(source, target, importContext, hash);
        // Set common attributes
        target.setAltText(source.elementTextTrim(XP_NAME));
        Map<Locale, String> descriptionValues = getConverterLanguageUtil().getLocalizedValues(source, XP_DESCRIPTION);
        Locale defaultLocale=getCommonI18NService().getLocaleForIsoCode(DEFAULT_LANGUAGE_CODE);
        String value=descriptionValues.get(defaultLocale);
        target.setDescription(value);
        target.setPimXmlHashMaster(hash);
        target.setPimHashTimestamp(new Date());
    }

    /**
     * Set the media URLs.
     * 
     * @param source
     * @param target
     */
    public abstract void setMediaURLs(final Element source, final T target);

    /**
     * Convert media special attributes. Depending on the media type, some attributes does not exist in the other media types.
     * 
     * @param source
     * @param target
     * @param importContext
     * @param hash
     */
    public abstract void convertSpecialAttibutes(final Element source, final T target, final ImportContext importContext, final String hash);

    /**
     * Find the languages of the current asset
     * 
     * @param source
     *            the XML Asset element
     * @return the list of languages related to the current media asset.
     */
    protected List<LanguageModel> getLanguages(final Element source) {
        final List<Node> languageNodes = source.selectNodes(XP_LANGUAGE);
        if (CollectionUtils.isEmpty(languageNodes)) {
            LOG.error("No language(s) defined for download media [{}]", new Object[] { getId(source) });
            throw new ElementConverterException("Error converting download media");
        }

        final List<LanguageModel> languages = new ArrayList<LanguageModel>();
        for (final Node node : languageNodes) {
            final String value = StringUtils.lowerCase(node.getStringValue(), Locale.ROOT);
            if (ALL_LANGUAGES_CODE.equals(value)) {
                return null;
            }
            final LanguageModel language = getNamicsCommonI18NService().getLanguageModelByIsocodePim(value);
            if (language == null) {
                LOG.error("Could not resolve language [{}] for download media [{}]", new Object[] { value, getId(source) });
                throw new ElementConverterException("Error converting download media");
            }
            languages.add(language);
        }

        return languages;
    }

    // Getters & Setters

    public NamicsCommonI18NService getNamicsCommonI18NService() {
        return namicsCommonI18NService;
    }

    public void setNamicsCommonI18NService(final NamicsCommonI18NService namicsCommonI18NService) {
        this.namicsCommonI18NService = namicsCommonI18NService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
    
    public ConverterLanguageUtil getConverterLanguageUtil() {
		return converterLanguageUtil;
	}

	public void setConverterLanguageUtil(ConverterLanguageUtil converterLanguageUtil) {
		this.converterLanguageUtil = converterLanguageUtil;
	}
	
	 public CommonI18NService getCommonI18NService() {
	     return commonI18NService;
	 }

	 public void setCommonI18NService(final CommonI18NService commonI18NService) {
	     this.commonI18NService = commonI18NService;
	 }

}
