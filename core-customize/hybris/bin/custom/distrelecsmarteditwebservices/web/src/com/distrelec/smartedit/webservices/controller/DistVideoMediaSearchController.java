package com.distrelec.smartedit.webservices.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.facades.media.DistMediaFacade;

import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

@Controller
@IsAuthorizedCmsManager
@RequestMapping("/distvideomedia")
public class DistVideoMediaSearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistVideoMediaSearchController.class);

    @Autowired
    private DistMediaFacade distMediaFacade;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<MediaData> findCategories(@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "currentPage", required = false, defaultValue = "0") Integer currentPage,
                                          @RequestParam(name = "mask", required = false, defaultValue = "") String mask,
                                          @RequestParam(name = "catalogVersionUuid") String catalogVersionUuid) {
        return distMediaFacade.searchVideoMedia(mask, currentPage, pageSize, catalogVersionUuid);
    }

    @RequestMapping(path = { "/{code}" }, method = RequestMethod.GET)
    @ResponseBody
    public MediaData findCategories(@PathVariable(name = "code") String mediaCode,
                                    @RequestParam(name = "catalogVersionUuid") String catalogVersionUuid) {
        return distMediaFacade.findVideoMedia(mediaCode, catalogVersionUuid);
    }

}
