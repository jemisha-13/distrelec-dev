package com.namics.hybris.ffsearch.export.sequence;

import java.io.InputStream;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaExporter implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(MediaExporter.class);

    private final MediaModel mediaModel;
    private final InputStream inputStream;
    private final MediaService mediaService;
    private final Tenant tenant;

    public MediaExporter(final MediaModel mediaModel, final InputStream inputStream, final MediaService mediaService, final Tenant tenant) {
        this.mediaModel = mediaModel;
        this.inputStream = inputStream;
        this.mediaService = mediaService;
        this.tenant = tenant;
    }

    @Override
    public void run() {
        // reactivate tenant
        Registry.setCurrentTenant(tenant);

        mediaService.setStreamForMedia(mediaModel, inputStream);

        LOG.info("end");
    }
}
