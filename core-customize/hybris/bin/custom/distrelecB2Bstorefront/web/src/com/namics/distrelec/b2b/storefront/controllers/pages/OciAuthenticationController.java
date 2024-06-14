/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.search.impl.DefaultDistProductSearchFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.distrelecoci.constants.DistrelecOciConstants;
import com.namics.distrelec.distrelecoci.exception.OciException;
import com.namics.distrelec.distrelecoci.utils.OutboundSection;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;

/**
 * Controller for e-procurement pages.
 */
@Controller
public class OciAuthenticationController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(OciAuthenticationController.class);

    private static final String PAGE_OCI = "ociPage";

    private static final String SEARCH_QUERY_PAGE_SIZE = "distrelec.oci.ff.page.size";

    @Autowired
    private DefaultDistProductSearchFacade factFinderSearchFacade;

    @Autowired
    private DistProductService productService;

    @RequestMapping(value = "/ociEntry")
    public void ociEntry(final HttpServletRequest request, final HttpServletResponse response) throws OciException {
        // do OCI login and set all necessary jaloSession attributes
        getDistOciService().doOciLogin(request, response);
    }

    @RequestMapping(value = "/ociSuccess", method = RequestMethod.GET)
    public String ociEntrySuccess(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException, OciException {
        addGlobalModelAttributes(model, request);

        // DISTRELEC-8744 do an FF request to read products
        List<ProductModel> productList = null;
        final String functionField = getOutboundSectionField(DistrelecOciConstants.FUNCTION);
        getOutboundSectionField(DistrelecOciConstants.SEARCHSTRING);
        if (StringUtils.equals(functionField, DistrelecOciConstants.BACKGROUND_SEARCH)) {
            // FF search params
            final SearchStateData state = new SearchStateData();
            final SearchQueryData searchQueryData = new SearchQueryData();
            searchQueryData.setValue(getOutboundSectionField(DistrelecOciConstants.SEARCHSTRING));
            state.setQuery(searchQueryData);
            state.setUrl(null);

            final PageableData paging = new PageableData();
            paging.setCurrentPage(1);
            paging.setPageSize(getConfigurationService().getConfiguration().getInt(SEARCH_QUERY_PAGE_SIZE, 50));

            try {
                final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = factFinderSearchFacade.search(false, state, paging,
                        DistSearchType.valueOf("TEXT"), "", true, "", null);

                if (!CollectionUtils.isEmpty(searchPageData.getResults())) {
                    productList = transfromFFresults(searchPageData.getResults());
                }
            } catch (final Exception e) {
                LOG.error("{} FactFinder search Exception occured: {}", ErrorSource.FACTFINDER.getCode(), e.getMessage(), e);
            }
        }

        final String ociFunctionForm = getDistOciService().getOciFunctionForm(productList != null ? productList : Collections.<ProductModel> emptyList());
        if (StringUtils.isNotBlank(ociFunctionForm)) {
            model.addAttribute("sendForm", isCurrentEnvironmentDev());
            model.addAttribute("ociForm", ociFunctionForm);

            storeCmsPageInModel(model, getContentPageForLabelOrId(PAGE_OCI));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PAGE_OCI));

            return ControllerConstants.Views.Pages.EProcurement.OCI.OciPage;
        }

        // redirect to dedicated page if additional function is called otherwise
        // to home page
        final String ociLoginRedirect = getDistOciService().getOciRedirectUrl();

        // Check if a plain page gets provided that opens a new window instead
        // of using the shop in a iFrame
        if (getDistOciService().openInNewWindow()) {
            final StringBuilder ociSuccessForm = new StringBuilder();
            ociSuccessForm.append("<form name=\"OrderForm\" action=\"").append(ociLoginRedirect).append("\" method=\"get\" target=\"_top\">");
            ociSuccessForm.append("<input type=\"submit\" name=\"gotoshop\" value=\"Go to shop\" />");
            ociSuccessForm.append("</form>");

            model.addAttribute("sendForm", isCurrentEnvironmentDev());
            model.addAttribute("ociForm", ociSuccessForm);

            storeCmsPageInModel(model, getContentPageForLabelOrId(PAGE_OCI));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PAGE_OCI));

            return ControllerConstants.Views.Pages.EProcurement.OCI.OciPage;
        }

        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + ociLoginRedirect);
    }

    /**
     * Transforms the search data to {@code ProductModel}s
     * 
     * @param input
     * @return a list of {@code ProductModel}
     */
    private List<ProductModel> transfromFFresults(final List<ProductData> input) {
        if (CollectionUtils.isEmpty(input)) {
            return Collections.<ProductModel> emptyList();
        }

        final List<String> codes = (List<String>) CollectionUtils.collect(input, new Transformer() {

            @Override
            public Object transform(final Object input) {
                return ((ProductData) input).getCode();
            }
        });
        return productService.getProductListForSapCodes(codes);
    }

    private String getOutboundSectionField(final String field) {
        final Object sessionAttribute = JaloSession.getCurrentSession().getAttribute(DistrelecOciConstants.OUTBOUND_SECTION_DATA);
        if (sessionAttribute != null) {
            return ((OutboundSection) sessionAttribute).getField(field);
        }
        return "";
    }
}
