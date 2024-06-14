package com.namics.hybris.toolbox;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class StringOperationTest extends TestCase {

    public void testSplitString() {
        final String[] delimiters = new String[] { ",", "@SPL@" };
        final String stringToSplit = ",text,hallo@SPL@@SPL@world,h@SPL@w,text@SPL@";
        final String[] expectedResult = new String[] { "", "text", "hallo", "", "world", "h", "w", "text", "" };

        final List<String> tokens = StringOperation.splitString(stringToSplit, delimiters);

        int i = 0;
        for (final Iterator<String> iterator = tokens.iterator(); iterator.hasNext();) {
            final String token = iterator.next();
            assertEquals(expectedResult[i++], token);
        }
    }

    public void testStripHtmlWithRegExp() {
        final String expectedResult = "This is a plain Text";
        final String htmlText = "<span><p>This is a plain Text</p></br></span>";

        final String noHtmlText = StringOperation.stripHtmlWithRegExp(htmlText);
        System.out.println("noHtmlText: " + noHtmlText);
        assertEquals(expectedResult, noHtmlText);
    }

}
