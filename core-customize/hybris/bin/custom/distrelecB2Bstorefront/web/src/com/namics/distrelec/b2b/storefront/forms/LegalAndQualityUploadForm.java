package com.namics.distrelec.b2b.storefront.forms;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class LegalAndQualityUploadForm {
    private CommonsMultipartFile file;

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }
}
