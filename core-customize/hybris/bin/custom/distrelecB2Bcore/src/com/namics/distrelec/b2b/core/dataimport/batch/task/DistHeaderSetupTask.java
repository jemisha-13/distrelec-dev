package com.namics.distrelec.b2b.core.dataimport.batch.task;

import java.io.File;

import com.namics.distrelec.b2b.core.dataimport.batch.DistBatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.HeaderSetupTask;

public class DistHeaderSetupTask extends HeaderSetupTask {

    @Override
    public BatchHeader execute(final File file) {
        final DistBatchHeader result = new DistBatchHeader();
        result.setFile(file);
        result.setStoreBaseDirectory(storeBaseDirectory);
        result.setCatalog(catalog);
        result.setNet(net);
        return result;
    }
}
