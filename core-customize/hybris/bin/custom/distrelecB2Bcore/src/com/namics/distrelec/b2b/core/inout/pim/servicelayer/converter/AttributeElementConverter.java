/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimAttributeDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.ClassAttributeAssignmentTypeMap;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Converts an "Attribute" XML element into a {@link ClassificationAttributeModel}.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class AttributeElementConverter implements PimImportElementConverter<ClassificationAttributeModel> {

	private static final Logger LOG = LogManager.getLogger(AttributeElementConverter.class);
	private static final String ATTRIBUTE_MULTI_VALUED = "MultiValued";
	private static final String ATTRIBUTE_CALCULATED = "Derived";
	private static final String ATTRIBUTE_NAME = "Name";
	private static final String XP_UNIT_ID = "Validation/UnitLink/@UnitID";
	private static final String XP_BASE_TYPE = "Validation/@BaseType";
	private static final String XP_ROOT_ATTRIBUTE = "MetaData/MultiValue[@AttributeID='usage_in_e_platform_lov']/Value";
	private static final String XP_ELEMENT_CALCULATED = "ValueTemplate";

	@Autowired
	private ConverterLanguageUtil converterLanguageUtil;

	@Override
	public String getId(final Element source) {
		return source.attributeValue(ATTRIBUTE_ID);
	}

	@Override
	public void convert(final Element source, final ClassificationAttributeModel target, final ImportContext importContext, final String hash) {

		final PimAttributeDto attributeDto = new PimAttributeDto();

		Map<Locale, String> nameValues = getConverterLanguageUtil().getLocalizedValues(source, ATTRIBUTE_NAME);

		nameValues.forEach((locale, name) -> {
			if (locale != null) {
				target.setName(name, locale);
				if ("en".equals(locale.getLanguage())) {
					attributeDto.setName(name);
				}
			}
		});

		attributeDto.setCode(target.getCode());
		//attributeDto.setName(name);
		attributeDto.setMultiValued(BooleanUtils.toBooleanObject(source.attributeValue(ATTRIBUTE_MULTI_VALUED)));
		attributeDto.setCalculated(Boolean.valueOf(isCalculated(source)));
		// DISTRELEC-6837 update for the embedded values
		attributeDto.setEmbedded(isEmbedded(source));

		final String baseType = getBaseType(source);
		attributeDto.setAttributeTypeCode(baseType);

		if (baseType.equalsIgnoreCase(ClassAttributeAssignmentTypeMap.TEXT_PIM_TYPE)) {
			// DISTRELEC-6067 attribute type text doesn't support units.
			attributeDto.setUnitCode(null);
		} else {
			attributeDto.setUnitCode(source.valueOf(XP_UNIT_ID));
		}

		importContext.getAttributeDtos().put(attributeDto.getCode(), attributeDto);

		if (importContext.isUseDynamicRootProductFeatures()) {
			checkRootAttribute(source, target, importContext);
		}

	}

	/**
	 * Calculated and Embedded attributes are considered as type "text" if baseType is not specified then "text" type is the default.
	 */
	private String getBaseType(final Element source) {
		final Boolean calculated = BooleanUtils.toBooleanObject(source.attributeValue(ATTRIBUTE_CALCULATED));
		final String typeXml = getRealBaseType(source);
		return (calculated.booleanValue() || isStringAttributeType(typeXml)) ? ClassAttributeAssignmentTypeMap.TEXT_PIM_TYPE : typeXml;
	}

	private boolean isCalculated(final Element source) {
		return source.element(XP_ELEMENT_CALCULATED) != null || BooleanUtils.isTrue(BooleanUtils.toBooleanObject(source.attributeValue(ATTRIBUTE_CALCULATED)));
	}

	private Boolean isEmbedded(final Element source) {
		return ClassAttributeAssignmentTypeMap.EMBEDDED_NUMBER_PIM_TYPE.equalsIgnoreCase(getRealBaseType(source));
	}

	private String getRealBaseType(final Element source) {
		return source.valueOf(XP_BASE_TYPE);
	}

	private boolean isStringAttributeType(final String xmlType) {
		if (ClassAttributeAssignmentTypeMap.TYPE_TRANSLATIONS.containsKey(xmlType)) {
			return ClassAttributeAssignmentTypeMap.TYPE_TRANSLATIONS.get(xmlType).equals(ClassificationAttributeTypeEnum.STRING);
		}
		return true;
	}

	private void checkRootAttribute(final Element source, final ClassificationAttributeModel target, final ImportContext importContext) {
		final XPath xpath = source.createXPath(XP_ROOT_ATTRIBUTE);
		final List<Element> nodes = xpath.selectNodes(source);
		if (CollectionUtils.isNotEmpty(nodes)) {
			final String value = nodes.get(0).getTextTrim();
			if ("ePlatform".equals(value)) {
				importContext.getWhitelistedRootProductFeatures().add(target.getCode());
				LOG.debug("Found new root attribute: {}", target.getCode());
			}
		}
	}

	public ConverterLanguageUtil getConverterLanguageUtil() {
		return converterLanguageUtil;
	}

	public void setConverterLanguageUtil(final ConverterLanguageUtil converterLanguageUtil) {
		this.converterLanguageUtil = converterLanguageUtil;
	}
}
