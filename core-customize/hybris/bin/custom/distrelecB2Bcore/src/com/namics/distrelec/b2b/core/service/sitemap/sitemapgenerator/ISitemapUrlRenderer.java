package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.io.IOException;
import java.io.OutputStreamWriter;

interface ISitemapUrlRenderer<T extends ISitemapUrl> {

    public Class<T> getUrlClass();

    public String getXmlNamespaces();

    public void render(final T url, final OutputStreamWriter out, final W3CDateFormat dateFormat) throws IOException;
}
