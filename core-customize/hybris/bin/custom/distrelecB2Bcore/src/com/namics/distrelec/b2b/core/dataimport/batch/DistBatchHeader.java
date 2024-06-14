package com.namics.distrelec.b2b.core.dataimport.batch;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;

public class DistBatchHeader extends BatchHeader {

    private String erpSequenceId;

    public String getErpSequenceId() {
        return erpSequenceId;
    }

    public void setErpSequenceId(String erpSequenceId) {
        this.erpSequenceId = erpSequenceId;
    }
}
