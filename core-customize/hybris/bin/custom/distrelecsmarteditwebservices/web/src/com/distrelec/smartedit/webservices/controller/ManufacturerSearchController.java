package com.distrelec.smartedit.webservices.controller;

import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.smartedit.SmarteditManufacturerData;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@IsAuthorizedCmsManager
@RequestMapping("/manufacturers")
public class ManufacturerSearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategorySearchController.class);

    @Resource
    private WebPaginationUtils webPaginationUtils;

    @Autowired
    private DistManufacturerFacade distManufacturerFacade;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<SmarteditManufacturerData> findCategories(@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                          @RequestParam(name = "currentPage", required = false, defaultValue = "0") Integer currentPage,
                                                          @RequestParam(name = "mask", required = false, defaultValue = "") String mask) {
        catalogVersionService.setSessionCatalogVersion("distrelecProductCatalog", "Online");
        return distManufacturerFacade.searchManufacturers(mask, currentPage, pageSize);
    }

    @RequestMapping(path = {"/{code}"}, method = RequestMethod.GET)
    @ResponseBody
    public SmarteditManufacturerData findCategories(@PathVariable(name = "code") String manufacturerCode) {
        return distManufacturerFacade.findManufacturerByCode(manufacturerCode);
    }
}
