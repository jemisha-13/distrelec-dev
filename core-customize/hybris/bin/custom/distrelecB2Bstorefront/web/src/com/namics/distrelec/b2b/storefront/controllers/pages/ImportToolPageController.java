/*
 * Copyright 2000-2013 Distrelec. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.namics.distrelec.b2b.facades.importtool.DistImportToolFacade;
import com.namics.distrelec.b2b.facades.importtool.exception.ImportToolException;
import com.namics.distrelec.b2b.facades.importtool.exception.ImportToolException.ErrorSource;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.BomToolUploadForm;
import com.namics.distrelec.b2b.storefront.forms.ImportToolUploadForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Controller for the import tool (BOM Manager).
 * 
 * @author daezamofinl, Distrelec
 */
@Controller
@RequestMapping("/import-tool")
public class ImportToolPageController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(ImportToolPageController.class);

    private static final String REDIRECT_TO_UPLOAD_FORM_PAGE = addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/import-tool");
    private static final String REDIRECT_TO_BOM_TOOL_UPLOAD_FORM_PAGE = addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/bom-tool");
    private static final String FORWARD_TO_MATCHING_PAGE = FORWARD_PREFIX + "/import-tool/matching";
    private static final String BOM_TOOL_ENABLED="new.bom.tool.enabled";
    // CMS Pages
    private static final String IMPORT_TOOL_FILE_UPLOAD_CMS_PAGE = "import-tool-upload";
    private static final String IMPORT_TOOL_MATCHING_CMS_PAGE = "import-tool-matching";
    private static final String IMPORT_TOOL_REVIEW_CMS_PAGE = "import-tool-review";

    private static final String UPLOAD_FORM_FILE = "uploadFormFile";

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistImportToolFacade distImportToolFacade;
    
    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(method = RequestMethod.GET)
    public String fileUpload(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        if(sessionService.getAttribute("uploadFormData") !=null && sessionService.getAttribute("uploadFormData") instanceof ImportToolUploadForm) {
        ImportToolUploadForm importToolUploadForm = (ImportToolUploadForm) sessionService.getAttribute("uploadFormData");
        if (importToolUploadForm != null) {
            sessionService.removeAttribute("uploadFormData");
            importToolUploadForm = null;
        }

        importToolUploadForm = new ImportToolUploadForm();
        model.addAttribute("uploadForm", importToolUploadForm);
        }
        
        if(sessionService.getAttribute("uploadFormData") !=null && sessionService.getAttribute("uploadFormData") instanceof BomToolUploadForm) {
        	BomToolUploadForm importToolUploadForm = (BomToolUploadForm) sessionService.getAttribute("uploadFormData");
            if (importToolUploadForm != null) {
                sessionService.removeAttribute("uploadFormData");
                importToolUploadForm = null;
            }
            importToolUploadForm = new BomToolUploadForm();
            model.addAttribute("uploadForm", importToolUploadForm);
            }
        
        final ContentPageModel uploadPage = getContentPageForLabelOrId(IMPORT_TOOL_FILE_UPLOAD_CMS_PAGE);
        storeCmsPageInModel(model, uploadPage);
        setUpMetaDataForContentPage(model, uploadPage);
        model.addAttribute("breadcrumbs", getSimpleBreadcrumbBuilder().getBreadcrumbs(uploadPage.getTitle(),uploadPage.getTitle(Locale.ENGLISH)));
        Boolean isBomToolEnabled=!StringUtils.isBlank(configurationService.getConfiguration().getString(BOM_TOOL_ENABLED)) ? Boolean.valueOf(configurationService.getConfiguration().getString(BOM_TOOL_ENABLED)) : Boolean.TRUE;
        if(isBomToolEnabled) {
        	return REDIRECT_TO_BOM_TOOL_UPLOAD_FORM_PAGE;
        }
        return ControllerConstants.Views.Pages.ImportTool.ImportToolUploadPage;
    }

    // Redirect the customer to the upload page if he try to access the review page with the get method
    @RequestMapping(value = "/review", method = RequestMethod.GET)
    public String review() {
        return REDIRECT_TO_UPLOAD_FORM_PAGE;
    }

    // BOM review page
    @RequestMapping(value = "/matching", method = { RequestMethod.POST, RequestMethod.GET })
    public String matching(final Model model, @ModelAttribute(value = "uploadFormFile")
    final ImportToolUploadForm uploadForm, final BindingResult result, final RedirectAttributes redirectAttributes, final HttpServletRequest request)
            throws Exception {

        List<String[]> importResult = null;

        try {
            if (!result.hasErrors()) {
                final ImportToolUploadForm importToolUploadForm = (ImportToolUploadForm) sessionService.getAttribute("uploadFormData");
                if (importToolUploadForm != null) {
                    uploadForm.setData(importToolUploadForm.getData());
                    uploadForm.setFile(importToolUploadForm.getFile());
                    uploadForm.setName(importToolUploadForm.getName());
                }
                // Add the file to the session
                sessionService.setAttribute(UPLOAD_FORM_FILE, uploadForm.getFile());
                if (uploadForm.getFile() != null && uploadForm.getFile().getOriginalFilename() != null
                        && uploadForm.getFile().getOriginalFilename().length() > 1) { // File version

                    final String filePath = System.getProperty("java.io.tmpdir") + File.separator + uploadForm.getFile().getOriginalFilename();
                    final FileOutputStream outputStream = new FileOutputStream(new File(filePath));
                    outputStream.write(uploadForm.getFile().getFileItem().get());
                    outputStream.close();

                    // Is it an .csv or an xls?
                    String extension = null;

                    final int position = uploadForm.getFile().getOriginalFilename().lastIndexOf('.');
                    if (position > 0) {
                        extension = uploadForm.getFile().getOriginalFilename().substring(position + 1);
                    }

                    // https://wiki.namics.com/display/distrelint/C300-BOMDataImport : only xls and csv allowed
                    if ("csv".equals(extension) || "xls".equals(extension) || "xlsx".equals(extension) || "txt".equals(extension)) {
                        importResult = getDistImportToolFacade().getLinesFromFile(filePath);
                    }
                } else { // wrong file extension
                    redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                            Collections.singletonList("import-tool.file.extension.notSupported"));
                    return REDIRECT_TO_UPLOAD_FORM_PAGE;
                }
            }

        } catch (final ImportToolException e) {
            LOG.error("An error occurred during file import.", e);
            if (ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.copyPasteSeparatorNotFound"));
            } else if (ErrorSource.FIELD_SEPARATOR_NOT_FOUND.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.separatorNotFound"));
            } else if (ErrorSource.FILE_EMPTY.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.empty"));
            } else if (ErrorSource.NO_DATA.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.noDataFound"));
            } else if (ErrorSource.TOO_MANY_LINES.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.limitExceeded"));
            } else if (ErrorSource.CUSTOMER_REFERENCE_FIELD.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.customerReferenceToLong"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
            return REDIRECT_TO_UPLOAD_FORM_PAGE;

        } catch (final Exception e) {
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.template.error"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
        }
        if (importResult == null) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.nothingToLoad"));
            return REDIRECT_TO_UPLOAD_FORM_PAGE;
        }

        model.addAttribute("fileLines", importResult);

        // Display of validation message if at least one item of the import file/list could not be matched with a product of the sales org
        // if (!((List<String[]>) importResult.get("notMatchingProducts")).isEmpty()) {
        // GlobalMessages.addErrorMessage(model, "import-tool.warning.products.notfound");
        // }

        addGlobalModelAttributes(model, request);
        final ContentPageModel contentPage = getContentPageForLabelOrId(IMPORT_TOOL_MATCHING_CMS_PAGE);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        model.addAttribute("breadcrumbs", getSimpleBreadcrumbBuilder().getBreadcrumbs(contentPage.getTitle(),contentPage.getTitle(Locale.ENGLISH)));

        return ControllerConstants.Views.Pages.ImportTool.ImportToolMatchingPage;
    }

    @RequestMapping(value = "/review-file", method = RequestMethod.GET)
    public String reviewFile() {
        // GET method redirect to the upload page to solve the 404 issue
        return REDIRECT_TO_UPLOAD_FORM_PAGE;
    }

    // BOM review matching page
    @RequestMapping(value = "/review-file", method = RequestMethod.POST)
    public String reviewFile(final Model model, //
            @ModelAttribute(value = "articleNumberPosition")
            final int articleNumberPosition, //
            @ModelAttribute(value = "quantityPosition")
            final int quantityPosition, //
            @ModelAttribute(value = "referencePosition")
            final int referencePosition, //
            @RequestParam(value = "ignoreFirstRow", defaultValue = "false")
            final boolean ignoreFirstRow, //
            final BindingResult result, final RedirectAttributes redirectAttributes, final HttpServletRequest request) throws Exception {

        Map<String, Object> importResult = null;

        try {
            if (!result.hasErrors()) {
                if ("999".equals(Integer.toString(articleNumberPosition)) || "999".equals(Integer.toString(quantityPosition))) {
                    GlobalMessages.addErrorMessage(model, "import-tool.file.noColumnsSelected");
                    final ImportToolUploadForm importToolUploadForm = new ImportToolUploadForm();
                    importToolUploadForm.setFile((CommonsMultipartFile) sessionService.getAttribute(UPLOAD_FORM_FILE));
                    sessionService.setAttribute("uploadFormData", importToolUploadForm);
                    return FORWARD_TO_MATCHING_PAGE;
                }

                final CommonsMultipartFile file = sessionService.getAttribute(UPLOAD_FORM_FILE);
                final ImportToolUploadForm importToolUploadForm = new ImportToolUploadForm();
                importToolUploadForm.setFile(file);
                sessionService.setAttribute("uploadFormData", importToolUploadForm);

                if (file != null && file.getOriginalFilename() != null && file.getOriginalFilename().length() > 1) { // File version
                    final String filePath = System.getProperty("java.io.tmpdir") + File.separator + file.getOriginalFilename();

                    // Is it an .csv or an xls?
                    String extension = null;

                    final int position = file.getOriginalFilename().lastIndexOf('.');
                    if (position > 0) {
                        extension = file.getOriginalFilename().substring(position + 1);
                    }

                    // https://wiki.namics.com/display/distrelint/C300-BOMDataImport : only xls and csv allowed
                    if ("csv".equals(extension) || "xls".equals(extension) || "xlsx".equals(extension) || "txt".equals(extension)) {
                        importResult = getDistImportToolFacade().searchProductsFromFile(filePath, articleNumberPosition, quantityPosition, referencePosition,
                                ignoreFirstRow);
                    }
                } else { // wrong file extension
                    redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                            Collections.singletonList("import-tool.file.extension.notSupported"));
                    return REDIRECT_TO_UPLOAD_FORM_PAGE;
                }
            }
        } catch (final ImportToolException e) {
            LOG.error("An error occurred during file import.", e);

            switch (e.getErrorSource()) {
            case COPY_PAST_SEPARATOR_NOT_FOUND:
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.copyPasteSeparatorNotFound"));
                break;
            case FIELD_SEPARATOR_NOT_FOUND:
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.separatorNotFound"));
                break;
            case FILE_EMPTY:
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.empty"));
                break;
            case NO_DATA:
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.noDataFound"));
                break;
            case CUSTOMER_REFERENCE_FIELD:
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.customerReferenceToLong"));
                break;
            case TOO_MANY_LINES:
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.limitExceeded"));
                break;
            default:
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
                break;
            }

            return REDIRECT_TO_UPLOAD_FORM_PAGE;

        } catch (final Exception e) {
            LOG.error("An error occurred during file import.", e);
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.template.error"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
        }

        if (importResult == null) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.nothingToLoad"));
            return REDIRECT_TO_UPLOAD_FORM_PAGE;
        }

        model.addAttribute("quantityAdjustedCount", importResult.get("quantityAdjustedCount"));
        model.addAttribute("matchingProducts", importResult.get("matchingProducts"));
        model.addAttribute("unavailableProducts", importResult.get("unavailableProducts"));

        // Display of validation message if at least one item of the import file/list could not be matched with a product of the sales org
        if (!((List<String[]>) importResult.get("notMatchingProducts")).isEmpty()) {
            GlobalMessages.addErrorMessage(model, "import-tool.warning.products.notfound");
        }

        model.addAttribute("notMatchingProducts", importResult.get("notMatchingProducts"));

        addGlobalModelAttributes(model, request);
        final ContentPageModel contentPage = getContentPageForLabelOrId(IMPORT_TOOL_REVIEW_CMS_PAGE);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        model.addAttribute("breadcrumbs", getSimpleBreadcrumbBuilder().getBreadcrumbs(contentPage.getTitle(),contentPage.getTitle(Locale.ENGLISH)));

        return ControllerConstants.Views.Pages.ImportTool.ImportToolReviewPage;
    }

    // BOM review page
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public String review(final Model model, @ModelAttribute(value = "uploadFormFile")
    final ImportToolUploadForm uploadForm, final BindingResult result, final RedirectAttributes redirectAttributes, final HttpServletRequest request)
            throws Exception {
        Map<String, Object> importResult = null;

        try {
            if (!result.hasErrors()) {

                if (uploadForm.getData() != null && uploadForm.getData().length() != 0) { // Copy paste version
                    // importRows = createArrayFromCopyPaste(uploadForm.getData());
                    importResult = getDistImportToolFacade().searchProductsFromData(uploadForm.getData());

                } else if (uploadForm.getFile() != null && uploadForm.getFile().getOriginalFilename() != null
                        && uploadForm.getFile().getOriginalFilename().length() > 1) { // File version
                    FileOutputStream outputStream = null;
                    final String filePath = System.getProperty("java.io.tmpdir") + File.separator + uploadForm.getFile().getOriginalFilename();

                    outputStream = new FileOutputStream(new File(filePath));
                    outputStream.write(uploadForm.getFile().getFileItem().get());
                    outputStream.close();

                    // Is it an .csv or an xls?
                    String extension = null;

                    final int position = uploadForm.getFile().getOriginalFilename().lastIndexOf('.');
                    if (position > 0) {
                        extension = uploadForm.getFile().getOriginalFilename().substring(position + 1);
                    }

                    // https://wiki.namics.com/display/distrelint/C300-BOMDataImport : only xls and csv allowed
                    if ("csv".equals(extension) || "xls".equals(extension) || "xlsx".equals(extension) || "txt".equals(extension)) {
                        importResult = getDistImportToolFacade().searchProductsFromFile(filePath);
                    }
                } else { // wrong file extension
                    redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                            Collections.singletonList("import-tool.file.extension.notSupported"));
                    return REDIRECT_TO_UPLOAD_FORM_PAGE;
                }
            }

        } catch (final ImportToolException e) {
            LOG.error("An error occurred during file import.", e);
            if (ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.copyPasteSeparatorNotFound"));
            } else if (ErrorSource.FIELD_SEPARATOR_NOT_FOUND.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.separatorNotFound"));
            } else if (ErrorSource.FILE_EMPTY.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.empty"));
            } else if (ErrorSource.NO_DATA.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.noDataFound"));
            } else if (ErrorSource.CUSTOMER_REFERENCE_FIELD.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                        Collections.singletonList("import-tool.file.customerReferenceToLong"));
            } else if (ErrorSource.TOO_MANY_LINES.equals(e.getErrorSource())) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.limitExceeded"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
            return REDIRECT_TO_UPLOAD_FORM_PAGE;

        } catch (final Exception e) {
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.template.error"));
            } else {
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.error"));
            }
        }
        if (importResult == null) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("import-tool.file.nothingToLoad"));
            return REDIRECT_TO_UPLOAD_FORM_PAGE;
        }

        model.addAttribute("quantityAdjustedCount", importResult.get("quantityAdjustedCount"));
        model.addAttribute("matchingProducts", importResult.get("matchingProducts"));
        model.addAttribute("unavailableProducts", importResult.get("unavailableProducts"));

        // Display of validation message if at least one item of the import file/list could not be matched with a product of the sales org
        if (!((List<String[]>) importResult.get("notMatchingProducts")).isEmpty()) {
            GlobalMessages.addErrorMessage(model, "import-tool.warning.products.notfound");
        }

        model.addAttribute("notMatchingProducts", importResult.get("notMatchingProducts"));

        addGlobalModelAttributes(model, request);
        final ContentPageModel contentPage = getContentPageForLabelOrId(IMPORT_TOOL_REVIEW_CMS_PAGE);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        model.addAttribute("breadcrumbs", getSimpleBreadcrumbBuilder().getBreadcrumbs(contentPage.getTitle(),contentPage.getTitle(Locale.ENGLISH)));

        return ControllerConstants.Views.Pages.ImportTool.ImportToolReviewPage;
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(final SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }

    public DistImportToolFacade getDistImportToolFacade() {
        return distImportToolFacade;
    }

    public void setDistImportToolFacade(final DistImportToolFacade distImportToolFacade) {
        this.distImportToolFacade = distImportToolFacade;
    }

}
