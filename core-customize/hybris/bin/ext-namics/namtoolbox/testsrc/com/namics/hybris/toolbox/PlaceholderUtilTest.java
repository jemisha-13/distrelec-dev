package com.namics.hybris.toolbox;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

public class PlaceholderUtilTest extends TestCase {

    protected Map<String, Object> props;

    protected Properties results;

    protected void setUp() throws Exception {

        this.props = new HashMap<String, Object>();

        this.props.put("nullPlaceholder", null);
        this.props.put("emptyPlaceholder", "");
        this.props.put("noPlaceholder", "Text without a Placeholder.");
        this.props.put("useOfNormalePlaceholder", "Text with a ${noPlaceholder} Placeholder.");
        this.props.put("outerPlaceholder", "Text with inner ${innerPlaceholder} Placeholder.");
        this.props.put("innerPlaceholder", "Text with inner ${moreinnerPlaceholder} Placeholder.");
        this.props.put("moreinnerPlaceholder", "Inner Text without a Placeholder.");
        this.props.put("circularPlaceholder", "Text with a circular ${circularPlaceholder1} Placeholder.");
        this.props.put("circularPlaceholder1", "Text with a circular ${circularPlaceholder2} Placeholder.");
        this.props.put("circularPlaceholder2", "Text with a circular ${circularPlaceholder3} Placeholder.");
        this.props.put("circularPlaceholder3", "Text with a circular ${circularPlaceholder1} Placeholder.");

        this.results = new Properties();
        this.results.setProperty("noPlaceholder", "Text without a Placeholder.");
        this.results.setProperty("useOfNormalePlaceholder", "Text with a Text without a Placeholder. Placeholder.");
        this.results.setProperty("outerPlaceholder", "Text with inner Text with inner Inner Text without a Placeholder. Placeholder. Placeholder.");
    }

    /*
     * Test method for 'com.namics.util.PlaceholderUtil.parseStringValue(String, Properties)'
     */
    public void testParseStringValueStringProperties1() {

        String stringToParse;
        String resultOfParse;
        String expectedResult;

        stringToParse = (String) this.props.get("noPlaceholder");
        resultOfParse = PlaceholderUtil.parseStringValue(stringToParse, this.props);
        expectedResult = this.results.getProperty("noPlaceholder");
        assertEquals(expectedResult, resultOfParse);

    }

    /*
     * Test method for 'com.namics.util.PlaceholderUtil.parseStringValue(String, Properties)'
     */
    public void testParseStringValueStringProperties2() {

        String stringToParse;
        String resultOfParse;
        String expectedResult;

        stringToParse = (String) this.props.get("useOfNormalePlaceholder");
        resultOfParse = PlaceholderUtil.parseStringValue(stringToParse, this.props);
        expectedResult = this.results.getProperty("useOfNormalePlaceholder");
        assertEquals(expectedResult, resultOfParse);

    }

    /*
     * Test method for 'com.namics.util.PlaceholderUtil.parseStringValue(String, Properties)'
     */
    public void testParseStringValueStringProperties3() {

        String stringToParse;
        String resultOfParse;
        String expectedResult;

        stringToParse = (String) this.props.get("outerPlaceholder");
        resultOfParse = PlaceholderUtil.parseStringValue(stringToParse, this.props);
        expectedResult = this.results.getProperty("outerPlaceholder");
        assertEquals(expectedResult, resultOfParse);

    }

    /*
     * Test method for 'com.namics.util.PlaceholderUtil.parseStringValue(String, Properties)'
     */
    public void testParseStringValueStringProperties4() {

        String stringToParse;
        String resultOfParse;

        try {
            stringToParse = (String) this.props.get("circularPlaceholder");
            resultOfParse = PlaceholderUtil.parseStringValue(stringToParse, this.props);
            fail("Es wird eine Exception vom Typ IllegalArgumentException erwartet." + resultOfParse);
        } catch (IllegalArgumentException e) {
            // that's right
        }

    }

}
