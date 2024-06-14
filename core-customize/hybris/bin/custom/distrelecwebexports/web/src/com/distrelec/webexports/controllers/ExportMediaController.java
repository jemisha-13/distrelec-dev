package com.distrelec.webexports.controllers;

import com.namics.distrelec.b2b.core.constants.DistConstants.AzureMediaFolder;
import com.namics.distrelec.b2b.core.media.storage.DistMediaStorageStrategy;
import com.namics.distrelec.b2b.core.media.storage.impl.DistStoredMediaData;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.storage.MediaStorageRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.stream.Collectors;

@Controller()
@RequestMapping("/")
public class ExportMediaController {

    public static final String REDIRECT_PREFIX = "redirect:";
    public static final String ExportsPage = "miscExportsPage";

    @Autowired
    private MediaStorageConfigService mediaStorageConfigService;

    @Autowired
    private MediaStorageRegistry mediaStorageRegistry;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listFilesInRootFolder(Model model) {
        return getFiles("", model);
    }

    @RequestMapping(value = "/{path:.*}/", method = RequestMethod.GET)
    public String listFilesInFolder(@PathVariable  String path, Model model) {
        return getFiles(path, model);
    }

    private String getFiles(String path, Model model) {
        MediaFolderConfig config = getExportsMediaFolderConfig();
        DistMediaStorageStrategy mediaStorageStrategy = getMediaStorageStrategy(config);
        String fullpath = "export/" + path;
        List<DistStoredMediaData> exportMedias = mediaStorageStrategy.listFilesInFolder(config, fullpath).stream()
                                                                     .filter(DistStoredMediaData::isDownloaded)
                                                                     .collect(Collectors.toList());
        model.addAttribute("exportMedias", exportMedias);
        model.addAttribute("pageTitle", "Category listing for " + fullpath);
        return ExportsPage;
    }

    protected MediaFolderConfig getExportsMediaFolderConfig() {
        MediaFolderConfig config = getMediaStorageConfigService().getConfigForFolder(AzureMediaFolder.EXPORTS);
        return config;
    }

    protected DistMediaStorageStrategy getMediaStorageStrategy(MediaFolderConfig config) {
        DistMediaStorageStrategy mediaStorageStrategy =
                (DistMediaStorageStrategy) getMediaStorageRegistry().getStorageStrategyForFolder(config);
        return mediaStorageStrategy;
    }

    public MediaStorageConfigService getMediaStorageConfigService() {
        return mediaStorageConfigService;
    }

    public void setMediaStorageConfigService(MediaStorageConfigService mediaStorageConfigService) {
        this.mediaStorageConfigService = mediaStorageConfigService;
    }

    public MediaStorageRegistry getMediaStorageRegistry() {
        return mediaStorageRegistry;
    }

    public void setMediaStorageRegistry(MediaStorageRegistry mediaStorageRegistry) {
        this.mediaStorageRegistry = mediaStorageRegistry;
    }
}
