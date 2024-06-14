package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.category.DistCategoryIndexFacade;
import com.namics.distrelec.b2b.facades.category.DistProductFamilyFacade;
import com.namics.distrelec.b2b.facades.category.data.DistCategoryPageData;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexDataList;
import com.namics.distrelec.mapping.converters.ProductImagesMapToWsConverter;
import com.namics.distrelec.occ.core.categories.ws.dto.RelatedWsDTO;
import com.namics.distrelec.occ.core.product.data.DistProductFamilyWsData;
import com.namics.distrelec.occ.core.readonly.ReadOnly;
import com.namics.distrelec.occ.core.topcategories.ws.dto.DistCategoryIndexListWsDTO;
import com.namics.distrelec.occ.core.topcategories.ws.dto.DistCategoryPageWsDTO;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsoccaddon.mapping.converters.DataToWsConverter;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercewebservicescommons.dto.product.CategoryWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Distrelec Category Controller
 */
@Controller
@Tag(name = "Category")
public class DistrelecCategoryController extends BaseController {

    protected static final Logger LOG = LogManager.getLogger(DistrelecCategoryController.class);

    private static final String LOG_CATEGORY_NOT_FOUND = "PageData with categoryCode : [%s] not found!";

    private static final String LOG_CATEGORY_EMPTY = "categoryCode is empty!";

    @Autowired
    @Qualifier("distCategoryFacade")
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    @Qualifier("relatedDataToWsConverter")
    private DataToWsConverter<RelatedData, List<RelatedWsDTO>> relatedDataToWsConverter;

    @Resource(name = "distCustomerFacade")
    private DistCustomerFacade distCustomerFacade;

    @Autowired
    private DistProductFamilyFacade distProductFamilyFacade;

    @Autowired
    private ProductImagesMapToWsConverter productImagesMapToWsConverter;

    @Autowired
    private DistCategoryIndexFacade distCategoryIndexFacade;

    @ReadOnly
    @GetMapping("/{baseSiteId}/categorypage/{categoryCode}")
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @ResponseBody
    @Operation(operationId = "getCategoryPageData", summary = "Gets the category page data with source & subcategories.", description = "Lists available source with all subcategoires for categories page")
    @ApiBaseSiteIdParam
    public DistCategoryPageWsDTO getCategoryPageData(@Parameter(description = "categoryCode", required = true) @PathVariable final String categoryCode,
                                                     @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (StringUtils.isNotEmpty(categoryCode)) {
            DistCategoryPageData distCategoryPageData = distCategoryFacade.getCategoryPageDataForOCC(categoryCode);
            if (distCategoryPageData != null) {
                DistCategoryPageWsDTO response = new DistCategoryPageWsDTO();
                List<CategoryWsDTO> subCategories = getDataMapper().mapAsList(distCategoryPageData.getSubCategories(), CategoryWsDTO.class, fields);
                CategoryWsDTO sourceCategory = getDataMapper().map(distCategoryPageData.getSourceCategory(), CategoryWsDTO.class, fields);
                CategoryModel categoryModel = distCategoryService.getCategoryForCode(categoryCode);
                setRelatedData(fields, sourceCategory, categoryModel);
                response.setBreadcrumbs(getDataMapper().mapAsList(distCategoryPageData.getBreadcrumbs(), CategoryWsDTO.class, fields));
                response.setSourceCategory(sourceCategory);
                response.setSubCategories(subCategories);
                response.setShowCategoriesOnly(CollectionUtils.isNotEmpty(subCategories));
                return response;
            } else {
                throw new IllegalStateException(String.format(LOG_CATEGORY_NOT_FOUND, categoryCode));
            }
        } else {
            throw new IllegalStateException(LOG_CATEGORY_EMPTY);
        }
    }

    @ReadOnly
    @GetMapping(value = "/{baseSiteId}/productfamily/{code}")
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @ResponseBody
    @Operation(operationId = "getProductFamilyData", summary = "Gets the product family data")
    @ApiBaseSiteIdParam
    public DistProductFamilyWsData getProductFamilyData(@Parameter(description = "code") @PathVariable String code,
                                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) String fields) {
        if (StringUtils.isNotBlank(code)) {
            Optional<CategoryData> productFamilyCategory = distProductFamilyFacade.findProductFamily(code);
            if (productFamilyCategory.isPresent()) {
                CategoryData categoryData = productFamilyCategory.get();
                DistProductFamilyWsData productFamilyWsData = getDataMapper().map(categoryData, DistProductFamilyWsData.class, fields);
                if (isNotEmpty(categoryData.getFamilyMedia())) {
                    productFamilyWsData.setFamilyMedia(productImagesMapToWsConverter.convert(categoryData.getFamilyMedia(), fields));
                }
                return productFamilyWsData;
            } else {
                throw new IllegalArgumentException("Product family not found");
            }
        } else {
            throw new IllegalArgumentException("Code must not be empty or blank");
        }
    }

    @ReadOnly
    @RequestMapping(value = "/{baseSiteId}/categories/index", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getCategoriesIndex", summary = "Get categories index data", description = "Index of categories")
    @ApiBaseSiteIdParam
    public DistCategoryIndexListWsDTO getCategoriesIndex(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        DistCategoryIndexDataList dataList = new DistCategoryIndexDataList();
        dataList.setCategories(distCategoryIndexFacade.getCategoryIndexData());
        return getDataMapper().map(dataList, DistCategoryIndexListWsDTO.class, fields);
    }

    @ReadOnly
    @RequestMapping(value = "/{baseSiteId}/topcats", method = RequestMethod.GET)
    @Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getTopCategories',#fields)")
    @ResponseBody
    @Operation(operationId = "getTopCategories", summary = "Get a list of Top Categories and searchBar Categories.", description = "Lists top categories & searchbarCategories.")
    @ApiBaseSiteIdParam
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    public DistCategoryIndexListWsDTO getTopCategories(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        DistCategoryIndexDataList dataList = new DistCategoryIndexDataList();
        dataList.setCategories(distCategoryIndexFacade.getTopCategoryDataForOCC());
        dataList.setSearchbarCategories(distCustomerFacade.getTopCategories());
        return getDataMapper().map(dataList, DistCategoryIndexListWsDTO.class, fields);
    }

    private void setRelatedData(String fields, CategoryWsDTO sourceCategory, CategoryModel categoryModel) {
        RelatedData relatedData = distCategoryFacade.findCategoryRelatedData(categoryModel);
        if (relatedData != null) {
            sourceCategory.setRelatedData(relatedDataToWsConverter.convert(relatedData, fields));
        }
    }
}
