package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException.Reason.FILE_CONTENT_BLANK;
import static com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException.Reason.FILE_EXTENSION_UNSUPPORTED;
import static com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException.Reason.FILE_LIST_LIMIT_EXCEEDED;
import static com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException.Reason.FILE_SIZE_LIMIT_EXCEEDED;
import static com.namics.distrelec.occ.core.security.SecuredAccessConstants.ROLE_QUALITYANDLEGAL;
import static com.namics.distrelec.occ.core.security.SecuredAccessConstants.ROLE_TRUSTED_CLIENT;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.namics.distrelec.b2b.facades.compliance.DistComplianceFacade;
import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalFacade;
import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException;
import com.namics.distrelec.occ.core.qualityandlegal.ws.dto.QualityAndLegalDownloadWsDTO;
import com.namics.distrelec.occ.core.readonly.ReadOnly;
import com.namics.distrelec.occ.core.v2.helper.quality.DistQualityAndLegalBaseHelper;
import com.namics.distrelec.occ.core.v2.helper.quality.DistQualityAndLegalExcelExportHelper;
import com.namics.distrelec.occ.core.v2.helper.quality.DistQualityAndLegalPdfExportHelper;
import com.namics.distrelec.occ.core.v2.helper.quality.DistQualityAndLegalTemplateDownloadHelper;
import com.namics.distrelec.occ.core.v2.response.EnvironmentalDocumentationFileUploadResponse;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JRException;

@Controller
@Tag(name = "Quality and Legal")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/quality-and-legal")
public class DistrelecQualityAndLegalController extends BaseController {

    private static final Logger LOG = LogManager.getLogger(DistrelecQualityAndLegalController.class);

    private static final long MAX_FILE_SIZE_IN_BYTES = 1048576L;

    private static final String DEFAULT_GENERIC_FILE_UPLOAD_ERROR_MESSAGE = "Upload failed! Something went wrong while uploading the file";

    @Autowired
    private DistQualityAndLegalFacade distQualityAndLegalFacade;

    @Autowired
    private DistComplianceFacade distComplianceFacade;

    @Autowired
    private DistQualityAndLegalExcelExportHelper distQualityAndLegalExcelExportHelper;

    @Autowired
    private DistQualityAndLegalPdfExportHelper distQualityAndLegalPdfExportHelper;

    @Autowired
    private DistQualityAndLegalBaseHelper distQualityAndLegalBaseHelper;

    @Autowired
    private DistQualityAndLegalTemplateDownloadHelper distQualityAndLegalTemplateDownloadHelper;

    @Secured({ ROLE_TRUSTED_CLIENT, ROLE_QUALITYANDLEGAL })
    @PostMapping(value = "/upload-file", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "uploadFile", summary = "Upload XLS(x) or CSV/txt file containing product codes", description = "Returns the list of parsed product codes")
    @ApiBaseSiteIdParam
    public ResponseEntity<EnvironmentalDocumentationFileUploadResponse> uploadFile(@Parameter(description = "Data (file) for Q&L product codes upload", required = true) @RequestPart final MultipartFile file) {
        if (file == null) {
            return handleInternalServerErrorOnFileUpload();
        }

        final String filePath = System.getProperty("java.io.tmpdir") + File.separator + file.getOriginalFilename();
        List<String> rawCodes;

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            checkFileSize(file);
            IOUtils.write(file.getBytes(), outputStream);
            rawCodes = distQualityAndLegalFacade.getProductCodes(filePath);
        } catch (DistQualityAndLegalInvalidFileUploadException e) {
            return handleFileUploadErrors(e);
        } catch (Exception e) {
            LOG.error(e);
            return handleInternalServerErrorOnFileUpload();
        }

        List<String> cleanedCodes = rawCodes.stream()
                                            .map(distQualityAndLegalFacade::cleanup)
                                            .collect(Collectors.toList());

        List<String> existingCodes;
        try {
            existingCodes = distQualityAndLegalFacade.findExistingProductCodes(cleanedCodes);
        } catch (DistQualityAndLegalInvalidFileUploadException e) {
            LOG.error(e);
            return handleFileUploadErrors(e);
        }
        List<String> invalidProductCodes = distQualityAndLegalFacade.filterInvalidProductCodes(rawCodes, existingCodes);

