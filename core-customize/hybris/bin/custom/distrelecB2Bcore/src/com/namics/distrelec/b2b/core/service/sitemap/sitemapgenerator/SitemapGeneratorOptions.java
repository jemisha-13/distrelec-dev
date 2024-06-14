package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

class SitemapGeneratorOptions extends AbstractSitemapGeneratorOptions<SitemapGeneratorOptions> {

    public SitemapGeneratorOptions(URL baseUrl, File baseDir) {
        super(baseUrl, baseDir);
    }

    public SitemapGeneratorOptions(String baseUrl, File baseDir) throws MalformedURLException {
        this(new URL(baseUrl), baseDir);
    }

}
