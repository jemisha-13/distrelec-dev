package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Catalog.DEFAULT_CATALOG_ID;
import static com.namics.distrelec.b2b.core.constants.DistConstants.CatalogVersion.ONLINE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.facades.compliance.DistComplianceFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.occ.core.readonly.ReadOnly;
import com.namics.distrelec.occ.core.swagger.CommonQueryParams;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.media.NoDataAvailableException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(value = "/{baseSiteId}/compliance-document")
@CacheControl(directive = CacheControlDirective.PUBLIC)
@Tag(name = "Compliance documents")
@CommonQueryParams
public class ComplianceDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ComplianceDocumentController.class);

    private static final String EXCEL_APPLICATION_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Autowired
    private DistComplianceFacade distComplianceFacade;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private DistrelecProductFacade distrelecProductFacade;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @ReadOnly
    @Operation(operationId = "getRohsPdf", summary = "Get ROHS certificate document", description = "Get ROHS certificate document")
    @ApiBaseSiteIdParam
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

    @ReadOnly
    @Operation(operationId = "getPdfForMedia", summary = "Get PDF for given media code", description = "Get PDF for given media code")
    @ApiBaseSiteIdParam
    @GetMapping(value = "/document/Conflict_{mediaCode:.*}", produces = MediaType.APPLICATION_PDF_VALUE)
    @Deprecated(since = "since 2.3 - there is a dedicated endpoint for Conflict Minerals (/document/Conflict_Mineral.pdf) and there appears to be no other documents starting with 'Conflict'", forRemoval = true)
    public ResponseEntity<InputStreamResource> getPdfForMedia(@PathVariable("mediaCode") final String mediaCode) throws NoDataAvailableException,
                                                                                                                 IllegalArgumentException {
        CatalogVersionModel defaultOnlineCatalog = catalogVersionService.getCatalogVersion(DEFAULT_CATALOG_ID, ONLINE);
        MediaModel media = mediaService.getMedia(defaultOnlineCatalog, mediaCode);
        InputStream pdf = mediaService.getStreamFromMedia(media);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.add("Content-Disposition", "inline; filename=\"Conflict_" + mediaCode + "\"");
        return ResponseEntity.ok().headers(header).contentType(MediaType.APPLICATION_PDF)
                             .body(new InputStreamResource(pdf));
    }

    @ReadOnly
    @Operation(operationId = "downloadConflictMineral", summary = "Download Conflict Mineral declaration", description = "Download Conflict Mineral declaration")
    @ApiBaseSiteIdParam
    @GetMapping(path = "/document/Conflict_Mineral.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadConflictMineral() {
        InputStream pdf = distComplianceFacade.getPDFStreamForConflictMineral();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Conflict_Mineral.pdf");
        return getCompliancePDF(pdf, headers);
    }

    @ReadOnly
    @Operation(operationId = "downloadConflictMineralCertificate", summary = "Download conflict mineral certificate", description = "Download conflict mineral certificate")
    @ApiBaseSiteIdParam
    @GetMapping(path = "/conflict-mineral-certificate/{mediaCode:.+}", produces = EXCEL_APPLICATION_TYPE)
    public ResponseEntity<byte[]> downloadConflictMineralCertificate(@PathVariable("mediaCode") final String mediaCode) throws IOException {
        try {
            CatalogVersionModel defaultOnlineCatalog = catalogVersionService.getCatalogVersion(DEFAULT_CATALOG_ID, ONLINE);
            MediaModel media = mediaService.getMedia(defaultOnlineCatalog, mediaCode);
            byte[] bytes = IOUtils.toByteArray(mediaService.getStreamFromMedia(media));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(EXCEL_APPLICATION_TYPE));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + mediaCode + "\"");
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (UnknownIdentifierException e) {
            return ResponseEntity.notFound()
                                 .build();
        }
    }

    @ReadOnly
    @Operation(operationId = "downloadBatteryCompliance", summary = "Download battery compliance certificate", description = "Download battery compliance certificate")
    @ApiBaseSiteIdParam
    @GetMapping(path = "/document/Battery_Compliance.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadBatteryCompliance() {
        InputStream pdf = distComplianceFacade.getPDFStreamForBatteryCompliance();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Battery_Compliance.pdf");
        return getCompliancePDF(pdf, headers);
    }

    @ReadOnly
    @Operation(operationId = "downloadWEEE", summary = "Download WEEE certificate", description = "Download WEEE certificate")
    @ApiBaseSiteIdParam
    @GetMapping(path = "/document/WEEE.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadWEEE() {
        InputStream pdf = distComplianceFacade.getPDFStreamForWEEE();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=WEEE_statement.pdf");
        return getCompliancePDF(pdf, headers);
    }

    @ReadOnly
    @Operation(operationId = "downloadDisposalOfPackagingWasteStatement", summary = "Download Disposal Of Packaging Waste Statement", description = "Download Disposal Of Packaging Waste Statement")
    @ApiBaseSiteIdParam
    @GetMapping(path = "/document/disposal_of_packaging_waste.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadDisposalOfPackagingWasteStatement() {
        InputStream pdf = distComplianceFacade.getPDFStreamForDisposalOfPackagingWaste();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=disposal_of_packaging_waste.pdf");
        return getCompliancePDF(pdf, headers);
    }

    @ReadOnly
    @ApiBaseSiteIdParam
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

    private ResponseEntity<InputStreamResource> getCompliancePDF(InputStream pdf, HttpHeaders headers) {
        try {
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                                 .body(new InputStreamResource(pdf));
        } catch (UnknownIdentifierException e) {
            return ResponseEntity.notFound()
                                 .build();
        }
    }

}
