/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import com.google.common.base.Charsets;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link DistUtils} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class DistUtilsTest {

	private static final String PRODUCT_NAME = "Multipurpose Tool Multi-Purpose 420HC Knife / 420HC Serrated Knife / Can " +
			"/ Bottle Opener / Can / Bottle Opener / Large Screwdriver / Medium Screwdriver / Oxygen Tank Wrench " +
			"/ Phillips Screwdriver / Saw / Small Screwdriver / Spring";
	private static final int CHARACTER_LIMIT = 180;

	@Test
	public void testTruncateWithCharset() {
		// Init
		final char[] chars = new char[300];
		Arrays.fill(chars, 'a');
		String string = new String(chars);

		// Action
		string = DistUtils.truncateWithCharset(string, 255, Charsets.UTF_8);

		// Evaluation
		assertEquals(255, string.length());
	}

	@Test
	public void testTruncateWithCharsetSpecialChar() {
		// Init
		final char[] chars = new char[300];
		Arrays.fill(chars, '�');
		String string = new String(chars);

		// Action
		string = DistUtils.truncateWithCharset(string, 255, Charsets.UTF_8);

		// Evaluation
		assertEquals(255, string.getBytes(Charsets.UTF_8).length);
	}

	@Test
	public void testProductNameTruncation(){
		final String expected = PRODUCT_NAME.substring(0, CHARACTER_LIMIT);
		final String result = DistUtils.truncateProductName(CHARACTER_LIMIT, PRODUCT_NAME);

		assertEquals(CHARACTER_LIMIT, result.length());
		assertTrue(expected.equalsIgnoreCase(result));
	}

	@Test
	public void testReturnEmptyWhenNameIsEmpty(){
		final String expected = StringUtils.EMPTY;
		final String result = DistUtils.truncateProductName(CHARACTER_LIMIT, StringUtils.EMPTY);

		assertEquals(0, result.length());
		assertTrue(expected.equalsIgnoreCase(result));
	}

	@Test
	public void testReturnEmptyWhenNameIsNull(){
		final String result = DistUtils.truncateProductName(CHARACTER_LIMIT, null);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testStartsWithHttpHttps() {
		assertTrue(DistUtils.startsWithHttpHttps("http://test"));
		assertTrue(DistUtils.startsWithHttpHttps("https://test"));
		assertTrue(DistUtils.startsWithHttpHttps("HTTP://test"));
		assertFalse(DistUtils.startsWithHttpHttps("htps://test"));
	}
}
