/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * ImportToolUploadForm
 * 
 * @author daezamofinl, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class ImportToolUploadForm {
    private String name;
    private String data;
    private CommonsMultipartFile file;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(final CommonsMultipartFile file) {
        this.file = file;
        this.name = file.getOriginalFilename();
    }

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }

}
