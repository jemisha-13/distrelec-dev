package com.namics.distrelec.occ.core.v2.controller;

import static de.hybris.platform.commercefacades.product.ProductOption.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.namics.distrelec.occ.core.readonly.ReadOnly;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistCompareListFacade;
import com.namics.distrelec.occ.core.v2.helper.ProductsHelper;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Web Services Controller to expose the functionality of the {@link de.hybris.platform.commercefacades.product.ProductFacade} and
 * SearchFacade.
 */

@Controller
@Tag(name = "B2B - Products")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/products")
public class DistB2BProductsController extends BaseController {

    @Resource(name = "distrelecProductFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistProductService productService;

    @ReadOnly
    @RequestMapping(value = "/{productCode}/prices", method = RequestMethod.GET)
    @CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
    @Cacheable(value = "productCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,#productCode,#fields,#channel)", condition = "!@defaultDistCommercePriceService.isOnlinePricingCustomer()" )
    @ResponseBody
    @Operation(operationId = "getProductPrices", summary = "Get product price details.", description = "Returns details of a single product according to a product code.")
    @ApiBaseSiteIdAndUserIdParam
    public ProductWsDTO getProductPrices(@Parameter(description = "Product identifier", required = true) @PathVariable final String productCode,
                                         @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                         @ApiFieldsParam @RequestParam(defaultValue = "B2B") final String channel) {
        ProductModel productModel = productService.getProductForCode(productCode);
        if (productModel == null || StringUtils.isNotBlank(productModel.getCatPlusSupplierAID())) {
            throw new UnknownIdentifierException(String.format("Product with code '%s' not found!", productCode));
        }
        ProductData b2bProductData = productFacade.getProductForCodeAndOptions(productModel, Arrays.asList(MIN_BASIC,
                                                                                                           PRICE,
                                                                                                           ONLINE_PRICE,
                                                                                                           VOLUME_PRICES));
        if (Objects.nonNull(b2bProductData)) {
            return getDataMapper().map(b2bProductData, ProductWsDTO.class, fields);
        }
        throw new UnknownIdentifierException(String.format("Product with code '%s' not found!", productCode));
    }

}
