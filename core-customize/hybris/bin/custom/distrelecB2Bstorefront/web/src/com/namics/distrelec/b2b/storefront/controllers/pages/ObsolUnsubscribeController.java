package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.namics.distrelec.b2b.core.obsolescence.service.DistObsolescenceService;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;


@Controller
@RequestMapping("/**/unsubscribeobsolescence")
public class ObsolUnsubscribeController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(ObsolUnsubscribeController.class);
    private static final String OBSOL_UNSUBSCRIBE_CMS_PAGE = "obsol-unsubscribe";    

    
	@Autowired
	private DistObsolescenceService distObsolescenceService;

	
    @RequestMapping(method = RequestMethod.GET)
    public String unsubscribeObsolescence(@RequestParam(value="uid", required=true) String customerUid, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException, UnsupportedEncodingException {
    	try{
    		distObsolescenceService.unsubscribeObsolescence(customerUid);
    	            
    	}catch(Exception ex) {
            LOG.debug("Error while unsubscribing from obsolescence: ", customerUid,ex);
    	}
        addGlobalModelAttributes(model, request);
        storeCmsPageInModel(model, getContentPageForLabelOrId(OBSOL_UNSUBSCRIBE_CMS_PAGE));
        return getViewForPage(model);
    }
}
