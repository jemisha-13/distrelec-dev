package com.namics.hybris.toolbox.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XmlUtilTest {

    @Test
    public void testGenerateXmlAttribute() {
        assertEquals("key=\"value\"", XmlUtil.generateXmlAttribute("key", "value"));

    }

    @Test
    public void testGenerateXmlLineStringString() {
        final String pattern = "<myTag key1=## />";
        final String expected = "<myTag key1=\"myValue1\" />";

        assertEquals(expected, XmlUtil.generateXmlLine(pattern, "myValue1"));
    }

    @Test
    public void testGenerateXmlLineStringStringArray() {
        final String[] attributeArray = new String[] { "myValue1", "myValue2" };
        final String pattern = "<myTag key1=## key2=## />";
        final String expected = "<myTag key1=\"myValue1\" key2=\"myValue2\" />";

        assertEquals(expected, XmlUtil.generateXmlLine(pattern, attributeArray));
    }

}
