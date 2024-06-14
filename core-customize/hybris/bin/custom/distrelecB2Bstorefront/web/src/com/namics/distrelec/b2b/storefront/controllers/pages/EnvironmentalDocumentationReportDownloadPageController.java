package com.namics.distrelec.b2b.storefront.controllers.pages;

import static com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException.Reason.*;
import static com.namics.distrelec.b2b.storefront.controllers.ControllerConstants.Views.Fragments.Quality.EnvironmentalDocumentationDownloadPage;
import static com.namics.distrelec.b2b.storefront.controllers.ControllerConstants.Views.Fragments.Quality.EnvironmentalDocumentationDownloadPageUrl;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants.SeoRobots.META_ROBOTS;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.namics.distrelec.b2b.facades.compliance.DistComplianceFacade;
import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalFacade;
import com.namics.distrelec.b2b.facades.legal.DistQualityAndLegalInvalidFileUploadException;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.storefront.annotations.RequireHardLogin;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.excepions.DistrelecBadRequestException;
import com.namics.distrelec.b2b.storefront.forms.LegalAndQualityUploadForm;
import com.namics.distrelec.b2b.storefront.forms.QualityAndLegalDownloadForm;
import com.namics.distrelec.b2b.storefront.helper.DistQualityAndLegalExcelExportHelper;
import com.namics.distrelec.b2b.storefront.helper.DistQualityAndLegalPdfExportHelper;
import com.namics.distrelec.b2b.storefront.response.EnvironmentalDocumentationFileUploadResponse;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequireHardLogin
@RequestMapping(EnvironmentalDocumentationDownloadPageUrl)
public class EnvironmentalDocumentationReportDownloadPageController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(EnvironmentalDocumentationReportDownloadPageController.class);

    private static final String ENVIRONMENTAL_DOCUMENTATION_DOWNLOAD_PAGE_ID = "environmentalDocumentationDownloadPage";

    private static final long MAX_FILE_SIZE_IN_BYTES = 1048576L;

    private static final String DEFAULT_GENERIC_FILE_UPLOAD_ERROR_MESSAGE = "Upload failed! Something went wrong while uploading the file";

    @Autowired
    private DistQualityAndLegalFacade distQualityAndLegalFacade;

    @Autowired
    @Qualifier("distrelecProductFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistComplianceFacade distComplianceFacade;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistQualityAndLegalExcelExportHelper distQualityAndLegalExcelExportHelper;

    @Autowired
    private DistQualityAndLegalPdfExportHelper distQualityAndLegalPdfExportHelper;

    @GetMapping
    public String batchDownload(Model model, HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        model.addAttribute("file", new LegalAndQualityUploadForm());
        model.addAttribute("downloadForm", new QualityAndLegalDownloadForm());
        model.addAttribute(META_ROBOTS, NOINDEX_NOFOLLOW);
        final ContentPageModel contentPage = getContentPageForLabelOrId(ENVIRONMENTAL_DOCUMENTATION_DOWNLOAD_PAGE_ID);
        model.addAttribute("breadcrumbs", simpleBreadcrumbBuilder.getBreadcrumbs(contentPage.getTitle(), contentPage.getTitle(Locale.ENGLISH)));
        populatePage(model, contentPage);
        return EnvironmentalDocumentationDownloadPage;
    }

    @PostMapping("/upload-file")
    public ResponseEntity<EnvironmentalDocumentationFileUploadResponse> uploadFile(@ModelAttribute(value = "file") LegalAndQualityUploadForm uploadForm) {
        if (uploadForm == null || uploadForm.getFile() == null) {
            return handleInternalServerErrorOnFileUpload();
        }

        final String filePath = System.getProperty("java.io.tmpdir") + File.separator + uploadForm.getFile().getOriginalFilename();
        List<String> rawCodes;

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            checkFileSize(uploadForm.getFile());
            outputStream.write(uploadForm.getFile().getFileItem().get());
            rawCodes = distQualityAndLegalFacade.getProductCodes(filePath);
        } catch (IOException e) {
            LOG.error(e);
            return handleInternalServerErrorOnFileUpload();
        } catch (DistQualityAndLegalInvalidFileUploadException e) {
            return handleFileUploadErrors(e);
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

        return ResponseEntity.ok(new EnvironmentalDocumentationFileUploadResponse.Builder()
                                                                                           .withProductCodes(existingCodes, invalidProductCodes)
                                                                                           .build());
    }

    @PostMapping(value = "/excel")
    public HttpEntity<ByteArrayResource> downloadXlsReport(@ModelAttribute(value = "downloadForm") QualityAndLegalDownloadForm downloadForm) {
        if (downloadForm == null || isEmpty(downloadForm.getProductCodes())) {
            throw new DistrelecBadRequestException();
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             XSSFWorkbook workbook = distQualityAndLegalExcelExportHelper.generateEnvironmentalInformationXlsReport(getProductsForReport(downloadForm.getProductCodes()))) {
            workbook.write(stream);

            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),
                                        getHeadersForDownloadFiles(getEnvironmentalInformationXlsxReportFileName()), CREATED);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/pdf")
    public HttpEntity<ByteArrayResource> downloadPdfReport(@ModelAttribute(value = "downloadForm") QualityAndLegalDownloadForm downloadForm) {
        if (downloadForm == null || isEmpty(downloadForm.getProductCodes())) {
            throw new DistrelecBadRequestException();
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            distQualityAndLegalPdfExportHelper.getPDFStreamForQualityAndLegalReport(getProductsForReport(downloadForm.getProductCodes()), stream);
            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()), getHeadersForDownloadFiles(getEnvironmentalInformationPdfReportFileName()),
                                        CREATED);
        } catch (IOException | JRException e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/bulk-certificates")
    public HttpEntity<ByteArrayResource> downloadBulkCertificates(@ModelAttribute(value = "downloadForm") QualityAndLegalDownloadForm downloadForm)
                                                                                                                                                    throws IOException {
        if (downloadForm == null || isEmpty(downloadForm.getProductCodes())) {
            throw new DistrelecBadRequestException();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<ProductData> products = getProductsForReport(downloadForm.getProductCodes());
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
            throw new DistrelecBadRequestException();
        }

        return new ResponseEntity<>(new ByteArrayResource(outputStream.toByteArray()), getHeadersForDownloadFiles(getBulkDownloadPdfReportFileName()), CREATED);
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

    private String getDatePartForFileName() {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    private void populatePage(Model model, ContentPageModel contentPage) {
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
    }

    private List<ProductData> getProductsForReport(List<String> productCodes) {
        final List<ProductOption> productOptions = Arrays.asList(ProductOption.BASIC,
                                                                 ProductOption.MIN_BASIC,
                                                                 ProductOption.DESCRIPTION,
                                                                 ProductOption.DIST_MANUFACTURER,
                                                                 ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION);
        return productFacade.getProductListForCodesAndOptions(productCodes, productOptions);
    }

    private void checkFileSize(CommonsMultipartFile file) throws DistQualityAndLegalInvalidFileUploadException {
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

        EnvironmentalDocumentationFileUploadResponse response = new EnvironmentalDocumentationFileUploadResponse.Builder()
                                                                                                                          .withErrorMessage(errorMessage)
                                                                                                                          .build();
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<EnvironmentalDocumentationFileUploadResponse> handleInternalServerErrorOnFileUpload() {
        String errorMessage = getMessageSource().getMessage("qualityAndLegal.upload.error.generic.message",
                                                            null,
                                                            DEFAULT_GENERIC_FILE_UPLOAD_ERROR_MESSAGE,
                                                            getI18nService().getCurrentLocale());
        EnvironmentalDocumentationFileUploadResponse response = new EnvironmentalDocumentationFileUploadResponse.Builder()
                                                                                                                          .withErrorMessage(errorMessage)
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
