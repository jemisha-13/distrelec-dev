/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.sapymktsync.services;

import de.hybris.platform.util.logging.CommonsHybrisLog4jWrapper;
import de.hybris.platform.util.logging.HybrisLogger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTool
{
	private static final Logger LOG = LoggerFactory.getLogger(TestTool.class);

	public static void disableCertificates() throws Exception
	{
		final TrustManager[] trustAllCerts =
		{ (TrustManager) Proxy.getProxyClass(X509TrustManager.class.getClassLoader(), X509TrustManager.class)
				.getConstructor(InvocationHandler.class).newInstance((InvocationHandler) (o, m, args) -> null) };

		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	public static void fixLogger(final Class<?> clazz) throws Exception
	{
		try
		{ // 5.6
			final Logger logger1 = LoggerFactory.getLogger(clazz);
			final Field fLogger1 = Class.forName("org.slf4j.impl.JCLLoggerAdapter").getDeclaredField("log");
			fLogger1.setAccessible(true);
			final CommonsHybrisLog4jWrapper logger2 = (CommonsHybrisLog4jWrapper) fLogger1.get(logger1);
			final Field fLogger2 = Log4JLogger.class.getDeclaredField("logger");
			fLogger2.setAccessible(true);
			final HybrisLogger logger3 = (HybrisLogger) fLogger2.get(logger2);
			logger3.setLevel(Level.DEBUG);
		}
		catch (Exception e)
		{
			LOG.info("{}", "5.6 Debug log fail for " + clazz.getName());
		}
		try
		{
			// 6.4
			final Logger logger1 = LoggerFactory.getLogger(clazz);
			final Field fLogger1 = Class.forName("org.apache.logging.slf4j.Log4jLogger").getDeclaredField("logger");
			fLogger1.setAccessible(true);
			final Object logger2 = fLogger1.get(logger1);

			final Field fLogger2 = Class.forName("de.hybris.platform.util.logging.log4j2.HybrisLog4j2Logger")
					.getDeclaredField("logger");
			fLogger2.setAccessible(true);
			final Object logger3 = fLogger2.get(logger2);
			final Field fLogger3 = Class.forName("org.apache.logging.log4j.core.Logger").getDeclaredField("privateConfig");
			fLogger3.setAccessible(true);
			final Object logger4 = fLogger3.get(logger3);
			final Field fLogger4 = logger4.getClass().getDeclaredField("intLevel");
			fLogger4.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(fLogger4, fLogger4.getModifiers() & ~Modifier.FINAL);
			//fLogger4.set(logger4, org.apache.logging.log4j.Level.DEBUG);
			fLogger4.set(logger4, 500);
		}
		catch (Exception e)
		{
			LOG.info("{}", "6.4 Debug log fail for " + clazz.getName());
		}
	}
}
