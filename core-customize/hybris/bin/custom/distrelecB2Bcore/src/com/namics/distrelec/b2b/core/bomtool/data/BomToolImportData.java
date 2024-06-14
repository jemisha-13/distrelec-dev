package com.namics.distrelec.b2b.core.bomtool.data;

import de.hybris.platform.validation.annotations.NotBlank;

import java.util.List;
public class BomToolImportData {

	@NotBlank
	private String fileName;
	private List<BomToolImportEntryData> entry;
	
    public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public List<BomToolImportEntryData> getEntry() {
		return entry;
	}


	public void setEntry(List<BomToolImportEntryData> entry) {
		this.entry = entry;
	}

}
