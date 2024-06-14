package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.CRLF;
import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.TAB_CHAR;
import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.TAB_CHAR_X2;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.collections.CollectionUtils;

public abstract class AbstractSitemapUrlRenderer<T extends WebSitemapUrl> implements ISitemapUrlRenderer<T> {

    /**
     * @param url
     * @param out
     * @param dateFormat
     * @param additionalData
     * @throws IOException
     */
    public void render(final WebSitemapUrl url, final OutputStreamWriter out, final W3CDateFormat dateFormat, final String additionalData) throws IOException {
        final StringBuilder xmlSB = new StringBuilder();

        xmlSB.append(TAB_CHAR).append("<url>").append(CRLF);
        xmlSB.append(TAB_CHAR_X2).append("<loc>").append(url.getUrl().toString()).append("</loc>").append(CRLF);
        if (url.getLastMod() != null) {
            xmlSB.append(TAB_CHAR_X2).append("<lastmod>").append(dateFormat.format(url.getLastMod())).append("</lastmod>").append(CRLF);
        }
        if (url.getChangeFreq() != null) {
            xmlSB.append(TAB_CHAR_X2).append("<changefreq>").append(url.getChangeFreq().toString()).append("</changefreq>\n");
        }
        if (url.getPriority() != null) {
            xmlSB.append(TAB_CHAR_X2).append("<priority>").append(url.getPriority().toString()).append("</priority>\n");
        }

        if (CollectionUtils.isNotEmpty(url.getLinks())) {
            for (final WebSitemapLink link : url.getLinks()) {
                xmlSB.append(TAB_CHAR_X2).append("<xhtml:link rel=\"").append(link.getRel()).append("\" hreflang=\"");
                xmlSB.append(link.getHreflang()).append("\" href=\"");
                xmlSB.append(link.getHref()).append("\" />").append(CRLF);
            }
        }
        if (additionalData != null) {
            xmlSB.append(additionalData);
        }
        xmlSB.append(TAB_CHAR).append("</url>").append(CRLF);

        // Write the XML content to the stream
        out.write(xmlSB.toString());
    }

    public void renderTag(final StringBuilder sb, final String namespace, final String tagName, final Object value) {
        if (value == null) {
            return;
        }
        sb.append("      <");
        sb.append(namespace);
        sb.append(':');
        sb.append(tagName);
        sb.append('>');
        sb.append(value);
        sb.append("</");
        sb.append(namespace);
        sb.append(':');
        sb.append(tagName);
        sb.append(">\n");
    }
}
