package com.namics.hybris.toolbox.xml;

public class XmlUtil {

    public static String QUOTE = "\"";
    public static String PLACEHOLDER = "##";

    /**
     * Generate a XML Attribute.
     * 
     * @return A String like ' key="value" '.
     */
    public static String generateXmlAttribute(final String key, final String value) {
        return key + "=\"" + value + "\"";
    }

    /**
     * @see #generateXmlLine(String, String[])
     */
    public static String generateXmlLine(final String tagBody, final String attribute) {
        final String[] attributeArray = new String[] { attribute };

        return generateXmlLine(tagBody, attributeArray);
    }

    /**
     * @see #generateXmlLine(String, String[])
     */
    public static String generateXmlLine(final String tagBody, final String attribute1, final String attribute2) {
        final String[] attributeArray = new String[] { attribute1, attribute2 };

        return generateXmlLine(tagBody, attributeArray);
    }

    /**
     * Inserts in a statement with placeholders all values of <code>attributeValues</code>. In a string <mytag attribute="@@"
     * otherattribute="@@" /> the '@@' are replaced by the first and seconde values in <code>attributeValues</code>
     * 
     * @param tagBody
     *            An xml statement.
     * @param attributeValues
     *            An array of values.
     * @return The result string.
     */
    public static String generateXmlLine(final String tagBody, final String[] attributeValues) {
        final StringBuffer sb = new StringBuffer();

        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < attributeValues.length; i++) {
            final String replacer = QUOTE + attributeValues[i] + QUOTE;
            endIndex = tagBody.indexOf(PLACEHOLDER, startIndex);
            if (endIndex == -1) {

                break;
            }
            sb.append(tagBody.substring(startIndex, endIndex));
            sb.append(replacer);
            startIndex = endIndex + PLACEHOLDER.length();
        }
        sb.append(tagBody.substring(startIndex));

        return sb.toString();
    }

}
