/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.fest.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.integration.azure.uploader.impl.AzureFileUploaderImpl;
import com.namics.distrelec.b2b.core.service.sitemap.WebSitemapService;
import com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.SitemapIndexGenerator;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.W3CDateFormat;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapGenerator;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapUrl;
import com.namics.distrelec.b2b.core.service.sitemap.utils.SitemapThreadFactory;
import com.namics.distrelec.b2b.core.service.sitemap.utils.WebSitemapHelper;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.regioncache.ConcurrentHashSet;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * {@code DefaultWebSitemapService}
 * <p>
 * First implementation done by csieber, Namics AG.<br/>
 * since Namics Extensions 1.0
 * </p>
 * <p>
 * Rewritten by <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * </p>
 * 
 * @author csieber, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DefaultWebSitemapService implements WebSitemapService {

    private static final String WEBSITEMAP_DATA_ROOTDIR = "websitemap.data.rootdir";

    private static final String WEBSITEMAP_INDEX_FILENAME = "sitemap_index.xml";

    private static final String SYS_MASTER_EXPORT_CONTAINER = "sys-master-exports";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebSitemapService.class);

    private ConfigurationService configurationService;

    private WebSitemapHelper webSitemapHelper;

    @Autowired
    private AzureFileUploaderImpl azureFileUploader;

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.sitemap.WebSitemapService#generateSitemapForSite(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public void generateSitemapForSite(final CMSSiteModel cmsSiteModel, final String domain, List<WebSitemapUrlsProvider> webSitemapUrlsProviderList) {
        String websiteId = cmsSiteModel.getUid();
        final String webSitemapRootDir = configurationService.getConfiguration().getString(WEBSITEMAP_DATA_ROOTDIR);
        final String dirPath = (webSitemapRootDir + "/" + websiteId).replace("/", File.separator);
        final String dirPathTmp = System.getProperty("java.io.tmpdir") + File.separator + "sitemap" + File.separator + websiteId + "-tmp";
        LOG.info("Temporary sitemap folder: " + dirPathTmp);
        final File tmpDir = getOrMakeDirs(dirPathTmp);
        try {
            FileUtils.cleanDirectory(tmpDir);
        } catch (final IOException e) {
            LOG.error("Could not clean up temp directory: " + dirPathTmp, e);
        }

        final File dir = getOrMakeDirs(dirPath);

        try {
            if (CollectionUtils.isNotEmpty(webSitemapUrlsProviderList)) {
                // Creating a task manager to handle some operations asynchronously.
                final ExecutorService executorService = Executors.newFixedThreadPool(webSitemapUrlsProviderList.size() * 2, new SitemapThreadFactory(
                                                                                                                                                     "sitemap-"
                                                                                                                                                     + websiteId
                                                                                                                                                     + "-",
                                                                                                                                                     Thread.NORM_PRIORITY));

                final Iterator<WebSitemapUrlsProvider> iterator = webSitemapUrlsProviderList.iterator();
                final Set<File> sitemapIndexFiles = new ConcurrentHashSet<File>();
                final W3CDateFormat dateFormat = new W3CDateFormat(W3CDateFormat.Pattern.DAY);

                while (iterator.hasNext()) {
                    // Take the next element
                    final WebSitemapUrlsProvider provider = iterator.next();
                    LOG.info("Starting generating URLs for {}", provider.getEntityName());
                    // Remove the current element from the list
                    iterator.remove();

                    // Create the URLs map
                    final Map<String, Collection<WebSitemapUrl>> urlsMap = new ConcurrentHashMap<String, Collection<WebSitemapUrl>>();
                    // Start processing
                    addUrlsToList(urlsMap, websiteId, provider);

                    executorService.submit(new Runnable() {

                        @Override
                        public void run() {
                            final long time = System.currentTimeMillis();
                            WebSitemapGenerator webSitemapGenerator;
                            try {
                                webSitemapGenerator = WebSitemapGenerator.builder(domain, tmpDir).dateFormat(dateFormat).gzip(true).build();

                                if (MapUtils.isNotEmpty(urlsMap)) {
                                    webSitemapGenerator.addUrls(urlsMap);
                                    final List<File> generatedWebSitemapFiles = webSitemapGenerator.write();
                                    sitemapIndexFiles.addAll(generatedWebSitemapFiles);
                                } else {
                                    LOG.warn("No urls found for {} and entityName {}", new Object[] { websiteId, provider.getEntityName() });
                                }
                            } catch (final MalformedURLException e) {
                                LOG.error("Could not generate websitemap for website with id: " + websiteId, e);
                            }
                            // Clear the URL maps
                            urlsMap.clear();
                            LOG.info("{} - {} --> Time: {} ms",
                                     new Object[] { websiteId, provider.getClass().getSimpleName(), Long.valueOf(System.currentTimeMillis() - time) });
                        }
                    });

                    // Invoke GC to clean up memory if the ratio between the free space and the max space is < 5%
                    if ((double) Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory() < 0.05) {
                        System.gc();
                    }
                }

                // Shutting down the task manager
                executorService.shutdown();
                // Waiting for submitted tasks termination. Max wait = 24h
                executorService.awaitTermination(1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS);

                // Write the sitemap index
                writeSitemapIndex(domain, tmpDir, new ArrayList<File>(sitemapIndexFiles), dateFormat, false);
            }

            cleanUpTmpFiles(dir, tmpDir);
        } catch (final MalformedURLException e) {
            LOG.error("Could not generate websitemap for website with id: " + websiteId, e);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void addUrlsToList(final Map<String, Collection<WebSitemapUrl>> urlsMap, final String websiteId, final WebSitemapUrlsProvider provider) {
        // Set the blacklist
        provider.setBlackList(getBlackList(websiteId, provider));
        final long time = System.currentTimeMillis();
        final Collection<WebSitemapUrl> webSitemapUrls = provider.getWebSitemapUrlsForWebsite();
        LOG.info("{} - {} URL generation total time: {} ms",
                 new String[] { websiteId, provider.getEntityName(), String.valueOf(System.currentTimeMillis() - time) });

        urlsMap.put(provider.getEntityName(), webSitemapUrls);
    }

    private List<String> getBlackList(final String websiteId, final WebSitemapUrlsProvider provider) {
        return getWebSitemapHelper().getBlacklistForSiteAndEntity(websiteId, provider.getEntityName());
    }

    /**
     * Write the sitemap index to the file
     *
     * @param baseUrl
     * @param baseDir
     * @param files
     * @param dateFormat
     * @param autoValidate
     * @throws MalformedURLException
     */
    private void writeSitemapIndex(final String baseUrl, final File baseDir, final List<File> files, final W3CDateFormat dateFormat, final boolean autoValidate)
                                                                                                                                                                 throws MalformedURLException {

        // Sort the files
        Collections.sort(files, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        final File sitemap_index = new File(baseDir, WEBSITEMAP_INDEX_FILENAME);

        final SitemapIndexGenerator sitemapIndexGenerator = new SitemapIndexGenerator.Options(baseUrl, sitemap_index).dateFormat(dateFormat)
                                                                                                                     .autoValidate(autoValidate).build();
        sitemapIndexGenerator.addUrls(files).write();
    }

    /**
     * Check if the file given by its absolute path exists or not. In case if the folder does not exist then tries to create it.
     * 
     * @param dirPath
     * @return the file descriptor of the folder with path {@code dirPath}
     */
    private File getOrMakeDirs(final String dirPath) {
        final File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.error("Could not create the directory " + dirPath);
        }

        return dir;
    }

    private void cleanUpTmpFiles(final File dir, final File tmpDir) {
        boolean error = false;
        final File[] files = tmpDir.listFiles();
        try {
            FileUtils.cleanDirectory(dir);
            if (!Arrays.isEmpty(files)) {
                for (final File file : files) {
                    azureFileUploader.uploadToContainerAndDeleteLocalFile(file, dir.getPath(), SYS_MASTER_EXPORT_CONTAINER);
                }
            }

            if (!error && tmpDir.exists()) {
                try {
                    FileUtils.deleteDirectory(tmpDir);
                } catch (final IOException e) {
                    LOG.error("Could not delete tmp dir " + tmpDir.getAbsolutePath(), e);
                }
            }
        } catch (final IOException e) {
            LOG.error("Could not clean dir " + dir.getAbsolutePath(), e);
        }
    }

    @Override
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public WebSitemapHelper getWebSitemapHelper() {
        return webSitemapHelper;
    }

    @Override
    public void setWebSitemapHelper(final WebSitemapHelper webSitemapHelper) {
        this.webSitemapHelper = webSitemapHelper;
    }
}
