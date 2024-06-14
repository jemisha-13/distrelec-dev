/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.spring;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;

public class ResourceUtilsTest {

    @Test
    public void testReceiveContentFromUrl() throws Exception {

        final String url = "http://www.google.ch";
        final InputStream inputStream = ResourceUtils.receiveContentFromUrl(url);
        final String resultString = ResourceUtils.convertStreamToString(inputStream);
        Assert.assertTrue("The content of '" + url + "' couldn't be streamed in.", resultString.toLowerCase().contains("google"));
    }

    @Test
    public void testCreateResourceFromString() throws Exception {
        Assert.assertTrue(ResourceUtils.createResourceFromString("classpath:C:\\mydata\\", false) instanceof ClassPathResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("classpath:f:/mydata/", false) instanceof ClassPathResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("classpath:test/images/Water lilies.jpg", false) instanceof ClassPathResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("http://www.google.ch", false) instanceof UrlResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("ftp://ftp.ftpserver.ch", false) instanceof UrlResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("C:\\mydata\\", false) instanceof FileSystemResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("f:/mydata/", false) instanceof FileSystemResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("\\\\server\\uncpath\\", false) instanceof FileSystemResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("//server/uncpath/", false) instanceof FileSystemResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("file:C:\\mydata\\", false) instanceof UrlResource);
        Assert.assertTrue(ResourceUtils.createResourceFromString("file:f:/mydata/", false) instanceof UrlResource);
    }

    @Test
    public void testRegexExpressionsPositive() throws Exception {
        final String localTeststring = "C:\\mydata\\";
        final String uncTeststring = "\\\\server\\uncpath\\";
        final Matcher localFilenameMatcher = ResourceUtils.LOCAL_FILENAME_PATTERN.matcher(localTeststring);
        final Matcher uncFilenameMatcher = ResourceUtils.UNC_FILENAME_PATTERN.matcher(uncTeststring);

        Assert.assertTrue(ResourceUtils.LOCAL_FILENAME_PATTERN + " is not matching " + localTeststring + ".", localFilenameMatcher.matches());
        Assert.assertTrue(ResourceUtils.UNC_FILENAME_PATTERN + " is not matching " + uncTeststring + ".", uncFilenameMatcher.matches());
    }

    @Test
    public void testRegexExpressionsNegative() throws Exception {
        final String localTeststring = "ftp://ftp.ftpserver.ch";
        final String uncTeststring = "file:C:\\mydata\\";
        final Matcher localFilenameMatcher = ResourceUtils.LOCAL_FILENAME_PATTERN.matcher(localTeststring);
        final Matcher uncFilenameMatcher = ResourceUtils.UNC_FILENAME_PATTERN.matcher(uncTeststring);

        Assert.assertFalse(ResourceUtils.LOCAL_FILENAME_PATTERN + " is matching " + localTeststring + ".", localFilenameMatcher.matches());
        Assert.assertFalse(ResourceUtils.UNC_FILENAME_PATTERN + " is matching " + uncTeststring + ".", uncFilenameMatcher.matches());
    }

    @Test
    public void testConvertStreamToString() throws Exception {
        final String stringToCompare = "http://www.google.ch\nHello google";
        final InputStream inputStream = new StringBufferInputStream(stringToCompare);

        final String resultString = ResourceUtils.convertStreamToString(inputStream);
        Assert.assertEquals(stringToCompare, resultString);

    }

}