        return ok(new EnvironmentalDocumentationFileUploadResponse.Builder().withProductCodes(existingCodes, invalidProductCodes)
                                                                            .build());
    }

    @Secured({ ROLE_TRUSTED_CLIENT, ROLE_QUALITYANDLEGAL })
    @PostMapping(value = "/excel", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "downloadXlsReport", summary = "Download XLSX report", description = "Returns the excel sheet file")
    @ApiBaseSiteIdParam
    public HttpEntity<ByteArrayResource> downloadXlsReport(@Parameter(description = "List of product codes for which the report is generated", required = true) @RequestBody final QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO) {
        if (qualityAndLegalDownloadWsDTO == null || isEmpty(qualityAndLegalDownloadWsDTO.getProductCodes())) {
            return badRequest().build();
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             XSSFWorkbook workbook = distQualityAndLegalExcelExportHelper.generateEnvironmentalInformationXlsReport(qualityAndLegalDownloadWsDTO)) {
            workbook.write(stream);

            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),
                                        getHeadersForDownloadFiles(getEnvironmentalInformationXlsxReportFileName()), CREATED);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @Secured({ ROLE_TRUSTED_CLIENT, ROLE_QUALITYANDLEGAL })
    @PostMapping(value = "/pdf", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "downloadPdfReport", summary = "Download PDF report", description = "Returns the PDF file")
    @ApiBaseSiteIdParam
    public HttpEntity<ByteArrayResource> downloadPdfReport(@Parameter(description = "List of product codes for which the report is generated", required = true) @RequestBody final QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO) {
        if (qualityAndLegalDownloadWsDTO == null || isEmpty(qualityAndLegalDownloadWsDTO.getProductCodes())) {
            return badRequest().build();
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            distQualityAndLegalPdfExportHelper.getPDFStreamForQualityAndLegalReport(qualityAndLegalDownloadWsDTO, stream);
            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()), getHeadersForDownloadFiles(getEnvironmentalInformationPdfReportFileName()),
                                        CREATED);
        } catch (IOException | JRException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @Secured({ ROLE_TRUSTED_CLIENT, ROLE_QUALITYANDLEGAL })
    @PostMapping(value = "/bulk-certificates", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "downloadBulkCertificates", summary = "Download the PDF file containing all products statements in bulk", description = "Returns the PDF file")
    @ApiBaseSiteIdParam
    public HttpEntity<ByteArrayResource> downloadBulkCertificates(@Parameter(description = "List of product codes for which the report is generated", required = true) @RequestBody final QualityAndLegalDownloadWsDTO qualityAndLegalDownloadWsDTO) throws IOException {
        if (qualityAndLegalDownloadWsDTO == null || isEmpty(qualityAndLegalDownloadWsDTO.getProductCodes())) {
            return badRequest().build();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<ProductData> products = distQualityAndLegalBaseHelper.getProductsForReport(qualityAndLegalDownloadWsDTO.getProductCodes());
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();

        for (ProductData product : products) {
            boolean isRNDProduct = distComplianceFacade.isRNDProduct(product);

            if (distComplianceFacade.isRndProductAndIsNotROHSConform(product, isRNDProduct)
                    || !distComplianceFacade.isNotRndProductAndIsNotAllowedForSiteAndNotCompliant(product, isRNDProduct)) {
                InputStream rohsPdf = isRNDProduct ? distComplianceFacade.getPDFStreamForROHS_RND(product)
                                                   : distComplianceFacade.getPDFStreamForROHS(product);

                if (rohsPdf != null) {
                    pdfMergerUtility.addSource(rohsPdf);
                }
            }

            if (product.getHasSvhc() != null) {
                boolean hasSvhc = product.getHasSvhc();
                InputStream svhcPdf = isRNDProduct ? distComplianceFacade.getPDFStreamForSvhc_RND(product, hasSvhc)
                                                   : distComplianceFacade.getPDFStreamForSvhc(product, hasSvhc);

                if (svhcPdf != null) {
                    pdfMergerUtility.addSource(svhcPdf);
                }
            }
        }

        pdfMergerUtility.setDestinationStream(outputStream);
        pdfMergerUtility.mergeDocuments();

        if (outputStream.size() == 0) {
            return badRequest().build();
        }

        return new ResponseEntity<>(new ByteArrayResource(outputStream.toByteArray()), getHeadersForDownloadFiles(getBulkDownloadPdfReportFileName()), CREATED);
    }

    @ReadOnly
    @Secured({ ROLE_TRUSTED_CLIENT, ROLE_QUALITYANDLEGAL })
    @GetMapping(value = "/template", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "downloadFileUploadTemplate", summary = "Download File Upload Template", description = "Returns the excel sheet file that can be used as a template to upload products")
    @ApiBaseSiteIdParam
    public HttpEntity<ByteArrayResource> downloadFileUploadTemplate() {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             XSSFWorkbook workbook = distQualityAndLegalTemplateDownloadHelper.generateFileUploadTemplate()) {
            workbook.write(stream);

            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),
                                        getHeadersForDownloadFiles(getEnvironmentalInformationXlsxFileUploadTemplateFileName()), OK);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    private String getEnvironmentalInformationXlsxReportFileName() {
        return "environmental-information-" + getDatePartForFileName() + ".xlsx";
    }

    private String getEnvironmentalInformationPdfReportFileName() {
        return "environmental-information-" + getDatePartForFileName() + ".pdf";
    }

    private String getBulkDownloadPdfReportFileName() {
        return "environmental-information-bulk" + getDatePartForFileName() + ".pdf";
    }

    private String getEnvironmentalInformationXlsxFileUploadTemplateFileName() {
        return "DistrelecImportFileTemplate-" + getI18nService().getCurrentLocale().getLanguage() + ".xlsx";
    }

    private String getDatePartForFileName() {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    private void checkFileSize(MultipartFile file) throws DistQualityAndLegalInvalidFileUploadException {
        if (file.getSize() > MAX_FILE_SIZE_IN_BYTES) {
            throw new DistQualityAndLegalInvalidFileUploadException(FILE_SIZE_LIMIT_EXCEEDED);
        }
    }

    private ResponseEntity<EnvironmentalDocumentationFileUploadResponse> handleFileUploadErrors(DistQualityAndLegalInvalidFileUploadException exception) {
        String errorMessage;

        if (FILE_EXTENSION_UNSUPPORTED.equals(exception.getReason())) {
            errorMessage = getMessageSource().getMessage("qualityAndLegal.upload.error.fileFormatUnsupported.message",
                                                         null,
                                                         "Upload failed! Unsupported file format",
                                                         getI18nService().getCurrentLocale());
        } else if (FILE_CONTENT_BLANK.equals(exception.getReason())) {
            errorMessage = getMessageSource().getMessage("qualityAndLegal.upload.error.fileNoContent.message",
                                                         null,
                                                         "Upload failed! File content was blank",
                                                         getI18nService().getCurrentLocale());
        } else if (FILE_SIZE_LIMIT_EXCEEDED.equals(exception.getReason())) {
            errorMessage = getMessageSource().getMessage("qualityAndLegal.upload.error.fileSizeLimit.message",
                                                         null,
                                                         "Upload failed! File size limit exceeded",
                                                         getI18nService().getCurrentLocale());
        } else if (FILE_LIST_LIMIT_EXCEEDED.equals(exception.getReason())) {
            errorMessage = getMessageSource().getMessage("qualityAndLegal.upload.error.fileListLimit.message",
                                                         null,
                                                         "Upload failed! The list of products is over 200",
                                                         getI18nService().getCurrentLocale());
        } else {
            errorMessage = getMessageSource().getMessage("qualityAndLegal.upload.error.generic.message",
                                                         null,
                                                         DEFAULT_GENERIC_FILE_UPLOAD_ERROR_MESSAGE,
                                                         getI18nService().getCurrentLocale());
        }

        EnvironmentalDocumentationFileUploadResponse response = new EnvironmentalDocumentationFileUploadResponse.Builder().withErrorMessage(errorMessage)
                                                                                                                          .build();
        return badRequest().body(response);
    }

    private ResponseEntity<EnvironmentalDocumentationFileUploadResponse> handleInternalServerErrorOnFileUpload() {
        String errorMessage = getMessageSource().getMessage("qualityAndLegal.upload.error.generic.message",
                                                            null,
                                                            DEFAULT_GENERIC_FILE_UPLOAD_ERROR_MESSAGE,
                                                            getI18nService().getCurrentLocale());
        EnvironmentalDocumentationFileUploadResponse response = new EnvironmentalDocumentationFileUploadResponse.Builder().withErrorMessage(errorMessage)
                                                                                                                          .build();
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }

    private HttpHeaders getHeadersForDownloadFiles(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "force-download"));
        headers.set(CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return headers;
    }

}
