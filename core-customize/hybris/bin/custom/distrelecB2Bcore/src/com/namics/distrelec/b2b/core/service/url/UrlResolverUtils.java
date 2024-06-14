/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;

/**
 * {@code UrlResolverUtils}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public final class UrlResolverUtils {

    private static final int MAX_APPLY = 5;
    private static final String MANUFACTURER = "manufacturer";
    private static final String PLUS = "+";
    private static final String PLUS_ENCODED = "%2B";
    private static final String ONE_SPACE = " ";
    private static final String DASH = "-";
    private static final String SLASH = "/";
    private static final String UTF_8 = "utf-8";
    private static final DistPattern[] DIST_PATTERNS = { //
    //
            new DistPattern("(\\s|-)+", DASH, 1), //
            new DistPattern("(/.?-)|(-.?/)", SLASH, MAX_APPLY), //
            new DistPattern("(-.-)+", DASH, MAX_APPLY), //
            new DistPattern("//", SLASH, MAX_APPLY), //
            new DistPattern("^(.?-)|(-.?)$", StringUtils.EMPTY, MAX_APPLY) //
    };

    /**
     * Create a new instance of {@code UrlResolverUtils}
     */
    private UrlResolverUtils() {
        super();
    }

    /**
     * Try to find a replacement to the source string
     * 
     * @param src
     *            the source string to replace
     * @return a string replacement for the source string if any exists. If the source string is null or there is no replacement found, then
     *         returns {@code ONE_SPACE}
     */
    protected static String replace(final String src) {
        if (src == null || !UrlResolverConstants.ACCENTED_CHARS_REPLACEMENTS.containsKey(src)) {
            return ONE_SPACE;
        }

        return UrlResolverConstants.ACCENTED_CHARS_REPLACEMENTS.get(src);
    }

    /**
     * Normalize the specified URL
     * 
     * @param url
     *            the source URL to normalize.
     * @param encodeBefore
     * @return normalized URL
     */
    public static String normalize(String url, final boolean encodeBefore) {
        if (url == null) {
            return null;
        }

        String decodedURL = url;
        String entityID = StringUtils.EMPTY;
        if(isAManufacturerAndHasAPlusInUrl(url, decodedURL)){
            url = url.replace(PLUS, PLUS_ENCODED);
        }

        try {
            decodedURL = URIUtil.decode(encodeBefore ? urlSafe(url) : url, UTF_8);
        } catch (final URIException urie) {
            // NOP
        }

        if (decodedURL.contains("/c/")
                || decodedURL.contains("/p/")
                || decodedURL.contains("/manufacturer/")
                || decodedURL.contains("/" + DistConstants.UrlTags.PRODUCT_FAMILY + "/")) {
            final int x = decodedURL.lastIndexOf('/');
            if (x > 0) {
                entityID = decodedURL.substring(x);
                decodedURL = decodedURL.substring(0, x);
            }
        }

        final StringBuilder urlBuilder = new StringBuilder();

        for (int i = 0; i < decodedURL.length(); i++) {
            urlBuilder.append(replace(StringUtils.EMPTY + decodedURL.charAt(i)));
        }

        return applyPatterns(urlBuilder.toString()) + entityID;
    }

    private static boolean isAManufacturerAndHasAPlusInUrl(final String url, final String decodedURL) {
        return decodedURL.contains(SLASH + MANUFACTURER + SLASH) && url.contains(PLUS);
    }

    /**
     * Normalize the specified URL
     * 
     * @param url
     *            the source URL to normalize.
     * @return normalized URL
     * @see #normalize(String, boolean)
     */
    public static String normalize(final String url) {
        return normalize(url, false);
    }

    /**
     * Encode the specified text using {@link URLEncoder}
     * 
     * @param text
     * @return the encoded string
     */
    public static String urlSafe(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String encodedText;
        try {
            encodedText = URLEncoder.encode(text, "utf-8");
        } catch (final UnsupportedEncodingException encodingException) {
            encodedText = text;
        }

        // Cleanup the text
        String cleanedText = encodedText;
        cleanedText = cleanedText.replaceAll("%2F", "/");
        cleanedText = cleanedText.replaceAll("[^%A-Za-z0-9\\-]+", "-");
        return cleanedText;
    }

    /**
     * Apply all the patterns by looping on the patterns array until getting the correct form. Each pattern has a max number of application
     * on the URL. To avoid infinite loops, we placed a max number of total loops, i.e., each pattern can be applied 0 &lt; n &lt;
     * {@code MAX_APPLY}.
     * 
     * @param input
     *            the char sequence input on which patterns will be applied.
     * @return the final string after applying all patterns.
     */
    private static String applyPatterns(final String input) {
        String sequence = input;
        int steps = 0;
        boolean ok = true;
        final Matcher[] matchers = createMatchers(sequence);
        do {
            ok = true;
            for (int i = 0; i < matchers.length; i++) {
                matchers[i].reset(sequence);
                if (steps < DIST_PATTERNS[i].maxApply && matchers[i].find()) {
                    sequence = matchers[i].replaceAll(DIST_PATTERNS[i].replacement);
                    ok = false;
                }
            }
            steps++;
        } while (!ok && steps < MAX_APPLY);

        return sequence;
    }

    /**
     * Create an array of {@link Matcher}s for the specified sequence
     * 
     * @param sequence
     *            the initial input
     * @return an array of {@link Matcher}s
     */
    private static Matcher[] createMatchers(final CharSequence sequence) {
        final Matcher[] matchers = new Matcher[DIST_PATTERNS.length];
        for (int i = 0; i < DIST_PATTERNS.length; i++) {
            matchers[i] = DIST_PATTERNS[i].pattern.matcher(sequence);
        }
        return matchers;
    }

    /**
     * {@code DistPattern}
     * 
     * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
     * @since Distrelec 3.4
     */
    private static class DistPattern {
        private int maxApply;
        private Pattern pattern;
        private String replacement;

        /**
         * Create a new instance of {@code DistPattern}
         * 
         * @param regex
         * @param replacement
         * @param maxApply
         */
        public DistPattern(final String regex, final String replacement, final int maxApply) {
            this.pattern = Pattern.compile(regex);
            this.replacement = replacement;
            this.maxApply = maxApply;
        }
    }
}
