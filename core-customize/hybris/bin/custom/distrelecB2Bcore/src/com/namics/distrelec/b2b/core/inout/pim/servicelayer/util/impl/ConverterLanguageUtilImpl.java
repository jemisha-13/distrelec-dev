package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.impl;


import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.i18n.dao.DistLanguageDao;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ConverterLanguageUtilImpl implements ConverterLanguageUtil {

	private static final Logger LOG = LogManager.getLogger(ConverterLanguageUtilImpl.class);

	private static final String STD_LANG = "std.lang.all";
	private static final String STD_LANG_FALLBACK = "en";

	private static final String ATTR_ID = "ID";
	public static final String ATTR_QUALIFIER = "QualifierID";
	private static final String ATTR_QUALIFIER_LOV = "LOVQualifierID";
	private static final String ATTR_QUALIFIER_DERIVED = "DerivedContextID";

	private static final String IGNORE_PATTERN_REGEXP = "-999|-888";
	private static final Pattern IGNORE_PATTERN  = Pattern.compile(IGNORE_PATTERN_REGEXP);

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private NamicsCommonI18NService i18NService;

	@Autowired
	private DistLanguageDao languageDao;


	@Override
	public Locale getLocaleForElement(Element element) {
		return applyLocaleFunction(element, attr -> resolveLocaleFromAttribute(attr));
	}

	@Override
	public boolean isStandardLocale(Element element) {
		return applyLocaleFunction(element, attr -> STD_LANG.equals(attr));
	}

	private <T> T applyLocaleFunction(Element element, Function<String, T> localeFunction) {
		if (element == null) {
			return null;
		}


		// check if attribute QualifierID is present
		if (null != getFromQualifier(ATTR_QUALIFIER, element, localeFunction)) {
			return getFromQualifier(ATTR_QUALIFIER, element, localeFunction);
		} else if (null != getFromQualifier(ATTR_QUALIFIER_LOV, element, localeFunction)) {
			return getFromQualifier(ATTR_QUALIFIER_LOV, element, localeFunction);
		} else if (null != getFromQualifier(ATTR_QUALIFIER_DERIVED, element, localeFunction)) {
			return getFromQualifier(ATTR_QUALIFIER_DERIVED, element, localeFunction);
		} else {
			LOG.error("could not find locale for element: " + element);
		}

		return null;

	}

	private <T> T getFromQualifier(String attrQualifier, Element element, Function<String, T> localeFunction) {
		String attribute = element.attributeValue(attrQualifier);
		if (StringUtils.isEmpty(attribute)) {
			return null;
		}

		return localeFunction.apply(attribute);
	}

	private Locale resolveLocaleFromAttribute(String attribute) {
		if (STD_LANG.equals(attribute)) {
			// fallback to en for std.lang.all
			return new Locale(STD_LANG_FALLBACK);
		} else if (attribute.length() == 3) {
			return getLocaleForLanguage(attribute);
		} else if (attribute.contains("-")) {
			String[] split = attribute.split("-");
			if (null != split && split.length == 3) {
				// get language from
				return getLocaleForLanguage(split[1]);
			}
		}
		return null;
	}

	@Override
	public Locale getLocaleForLanguage(final String isocodePim) {
		final LanguageModel language = getI18NService().getLanguageModelByIsocodePim(isocodePim);
		if (language != null) {
			return new Locale(language.getIsocode());
		} else {
			LOG.error("Language is not found for pim isocode: " + isocodePim);
			return null;
		}
	}

	private Locale getLocaleFromConfigurationOrGetDefault() {
		final String defaultLanguageFromConfig = configurationService.getConfiguration().getString(DistConfigConstants.Pim.ATTRIBUTE_PIM_DEFAULT_LANGUAGE);
		return StringUtils.isEmpty(defaultLanguageFromConfig) ? new Locale(STD_LANG_FALLBACK) : new Locale(defaultLanguageFromConfig);
	}

	/**
	 * gel localized values
	 *
	 * @param source
	 * @param xpathString
	 * @return
	 */
	@Override
	public Map<Locale, String> getLocalizedValues(final Element source, final String xpathString) {
		boolean fallbackLanguage = true;
		return getLocalizedValues(source, xpathString, fallbackLanguage);
	}

	@Override
	public Map<Locale, String> getLocalizedValues(final Element source, final String xpathString, final boolean fallbackLanguage) {

		Map<Locale, String> ret = new HashMap<>();

		final XPath xpath = source.createXPath(xpathString);

		final List<Element> values = xpath.selectNodes(source);
		if (values != null) {
			for (final Element element : values) {
				Locale locale = getLocaleForElement(element);
				ret.put(locale, element.getTextTrim());
			}
		}

		String fallbackValue = null;

		if (fallbackLanguage) {
			if (ret.containsKey(Locale.ENGLISH)) {
				fallbackValue = ret.get(Locale.ENGLISH);
			}
		}

		if ((fallbackLanguage && fallbackValue != null) || !fallbackLanguage) {
			// in case of fallback, value must not be null

			Set<Locale> activeLocales = getActivePimLocales();
			for (Locale locale : activeLocales) {
				ret.putIfAbsent(locale, fallbackValue);
			}
		}

		return ret;
	}

	@Override
	public Map<Locale, Element> getLocalizedElements(Element source, String xpathString) {
		Map<Locale, Element> ret = new HashMap<>();

		final XPath xpath = source.createXPath(xpathString);

		final List<Element> values = xpath.selectNodes(source);
		if (values != null) {
			for (final Element element : values) {
				Locale locale = getLocaleForElement(element);
				boolean isStandardLocale = isStandardLocale(element);
				if (isStandardLocale) {
					ret.putIfAbsent(locale, element);
				} else {
					ret.put(locale, element);
				}
			}
		}

		return ret;
	}

	@Override
	public Map<Locale, Map<String, Element>> getLocalizedMvElements(Element source, String xpathString) {
		Map<Locale, Map<String, Element>> ret = new HashMap<>();
		Map<Element, String> superElements = new HashMap<>();

		final XPath xpath = source.createXPath(xpathString);

		final List<Element> values = xpath.selectNodes(source);
		if (values != null) {
			for (final Element element : values) {
				Element parent = element.getParent();
				if (!superElements.containsKey(parent)) {
					superElements.put(parent, String.valueOf(superElements.size()));
				}
				String id = superElements.get(parent);

				Locale locale = getLocaleForElement(element);
				if(!ret.containsKey(locale)) {
					ret.put(locale, new HashMap<>());
				}

				boolean isStandardLocale = isStandardLocale(element);
				if (isStandardLocale) {
					ret.get(locale).putIfAbsent(id, element);
				} else {
					ret.get(locale).put(id, element);
				}
			}
		}

		return ret;
	}


	/**
	 * get a list of languages which are actively used in PIM
	 *
	 * @return
	 */
	public Set<Locale> getActivePimLocales() {
		Set<Locale> activePimLocales = new HashSet<>();

		List<LanguageModel> languages = getLanguageDao().findLanguages();
		languages.stream().filter(language -> StringUtils.isNotEmpty(language.getIsocodePim())).forEach(language -> activePimLocales.add(new Locale(language.getIsocode())));

		return activePimLocales;
	}

	/**
	 * ignore values matching the pattern
	 * @return
	 */
	@Override
	public boolean ignoreValue(String value) {
		boolean matches = IGNORE_PATTERN.matcher(value).matches();
		return matches;
	}

	/**
	 *
	 * @return
	 */


	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public NamicsCommonI18NService getI18NService() {
		return i18NService;
	}

	public void setI18NService(NamicsCommonI18NService i18NService) {
		this.i18NService = i18NService;
	}

	public DistLanguageDao getLanguageDao() {
		return languageDao;
	}

	public void setLanguageDao(DistLanguageDao languageDao) {
		this.languageDao = languageDao;
	}
}
