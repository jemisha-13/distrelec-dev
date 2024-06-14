/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.hybris.ffsearch.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.model.export.DistCategoryStructureExportCronJobModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * {@code DistCategoryStructureExportJob}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.10
 */
public class DistCategoryStructureExportJob extends AbstractJobPerformable<DistCategoryStructureExportCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistCategoryStructureExportJob.class);

    private static final String DEFAULT_NAME_PREFIX = "export.category.structure.";
    private static final String CSV_FILE_SUFFIX = ".csv";
    private static final String ZIP_FILE_SUFFIX = ".zip";
    private static final String COLUMN_SEP = "\";\"";
    private static final String COLUMN_SE = "\"";
    private static final String CRLF = "\r\n";
    private static final String E_CRLF = "\"" + CRLF;

    private ConfigurationService configurationService;
    private CommonI18NService commonI18NService;
    private DistCategoryService distCategoryService;

    private DistFactFinderExportHelper exportHelper;

    private static final String[] HEADERS = { "Category-ID", "Parent-Categories-IDs", "Category-Name" };

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(final DistCategoryStructureExportCronJobModel cronJob) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(
                "SELECT {c." + CategoryModel.PK + "} FROM {" + CategoryModel._TYPECODE + "! AS c} WHERE {c." + CategoryModel.CODE + "} = 'cat-L0D_324785'");
        boolean success = true;
        try {
            final CategoryModel root = flexibleSearchService.<CategoryModel> searchUnique(searchQuery);

            if (CollectionUtils.isNotEmpty(root.getCategories())) {
                final Locale locale = cronJob.getChannel() != null && cronJob.getChannel().getLanguage() != null
                        ? getCommonI18NService().getLocaleForLanguage(cronJob.getChannel().getLanguage()) : new Locale("en");

                LOG.info("Starting export of category structure for channel " + cronJob.getChannel().getChannel());
                final String fileName = buildFileName(cronJob, cronJob.getChannel());

                final File exportFile = new File(getExportZipFilePath(cronJob, fileName));
                final ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(exportFile));
                zipOutputStream.putNextEntry(new ZipEntry(fileName + CSV_FILE_SUFFIX));

                // Start writing content to the zip file.
                zipOutputStream.write(buildHeadersString().getBytes());

                for (final CategoryModel child : root.getCategories()) {
                    exportCategoryTree(child, cronJob.getChannel().getCmsSite(), zipOutputStream, null, locale);
                }

                zipOutputStream.closeEntry();
                zipOutputStream.finish();
                IOUtils.closeQuietly(zipOutputStream);
                
                boolean scpUpload = getConfigurationService().getConfiguration().getBoolean(DistConstants.PropKey.FactFinder.EXPORT_UPLOAD_VIA_SCP, false);
                
                if (scpUpload) {
                    
                    StringBuilder uploadFileName = new StringBuilder(fileName)
                            .append(ZIP_FILE_SUFFIX);                    		
                    
                    // upload to scp
                    InputStream input = new FileInputStream(exportFile);
                     
                    getExportHelper().uploadToScp(input, uploadFileName.toString(), exportFile);

                }
                
                
                LOG.info("End of category structure export for channel " + cronJob.getChannel().getChannel());
            } else {
                LOG.info("No sub-category linked to the root category!");
            }
        } catch (final Exception exp) {
            LOG.error(exp.getMessage(), exp);
            success = false;
        }

        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    /**
     * Build the headers string which will be written into the destination export file.
     * 
     * @return the headers string.
     */
    private String buildHeadersString() {
        final StringBuilder sb = new StringBuilder(COLUMN_SE);
        for (final String header : HEADERS) {
            sb.append(header).append(COLUMN_SEP);
        }

        return sb.replace(sb.length() - 2, sb.length(), CRLF).toString();
    }

    /**
     * Builds the absolute file path excluding the extension.
     * 
     * @param cronJob
     * @return the export ZIP file absolute path.
     */
    protected String getExportZipFilePath(final DistCategoryStructureExportCronJobModel cronJob, final String fileName) {
        final String exportDirectoryPath = cronJob.getExportDirectory() != null ? cronJob.getExportDirectory()
                : getConfigurationService().getConfiguration().getString("distrelecfactfindersearch.export.upload.directory");
        if (exportDirectoryPath == null) {
            throw new RuntimeException("No export directory set!");
        }
        final File exportDirectory = new File(exportDirectoryPath);
        if (!exportDirectory.exists() && !exportDirectory.mkdirs()) {
            throw new RuntimeException("Could not create the export directory!");
        }

        final StringBuilder sb = new StringBuilder(exportDirectoryPath)
                .append(exportDirectoryPath.endsWith(File.separator) ? StringUtils.EMPTY : File.separator);

        return sb.append(fileName).append(".zip").toString();
    }

    /**
     * Build the destination file name from the channel
     * 
     * @param cronJob
     *            the target cronJob
     * @param channel
     *            the target channel
     * @return the absolute destination file name excluding any extension.
     */
    private String buildFileName(final DistCategoryStructureExportCronJobModel cronJob, final DistFactFinderExportChannelModel channel) {
        final String prefix = cronJob.getMediaPrefix() != null ? cronJob.getMediaPrefix() : DEFAULT_NAME_PREFIX;
        return new StringBuilder(prefix) //
                .append(prefix.endsWith(".") ? StringUtils.EMPTY : ".") //
                .append(channel.getChannel()) //
                .toString();
    }

    /**
     * Export the category tree. This method write first the given category data to the output stream, the process the sub-categories.
     * 
     * @param category
     *            the target category
     * @param outputStream
     *            the output stream where to write the data
     * @param parentPath
     *            the category parent path
     * @param locale
     *            the target locale
     * @throws IOException
     */
    private void exportCategoryTree(final CategoryModel category, final CMSSiteModel cmsSite, final OutputStream outputStream, final String parentPath,
            final Locale locale) throws IOException {
        if (category == null || StringUtils.isEmpty(category.getCode()) || getDistCategoryService().isCategoryEmptyForCMSSite(category, cmsSite)) {
            return;
        }

        // Write category data line
        outputStream.write(buildCategoryLine(category, parentPath, locale).getBytes());

        // Process sub-categories, if any
        // Don't process categories below product-line
        if (CollectionUtils.isNotEmpty(category.getCategories())
                && (category.getPimCategoryType() == null || !"Produktlinie".equals(category.getPimCategoryType().getCode()))) {

            final String newParentPath = (StringUtils.isNotEmpty(parentPath) ? parentPath + "/" : StringUtils.EMPTY) + category.getCode();
            for (final CategoryModel child : category.getCategories()) {
                exportCategoryTree(child, cmsSite, outputStream, newParentPath, locale);
            }
        }
    }

    /**
     * Build the category data line.
     * 
     * @param category
     *            the target category
     * @param parentPath
     *            the parent IDs path
     * @param locale
     *            the target locale
     * @return the category data line
     */
    private String buildCategoryLine(final CategoryModel category, final String parentPath, final Locale locale) {
        String categoryName = category.getName(locale);
        if(StringUtils.isEmpty(categoryName)){
            categoryName = category.getName(Locale.ENGLISH);
        }

        return new StringBuilder(COLUMN_SE) //
                .append(category.getCode()) //
                .append(COLUMN_SEP) //
                .append(StringUtils.isEmpty(parentPath) ? StringUtils.EMPTY : parentPath) //
                .append(COLUMN_SEP) //
                .append(categoryName) //
                .append(E_CRLF).toString();
    }

    // Getters & Setters

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }
    
    public DistFactFinderExportHelper getExportHelper() {
        return exportHelper;
    }

    @Required
    public void setExportHelper(final DistFactFinderExportHelper exportHelper) {
        this.exportHelper = exportHelper;
    }
}
