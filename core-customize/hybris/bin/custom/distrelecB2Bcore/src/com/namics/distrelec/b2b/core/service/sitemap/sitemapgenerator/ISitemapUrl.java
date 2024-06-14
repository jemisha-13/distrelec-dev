package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.net.URL;
import java.util.Date;

interface ISitemapUrl {

    public abstract Date getLastMod();

    public abstract URL getUrl();

}