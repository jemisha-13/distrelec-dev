/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants;
import com.namics.distrelec.b2b.core.util.DistUtils;

public class DistSitemapEncodeUtil {
    public static String sanitizeUrlForProduct(final String urlString) throws MalformedURLException {
        return sanitizeUrlDefault(urlString);
    }

    public static String sanitizeUrlForCategory(final String urlString) throws MalformedURLException {
        return sanitizeUrlCategory(urlString);
    }

    public static String sanitizeUrlForManufacturer(final String urlString) throws MalformedURLException {
        return sanitizeUrlManuString(urlString);
    }

    public static void main(String[] args) throws MalformedURLException {
        String url = "http://distrelec-ch.local:9001/de/Automation/Schalt- |@| Mess- und Zähl- |@| Überwachungs- |@| Befehls- |@| Meldegeräte/Positionsschalter/Positionsschalter/Positionsschalter mit Metall- oder Kunststoffgehäuse nach EN50047/c/cat-DC-41241";
        System.out.println(url);
        System.out.println(sanitizeUrlDefault(url));
    }

    private static String sanitizeUrlDefault(String urlString) throws MalformedURLException {
        final URL url = new URL(urlString);
        final StringBuilder sb = new StringBuilder();
        // first build up the protocol, the hostname and eventually the port
        convertHostnamePort(url, sb);

        // then deduce the full path for url encode each part
        final String path = getPath(urlString, sb);

        final String[] parts = path.split(SitemapConstants.SLASH);

        for (String part : parts) {
            part = part.replace(SitemapConstants.SLASH_SURROGATE, SitemapConstants.SLASH);
            sb.append(urlSafe(part)).append(SitemapConstants.SLASH);
        }
        final String cleanUrl = sb.toString();

        return removeLastSlash(cleanUrl);

    }

    private static String sanitizeUrlCategory(String urlString) throws MalformedURLException {
        final URL url = new URL(urlString);
        final StringBuilder sb = new StringBuilder();
        // first build up the protocol, the hostname and eventually the port
        convertHostnamePort(url, sb);

        // then deduce the full path for url encode each part
        final String path = getPath(urlString, sb);

        final String[] parts = path.split(SitemapConstants.SLASH);
        int count = 1;
        for (String part : parts) {
            part = part.replace(SitemapConstants.SLASH_SURROGATE, SitemapConstants.SLASH);

            // take care of not encoding the category code
            if (count == parts.length && part.startsWith("cat-")) {
                sb.append(part).append(SitemapConstants.SLASH);
            } else {
                sb.append(urlSafe(part)).append(SitemapConstants.SLASH);
            }

            count++;
        }
        final String cleanUrl = sb.toString();

        return removeLastSlash(cleanUrl);

    }

    private static String getPath(String urlString, final StringBuilder sb) {
        return urlString.substring(sb.toString().length(), urlString.length());
    }

    private static void convertHostnamePort(final URL url, final StringBuilder sb) {
        sb.append(url.getProtocol()).append("://").append(url.getHost()).append((url.getPort() > 0 ? ":" + url.getPort() : ""));
    }

    private static String sanitizeUrlManuString(String urlString) throws MalformedURLException {
        final URL url = new URL(urlString);
        final StringBuilder sb = new StringBuilder();
        convertHostnamePort(url, sb);

        // then deduce the full path for url encode each part
        final String path = getPath(urlString, sb);

        final String[] parts = path.split(SitemapConstants.SLASH);

        for (final String part : parts) {
            sb.append(DistUtils.encodeString(part)).append(SitemapConstants.SLASH);
        }
        final String cleanUrl = sb.toString();

        return removeLastSlash(cleanUrl);

    }

    private static String removeLastSlash(final String cleanUrl) {
        return cleanUrl.endsWith(SitemapConstants.SLASH) ? cleanUrl.substring(0, cleanUrl.length() - 1) : cleanUrl;
    }

    /**
     * Encode string with UTF8 encoding and then convert a string into a URL safe version of the string. Only upper and lower case letters
     * and numbers are retained. All other characters are replaced by a hyphen (-).
     * 
     * @param text
     *            the text to sanitize
     * @return the safe version of the text
     */
    public static String urlSafeOld(final String text) {

        if (text == null || text.isEmpty()) {
            return "";
        }
        String[] parts = text.split("-");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = StringUtils.trim(parts[i]);
        }
        String trimmedUrl = StringUtils.join(parts, "-");
        String encodedText;
        try {
            encodedText = URLEncoder.encode(trimmedUrl, "utf-8");
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
     * Encode string with UTF8 encoding and then convert a string into a URL safe version of the string. Only upper and lower case letters
     * and numbers are retained. All other characters are replaced by a hyphen (-).
     * 
     * @param text
     *            the text to sanitize
     * @return the safe version of the text
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
}
