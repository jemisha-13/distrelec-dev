/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.url;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link UrlResolverUtils}.
 * 
 * @author ascherrer, Namics AG
 */
public class UrlResolverUtilsTest {

    /**
     * Capital letters to be replaced by small letters.
     */
    @Test
    public void testResolveURL1() {
        final String URL = "TestString";
        assertEquals("teststring", UrlResolverUtils.normalize(URL));
    }

    /**
     * Spaces to be replaced by a hyphen.
     */
    @Test
    public void testResolveURL2() {
        final String URL = "test string    with spaces /";
        assertEquals("test-string-with-spaces/", UrlResolverUtils.normalize(URL));
        assertEquals("test-string-with-spaces", UrlResolverUtils.normalize(URL, true));
    }

    /**
     * Spaces to be replaced by a hyphen.
     */
    @Test
    public void testResolveURL21() {
        String URL = "LK 4-B 025CM RED";
        assertEquals("lk-025cm-red", UrlResolverUtils.normalize(URL));
        assertEquals("lk-025cm-red", UrlResolverUtils.normalize(URL, true));

        URL = "LK425-A/X 050CM GREEN-YELLOW";
        assertEquals("lk425/050cm-green-yellow", UrlResolverUtils.normalize(URL));
        assertEquals("lk425-050cm-green-yellow", UrlResolverUtils.normalize(URL, true));

        URL = "U.FL-2LP-068/N1T-A-(100)";
        assertEquals("fl-2lp-068/n1t-100", UrlResolverUtils.normalize(URL));
        assertEquals("fl-2lp-068-n1t-100", UrlResolverUtils.normalize(URL, true));
    }

    /**
     * Umlauts to be replaced by ae / oe / ue.
     */
    @Test
    public void testResolveURL3() {
        final String URL = "umlauts-like-ä-ö-ü-and-Ä-Ö-Ü-will-be-replaced";
        assertEquals("umlauts-like-and-will-be-replaced", UrlResolverUtils.normalize(URL));
        assertEquals("umlauts-like-ae-oe-ue-and-ae-oe-ue-will-be-replaced", UrlResolverUtils.normalize(URL, true));
    }

    /**
     * Accented characters to be replaced.
     */
    @Test
    public void testResolveURL4() {
        final String URL = "áâàå-óôòõ-úûù";
        assertEquals("", UrlResolverUtils.normalize(URL));
        assertEquals("aaaa-oooo-uuu", UrlResolverUtils.normalize(URL, true));
    }

    /**
     * Multiple hyphens to be replaced by a single hyphen.
     */
    @Test
    public void testResolveURL5() {
        final String URL = "use-some--text---to----test";
        assertEquals("use-some-text-to-test", UrlResolverUtils.normalize(URL));
        assertEquals("use-some-text-to-test", UrlResolverUtils.normalize(URL, true));
    }

    /**
     * Single character to be replaced by a hyphen.
     */
    @Test
    public void testResolveURL6() {
        final String URL = "this-is-a-test";
        assertEquals("this-is-test", UrlResolverUtils.normalize(URL));
        assertEquals("this-is-test", UrlResolverUtils.normalize(URL, true));
    }

    /**
     * Slash to be replaced by a hyphen.
     */
    @Test
    public void testResolveURL7() {
        final String URL = "test string  /  with spaces /";
        assertEquals("test-string/with-spaces/", UrlResolverUtils.normalize(URL));
        assertEquals("test-string-with-spaces", UrlResolverUtils.normalize(URL, true));
    }

    @Test
    public void testResolveBrandLinkUrl(){
        final String url = "/en/manufacturer/regeltechnik-gmbh/man_s+s";
        assertEquals("/en/manufacturer/regeltechnik-gmbh/man_s+s", UrlResolverUtils.normalize(url));
    }

}
