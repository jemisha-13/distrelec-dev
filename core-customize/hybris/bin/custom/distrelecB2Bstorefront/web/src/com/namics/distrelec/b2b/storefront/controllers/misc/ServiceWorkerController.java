package com.namics.distrelec.b2b.storefront.controllers.misc;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serves /service-worker.js if available for current site
 */
@Controller
@RequestMapping("/service-worker.js")
public class ServiceWorkerController {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceWorkerController.class);

    @Autowired
    CMSSiteService cmsSiteService;

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping
    @ResponseBody
    public ResponseEntity<String> getServiceWorkerJs() {
        try {
            Optional<File> serviceWorkerJsFile = findServiceWorkerJsFile();
            if (serviceWorkerJsFile.isPresent()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(serviceWorkerJsFile.get()))) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType("text/javascript;charset=UTF-8"))
                            .body(reader.lines().collect(Collectors.joining()));
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            LOG.error("Failed to read service-worker.js for site " + cmsSiteService.getCurrentSite().getUid());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Optional<File> findServiceWorkerJsFile() throws IOException {
        String siteId = cmsSiteService.getCurrentSite().getUid();
        Resource resource = resourceLoader.getResource("WEB-INF/site/" + siteId + "/service-worker.js");
        if (resource.exists()) {
            return Optional.of(resource.getFile());
        } else {
            return Optional.empty();
        }
    }

}
