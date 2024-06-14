package com.namics.distrelec.b2b.core.integration.azure.uploader;

import java.io.File;

public interface AzureFileUploader {
    void uploadAndDeleteLocalFile(File file, String path);
    public void downloadAllAndKeepOriginals(String cloudPath, String localPath);
    public void downloadAllAndDeleteInCloud(String cloudPath, String localPath);
}
