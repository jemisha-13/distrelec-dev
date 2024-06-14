package com.namics.hybris.toolbox;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Kopiert von Springs PropertyPlaceholderConfigurer. Erm�glicht, einen String mit Platzhaltern zu parsen, und die Platzhaltern in einem
 * Properties-Objekt zu �bergeben.
 * 
 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 * 
 * @author Juergen Hoeller
 * @author jweiss
 * @since 02.10.2003
 * 
 */
public abstract class PlaceholderUtil {

    /** Logger for this class and subclasses */
    protected static final Log LOG = LogFactory.getLog(PlaceholderUtil.class);

    /**
     * Default placeholder prefix: "?"
     * 
     * Es wurde bewusst nicht ${ genommen, damit nicht Spring versucht, den Platzhalter auszuwerten.
     * 
     */
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    /**
     * Default placeholder suffix: " " This is rather a thin definition because it urge the user always to put an end after the placeholder
     * name. Other suffixes are the line break or the '}', but these characters are not supported in this version.
     */
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    /**
     * Entscheidet, ob eine Exception geworfen werden soll (false) oder ob der Placeholder einfach stehen gelassen werden soll.
     */
    public static boolean ignoreUnresolvablePlaceholders = true;

    /**
     * Parse the given String value recursively, to be able to resolve nested placeholders (when resolved property values in turn contain
     * placeholders again).
     * 
     * @param strVal
     *            the String value to parse
     * @param props
     *            the Properties to resolve placeholders against
     * @throws IllegalArgumentException
     *             if invalid values are encountered
     * @see #resolvePlaceholder(String, java.util.Properties)
     */
    public static String parseStringValue(final String strVal, final Map<String, Object> props) {
        if (strVal == null || props == null || props.isEmpty()) {
            return strVal;
        }
        return parseStringValue(strVal, props, null);
    }

    /**
     * Parse the given String value recursively, to be able to resolve nested placeholders (when resolved property values in turn contain
     * placeholders again).
     * 
     * @param strVal
     *            the String value to parse
     * @param props
     *            the Properties to resolve placeholders against
     * @param originalPlaceholder
     *            the original placeholder, used to detect circular references between placeholders. Only non-null if we're parsing a nested
     *            placeholder.
     * @throws IllegalArgumentException
     *             if invalid values are encountered
     * @see #resolvePlaceholder(String, java.util.Properties)
     */
    protected static String parseStringValue(final String strVal, final Map<String, Object> props, final String originalPlaceholder) {

        final StringBuffer buf = new StringBuffer(strVal);

        // The following code does not use JDK 1.4's StringBuffer.indexOf(String)
        // method to retain JDK 1.3 compatibility. The slight loss in performance
        // is not really relevant, as this code will typically just run on startup.

        int startIndex = strVal.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
        while (startIndex != -1) {
            final int endIndex = buf.toString().indexOf(DEFAULT_PLACEHOLDER_SUFFIX, startIndex + DEFAULT_PLACEHOLDER_PREFIX.length());
            if (endIndex != -1) {
                final String placeholder = buf.substring(startIndex + DEFAULT_PLACEHOLDER_PREFIX.length(), endIndex);
                String originalPlaceholderToUse = null;

                if (originalPlaceholder != null) {
                    originalPlaceholderToUse = originalPlaceholder;
                    if (placeholder.equals(originalPlaceholder)) {
                        throw new IllegalArgumentException("Circular placeholder reference '" + placeholder + "' in property definitions");
                    }
                } else {
                    originalPlaceholderToUse = placeholder;
                }

                String propVal = resolvePlaceholder(placeholder, props);
                if (propVal != null) {
                    // Recursive invocation, parsing placeholders contained in the
                    // previously resolved placeholder value.
                    propVal = parseStringValue(propVal, props, originalPlaceholderToUse);
                    // The Placeholder suffix is not replaced, because otherwise the end space is missing.
                    // buf.replace(startIndex, endIndex + DEFAULT_PLACEHOLDER_SUFFIX.length(), propVal);
                    buf.replace(startIndex, endIndex + DEFAULT_PLACEHOLDER_SUFFIX.length(), propVal);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Resolved placeholder '" + placeholder + "' to value [" + propVal + "]");
                    }
                    startIndex = buf.toString().indexOf(DEFAULT_PLACEHOLDER_PREFIX, startIndex + propVal.length() + DEFAULT_PLACEHOLDER_SUFFIX.length());
                } else if (ignoreUnresolvablePlaceholders) {
                    // Proceed with unprocessed value.
                    startIndex = buf.toString().indexOf(DEFAULT_PLACEHOLDER_PREFIX, endIndex + DEFAULT_PLACEHOLDER_SUFFIX.length());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Couldn't resolved placeholder '" + placeholder + "'");
                    }
                } else {
                    throw new IllegalArgumentException("Could not resolve placeholder '" + placeholder + "'");
                }
            } else {
                startIndex = -1;
            }
        }

        return buf.toString();
    }

    /**
     * Resolve the given placeholder using the given properties. Default implementation simply checks for a corresponding property key.
     * <p>
     * Subclasses can override this for customized placeholder-to-key mappings or custom resolution strategies, possibly just using the
     * given properties as fallback.
     * 
     * @param placeholder
     *            the placeholder to resolve
     * @param props
     *            the merged properties of this configurer
     * @return the resolved value, or <code>null</code> if none
     */
    protected static String resolvePlaceholder(final String placeholder, final Map<String, Object> props) {
        final Object value = props.get(placeholder);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

}
