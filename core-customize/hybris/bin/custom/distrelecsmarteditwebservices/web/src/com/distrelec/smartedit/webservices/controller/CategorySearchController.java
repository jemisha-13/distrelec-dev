package com.distrelec.smartedit.webservices.controller;

import com.distrelec.smartedit.webservices.strategy.DistrelecBaseLocaleStrategy;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Controller
@IsAuthorizedCmsManager
@RequestMapping("/categories")
public class CategorySearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategorySearchController.class);

    @Resource
    private WebPaginationUtils webPaginationUtils;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistrelecBaseLocaleStrategy distrelecBaseLocaleStrategy;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> findCategories(@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                              @RequestParam(name = "currentPage", required = false, defaultValue = "0") Integer currentPage,
                                              @RequestParam(name = "text", required = false, defaultValue = "") String text) {
        final Map<String, Object> results = newHashMap();
        try {
            Locale initial = distrelecBaseLocaleStrategy.setLocaleToBase(i18NService.getCurrentLocale());
            catalogVersionService.setSessionCatalogVersion("distrelecProductCatalog", "Online");

            SearchResult<CategoryData> searchResults = distCategoryFacade.searchCategory(text, currentPage, pageSize);

            final PaginationWsDTO paginationWsDTO = webPaginationUtils.buildPagination(searchResults);

            results.put(CmswebservicesConstants.WSDTO_RESPONSE_PARAM_RESULTS, searchResults.getResult());
            results.put(CmswebservicesConstants.WSDTO_RESPONSE_PARAM_PAGINATION, paginationWsDTO);

            distrelecBaseLocaleStrategy.revertLocale(initial);
        } catch (final ValidationException e) {
            LOGGER.info("Validation exception", e);
            throw new WebserviceValidationException(e.getValidationObject());
        }

        return results;
    }

    @RequestMapping(path = "{categoryCode}", method = RequestMethod.GET)
    @ResponseBody
    public CategoryData findCategory(@PathVariable(name = "categoryCode") String code) {
        catalogVersionService.setSessionCatalogVersion("distrelecProductCatalog", "Online");

        return distCategoryFacade.findCategory(code);
    }
}
