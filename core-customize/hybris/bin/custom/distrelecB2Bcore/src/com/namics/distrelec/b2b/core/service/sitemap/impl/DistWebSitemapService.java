package com.namics.distrelec.b2b.core.service.sitemap.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.activemq.artemis.utils.ConcurrentHashSet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.namics.distrelec.b2b.core.service.sitemap.utils.WebSitemapHelper;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DistWebSitemapService implements WebSitemapService {

    private static final String WEBSITEMAP_DATA_ROOTDIR = "websitemap.data.rootdir";

    private static final String WEBSITEMAP_INDEX_FILENAME = "sitemap_index";

    private static final String SYS_MASTER_EXPORT_CONTAINER = "sys-master-exports";

    private static final Logger LOG = LoggerFactory.getLogger(DistWebSitemapService.class);

    private ConfigurationService configurationService;

    @Autowired
    private AzureFileUploaderImpl azureFileUploader;

    @Override
    public void generateSitemapForSite(final CMSSiteModel cmsSiteModel, String domain, List<WebSitemapUrlsProvider> webSitemapUrlsProviderList) {
        String websiteId = cmsSiteModel.getUid();

        final String dirPathTmp = System.getProperty("java.io.tmpdir") + File.separator + "sitemap" + File.separator + websiteId + "-tmp";
        LOG.info("Temporary sitemap folder: " + dirPathTmp);
        final File tmpDir = getOrMakeDirs(dirPathTmp);
        try {
            FileUtils.cleanDirectory(tmpDir);
        } catch (final IOException e) {
            LOG.error("Could not clean up temp directory: " + dirPathTmp, e);
        }

        final String webSitemapRootDir = configurationService.getConfiguration().getString(WEBSITEMAP_DATA_ROOTDIR);
        final String dirPath = (webSitemapRootDir + "/" + websiteId).replace("/", File.separator);
        final File dir = getOrMakeDirs(dirPath);

        final W3CDateFormat dateFormat = new W3CDateFormat(W3CDateFormat.Pattern.DAY);
        Set<LanguageModel> languages = null;
        try {
            final Map<LanguageModel, Set<File>> sitemapLanguageIndexFiles = new HashMap<>();
            for (WebSitemapUrlsProvider provider : ListUtils.emptyIfNull(webSitemapUrlsProviderList)) {
                LOG.info("Starting generating URLs for {}", provider.getEntityName());

                final long urlGenerationTime = System.currentTimeMillis();
                final Map<LanguageModel, Collection<WebSitemapUrl>> urlsMap = provider.getLanguageWebSitemapUrlsForWebsite();
                LOG.info("{} - {} URL generation total time: {} ms", websiteId, provider.getEntityName(), System.currentTimeMillis() - urlGenerationTime);

                languages = urlsMap.keySet();

                for (LanguageModel language : languages) {
                    final long writingTime = System.currentTimeMillis();
                    WebSitemapGenerator webSitemapGenerator;
                    try {
                        webSitemapGenerator = WebSitemapGenerator.builder(domain, tmpDir)
                                                                 .dateFormat(dateFormat)
                                                                 .language(language.getIsocode())
                                                                 .gzip(true)
                                                                 .build();

                        if (CollectionUtils.isNotEmpty(urlsMap.get(language))) {
                            Map<String, Collection<WebSitemapUrl>> providerMapping = new HashMap<>();
                            providerMapping.put(provider.getEntityName(), urlsMap.get(language));
                            webSitemapGenerator.addUrls(providerMapping);
                            if (!sitemapLanguageIndexFiles.containsKey(language)) {
                                sitemapLanguageIndexFiles.put(language, new ConcurrentHashSet<>());
                            }
                            sitemapLanguageIndexFiles.get(language).addAll(webSitemapGenerator.write());
                        } else {
                            LOG.warn("No urls found for {} and entityName {}", websiteId, provider.getEntityName());
                        }
                    } catch (final MalformedURLException e) {
                        LOG.error("Could not generate websitemap for website with id: " + websiteId, e);
                    }
                    LOG.info("{} - {} --> Writing Time: {} ms", websiteId, provider.getClass().getSimpleName(), System.currentTimeMillis() - writingTime);
                }

                // Invoke GC to clean up memory if the ratio between the free space and the max space is < 5%
                if ((double) Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory() < 0.05) {
                    System.gc();
                }
            }

            final Set<File> sitemapIndexFiles = new HashSet<>();
            for (LanguageModel language : languages) {
                sitemapIndexFiles.add(writeSitemapIndex(domain, tmpDir, new ArrayList<>(sitemapLanguageIndexFiles.get(language)), dateFormat,
                                                        language.getIsocode()));
            }
            writeSitemapIndex(domain, tmpDir, new ArrayList<>(sitemapIndexFiles), dateFormat, StringUtils.EMPTY);

            cleanUpTmpFiles(dir, tmpDir);
        } catch (final MalformedURLException e) {
            LOG.error("Could not generate websitemap for website with id: " + websiteId, e);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private File writeSitemapIndex(final String baseUrl, final File baseDir, final List<File> files, final W3CDateFormat dateFormat,
                                   String language) throws MalformedURLException {
        // Sort the files
        files.sort(Comparator.comparing(File::getName));

        final File sitemap_index = new File(baseDir, getSitemapIndexFilename(language));
        final SitemapIndexGenerator sitemapIndexGenerator = new SitemapIndexGenerator.Options(baseUrl, sitemap_index).dateFormat(dateFormat).build();
        sitemapIndexGenerator.addUrls(files).write();
        return sitemap_index;
    }

    private String getSitemapIndexFilename(String language) {
        return WEBSITEMAP_INDEX_FILENAME + (StringUtils.isNotBlank(language) ? "_" + language : "") + ".xml";
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

    @Override
    public void setWebSitemapHelper(final WebSitemapHelper webSitemapHelper) {

    }
}
