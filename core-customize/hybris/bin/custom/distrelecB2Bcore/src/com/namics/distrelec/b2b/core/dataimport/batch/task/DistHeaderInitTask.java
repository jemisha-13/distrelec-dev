package com.namics.distrelec.b2b.core.dataimport.batch.task;

import com.namics.distrelec.b2b.core.dataimport.batch.DistBatchHeader;
import com.namics.distrelec.b2b.core.dataimport.batch.util.ErpSequenceIdParser;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.HeaderInitTask;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.SequenceIdParser;
import org.springframework.beans.factory.annotation.Required;

public class DistHeaderInitTask extends HeaderInitTask {

    private ErpSequenceIdParser erpSequenceIdParser;

    @Override
    public BatchHeader execute(final BatchHeader header) {
        DistBatchHeader distHeader = (DistBatchHeader) super.execute(header);
        if (distHeader.getSequenceId() == null) {
            // only if sequence id is null
            distHeader.setErpSequenceId(getErpSequenceIdParser().getErpSequenceId(header.getFile()));
        }
        return distHeader;
    }

    public ErpSequenceIdParser getErpSequenceIdParser() {
        return erpSequenceIdParser;
    }

    @Required
    public void setErpSequenceIdParser(ErpSequenceIdParser erpSequenceIdParser) {
        this.erpSequenceIdParser = erpSequenceIdParser;
    }
}
