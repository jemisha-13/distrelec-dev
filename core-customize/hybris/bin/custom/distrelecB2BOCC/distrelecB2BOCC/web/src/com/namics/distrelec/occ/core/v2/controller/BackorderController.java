package com.namics.distrelec.occ.core.v2.controller;

import java.util.List;

import com.namics.distrelec.occ.core.readonly.ReadOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.facades.backorder.BackOrderFacade;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/{baseSiteId}/backorder")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "Backorder")
public class BackorderController extends BaseController {

    @Autowired
    private BackOrderFacade backOrderFacade;

    @ReadOnly
    @GetMapping(path = "alternate-products/{productCode}")
    @ApiBaseSiteIdParam
    public ProductListWsDTO getBackorderAlternateProducts(@PathVariable String productCode,
                                                          @RequestParam(defaultValue = "1", required = false) int quantity,
                                                          @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        ProductListWsDTO productList = new ProductListWsDTO();
        List<ProductData> alternateProducts = backOrderFacade.getAlternateProductList(productCode, quantity);
        List<ProductWsDTO> products = getDataMapper().mapAsList(alternateProducts, ProductWsDTO.class, fields);
        productList.setProducts(products);
        return productList;
    }

}
