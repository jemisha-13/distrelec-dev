package com.namics.hybris.toolbox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WildcardStringFinderTest {

    @Test
    public void testCompareTextToPattern() {
        String text = "This is my text with a number '1258949943' and a text 'hallo zusammen'.";
        final String template = "This is my text with a number '*' and a text '*'.";
        assertTrue("Wildcard didn't run proper.", WildcardStringFinder.compareTextToPattern(template, text));

        text = "This is my text with two number '1258949943' and a text 'hallo zusammen'.";
        assertFalse("Wildcard didn't run proper.", WildcardStringFinder.compareTextToPattern(template, text));

    }

    @Test
    public void testCompareTextToPatternDoubleQuote() {
        String text = "This is my text with a number \"1258949943\" and a text \"hallo zusammen\".";
        final String template = "This is my text with a number \"*\" and a text \"*\".";
        assertTrue("Wildcard didn't run proper with double quote.", WildcardStringFinder.compareTextToPattern(template, text));

        text = "This is my text with two number \"*\" and a text \"*\".";
        assertFalse("Wildcard didn't run proper with double quote.", WildcardStringFinder.compareTextToPattern(template, text));

    }

}
