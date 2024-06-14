package com.namics.hybris.ffsearch.export;

import com.google.common.base.Charsets;
import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.FactFinder;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import com.namics.hybris.ffsearch.exception.DistFactFinderExportUploadException;
import com.namics.hybris.ffsearch.export.query.DefaultDistFactFinderDetailQueryCreator;
import com.namics.hybris.ffsearch.export.sequence.Channel;
import com.namics.hybris.ffsearch.export.sequence.MediaEntry;
import com.namics.hybris.ffsearch.export.sequence.MediaExporter;
import com.namics.hybris.ffsearch.export.sequence.ProductDetailsExporter;
import com.namics.hybris.ffsearch.export.sequence.ProductExportBatch;
import com.namics.hybris.ffsearch.export.sequence.SequentialExportContext;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.model.export.DistFactFinderSequentialExportCronJobModel;
import com.namics.hybris.ffsearch.service.FactFinderIndexManagementService;
import com.namics.hybris.ffsearch.util.DistFactFinderUtils;
import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelper;
import com.opencsv.ResultSetHelperService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.util.Config;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DistFactFinderSequentialExportJob extends AbstractJobPerformable<DistFactFinderSequentialExportCronJobModel> {

    private static final char SEPARATOR = ';';
    private static final String CSV_FILE_SUFFIX = ".csv";

    private final Logger LOG = LoggerFactory.getLogger(DistFactFinderSequentialExportJob.class);

    private final ResultSetHelper resultSetHelper = new ResultSetHelperService();

    private CatalogVersionService catalogVersionService;
    private ConfigurationService configurationService;
    private DistFactFinderExportHelper exportHelper;
    private DistFactFinderDetailQueryCreator factFinderDetailQueryCreator;
    private DistFactFinderPkQueryCreator factFinderPkQueryCreator;
    private DistFlexibleSearchExecutionService flexibleSearchExecutionService;
    private FactFinderIndexManagementService factFinderIndexManagementService;
    private MediaService mediaService;

    @Autowired
    private DistSqlUtils distSqlUtils;

    @Override
    public PerformResult perform(final DistFactFinderSequentialExportCronJobModel cronJobModel) {
        CMSSiteModel cmsSiteModel = cronJobModel.getCmsSite();
        Collection<DistFactFinderExportChannelModel> channels = DistFactFinderUtils.getActiveChannelsForCMSSite(cmsSiteModel);

        if (CollectionUtils.isEmpty(channels)) {
            LOG.warn("There are not assigned active export channels");
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }

        // create context
        Tenant currentTenant = Registry.getCurrentTenant();
        DistFactFinderExportChannelModel channelModel = channels.iterator().next();
        CatalogVersionModel catalogVersionModel = channelModel.getCatalogVersion();
        String catalogId = catalogVersionModel.getCatalog().getId();
        String catalogVersion = catalogVersionModel.getVersion();
        SequentialExportContext exportContext = new SequentialExportContext(cronJobModel, cmsSiteModel, channels,
                currentTenant, catalogId, catalogVersion);

        prepareMedias(exportContext);

        CronJobResult cronJobResult = doExport(exportContext);


        closeMedias(exportContext);

        boolean scpUpload = getConfigurationService().getConfiguration().getBoolean(FactFinder.EXPORT_UPLOAD_VIA_SCP, false);
        if (CronJobResult.SUCCESS.equals(cronJobResult)) {
            try {
                LOG.info("Save exported files");
                getExportHelper().saveExternal(cronJobModel, false, scpUpload);
            } catch (DistFactFinderExportUploadException e) {
                LOG.error("Unable to upload exports", e);
                cronJobResult = CronJobResult.ERROR;
            }
        }

        if (CronJobResult.SUCCESS.equals(cronJobResult) && scpUpload) {
            try {
                LOG.info("Trigger import if required");
                boolean isAborted = triggerImportIfRequired(exportContext);
                if (isAborted) {
                    cronJobResult = CronJobResult.FAILURE;
                }
            } catch (Exception e) {
                LOG.error("Unable to index ff", e);
                cronJobResult = CronJobResult.ERROR;
            }
        }

        CronJobStatus cronJobStatus = CronJobResult.SUCCESS.equals(cronJobResult) ? CronJobStatus.FINISHED : CronJobStatus.ABORTED;

        LOG.info(String.format("Result: %s  %s", cronJobResult, cronJobStatus));
        return new PerformResult(cronJobResult, cronJobStatus);
    }

    @Override
    public boolean isAbortable() {
        return true;
    }

    protected CronJobResult doExport(final SequentialExportContext exportContext) {
        List<Long> productPks;
        try {
            productPks = getProductPks(exportContext);
        } catch (SQLException e) {
            LOG.error("unable to get pk list", e);
            return CronJobResult.ERROR;
        }
        CronJobResult cronJobResult = CronJobResult.SUCCESS;
        CMSSiteModel cmsSiteModel = exportContext.getCmsSite();
        DistSalesOrgModel salesOrgModel = cmsSiteModel.getSalesOrg();

        Configuration config = getConfigurationService().getConfiguration();
        int batchSize = config.getInt(FactFinder.BATCH_SIZE, 100);
        int queryTimeout = config.getInt(FactFinder.QUERY_TIMEOUT, 600);
        int maxTasksNum = config.getInt(FactFinder.MAX_TASKS_NUM, 0);
        int threadPoolSize = config.getInt(FactFinder.THREAD_POOL_SIZE, 3);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
        exportContext.setThreadPoolExecutor(threadPoolExecutor);

        // count tasks
        int tasks = (productPks.size() + batchSize - 1) / batchSize;

        if (maxTasksNum > 0) {
            tasks = Math.min(tasks, maxTasksNum);
        }

        CountDownLatch countDownLatch = new CountDownLatch(tasks);
        CountDownLatch exceptionLatch = new CountDownLatch(1);

        Map<String, Object> promotionalParams = getPromotionalParameters(DefaultDistFactFinderDetailQueryCreator.PROMOLABEL_ATTRNAME, cmsSiteModel);
        promotionalParams.put(DistFlexibleSearchExecutionService.QUERY_TIMEOUT_PARAM, queryTimeout);

        int submittedJobs = 0;
        String detailFlexSearchQuery = getFactFinderDetailQueryCreator().createDetailQuery(cmsSiteModel);
        final Map<String, Object> templateParameters = Collections.unmodifiableMap(getDetailsQueryParameters(cmsSiteModel, promotionalParams));
        List<Channel> batchChannels = prepareBatchChannels(cmsSiteModel);

        int offset = 0;
        while (productPks.size() > offset && submittedJobs < tasks) {
            List<Long> productPksBatch = productPks.subList(offset, Math.min(offset + batchSize, productPks.size()));
            ProductExportBatch productExportBatch = new ProductExportBatch(cmsSiteModel.getUid(), salesOrgModel.getCode(),
                    batchChannels, productPksBatch);

            ProductDetailsExporter productDetailsExporter = new ProductDetailsExporter(getCatalogVersionService(),
                    getFlexibleSearchExecutionService(), sessionService, resultSetHelper, exportContext, productExportBatch,
                    detailFlexSearchQuery, templateParameters, countDownLatch, exceptionLatch);
            threadPoolExecutor.submit(productDetailsExporter);
            offset += batchSize;
            submittedJobs++;
        }

        try {
            while (!countDownLatch.await(60, TimeUnit.SECONDS) || exceptionLatch.getCount() == 0) {
                if (clearAbortRequestedIfNeeded(exportContext.getCronJobModel()) || exceptionLatch.getCount() == 0) {
                    if (exceptionLatch.getCount() == 0) {
                        cronJobResult = CronJobResult.ERROR;
                    } else {
                        cronJobResult = CronJobResult.FAILURE;
                    }
                    threadPoolExecutor.shutdownNow();
                    while (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                        LOG.warn("pool did not terminate");
                    }
                    break;
                } else {
                    LOG.info(String.format("Number of remaining tasks: %s", countDownLatch.getCount()));
                }
            }
        } catch (InterruptedException e) {
            LOG.error("pool was interrupted", e);
            threadPoolExecutor.shutdownNow();
            cronJobResult = CronJobResult.FAILURE;
        }

        return cronJobResult;
    }

    protected List<Channel> prepareBatchChannels(final CMSSiteModel cmsSite) {
        List<Channel> batchChannels = new ArrayList<>();

        Collection<DistFactFinderExportChannelModel> channels = DistFactFinderUtils.getActiveChannelsForCMSSite(cmsSite);
        for (DistFactFinderExportChannelModel channelModel : channels) {
            DistSalesOrgModel salesOrg = cmsSite.getSalesOrg();
            LanguageModel language = channelModel.getLanguage();

            Channel batchChannel = new Channel(channelModel.getCode(), language.getIsocode());
            batchChannels.add(batchChannel);
        }

        return batchChannels;
    }

    protected List<Long> getProductPks(final SequentialExportContext exportContext) throws SQLException {
        List<Long> pkList = new ArrayList<>();
        ResultSet pkResultSet = null;
        CMSSiteModel cmsSiteModel = exportContext.getCmsSite();
        String pkFlexSearchQuery = getFactFinderPkQueryCreator().createPkQuery(cmsSiteModel);
        Map<String, Object> pkFlexSearchParams = new HashMap<>();
        pkFlexSearchParams.put(DistFactFinderPkQueryCreator.CMSSITE, Long.valueOf(cmsSiteModel.getPk().getLongValue()));
        pkFlexSearchParams.put(DistFactFinderPkQueryCreator.DATE, new Date());

        try {
            LOG.info("Fetch PKs for export");
            pkResultSet = getFlexibleSearchExecutionService().execute(pkFlexSearchQuery, pkFlexSearchParams);
            LOG.info("PKs were fetched");

            while (pkResultSet.next()) {
                Long pk = pkResultSet.getLong(1);
                pkList.add(pk);
            }
        } finally {
            getFlexibleSearchExecutionService().closeResultSet(pkResultSet);
        }

        LOG.debug("PKs were translated");

        return pkList;
    }

    protected Map<String, Object> getDetailsQueryParameters(final CMSSiteModel cmsSiteModel, final Map<String, Object> promotionalParams) {
        final Map<String, Object> parameters = new HashMap<>();

        // SalesOrg as PK
        DistSalesOrgModel salesOrgModel = cmsSiteModel.getSalesOrg();
        parameters.put(DistSalesOrgModel._TYPECODE, Long.valueOf(salesOrgModel.getPk().getLongValue()));

        // CMSSite as PK
        parameters.put(CMSSiteModel._TYPECODE, Long.valueOf(cmsSiteModel.getPk().getLongValue()));

        // Country as PK
        CountryModel countryModel = cmsSiteModel.getCountry();
        parameters.put(CountryModel._TYPECODE, Long.valueOf(countryModel.getPk().getLongValue()));

        // Date for pricerows
        parameters.put(DistFactFinderDetailQueryCreator.DATE, new Date());

        // Language ISO code
        Set<String> languages = DistFactFinderUtils.getLanguagesForCMSSite(cmsSiteModel);
        for (String language : languages) {
            parameters.put(DistFactFinderDetailQueryCreator.LANGUAGE_ISOCODE.concat(language), language);
        }

        parameters.putAll(promotionalParams);

        return parameters;
    }

    protected Map<String, Object> getPromotionalParameters(String paramPrefix, CMSSiteModel cmsSite) {
        Set<String> languages = DistFactFinderUtils.getLanguagesForCMSSite(cmsSite);
        Map<String, Object> parameters = new HashMap<>(10);
        for (String language : languages) {
            final StringBuilder query = new StringBuilder();
            query.append("SELECT {dpl.").append(DistPromotionLabelModel.CODE).append("}, ");
            query.append(distSqlUtils.concat("'code:'", "{dpl.".concat(DistPromotionLabelModel.CODE).concat("}"),
                    "',label:\"'", "COALESCE({dpl.".concat(DistPromotionLabelModel.NAME).concat("[").concat(language).concat("]:o}, '')"),
                    "'\",priority:'", "{dpl.".concat(DistPromotionLabelModel.PRIORITY).concat("}"),
                    "',rank:'", "{dpl.".concat(DistPromotionLabelModel.RANK).concat("}"),
                    "',active:'")).append(" AS VALUE ");
            query.append("FROM {").append(DistPromotionLabelModel._TYPECODE).append(" AS dpl} ");

            final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());
            flexibleSearchQuery.setResultClassList(Arrays.asList(String.class, String.class));
            final List<List<String>> searchResultRows = flexibleSearchService.<List<String>>search(flexibleSearchQuery).getResult();

            if (searchResultRows != null) {
                for (final List<String> resultRow : searchResultRows) {
                    parameters.put(paramPrefix + language + resultRow.get(0), resultRow.get(1));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("getPromotionalParameters: " + paramPrefix + resultRow.get(0) + " = " + resultRow.get(1));
                    }
                }
            }
        }
        return parameters;
    }

    protected void prepareMedias(final SequentialExportContext exportContext) {
        DistFactFinderSequentialExportCronJobModel cronJobModel = exportContext.getCronJobModel();
        Collection<DistFactFinderExportChannelModel> channels = exportContext.getChannels();

        // remove old medias
        for (MediaModel mediaModel : cronJobModel.getMedias()) {
            modelService.remove(mediaModel);
        }

        // create new medias for channels
        List<MediaModel> medias = new ArrayList<>();
        for (DistFactFinderExportChannelModel channelModel : channels) {
            MediaModel exportMedia = getExportHelper().createExportMedia(cronJobModel, channelModel);
            medias.add(exportMedia);

            // open input stream
            PipedInputStream inputStream = new PipedInputStream();
            Tenant currentTenant = exportContext.getTenant();
            Thread exportMediaThread = new Thread(new MediaExporter(exportMedia, inputStream, getMediaService(), currentTenant));
            exportMediaThread.start();

            // open piped output stream
            PipedOutputStream pipedOutputStream = new PipedOutputStream();
            try {
                inputStream.connect(pipedOutputStream);
            } catch (IOException e) {
                LOG.warn("Exception occurred for piped output stream", e);
            }

            // open zip output stream
            ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(pipedOutputStream);
            final String zipArchiveEntryName = getExportHelper().getExportName(cronJobModel, channelModel) + CSV_FILE_SUFFIX;
            final ZipArchiveEntry entry = new ZipArchiveEntry(zipArchiveEntryName);
            try {
                zipOutputStream.putArchiveEntry(entry);
            } catch (IOException e) {
                LOG.warn("Exception occurred for zip output stream", e);
            }

            // open csv writer
            CSVWriter writer = null;
            try {
                writer = new CSVWriter(new OutputStreamWriter(zipOutputStream, Charsets.UTF_8.name()), SEPARATOR, CSVWriter.DEFAULT_ESCAPE_CHARACTER);
            } catch (UnsupportedEncodingException e) {
                LOG.warn("Exception occurred for CSV writer", e);
            }

            // add media into map
            MediaEntry mediaEntry = new MediaEntry(channelModel.getCode(), inputStream, zipOutputStream, writer, exportMediaThread);
            exportContext.putMediaEntry(channelModel, mediaEntry);
        }

        cronJobModel.setMedias(medias);
        modelService.save(cronJobModel);
    }

    protected void closeMedias(final SequentialExportContext exportContext) {
        LOG.info("close medias");
        Collection<MediaEntry> mediaEntries = exportContext.getMediaEntries();

        for (MediaEntry mediaEntry : mediaEntries) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("close media %s", mediaEntry.getChannelCode()));
            }
            // flush csv writer
            CSVWriter writer = mediaEntry.getWriter();
            try {
                writer.flush();
            } catch (IOException e) {
                LOG.error("unable to flush writer", e);
            }
            // close zip output stream
            try {
                mediaEntry.getZipArchiveOutputStream().closeArchiveEntry();
            } catch (IOException e) {
                LOG.error("unable to close archive entry", e);
            }

            // close writer
            try {
                writer.close();
            } catch (IOException e) {
                LOG.error("unable to close writer", e);
            }

            // join export thread
            try {
                mediaEntry.getExportMediaThread().join();
            } catch (InterruptedException e) {
                LOG.error("unable to join export media thread", e);
            }

            // close input stream
            try {
                mediaEntry.getInputStream().close();
            } catch (IOException e) {
                LOG.error("unable to close input stream", e);
            }
        }

        ThreadPoolExecutor threadPoolExecutor = exportContext.getThreadPoolExecutor();
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdownNow();
            while (true) {
                try {
                    if (!!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) break;
                } catch (InterruptedException e) {
                    LOG.warn("pool did not terminate", e);
                }
                LOG.warn("pool did not terminate");
            }
        }
    }

    /**
     * Triggers fact-finder import.
     *
     * @return true if job is aborted.
     */
    private boolean triggerImportIfRequired(final SequentialExportContext exportContext) throws Exception {
        final boolean triggerImport = Config.getBoolean(FactFinder.TRIGGER_IMPORT, true);
        DistFactFinderSequentialExportCronJobModel cronJob = exportContext.getCronJobModel();
        if (triggerImport) {
            Collection<DistFactFinderExportChannelModel> channels = exportContext.getChannels();
            for (DistFactFinderExportChannelModel channel : channels) {
                if (clearAbortRequestedIfNeeded(cronJob)) {
                    cronJob.setFfImportTriggered(Boolean.FALSE);
                    cronJob.setSuggestFFImportTriggered(Boolean.FALSE);
                    modelService.save(cronJob);
                    return true;
                }
                LOG.info("Trigger import " + channel.getCode());
                getFactFinderIndexManagementService().startImport(cronJob, channel);
            }
        } else {
            cronJob.setFfImportTriggered(Boolean.FALSE);
            cronJob.setSuggestFFImportTriggered(Boolean.FALSE);
            modelService.save(cronJob);
            LOG.debug("Won't trigger FactFinder import");
        }
        return false;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    @Required
    public void setCatalogVersionService(final CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistFactFinderExportHelper getExportHelper() {
        return exportHelper;
    }

    @Required
    public void setExportHelper(final DistFactFinderExportHelper exportHelper) {
        this.exportHelper = exportHelper;
    }

    public DistFactFinderDetailQueryCreator getFactFinderDetailQueryCreator() {
        return factFinderDetailQueryCreator;
    }

    @Required
    public void setFactFinderDetailQueryCreator(final DistFactFinderDetailQueryCreator factFinderDetailQueryCreator) {
        this.factFinderDetailQueryCreator = factFinderDetailQueryCreator;
    }

    public DistFactFinderPkQueryCreator getFactFinderPkQueryCreator() {
        return factFinderPkQueryCreator;
    }

    @Required
    public void setFactFinderPkQueryCreator(final DistFactFinderPkQueryCreator factFinderPkQueryCreator) {
        this.factFinderPkQueryCreator = factFinderPkQueryCreator;
    }

    public FactFinderIndexManagementService getFactFinderIndexManagementService() {
        return factFinderIndexManagementService;
    }

    @Required
    public void setFactFinderIndexManagementService(final FactFinderIndexManagementService factFinderIndexManagementService) {
        this.factFinderIndexManagementService = factFinderIndexManagementService;
    }

    public DistFlexibleSearchExecutionService getFlexibleSearchExecutionService() {
        return flexibleSearchExecutionService;
    }

    @Required
    public void setFlexibleSearchExecutionService(final DistFlexibleSearchExecutionService flexibleSearchExecutionService) {
        this.flexibleSearchExecutionService = flexibleSearchExecutionService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }
}
