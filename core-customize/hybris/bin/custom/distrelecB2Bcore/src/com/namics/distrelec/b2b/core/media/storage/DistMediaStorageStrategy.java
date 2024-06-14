package com.namics.distrelec.b2b.core.media.storage;

import com.namics.distrelec.b2b.core.media.storage.impl.DistStoredMediaData;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.storage.MediaStorageStrategy;

import java.util.List;

public interface DistMediaStorageStrategy extends MediaStorageStrategy {

    List<DistStoredMediaData> listFilesInFolder(MediaFolderConfig mediaFolderConfig, String folder);
}
