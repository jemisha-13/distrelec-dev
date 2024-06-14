package com.namics.hybris.ffsearch.export.sequence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.model.export.DistFactFinderSequentialExportCronJobModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.Tenant;

public class SequentialExportContext {

    private final Collection<DistFactFinderExportChannelModel> channels;
    private final CMSSiteModel cmsSite;
    private final DistFactFinderSequentialExportCronJobModel cronJobModel;
    private final Tenant tenant;
    private final String catalogId;
    private final String catalogVersion;

    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * key: Channel#code
     */
    private final Map<String, MediaEntry> mediaEntryMap = new HashMap<>();
    private volatile boolean headerExported = false;

    public SequentialExportContext(final DistFactFinderSequentialExportCronJobModel cronJobModel,
            final CMSSiteModel cmsSite, final Collection<DistFactFinderExportChannelModel> channels,
            final Tenant tenant, final String catalogId, final String catalogVersion) {
        this.cronJobModel = cronJobModel;
        this.cmsSite = cmsSite;
        this.channels = channels;
        this.tenant = tenant;
        this.catalogId = catalogId;
        this.catalogVersion = catalogVersion;
    }

    public Collection<DistFactFinderExportChannelModel> getChannels() {
        return channels;
    }

    public CMSSiteModel getCmsSite() {
        return cmsSite;
    }

    public DistFactFinderSequentialExportCronJobModel getCronJobModel() {
        return cronJobModel;
    }

    public boolean isHeaderExported() {
        return headerExported;
    }

    public void setHeaderExported() {
        headerExported = true;
    }

    public Collection<MediaEntry> getMediaEntries() {
        return mediaEntryMap.values();
    }

    public MediaEntry getMediaEntry(final DistFactFinderExportChannelModel channelModel) {
        return getMediaEntry(channelModel.getCode());
    }

    public MediaEntry getMediaEntry(final String channelCode) {
        return mediaEntryMap.get(channelCode);
    }

    public void putMediaEntry(final DistFactFinderExportChannelModel channelModel, final MediaEntry mediaEntry) {
        mediaEntryMap.put(channelModel.getCode(), mediaEntry);
    }

    public Tenant getTenant() {
        return tenant;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public String getCatalogVersion() {
        return catalogVersion;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public void setThreadPoolExecutor(final ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }
}
