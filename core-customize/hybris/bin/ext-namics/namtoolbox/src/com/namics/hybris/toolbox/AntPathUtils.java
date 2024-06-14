package com.namics.hybris.toolbox;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

public class AntPathUtils {

    protected static AntPathMatcher matcher = new AntPathMatcher();

    public static boolean isPattern(final String str) {
        return AntPathUtils.matcher.isPattern(str);
    }

    public static boolean match(final String pattern, final String str) {
        return AntPathUtils.matcher.match(pattern, str);
    }

    public static String returnBetterMatchingAntPathPattern(final String antPattern1, final String antPattern2) {

        if (antPattern1.equalsIgnoreCase(antPattern2)) {
            return antPattern1;
        }

        final String[] directoyTokkens1 = StringUtils.tokenizeToStringArray(antPattern1, "/", false, true);
        final String[] directoyTokkens2 = StringUtils.tokenizeToStringArray(antPattern2, "/", false, true);

        for (int i = 0; i < directoyTokkens1.length && i < directoyTokkens2.length; i++) {
            final String tokken1 = directoyTokkens1[i];
            final String tokken2 = directoyTokkens2[i];

            boolean occurent1;
            boolean occurent2;

            if (tokken1.equalsIgnoreCase(tokken2)) {
                continue;
            }

            occurent1 = StringUtils.countOccurrencesOf(tokken1, "**") > 0;
            occurent2 = StringUtils.countOccurrencesOf(tokken2, "**") > 0;
            if (occurent1 != occurent2) {
                return occurent1 ? antPattern2 : antPattern1;
            }

            occurent1 = StringUtils.countOccurrencesOf(tokken1, "*") > 0;
            occurent2 = StringUtils.countOccurrencesOf(tokken2, "*") > 0;
            if (occurent1 != occurent2) {
                return occurent1 ? antPattern2 : antPattern1;
            }

            occurent1 = StringUtils.countOccurrencesOf(tokken1, "?") > 0;
            occurent2 = StringUtils.countOccurrencesOf(tokken2, "?") > 0;
            if (occurent1 != occurent2) {
                return occurent1 ? antPattern2 : antPattern1;
            }
        }

        if (directoyTokkens1.length != directoyTokkens2.length) {
            return directoyTokkens1.length > directoyTokkens2.length ? antPattern1 : antPattern2;
        }

        return antPattern1;

    }

}
