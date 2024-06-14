package com.namics.distrelec.occ.core.v2.controller;

import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.occ.core.readonly.ReadOnly;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Secured(SecuredAccessConstants.ROLE_TESTAUTOMATOR)
@RequestMapping(path = "/{baseSiteId}/test-support")
public class TestSupportController extends BaseController {

    private static final List<ProductOption> PRODUCT_OPTIONS_WITHOUT_CATPLUSSSUPPLIERAID = Arrays.asList(
            ProductOption.ONLINE_PRICE,
            ProductOption.PRICE,
            ProductOption.SUMMARY,
            ProductOption.DESCRIPTION,
            ProductOption.GALLERY,
            ProductOption.CATEGORIES,
            ProductOption.REVIEW,
            ProductOption.PROMOTIONS,
            ProductOption.VARIANT_FULL,
            ProductOption.STOCK,
            ProductOption.PROMOTION_LABELS,
            ProductOption.COUNTRY_OF_ORIGIN,
            ProductOption.DIST_MANUFACTURER,
            ProductOption.VIDEOS,
            ProductOption.AUDIOS,
            ProductOption.IMAGE360,
            ProductOption.BREADCRUMBS,
            ProductOption.TECH_ATTRIBUTE,
            ProductOption.DOWNLOADS,
            ProductOption.DIST_MANUFACTURER,
            ProductOption.URL,
            ProductOption.METADATA,
            ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION,
            ProductOption.ADDITIONAL,
            ProductOption.BULK_PERMISSION,
            ProductOption.CHANNEL_AVAILABILITY,
            ProductOption.PRODUCT_INFORMATION);

    private final DistProductService productService;

    private final DataMapper dataMapper;

    private final DistrelecProductFacade productFacade;

    public TestSupportController(DistProductService productService, DataMapper dataMapper, DistrelecProductFacade productFacade) {
        this.productService = productService;
        this.dataMapper = dataMapper;
        this.productFacade = productFacade;
    }

    @ReadOnly
    @GetMapping(path = "products")
    public List<ProductWsDTO> getProductIdsPerSiteAndSalesStatus(@PathVariable("baseSiteId") String siteId,
                                                                 @RequestParam String salesStatus,
                                                                 @RequestParam(required = false) String itemCategoryGroup,
                                                                 @Min(1) @Max(500) @RequestParam(required = false, defaultValue = "500") int count,
                                                                 @Min(1) @RequestParam(required = false, defaultValue = "1") int page,
                                                                 @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        return productService.getProductsBySiteIdAndSalesStatus(siteId, salesStatus, itemCategoryGroup, count, page).stream()
                .map(model -> productFacade.getProductForCodeAndOptions(model, PRODUCT_OPTIONS_WITHOUT_CATPLUSSSUPPLIERAID))
                .map(data -> dataMapper.map(data, ProductWsDTO.class, fields))
                .collect(Collectors.toList());
    }

}
