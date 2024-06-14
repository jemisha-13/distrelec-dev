package com.namics.distrelec.b2b.core.timestampupdate;

import java.util.Date;

public class PimExportData {

	private String pimid;
	private Date firstAppearanceDate;
	
	public PimExportData() { }
	
	public PimExportData(String pimid, Date firstAppearanceDate) {
        this.pimid = pimid;
        this.firstAppearanceDate = firstAppearanceDate;
    }
	
	public String getPimid() {
		return pimid;
	}
	public void setPimid(String pimid) {
		this.pimid = pimid;
	}
	public Date getFirstAppearanceDate() {
		return firstAppearanceDate;
	}
	public void setFirstAppearanceDate(Date firstAppearanceDate) {
		this.firstAppearanceDate = firstAppearanceDate;
	}
}
