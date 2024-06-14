package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.facades.compliance.DistComplianceFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.media.NoDataAvailableException;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Catalog.DEFAULT_CATALOG_ID;
import static com.namics.distrelec.b2b.core.constants.DistConstants.CatalogVersion.ONLINE;

@Controller
@RequestMapping(value = ComplianceDocumentController.PAGE_MAPPING)
public class ComplianceDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ComplianceDocumentController.class);

    public static final String PAGE_MAPPING = "/compliance-document";

    private static final String EXCEL_APPLICATION_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Autowired
    private DistComplianceFacade distComplianceFacade;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private DistrelecProductFacade distrelecProductFacade;

    @Autowired
    private StoreSessionFacade storeSessionFacade;
    
    @Autowired
    private CatalogVersionService catalogVersionService;

    @GetMapping(value = "/pdf/ROHS_{productCode:.*}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getRohsPdf(@PathVariable("productCode") final String productCode) {
        ProductData productData = getProductData(productCode);

        if (productData == null) {
            return ResponseEntity.notFound()
                                 .build();
        }

        boolean isRNDProduct = distComplianceFacade.isRNDProduct(productData);

        if (distComplianceFacade.isRndProductAndIsNotROHSConform(productData, isRNDProduct)
                || distComplianceFacade.isNotRndProductAndIsNotAllowedForSiteAndNotCompliant(productData, isRNDProduct)) {
            return ResponseEntity.notFound()
                                 .build();
        }

        InputStream pdf = isRNDProduct ? distComplianceFacade.getPDFStreamForROHS_RND(productData) : distComplianceFacade.getPDFStreamForROHS(productData);
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ROHS_" + productCode + ".pdf");
        return ResponseEntity.ok().headers(header).contentType(MediaType.APPLICATION_PDF)
                             .body(new InputStreamResource(pdf));
    }

    protected ProductData getProductData(String productCode) {
        try {
            return distrelecProductFacade.getProductForOptions(productCode, Arrays.asList(ProductOption.BASIC, ProductOption.DIST_MANUFACTURER,
                                                                                                        ProductOption.DESCRIPTION));
        } catch (UnknownIdentifierException e) {
            return null;
        }
    }

    @GetMapping(value = "/document/Conflict_{mediaCode:.*}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getPdfForMedia(@PathVariable("mediaCode") final String mediaCode) throws NoDataAvailableException,
                                                                                                                 IllegalArgumentException {
    	CatalogVersionModel defaultOnlineCatalog = catalogVersionService.getCatalogVersion(DEFAULT_CATALOG_ID, ONLINE);
    	MediaModel media = mediaService.getMedia(defaultOnlineCatalog,mediaCode);
        InputStream pdf = mediaService.getStreamFromMedia(media);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.add("Content-Disposition", "inline; filename=\"Conflict_" + mediaCode + "\"");
        return ResponseEntity.ok().headers(header).contentType(MediaType.APPLICATION_PDF)
                             .body(new InputStreamResource(pdf));
    }

    @GetMapping(path = "/conflict-mineral-certificate/{mediaCode:.*}", produces = EXCEL_APPLICATION_TYPE)
    public HttpEntity<byte[]> download(@PathVariable("mediaCode") final String mediaCode) throws IOException {
    	CatalogVersionModel defaultOnlineCatalog = catalogVersionService.getCatalogVersion(DEFAULT_CATALOG_ID, ONLINE);
        byte[] bytes = IOUtils.toByteArray(mediaService.getStreamFromMedia(mediaService.getMedia(defaultOnlineCatalog,mediaCode)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(EXCEL_APPLICATION_TYPE));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + mediaCode + "\"");
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping(path = "/document/Battery_Compliance.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadBatteryCompliance() throws IOException {
        InputStream pdf = distComplianceFacade.getPDFStreamForBatteryCompliance();
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Battery_Compliance.pdf");
        return ResponseEntity.ok().headers(header).contentType(MediaType.APPLICATION_PDF)
                             .body(new InputStreamResource(pdf));
    }

    @GetMapping(path = "/document/WEEE.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadWEEE() throws IOException {
        InputStream pdf = distComplianceFacade.getPDFStreamForWEEE();
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=WEEE_statement.pdf");
        return ResponseEntity.ok().headers(header).contentType(MediaType.APPLICATION_PDF)
                             .body(new InputStreamResource(pdf));
    }

    @GetMapping(value = "/pdf/SVHC_{productCode:.*}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getSvhcPdf(@PathVariable("productCode") String productCode) {
        try {
            ProductData productData = getProductData(productCode);

            if (productData == null) {
                return ResponseEntity.notFound().build();
            }

            InputStream pdf;
            boolean hasSvhc = distComplianceFacade.productHasSvhc(productData);
            if (distComplianceFacade.isRNDProduct(productData)) {
                pdf = distComplianceFacade.getPDFStreamForSvhc_RND(productData, hasSvhc);
            } else {
                pdf = distComplianceFacade.getPDFStreamForSvhc(productData, hasSvhc);
            }

            if (pdf == null) {
                LOG.error("PDF template for No SVHC statement not found");
                return ResponseEntity.notFound().build();
            }

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=SVHC_\" + productCode + \".pdf");
            return ResponseEntity.ok()
                                 .headers(header)
                                 .contentType(MediaType.APPLICATION_PDF)
                                 .body(new InputStreamResource(pdf));
        } catch (UnknownIdentifierException e) {
            LOG.warn("No product with code {} found", productCode, e);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            LOG.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
