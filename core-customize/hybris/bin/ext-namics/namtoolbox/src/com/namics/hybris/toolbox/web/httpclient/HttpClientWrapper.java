/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.web.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

/***
 * Providers usecase driven method to send post and gets with Apache HttpClient 4.1 Handles SSL with untrusted certificates.
 * 
 * @author rhaeberli, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class HttpClientWrapper {

    private static final String ENCODING = "UTF-8";
    private static final Logger LOG = Logger.getLogger(HttpClientWrapper.class);
    private static DefaultHttpClient httpClient = new DefaultHttpClient();

    { // NOPMD
        this.setUpSSLWithUntrustedCertificate();
    }

    public HttpResponseResult executeGet(final String url) {
        final HttpGet httpGet = new HttpGet(url);
        return executeHttpMethod(httpGet);
    }

    public HttpResponseResult executePost(final String url, final Map<String, Object> postParameters) {
        final HttpPost httpPost = new HttpPost(url);
        preparePost(postParameters, httpPost);
        return executeHttpMethod(httpPost);

    }

    public HttpResponseResult executeHttpMethod(final HttpRequestBase request) {
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            throw new RuntimeException("Could not send HttpRequest.", e);
        }

        return getContentFolloowingRedirects(httpResponse);
    }

    public void preparePost(final Map<String, Object> postParameters, final HttpPost httpPost) {
        final Set<Entry<String, Object>> entrySet = postParameters.entrySet();
        final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        addingStringOrAllStringsOfArray(entrySet, nameValuePairs);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding " + ENCODING + " not supported", e);
        }
    }

    private void addingStringOrAllStringsOfArray(final Set<Entry<String, Object>> entrySet, final List<NameValuePair> nameValuePairs) {
        for (Entry<String, Object> entry : entrySet) {
            final Object value = entry.getValue();
            final String key = entry.getKey();
            if (value instanceof String[]) {
                final String[] values = (String[]) value;
                for (String valueOfArray : values) {
                    nameValuePairs.add(new BasicNameValuePair(key, valueOfArray));
                }
            } else if (value instanceof String) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), (String) value));
            }
        }
    }

    private HttpResponseResult getContentOfRepsonse(final HttpResponse httpResponse) {
        final StringBuilder content = new StringBuilder();
        String line = "";
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            while ((line = buffer.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read  Content of response.", e);
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }
        return new HttpResponseResult(content.toString(), httpResponse.getStatusLine().getStatusCode());

    }

    private void setUpSSLWithUntrustedCertificate() {
        final X509TrustManager tm = new X509TrustManager() {

            public void checkClientTrusted(final X509Certificate[] xcs, final String string) throws CertificateException {
            }

            public void checkServerTrusted(final X509Certificate[] xcs, final String string) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try {
            sslContext.init(null, new TrustManager[] { tm }, null);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }

        final SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        final ClientConnectionManager ccm = httpClient.getConnectionManager();

        final SchemeRegistry schemeRegistry = ccm.getSchemeRegistry();

        schemeRegistry.register(new Scheme("https", 443, socketFactory));
        schemeRegistry.register(new Scheme("https2", 9002, socketFactory));
    }

    private HttpResponseResult getContentFolloowingRedirects(final HttpResponse httpResponse) {
        HttpResponseResult content = null;
        if (isRedirect(httpResponse)) {
            final String destUrlOfRedirect = getDestUrlOfRedirect(httpResponse);
            LOG.warn("Redirected to " + destUrlOfRedirect);
            closeResponse(httpResponse);
            content = executeGet(destUrlOfRedirect);
        } else {
            content = getContentOfRepsonse(httpResponse);
        }
        return content;
    }

    private String getDestUrlOfRedirect(final HttpResponse httpResponse) {
        String url = "";
        final Header[] headers = httpResponse.getHeaders("Location");

        if (headers.length > 0) {
            url = headers[0].getValue();
        }
        return url;
    }

    private void closeResponse(final HttpResponse postResponse) {
        try {
            postResponse.getEntity().getContent().close();
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isRedirect(final HttpResponse postResponse) {
        final int statusCode = postResponse.getStatusLine().getStatusCode();
        return isStatusCode30X(statusCode);
    }

    private boolean isStatusCode30X(final int statusCode) {
        return statusCode / 100 == 3;
    }

}
