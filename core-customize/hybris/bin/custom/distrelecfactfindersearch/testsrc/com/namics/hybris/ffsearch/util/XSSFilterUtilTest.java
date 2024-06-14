package com.namics.hybris.ffsearch.util;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(DataProviderRunner.class)
public class XSSFilterUtilTest {

    private static String[][] xssData = new String[][]{
            { "<sCript>test</script>", "test" },
            { "<sCript>test", "test" },
            { "test</SCRIPT>", "test" },
            { " onclick=alert('test')", "alert('test')" },
            { "\tonerror=alert('test')", "alert('test')" },
            { "onLoad=alert('test')", "alert('test')" },
            { " onclick\n=alert('test')", "alert('test')" },
            { " onRandomHandler=alert('test')", "alert('test')" },
            { " onclick=alert(document.cookie)", "alert()" },
            { "\"search term\"", "\"search term\"" }
    };

    @DataProvider
    public static Object[][] getXssData() {
        return xssData;
    }

    @Test
    @UseDataProvider("getXssData")
    public void testFilterUsingRules(String inputData, String cleanData) {

       String cleanValue = XSSFilterUtil.filterUsingRules(inputData);
       assertEquals(cleanData, cleanValue);

    }

    @Test
    public void testSanitiseQueryString() {

        String testQueryString = "filter_CuratedProducts=1_Robotics&q%22%3E%3C/script%3E%3CDyEwZOt%3Eabc%3C/DyEwZOt%3Edef=*";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getQueryString()).thenReturn(testQueryString);
        String cleanValue = XSSFilterUtil.sanitiseQueryString(request);
        assertEquals("filter_CuratedProducts=1_Robotics&amp;q&#34;&gt;&lt;/script&gt;&lt;DyEwZOt&gt;abc&lt;/DyEwZOt&gt;def=*", cleanValue);
    }

}
