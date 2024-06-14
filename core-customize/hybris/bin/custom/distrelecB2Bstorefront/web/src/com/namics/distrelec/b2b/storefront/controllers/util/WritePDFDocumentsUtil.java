package com.namics.distrelec.b2b.storefront.controllers.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This utility calss is responsiable for writing a pdf file from an external source and then write it on the response
 * 
 * @author datneerajs, Elfa Distrelec AB
 * @since Namics Extensions 1.0
 * 
 */
public class WritePDFDocumentsUtil {

    public static void writePdftoResponse(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String url = request.getQueryString();
        if (StringUtils.isEmpty(url)) {
            return;
        }
        final URL aurl = new URL(url);
        final URLConnection urlConnection = aurl.openConnection();
        final int contentLength = urlConnection.getContentLength();
        final InputStream raw = urlConnection.getInputStream();
        final InputStream in = new BufferedInputStream(raw);
        final byte[] data = new byte[contentLength];
        int bytesRead = 0;
        int offset = 0;
        while (offset < contentLength) {
            bytesRead = in.read(data, offset, data.length - offset);
            if (bytesRead == -1) {
                break;
            }
            offset += bytesRead;
        }
        in.close();

        if (offset != contentLength) {
            throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
        }

        final ServletOutputStream out = response.getOutputStream();
        out.write(data);
        out.flush();
        out.close();
    }
}
