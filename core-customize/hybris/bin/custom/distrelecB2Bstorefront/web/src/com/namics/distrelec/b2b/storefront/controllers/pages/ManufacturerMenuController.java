/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerMenuData;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistMiniManufacturerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

/**
 * Controller for manufacturer stores page.
 */
@Controller
@RequestMapping("/manufacturerMenu")
public class ManufacturerMenuController extends AbstractPageController {

    protected static final Logger LOG = LogManager.getLogger(ManufacturerMenuController.class);

    @GetMapping(produces = "application/json")
    public String manufacturerStores(final Model model) throws CMSItemNotFoundException, UnsupportedEncodingException {
        model.addAttribute("manufacturers", getMenuData(distManufacturerFacade.getManufactures()));
         return ControllerConstants.Views.Pages.Manufacturer.ManufacturerMenuPage;
    }

   private List<DistManufacturerMenuData> getMenuData(Map<String, List<DistMiniManufacturerData>> menu){
	   List<DistManufacturerMenuData> menuDataList= new ArrayList<>();
       for (Map.Entry<String, List<DistMiniManufacturerData>> entry : menu.entrySet()) {
           DistManufacturerMenuData menuData = new DistManufacturerMenuData();
           menuData.setKey(entry.getKey());
           menuData.setManufacturerList(entry.getValue());
           menuDataList.add(menuData);
       }
	   return menuDataList;
   }
}
