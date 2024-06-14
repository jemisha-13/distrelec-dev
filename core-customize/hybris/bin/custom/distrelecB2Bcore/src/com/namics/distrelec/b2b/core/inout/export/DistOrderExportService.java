package com.namics.distrelec.b2b.core.inout.export;

import com.namics.distrelec.b2b.core.model.inout.DistOrderExportCronJobModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.io.InputStream;

public interface DistOrderExportService {

    boolean saveExternal(final InputStream data, final String exportedFileName);

    MediaModel createMedia(final InputStream data, final DistOrderExportCronJobModel cronJob);

}
