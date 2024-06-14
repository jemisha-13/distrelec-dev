/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.category.DistCategoryIndexFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.category.DistCategoryIndexFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.seo.DistLink;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.media.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.*;

/**
 * Controller for home page.
 */
@Controller
@RequestMapping({ "/", "/{lang:[a-z]{2}}" })
public class HomePageController extends AbstractPageController {

    private static final int START_DATE=3650;
    private static final String IN_PROGRESS_ORDER_STATUS="ERP_STATUS_IN_PROGRESS";
    
    private static final List<OrderStatus> ORDER_STATUS_LIST = List.of(new OrderStatus[]{ /* OrderStatus.OPEN, */ OrderStatus.ERP_STATUS_RECIEVED, OrderStatus.ERP_STATUS_IN_PROGRESS,
            OrderStatus.ERP_STATUS_PARTIALLY_SHIPPED, OrderStatus.ERP_STATUS_SHIPPED, OrderStatus.ERP_STATUS_CANCELLED});

    @Autowired
    private CommerceCategoryService commerceCategoryService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private DistCategoryIndexFacade distCategoryIndexFacade;

    @Resource(name = "distUserDashboardFacade")
    private DistUserDashboardFacade distUserDashboardFacade;

    @Autowired
    @Qualifier("erp.orderHistoryFacade")
    private OrderHistoryFacade orderHistoryFacade;

    @Resource(name = "erp.invoiceHistoryFacade")
    private InvoiceHistoryFacade invoiceHistoryFacade;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String home(@RequestParam(value = "logout", defaultValue = "false")
                       final boolean logout, final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException {
        if (logout) {
            if ("false".equals(request.getParameter("active"))) {
                redirectModel.addFlashAttribute(GlobalMessages.WARN_MESSAGES_HOLDER, "account.signout.active.error");
            } 
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + ROOT);
        }
        CMSSiteModel cmsSiteModel=(CMSSiteModel) getBaseSiteService().getCurrentBaseSite();
        final String sitename = cmsSiteModel.getUid();
        model.addAttribute("isReevooActivatedForWebshop", cmsSiteModel.isReevooActivated());
        model.addAttribute("isReevooBrandVisible",cmsSiteModel.isReevooBrandVisible());
        Map<String,String> languageTrkRefMapping=cmsSiteModel.getReevootrkref();
        if(CollectionUtils.isNotEmpty(languageTrkRefMapping.keySet())) {
	        final String currentLang = getI18nService().getCurrentLocale().getLanguage();
	        String trkRef=currentLang!=null ?languageTrkRefMapping.get(currentLang):languageTrkRefMapping.get(languageTrkRefMapping.keySet().toArray()[0]);
	        model.addAttribute(DistConstants.PropKey.Reevoo.TRKREF_ID,trkRef);
        }else {
        	 model.addAttribute(DistConstants.PropKey.Reevoo.TRKREF_ID,null);
        }
        
        if (Boolean.parseBoolean(request.getParameter("deactivated"))) {
            final String baseURL = getConfigurationService().getConfiguration().getString("website." + sitename + ".https", StringUtils.EMPTY);
            final String message = getMessageSource().getMessage("account.p4c.deactivated", new Object[] { baseURL }, StringUtils.EMPTY, getI18nService().getCurrentLocale());
            model.addAttribute(GlobalMessages.WARN_MESSAGES_HOLDER, message);
        }

        if ("true".equals(getSessionService().getAttribute("notFound"))) {
            model.addAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, "page.notfound.error");
            getSessionService().removeAttribute("notFound");
        }

        // eol handling
        if ("true".equals(getSessionService().getAttribute("eolHomepageFor"))) {
            model.addAttribute(GlobalMessages.WARN_MESSAGES_HOLDER, getMessageSource().getMessage("product.eol.attention.homepageFor",
                    new Object[] { request.getParameter("eolHomepageFor") }, StringUtils.EMPTY, getI18nService().getCurrentLocale()));
            getSessionService().removeAttribute("eolHomepageFor");
        }

        addGlobalModelAttributes(model, request);

