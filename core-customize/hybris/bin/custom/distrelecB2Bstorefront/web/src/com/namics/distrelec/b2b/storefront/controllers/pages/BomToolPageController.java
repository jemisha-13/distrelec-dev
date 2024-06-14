package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.bomtool.BomToolFileLimitExceededException;
import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportData;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.bomtool.BomToolSearchResultData;
import com.namics.distrelec.b2b.facades.bomtool.DistBomToolFacade;
import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException;
import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException.ErrorSource;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.BomToolUploadForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bom-tool")
public class BomToolPageController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(BomToolPageController.class);

    private static final String REDIRECT_TO_UPLOAD_FORM_PAGE = addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/bom-tool");
    private static final String FORWARD_TO_MATCHING_PAGE = FORWARD_PREFIX + "/bom-tool/matching";
    private static final String BOM_TOOL_MATCHING_PATTERN = "/matching";

    // CMS Pages
    private static final String IMPORT_TOOL_FILE_UPLOAD_CMS_PAGE = "import-tool-upload";
    private static final String IMPORT_TOOL_MATCHING_CMS_PAGE = "import-tool-matching";
    private static final String IMPORT_TOOL_REVIEW_CMS_PAGE = "import-tool-review";

    private static final String DEFAULT_BOM_FILE_NAME_PREFIX = "BOM uploaded on ";

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistBomToolFacade distBomToolFacade;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ModelService modelService;

    @GetMapping
    public String fileUpload(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        BomToolUploadForm bomToolUploadForm = new BomToolUploadForm();
        model.addAttribute("uploadForm", bomToolUploadForm);

        getAndSetUpContentPage(model, request, IMPORT_TOOL_FILE_UPLOAD_CMS_PAGE);
        return ControllerConstants.Views.Pages.BomTool.BomToolUploadPage;
    }

    // Redirect the customer to the upload page if he try to access the review page with the get method
    @GetMapping(value = "/review")
    public String review() {
        return REDIRECT_TO_UPLOAD_FORM_PAGE;
    }

    // Redirect the customer to the upload page if he try to access the review page with the get method
    @GetMapping(value = "/list-files")
    public @ResponseBody
    List<String> listFiles() {
        return distBomToolFacade.getSavedBomToolEntries().stream().map(BomToolImportData::getFileName).collect(Collectors.toList());
    }

    // BOM review page
    @RequestMapping(value = BOM_TOOL_MATCHING_PATTERN, method = {RequestMethod.POST, RequestMethod.GET})
    public String matching(final Model model, @ModelAttribute(value = "uploadFormFile") final BomToolUploadForm uploadForm, final BindingResult result, final RedirectAttributes redirectAttributes, final HttpServletRequest request)
            throws Exception {

        List<String[]> bomResult = null;

        try {
            Integer allowedFileCount = getConfigurationService().getConfiguration().getInt("bom.file.allowedlimit");
            long fileSize = getConfigurationService().getConfiguration().getLong("bom.file.allowed.size.limit");
            if (distBomToolFacade.getCountOfBomFilesUploadedForCurrentCustomer() >= allowedFileCount) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.limitexhausted"));
                return REDIRECT_TO_UPLOAD_FORM_PAGE;
            }
            if (uploadForm.getFile() != null && uploadForm.getFile().getSize() > fileSize) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.sizelimit"));
                return REDIRECT_TO_UPLOAD_FORM_PAGE;
            }
            if (!result.hasErrors()) {
                // Add the file to the session
                if (uploadForm.getFile() != null && uploadForm.getFile().getOriginalFilename() != null
                        && uploadForm.getFile().getOriginalFilename().length() > 1) { // File version

                    final String fileName = distBomToolFacade.generateBomToolFileName(uploadForm.getFile().getOriginalFilename());
                    distBomToolFacade.removeFileIfAlreadyExists(fileName);
                    final MediaModel media = distBomToolFacade.saveFileAsMedia(fileName, uploadForm.getFile());

                    String extension = null;
                    final int position = uploadForm.getFile().getOriginalFilename().lastIndexOf('.');
                    if (position > 0) {
                        extension = uploadForm.getFile().getOriginalFilename().substring(position + 1);
                    }

                    model.addAttribute("fileName", uploadForm.getFile().getOriginalFilename());
                    if (isFileExtensionSupported(extension)) {
                        bomResult = distBomToolFacade.getLinesFromFile(media);
                    }
                } else { // wrong file extension
                    redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                            Collections.singletonList("import-tool.file.extension.notSupported"));
                    return REDIRECT_TO_UPLOAD_FORM_PAGE;
                }
            }

        } catch (final BomToolFacadeException e) {
            LOG.error("An error occurred during BOM tool import.", e);
            setRedirectAttributesForError(redirectAttributes, e);
            return REDIRECT_TO_UPLOAD_FORM_PAGE;

        } catch (final Exception e) {
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.template.error"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
        }
        if (bomResult == null) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.nothingToLoad"));
            return REDIRECT_TO_UPLOAD_FORM_PAGE;
        }

        model.addAttribute("fileLines", bomResult);
        getAndSetUpContentPage(model, request, IMPORT_TOOL_MATCHING_CMS_PAGE);
        return ControllerConstants.Views.Pages.BomTool.BomToolMatchingPage;
    }

    @GetMapping(value = "/review-file")
    public String reviewFile() {
        // GET method redirect to the upload page to solve the 404 issue
        return REDIRECT_TO_UPLOAD_FORM_PAGE;
    }

    @PostMapping(value = "/save-file", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<String> saveBomFile(@Valid @RequestBody BomToolImportData importData) {
        try {
            distBomToolFacade.createBomFile(importData);
            return ResponseEntity.ok(importData.getFileName());
        } catch (BomToolFileLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    @PutMapping(value = "/edit-file", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String editBomFile(@Valid @RequestBody BomToolImportData importData) {
        distBomToolFacade.editBomFile(importData);
        return importData.getFileName();
    }

    @PutMapping(value = "/cart-timestamp", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String saveCartTimestamp(@Valid @RequestBody BomToolImportData importData) {
        distBomToolFacade.saveCartTimestamp(importData);
        return importData.getFileName();
    }

    @PostMapping(value = "/delete-file")
    public @ResponseBody
    ResponseEntity<List<String>> deleteBomFile(@RequestParam String filename) {
        if (distBomToolFacade.deleteBomFile(filename)) {
            return ResponseEntity.ok(distBomToolFacade.getSavedBomToolEntries().stream()
                    .map(BomToolImportData::getFileName)
                    .collect(Collectors.toList()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/copyFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<List<String>> copyBomFile(@RequestParam String filename) {
        try {
            distBomToolFacade.copyBomFile(filename);
            return ResponseEntity.ok(distBomToolFacade.getSavedBomToolEntries().stream()
                    .map(BomToolImportData::getFileName)
                    .collect(Collectors.toList()));
        } catch (BomToolFileLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }

    // BOM review matching page
    @PostMapping(value = "/review-file")
    public String reviewFile(final Model model,
                             @RequestParam(required = false) final Integer articleNumberPosition,
                             @RequestParam(required = false) final Integer quantityPosition,
                             @RequestParam(required = false) final Integer referencePosition,
                             @RequestParam(defaultValue = "false", required = false) final boolean ignoreFirstRow,
                             @RequestParam final String fileName,
                             final RedirectAttributes redirectAttributes, final HttpServletRequest request) throws Exception {

        BomToolSearchResultData bomtoolResult = null;

        try {
            if (articleNumberPosition == null || quantityPosition == null) {
                GlobalMessages.addErrorMessage(model, "import-tool.file.noColumnsSelected");
                return FORWARD_TO_MATCHING_PAGE;
            }
            final String savedFileName = distBomToolFacade.generateBomToolFileName(fileName);
            final MediaModel file = mediaService.getMedia(savedFileName);

            if (file != null) { // File version

                // Is it an .csv or an xls?
                String extension = null;

                final int position = fileName.lastIndexOf('.');
                if (position > 0) {
                    extension = fileName.substring(position + 1);
                }

                // https://wiki.namics.com/display/distrelint/C300-BOMDataImport : only xls and csv allowed
                if (isFileExtensionSupported(extension)) {
                    final Integer nullsafeReferencePosition = Optional.ofNullable(referencePosition).orElse(Integer.MIN_VALUE);
                    bomtoolResult = distBomToolFacade.searchProductsFromFile(file, articleNumberPosition, quantityPosition, nullsafeReferencePosition,
                            ignoreFirstRow);
                }
                modelService.remove(file);
            } else { // wrong file extension
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.extension.notSupported"));
                return REDIRECT_TO_UPLOAD_FORM_PAGE;
            }
        } catch (final BomToolFacadeException e) {
            LOG.error("An error occurred during file Bom Tool Import.", e);

            setRedirectAttributesForError(redirectAttributes, e);

            return REDIRECT_TO_UPLOAD_FORM_PAGE;

        } catch (final Exception e) {
            LOG.error("An error occurred during BOM Tool Import.", e);
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.template.error"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
        }

        if (bomtoolResult == null) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.nothingToLoad"));
            return REDIRECT_TO_UPLOAD_FORM_PAGE;
        }

        setBomToolResult(model, bomtoolResult);
        model.addAttribute("fileName", fileName);
        getAndSetUpContentPage(model, request, IMPORT_TOOL_REVIEW_CMS_PAGE);
        return ControllerConstants.Views.Pages.BomTool.BomToolReviewPage;
    }

    // BOM review matching page
    @GetMapping(value = "/load-file")
    public String loadFile(final Model model, @RequestParam final String fileName,
                           final RedirectAttributes redirectAttributes, final HttpServletRequest request) throws Exception {

        BomToolSearchResultData bomtoolResult = null;

        try {
            bomtoolResult = distBomToolFacade.loadBomFile(fileName);

        } catch (final BomToolFacadeException e) {
            LOG.error("An error occurred during file Bom Tool Import.", e);

            setRedirectAttributesForError(redirectAttributes, e);

            return REDIRECT_TO_UPLOAD_FORM_PAGE;

        } catch (final Exception e) {
            LOG.error("An error occurred during BOM Tool Import.", e);
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.template.error"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
        }

        if (bomtoolResult == null) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.nothingToLoad"));
            return REDIRECT_TO_UPLOAD_FORM_PAGE;
        }

        model.addAttribute("customerId", getUser().getUid());
        setBomToolResult(model, bomtoolResult);
        addGlobalModelAttributes(model, request);
        final ContentPageModel contentPage = getContentPageForLabelOrId(IMPORT_TOOL_REVIEW_CMS_PAGE);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        model.addAttribute("breadcrumbs", simpleBreadcrumbBuilder.getBreadcrumbs(contentPage.getTitle(), contentPage.getTitle(Locale.ENGLISH)));
        model.addAttribute("loadFile", Boolean.TRUE);
        model.addAttribute("fileName", fileName);

        return ControllerConstants.Views.Pages.BomTool.BomToolReviewPage;
    }

    // BOM review page
    @PostMapping(value = "/review")
    public String review(final Model model, @ModelAttribute(value = "uploadFormFile") final BomToolUploadForm uploadForm, final BindingResult result, final RedirectAttributes redirectAttributes, final HttpServletRequest request)
            throws Exception {
        BomToolSearchResultData bomToolResult = null;

        try {
            if (!result.hasErrors() && uploadForm.getData() != null && uploadForm.getData().length() != 0) {
                bomToolResult = distBomToolFacade.searchProductsFromData(uploadForm.getData());
            }
        } catch (final BomToolFacadeException e) {
            LOG.error("An error occurred during Bom Tool Import.", e);
            setRedirectAttributesForError(redirectAttributes, e);
            return REDIRECT_TO_UPLOAD_FORM_PAGE;

        } catch (final Exception e) {
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.template.error"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
        }
        if (bomToolResult == null) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.nothingToLoad"));
            return REDIRECT_TO_UPLOAD_FORM_PAGE;
        }

        model.addAttribute("fileName", getDefaultBomFilename());
        model.addAttribute("customerId", getUser().getUid());
        setBomToolResult(model, bomToolResult);
        getAndSetUpContentPage(model, request, IMPORT_TOOL_REVIEW_CMS_PAGE);
        return ControllerConstants.Views.Pages.BomTool.BomToolReviewPage;
    }

    private String getDefaultBomFilename() {
        LocalDateTime now = LocalDateTime.now();
        return DEFAULT_BOM_FILE_NAME_PREFIX + now.toString();
    }

    private void setBomToolResult(Model model, BomToolSearchResultData bomToolResult) {
        model.addAttribute("quantityAdjustedCount", bomToolResult.getQuantityAdjustedProductsCount());
        model.addAttribute("matchingProducts", bomToolResult.getMatchingProducts());
        model.addAttribute("unavailableProducts", bomToolResult.getUnavailableProducts());
        model.addAttribute("punchedOutProducts", bomToolResult.getPunchedOutProducts());
        // Display of validation message if at least one item of the import file/list could not be matched with a product of the sales org
        if (CollectionUtils.isNotEmpty(bomToolResult.getNotMatchingProductCodes())) {
            GlobalMessages.addErrorMessage(model, "import-tool.warning.products.notfound");
        }
        if (CollectionUtils.isNotEmpty(bomToolResult.getPunchedOutProducts())) {
            GlobalMessages.addErrorMessage(model, "bom.product.error.punchout", new String[]{getJoinedPunchedOutProductCodes(bomToolResult)}, DistConstants.Punctuation.PIPE);
        }
        model.addAttribute("adjustedQuantityProducts", bomToolResult.getQuantityAdjustedProducts());
        model.addAttribute("notMatchingProducts", bomToolResult.getNotMatchingProductCodes());
        model.addAttribute("duplicateMpnProductList", bomToolResult.getDuplicateMpnProducts());
    }

    private String  getJoinedPunchedOutProductCodes(BomToolSearchResultData bomToolResult) {
       return bomToolResult.getPunchedOutProducts()
                .stream()
               .filter(Objects::nonNull)
               .filter(result -> result.getProduct() != null)
                .map(result-> result.getProduct().getCode())
               .collect(Collectors.joining(", "));
    }

    @GetMapping(value = "/delete-entry", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String deleteBOMEntry(@RequestParam final String productCode, @RequestParam final String fileName) {
        distBomToolFacade.deleteBomImportEntry(productCode, fileName);
        return "ok";
    }

    @PostMapping(value = "/rename-file/{currentName:.+}")
    public @ResponseBody
    ResponseEntity<String> renameBOMFile(@PathVariable String currentName, @RequestBody String newName) {
        if (!StringUtils.equalsIgnoreCase(currentName, newName)) {
            String changedName = distBomToolFacade.renameBomImportFile(currentName, newName);
            if (changedName != null) {
                return ResponseEntity.ok(changedName);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.ok(currentName);
        }
    }

    private boolean isFileExtensionSupported(String extension) {
        List<String> supportedExtensions = new ArrayList<>();
        supportedExtensions.add(DistConstants.Export.FORMAT_CSV);
        supportedExtensions.add(DistConstants.Export.FORMAT_XLS);
        supportedExtensions.add("xlsx");
        supportedExtensions.add("txt");
        return supportedExtensions.contains(extension);
    }

    private void setRedirectAttributesForError(final RedirectAttributes redirectAttributes, BomToolFacadeException e) {
        if (ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND.equals(e.getErrorSource())) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.copyPasteSeparatorNotFound"));
        } else if (ErrorSource.FIELD_SEPARATOR_NOT_FOUND.equals(e.getErrorSource())) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.separatorNotFound"));
        } else if (ErrorSource.FILE_EMPTY.equals(e.getErrorSource())) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.empty"));
        } else if (ErrorSource.NO_DATA.equals(e.getErrorSource())) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.noDataFound"));
        } else if (ErrorSource.TOO_MANY_LINES.equals(e.getErrorSource())) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.limitExceeded"));
        } else if (ErrorSource.CUSTOMER_REFERENCE_FIELD.equals(e.getErrorSource())) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.customerReferenceToLong"));
        } else {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
        }
    }

    private void getAndSetUpContentPage(Model model, HttpServletRequest request, String importToolFileUploadCmsPage) throws CMSItemNotFoundException {
        final ContentPageModel uploadPage = getContentPageForLabelOrId(importToolFileUploadCmsPage);
        storeCmsPageInModel(model, uploadPage);
        setUpMetaDataForContentPage(model, uploadPage);
        model.addAttribute("breadcrumbs", simpleBreadcrumbBuilder.getBreadcrumbs(uploadPage.getTitle(), uploadPage.getTitle(Locale.ENGLISH)));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.BOMTOOL);
    }
}
