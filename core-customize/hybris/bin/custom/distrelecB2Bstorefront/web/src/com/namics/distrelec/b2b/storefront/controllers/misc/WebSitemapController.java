package com.namics.distrelec.b2b.storefront.controllers.misc;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hybris.platform.jalo.media.MediaManager;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.storefront.controllers.AbstractController;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

/**
 * Controller website xml and it's gzipped sub xml
 *
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 *
 */
@Controller
public class WebSitemapController extends AbstractController {
    private static final Logger LOG = Logger.getLogger(WebSitemapController.class);

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private MediaManager mediaManager;

    private String websitemapRootDir;
    private static final String SYS_MASTER_EXPORT_CONTAINER = "exports";

    @RequestMapping(value = { "/sitemap{number:[\\d]+}.xml.gz" }, method = RequestMethod.GET, produces = { "application/x-gzip" })
    public void getWebSitemapPartGZip(final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType("application/x-gzip");
        writeFileInputStreamByServletPath(request, response);
    }

    @RequestMapping(value = { "/sitemap_PRODUCT_{number:[\\d]+}.xml.gz", "/sitemap_PRODUCT.xml.gz","/sitemap_PRODUCT_{lang:[\\w]+}_{number:[\\d]+}.xml.gz", "/sitemap_PRODUCT_{lang:[\\w]+}.xml.gz", 
                              "/sitemap_CATEGORY_{number:[\\d]+}.xml.gz", "/sitemap_CATEGORY.xml.gz","/sitemap_CATEGORY_{lang:[\\w]+}_{number:[\\d]+}.xml.gz", "/sitemap_CATEGORY_{lang:[\\w]+}.xml.gz",
                              "/sitemap_MANUFACTURER_{number:[\\d]+}.xml.gz", "/sitemap_MANUFACTURER.xml.gz","/sitemap_MANUFACTURER_{lang:[\\w]+}_{number:[\\d]+}.xml.gz", "/sitemap_MANUFACTURER_{lang:[\\w]+}.xml.gz",
                               "/sitemap_PRODUCT_FAMILY_{number:[\\d]+}.xml.gz","/sitemap_PRODUCT_FAMILY.xml.gz","/sitemap_PRODUCT_FAMILY_{lang:[\\w]+}_{number:[\\d]+}.xml.gz","/sitemap_PRODUCT_FAMILY_{lang:[\\w]+}.xml.gz" },
                    method = RequestMethod.GET, produces = { "application/x-gzip" })
    public void getNamedWebSitemapPartGZip(final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType("application/x-gzip");
        writeFileInputStreamByServletPath(request, response);
    }

    @RequestMapping(value = { "/sitemap.xml.gz" }, method = RequestMethod.GET, produces = { "application/x-gzip" })
    public void getWebSitemapGZip(final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType("application/x-gzip");
        writeFileInputStreamByServletPath(request, response);
    }

    @RequestMapping(value = { "/sitemap_index.xml",  "/sitemap_index_{lang:[\\w]+}.xml"}, method = RequestMethod.GET, produces = { "application/xml" })
    public void getWebSitemapIndexXml(final HttpServletRequest request, final HttpServletResponse response) {
        writeFileInputStreamByServletPath(request, response);
    }

    @RequestMapping(value = { "/sitemap.xml" }, method = RequestMethod.GET, produces = { "application/xml" })
    public void getWebSitemapXml(final HttpServletRequest request, final HttpServletResponse response) {
        writeFileInputStreamByServletPath(request, response);
    }

    private void writeFileInputStreamByServletPath(final HttpServletRequest request, final HttpServletResponse response) {
        final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
        final String dirPath = this.websitemapRootDir + "/" + cmsSite.getUid();
        final String filePath = dirPath + request.getServletPath();

        try {
            final InputStream fileInputStream = mediaManager.getMediaAsStreamWithSize(SYS_MASTER_EXPORT_CONTAINER,filePath).getInputStream();
            IOUtils.copy(fileInputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (final IOException e) {
            LOG.error("Error writing file to output stream: " + filePath, e);
            throw new HttpNotFoundException("IOError writing file to output stream: " + filePath, e);
        }

    }

    @Value("${websitemap.data.rootdir}")
    public void setWebsitemapRootDir(final String websitemapRootDir) {
        this.websitemapRootDir = websitemapRootDir;
    }
}