        // Add Data Layer attributes
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.HOMEPAGE,
                DigitalDatalayerConstants.PageType.HOMEPAGE);


        // Add header Links
        final List<DistLink> headerLinkLang = new ArrayList<>();
        setupAlternateHreflangLinks(request, headerLinkLang, null, null);
        model.addAttribute("headerLinksLangTags", headerLinkLang);
        model.addAttribute("footerLinksLangTags", headerLinkLang);
        
        model.addAttribute("recommendedCategories", distCategoryIndexFacade.getTopCategoryIndexData());

        final boolean isOCICustomer = distUserDashboardFacade.isOciCustomer();
        final boolean isAribaCustomer = distUserDashboardFacade.isAribaCustomer();
        if(!isOCICustomer && !isAribaCustomer && !getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            model.addAttribute("newInvoicesCount", distUserDashboardFacade.getNewInvoicesCount());
            model.addAttribute("openOrdersCount", distUserDashboardFacade.getOpenOrdersCount());
        }

        ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(null);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        final Map<String, Object> modelMap = model.asMap();

        final String metaDescription = getMessageSource().getMessage("meta.description.homepage", null, StringUtils.EMPTY, getI18nService().getCurrentLocale());
        setUpMetaData(model, null!=modelMap.get("metaDescription") ? modelMap.get("metaDescription").toString():metaDescription);

        setMetaRobots(model,contentPageForLabelOrId);

        updatePageTitle(model, contentPageForLabelOrId);
        populateOpenGraphInformation(model, request);
        return getViewForPage(model);
    }

    protected void populateOpenGraphInformation(final Model model, final HttpServletRequest request) {
        final CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();
        final Map<String, Object> modelMap = model.asMap();

        final String requestURL = request.getRequestURL().toString();
        final String imageURL = (requestURL + getDistrelecLogoURL()).replace("//", "/");

        model.addAttribute("ogSiteName", cmsSiteModel.getName(getI18nService().getCurrentLocale()));
        model.addAttribute("ogProductTitle", modelMap.get(CMS_PAGE_TITLE).toString());
        model.addAttribute("ogProductImage", imageURL);
        model.addAttribute("ogProductDescription", modelMap.get("metaDescription").toString());
        model.addAttribute("sharePageUrl", requestURL);
    }

    private Integer getNewInvoicesCount() {
        DistOnlineInvoiceHistoryPageableData invoiceHistoryPageableData = new DistOnlineInvoiceHistoryPageableData();
        invoiceHistoryPageableData.setSort("byDate");

        CustomerData customer = getUser();

        invoiceHistoryPageableData.setCustomerID(customer.getUnit().getErpCustomerId());
        invoiceHistoryPageableData.setPageSize(DEFAULT_SEARCH_MAX_SIZE);

        LocalDate now = LocalDate.now();
        LocalDate before30days = now.minusDays(30);

        invoiceHistoryPageableData.setInvoiceDateFrom(convertLocalDate2Date(before30days));
        invoiceHistoryPageableData.setInvoiceDateTo(convertLocalDate2Date(now));

        final SearchPageData<DistB2BInvoiceHistoryData> searchPageData = invoiceHistoryFacade.getInvoiceSearchHistory(invoiceHistoryPageableData);
        return searchPageData.getResults().size();
    }

    private Integer getOpenOrdersCount() {
    	int approvalRequest=getOrderFacade().getApprovalRequestsCount();
    	 final DistOrderHistoryPageableData pageableData = createPageableData();
    	 pageableData.setStatus(IN_PROGRESS_ORDER_STATUS);
        final  List<OrderHistoryData> orderList = orderHistoryFacade.getOrdersForStatuses(pageableData);
        return CollectionUtils.isNotEmpty(orderList) ? orderList.size()+approvalRequest : 0;
    }
    
    private DistOrderHistoryPageableData createPageableData() {
    	DistOrderHistoryPageableData data = new DistOrderHistoryPageableData();
    	LocalDate endDate = LocalDate.now();
    	LocalDate fromDate = endDate.minusDays(START_DATE);
    	data.setFromDate(convertLocalDate2Date(fromDate));
    	data.setToDate(convertLocalDate2Date(endDate));
    	return data;
    }

    private Date convertLocalDate2Date(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    protected String getStatusCode(final String status) {
        return ORDER_STATUS_LIST.stream().map(os -> os.getCode()).filter(code -> code.equalsIgnoreCase(status)).findFirst().orElse("ALL");
    }

    protected String getDistrelecLogoURL() {
        try {

        	if(getCmsPageService().getHomepage()!=null) {
            return mediaService.getMedia(getCmsPageService().getHomepage().getCatalogVersion(), "/images/theme/distrelec_logo.png").getURL2();
        	}else {
        		return StringUtils.EMPTY;
        	}
        } catch (final SystemException exp) {
            // NOP
        }
        return StringUtils.EMPTY;
    }

    protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage) {
        storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
    }

    @Override
    public String getPageType() {
        return ThirdPartyConstants.PageType.HOME;
    }

}
