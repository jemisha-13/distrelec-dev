package com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto;

import java.util.Date;

public class PimXmlHashDto {

    private String pimXmlHashMaster;
    private Date pimHashTimestamp;

    public String getPimXmlHashMaster() {
        return pimXmlHashMaster;
    }

    public void setPimXmlHashMaster(final String pimXmlHashMaster) {
        this.pimXmlHashMaster = pimXmlHashMaster;
    }

    public Date getPimHashTimestamp() {
        return pimHashTimestamp;
    }

    public void setPimHashTimestamp(final Date pimHashTimestamp) {
        this.pimHashTimestamp = pimHashTimestamp;
    }
}
