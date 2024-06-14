package com.namics.distrelec.occ.core.v2.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.io.InputStream;

import com.namics.distrelec.occ.core.swagger.CommonQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCartService;
import com.namics.distrelec.b2b.facades.pdf.DistPDFGenerationFacade;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/{baseSiteId}/cart/pdf")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@CommonQueryParams
public class CartsPdfController {

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private DistCartService cartService;

    @Autowired
    private DistCommerceCartService commerceCartService;

    @Autowired
    private DistPDFGenerationFacade distPDFGenerationFacade;

    @GetMapping(value = "/{cartGuid}", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(operationId = "generateCartPdf", summary = "Generate PDF from the user cart", description = "Generate PDF from the user cart")
    @ApiBaseSiteIdParam
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable String cartGuid) {
        /**
         * An user authentication token is not received on this endpoint as it is routed through nodejs and a cart is
         * resolved directly as a prerequisite for cartLoaderStrategy is a valid user in a session.
         */
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
        CartModel cart = commerceCartService.getCartForGuidAndSite(cartGuid, currentBaseSite);
        cartService.setSessionCart(cart);

        InputStream pdf = distPDFGenerationFacade.getPDFStreamForCart();
        if (pdf == null) {
            return ResponseEntity.notFound()
                                 .build();
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Content-Disposition", distPDFGenerationFacade.getCartPdfHeaderValue());
        return ok()
                   .headers(header)
                   .contentType(MediaType.APPLICATION_PDF)
                   .body(new InputStreamResource(pdf));
    }
}
