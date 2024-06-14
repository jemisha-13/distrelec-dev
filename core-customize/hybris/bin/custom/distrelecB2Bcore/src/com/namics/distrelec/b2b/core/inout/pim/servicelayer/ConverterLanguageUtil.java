package com.namics.distrelec.b2b.core.inout.pim.servicelayer;

import org.dom4j.Element;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface ConverterLanguageUtil {

	Locale getLocaleForElement(Element nameElement);

	/**
	 * Returns true if element is marked as standard locale.
	 */
	boolean isStandardLocale(Element element);

	Map<Locale,String> getLocalizedValues(final Element source, final String xpathString);

	Map<Locale,String> getLocalizedValues(final Element source, final String xpathString, boolean fallbackLanguage);

	Map<Locale,Element> getLocalizedElements(final Element source, final String xpathString);

	Map<Locale, Map<String, Element>> getLocalizedMvElements(Element source, String xpathString);

	Set<Locale> getActivePimLocales();

	boolean ignoreValue(String value);

	Locale getLocaleForLanguage(final String isocodePim);

}
