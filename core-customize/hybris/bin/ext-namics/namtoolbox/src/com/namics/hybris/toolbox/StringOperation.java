package com.namics.hybris.toolbox;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.StringUtils;

/**
 * Util class for String operations.
 * 
 * @author jweiss, namics ag
 * @since MGB PIM 1.0
 */
public class StringOperation {

    /**
     * Ersetzt in einem String <code>source</code> alle <code>pattern</code> durch <code>replace</code>.
     * 
     * @see http://webxadmin.free.fr/article/string-replace-40.php
     * @param source
     *            Der urspr�ngliche String
     * @param pattern
     *            Der zu suchende String in <code>source</code>
     * @param replace
     *            Der String der anstelle von <code>pattern</code> eingesetzt wird.
     * @return Der String <code>source</code>, jedoch mit <code>replace</code> anstelle von <code>pattern</code>
     */
    public static String replace(final String source, final String pattern, final String replace) {
        if (source != null) {
            final int len = pattern.length();
            final StringBuffer buffer = new StringBuffer();
            int found = -1;
            int start = 0;

            while ((found = source.indexOf(pattern, start)) != -1) {
                buffer.append(source.substring(start, found));
                buffer.append(replace);
                start = found + len;
            }

            buffer.append(source.substring(start));

            return buffer.toString();
        } else {
            return "";
        }
    }

    /**
     * Ersetzt in einem String <code>source</code> alle <code>pattern</code> durch <code>replace</code>.<br>
     * Bei der Suche nach <code>source</code> wird nicht auf Gross-/Kleinschreibung geachtet.
     * 
     * @see http://webxadmin.free.fr/article/string-replace-40.php
     * @param source
     *            Der urspr�ngliche String
     * @param pattern
     *            Der zu suchende String in <code>source</code>
     * @param replace
     *            Der String der anstelle von <code>pattern</code> eingesetzt wird.
     * @return Der String <code>source</code>, jedoch mit <code>replace</code> anstelle von <code>pattern</code>
     */
    public static String replaceIgnoreCase(final String source, final String pattern, final String replace) {
        if (source != null) {
            final String sourceToLowerCase = source.toLowerCase();
            final String patternToLowerCase = pattern.toLowerCase();

            final int len = pattern.length();
            final StringBuffer buffer = new StringBuffer();
            int found = -1;
            int start = 0;

            while ((found = sourceToLowerCase.indexOf(patternToLowerCase, start)) != -1) {
                buffer.append(source.substring(start, found));
                buffer.append(replace);
                start = found + len;
            }

            buffer.append(source.substring(start));

            return buffer.toString();
        } else {
            return "";
        }
    }

    /**
     * Nimmt einen String entgegen und wandelt das erste Zeichen im String zu einem Grossbuchstaben um, z.B. "audi" -> "Audi".
     * 
     * @param source
     *            Der String, der umgewandelt werden soll.
     * @return Der String aus <code>source</code>, jedoch mit dem ersten Zeichen als Grossbuchstabe.
     */
    public static String firstCharToUpperCase(final String source) {
        return StringUtils.capitalize(source);
    }

    /**
     * Wiederholt das pattern <code>pattern</code> <code>numOfRepeats</code>-mal und gibt das Resultat zurück.<br>
     * Beispiel: Ein Passwort "dk79dkl" wird mit repeatString("*", "dk79dkl".length()) zu "*******".
     */
    public static String repeatString(final String pattern, final int numOfRepeats) {
        if (pattern == null || pattern.length() == 0) {
            return "";
        }
        final StringBuffer result = new StringBuffer(pattern.length() * numOfRepeats);

        for (int i = 0; i < numOfRepeats; i++) {
            result.append(pattern);
        }

        return result.toString();
    }

    /**
     * Gibt zurück, ob <code>searchToken</code> in <code>stringToSearch</code> vorkommt. Wenn eines der Argumente <code>null</code> ist,
     * wird <code>false</code> zurückgegeben.
     */
    public static boolean containsText(final String stringToSearch, final String searchToken) {
        if (stringToSearch == null || searchToken == null) {
            return false;
        } else {
            return stringToSearch.toLowerCase().indexOf(searchToken.toLowerCase()) > -1;
        }
    }

    /**
     * Trennt ein String <code>stringToSplit</code> bei jedem Auftreten eines der Strings in <code>delimiters</code>.
     */
    public static List<String> splitString(final String stringToSplit, final String[] delimiters) {
        final List<String> tokens = new ArrayList<String>();

        int tokenStartPos = 0;
        for (int inspectPos = 0; inspectPos < stringToSplit.length(); inspectPos++) {
            for (int j = 0; j < delimiters.length; j++) {
                final String delimiter = delimiters[j];
                if (stringToSplit.substring(inspectPos).startsWith(delimiter)) {
                    tokens.add(stringToSplit.substring(tokenStartPos, inspectPos));
                    tokenStartPos = inspectPos + delimiter.length();
                    inspectPos = inspectPos + delimiter.length() - 1;

                    break;
                }
            }
        }
        if (tokenStartPos <= stringToSplit.length()) {
            tokens.add(stringToSplit.substring(tokenStartPos));
        }

        return tokens;

    }

    /**
     * Cut the String to a size and appends a ellipse (...) if the String was longer.
     */
    public static String cutStringToMaxSize(final String longString, final int size) {
        String cuttedString = longString;
        if (cuttedString.length() > size) {
            cuttedString = cuttedString.substring(0, size) + "...";
        }

        return cuttedString;
    }

    /**
     * Check if the old and new value is equals inlcuding null check.
     * 
     * @param oldValue
     *            the old value
     * @param newValue
     *            the new value
     * @return true if both String are equals or null
     */
    public static boolean equals(final String oldValue, final String newValue) {
        if (oldValue == newValue) {
            return true;
        } else if (oldValue != null) {
            return oldValue.equals(newValue);
        }
        return false;
    }

    /**
     * Strips out html tags from a string with regexp.
     * 
     * @param source
     * 
     * @return a string without html tags
     */
    public static String stripHtmlWithRegExp(final String source) {
        if (source != null) {
            final String pattern1 = "\\<.*?>";
            String clean = StringEscapeUtils.unescapeHtml(source);
            clean = clean.replaceAll(pattern1, "");
            return clean;
        } else {
            return source;
        }

    }
}
