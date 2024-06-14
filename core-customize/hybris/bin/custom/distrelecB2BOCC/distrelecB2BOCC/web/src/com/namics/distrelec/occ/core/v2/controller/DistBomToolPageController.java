package com.namics.distrelec.occ.core.v2.controller;

import com.namics.distrelec.b2b.core.bomtool.BomToolFileLimitExceededException;
import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportData;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.bomtool.BomToolSearchResultData;
import com.namics.distrelec.b2b.facades.bomtool.DistBomToolFacade;
import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException;
import com.namics.distrelec.occ.core.bomtool.ws.dto.BomToolImportWsDTO;
import com.namics.distrelec.occ.core.bomtool.ws.dto.BomToolSearchResultWsDTO;
import com.namics.distrelec.occ.core.exceptions.BomToolFileException;
import com.namics.distrelec.occ.core.importtool.ws.dto.ImportToolMatchingWsDTO;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Tag(name = "BOM Tool")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/bom-tool")
public class DistBomToolPageController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(DistBomToolPageController.class);

    private static final List<String> SUPPORTED_EXTENSIONS = List.of("csv", "xls", "xlsx", "txt");

    @Autowired
    private DistBomToolFacade distBomToolFacade;

    @Resource(name = "bomToolImportDTOValidator")
    private Validator bomToolImportDTOValidator;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ModelService modelService;

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/list-files", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "listFiles", summary = "List BOM files", description = "Returns list of saved BOM files.")
    @ApiBaseSiteIdAndUserIdParam
    public List<String> listFiles() {
        return getSavedBomToolFiles();
    }

    private List<String> getSavedBomToolFiles() {
        return distBomToolFacade.getSavedBomToolEntries().stream()
                                .map(BomToolImportData::getFileName)
                                .collect(Collectors.toList());
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/save-file", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "saveBomFile", summary = "Save BOM file", description = "Creates BOM file and returns its name.")
    @ApiBaseSiteIdAndUserIdParam
    public String saveBomFile(@Parameter(description = "Data for BOM tool import", required = true) @RequestBody final BomToolImportWsDTO importWsDTO) {
        validate(importWsDTO, "bomToolImport", bomToolImportDTOValidator);
        BomToolImportData importData = getDataMapper().map(importWsDTO, BomToolImportData.class);
        distBomToolFacade.createBomFile(importData);
        return importData.getFileName();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/edit-file", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "editBomFile", summary = "Edit BOM file", description = "Updates BOM file and returns its name.")
    @ApiBaseSiteIdAndUserIdParam
    public String editBomFile(@Parameter(description = "Data for BOM tool import", required = true) @RequestBody final BomToolImportWsDTO importWsDTO) {
        validate(importWsDTO, "bomToolImport", bomToolImportDTOValidator);
        BomToolImportData importData = getDataMapper().map(importWsDTO, BomToolImportData.class);
        distBomToolFacade.editBomFile(importData);
        return importData.getFileName();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/cart-timestamp", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "saveCartTimestamp", summary = "Save cart timestamp", description = "Saves cart timestamp and returns file name.")
    @ApiBaseSiteIdAndUserIdParam
    public String saveCartTimestamp(@Parameter(description = "Data for BOM tool import", required = true) @RequestBody final BomToolImportWsDTO importWsDTO) {
        validate(importWsDTO, "bomToolImport", bomToolImportDTOValidator);
        BomToolImportData importData = getDataMapper().map(importWsDTO, BomToolImportData.class);
        distBomToolFacade.saveCartTimestamp(importData);
        return importData.getFileName();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/delete-file", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "deleteBomFile", summary = "Delete BOM file", description = "Deletes BOM file by name and returns list of saved bom files.")
    @ApiBaseSiteIdAndUserIdParam
    public ResponseEntity<List<String>> deleteBomFile(@Parameter(description = "File name", required = true) @RequestParam final String fileName) {
        if (distBomToolFacade.deleteBomFile(fileName)) {
            return ResponseEntity.status(HttpStatus.OK).body(getSavedBomToolFiles());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/copyFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "copyBomFile", summary = "Copy BOM file", description = "Copies BOM file by name and returns list of saved bom files.")
    @ApiBaseSiteIdAndUserIdParam
    public List<String> copyBomFile(@Parameter(description = "File name", required = true) @RequestParam final String fileName) {
        distBomToolFacade.copyBomFile(fileName);
        return getSavedBomToolFiles();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/delete-entry", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "deleteBomEntry", summary = "Delete BOM entry", description = "Deletes imported BOM entry by product code and file name.")
    @ApiBaseSiteIdAndUserIdParam
    public void deleteBOMEntry(@Parameter(description = "Product code", required = true) @RequestParam final String productCode,
                               @Parameter(description = "File name", required = true) @RequestParam final String fileName) {
        distBomToolFacade.deleteBomImportEntry(productCode, fileName);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/rename-file/{currentName:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "renameBOMFile", summary = "Rename BOM file", description = "Renames imported BOM file and returns its name.")
    @ApiBaseSiteIdAndUserIdParam
    public String renameBOMFile(@Parameter(description = "Current name", required = true) @PathVariable final String currentName,
                                @Parameter(description = "New name", required = true) @RequestParam final String newName) {
        return distBomToolFacade.renameBomImportFile(currentName, newName);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/load-file", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "loadFile", summary = "Load BOM file", description = "Loads BOM file by its name.")
    @ApiBaseSiteIdAndUserIdParam
    public BomToolSearchResultWsDTO loadFile(@Parameter(description = "File name", required = true) @RequestParam final String fileName,
                                             @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws BomToolFileException {
        BomToolSearchResultData bomToolResult;

        try {
            bomToolResult = distBomToolFacade.loadBomFile(fileName);
        } catch (final BomToolFacadeException e) {
            LOG.error("An error occurred during BOM Tool Import.", e);
            String messageKey = getErrorMessageKey(e);
            throw new BomToolFileException(getErrorMessage(messageKey));
        } catch (final Exception e) {
            LOG.error("An error occurred during BOM Tool Import.", e);
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                throw new BomToolFileException(getErrorMessage("import-tool.file.template.error"));
            } else {
                throw new BomToolFileException(getErrorMessage("import-tool.file.error"));
            }
        }

        if (bomToolResult == null) {
            throw new BomToolFileException(getErrorMessage("import-tool.file.nothingToLoad"));
        }

        BomToolSearchResultWsDTO searchResultWsDTO = getDataMapper().map(bomToolResult, BomToolSearchResultWsDTO.class, fields);
        populateErrorMessages(searchResultWsDTO);
        searchResultWsDTO.setCustomerId(getUserService().getCurrentUser().getUid());
        searchResultWsDTO.setLoadFile(Boolean.TRUE);
        searchResultWsDTO.setFileName(fileName);

        return searchResultWsDTO;
    }

    private String getErrorMessage(String messageKey) {
        return getMessageSource().getMessage(messageKey, null, getI18nService().getCurrentLocale());
    }

    private String getErrorMessageKey(BomToolFacadeException e) {
        if (BomToolFacadeException.ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND.equals(e.getErrorSource())) {
            return "import-tool.file.copyPasteSeparatorNotFound";
        } else if (BomToolFacadeException.ErrorSource.FIELD_SEPARATOR_NOT_FOUND.equals(e.getErrorSource())) {
            return "import-tool.file.separatorNotFound";
        } else if (BomToolFacadeException.ErrorSource.FILE_EMPTY.equals(e.getErrorSource())) {
            return "import-tool.file.empty";
        } else if (BomToolFacadeException.ErrorSource.NO_DATA.equals(e.getErrorSource())) {
            return "import-tool.file.noDataFound";
        } else if (BomToolFacadeException.ErrorSource.TOO_MANY_LINES.equals(e.getErrorSource())) {
            return "import-tool.file.limitExceeded";
        } else if (BomToolFacadeException.ErrorSource.CUSTOMER_REFERENCE_FIELD.equals(e.getErrorSource())) {
            return "import-tool.file.customerReferenceToLong";
        } else {
            return "import-tool.file.error";
        }
    }

    private void populateErrorMessages(BomToolSearchResultWsDTO searchResultWsDTO) {
        List<String> errorMessages = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(searchResultWsDTO.getNotMatchingProductCodes())) {
            errorMessages.add(getErrorMessage("import-tool.warning.products.notfound"));
        }

        if (CollectionUtils.isNotEmpty(searchResultWsDTO.getPunchedOutProducts())) {
            String errorMessage = getMessageSource().getMessage("bom.product.error.punchout",
                                                                new String[] { getPunchedOutProductCodes(searchResultWsDTO.getPunchedOutProducts()) },
                                                                getI18nService().getCurrentLocale());
            errorMessages.add(errorMessage);
        }
        searchResultWsDTO.setErrorMessages(errorMessages);
    }

    private String getPunchedOutProductCodes(List<ImportToolMatchingWsDTO> punchedOutProducts) {
        return punchedOutProducts.stream()
                                 .filter(Objects::nonNull)
                                 .filter(result -> result.getProduct() != null)
                                 .map(result -> result.getProduct().getCode())
                                 .collect(Collectors.joining(DistConstants.Punctuation.PIPE));
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/review", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "Review", summary = "Review BOM page", description = "Searches products from uploaded data.")
    @ApiBaseSiteIdAndUserIdParam
    public BomToolSearchResultWsDTO review(@Parameter(description = "Data for BOM tool upload", required = true) @RequestBody final String uploadData,
                                           @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws BomToolFileException {
        BomToolSearchResultData bomToolResult = null;

        if (uploadData != null && uploadData.length() != 0) {
            try {
                bomToolResult = distBomToolFacade.searchProductsFromData(uploadData);
            } catch (final BomToolFacadeException e) {
                LOG.error("An error occurred during BOM Tool Import.", e);
                String messageKey = getErrorMessageKey(e);
                throw new BomToolFileException(getErrorMessage(messageKey));
            } catch (final Exception e) {
                LOG.error("An error occurred during BOM Tool Import.", e);
                if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                    throw new BomToolFileException(getErrorMessage("import-tool.file.template.error"));
                } else {
                    throw new BomToolFileException(getErrorMessage("import-tool.file.error"));
                }
            }
        }
        if (bomToolResult == null) {
            throw new BomToolFileException(getErrorMessage("import-tool.file.nothingToLoad"));
        }

        BomToolSearchResultWsDTO searchResultWsDTO = getDataMapper().map(bomToolResult, BomToolSearchResultWsDTO.class, fields);
        populateErrorMessages(searchResultWsDTO);
        searchResultWsDTO.setFileName(getDefaultBomFilename());
        searchResultWsDTO.setCustomerId(getUserService().getCurrentUser().getUid());

        return searchResultWsDTO;
    }

    private String getDefaultBomFilename() {
        return "BOM uploaded on " + LocalDateTime.now();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/review-file", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(operationId = "Review File", summary = "Review BOM file", description = "Searches products from uploaded file.")
    @ApiBaseSiteIdAndUserIdParam
    public BomToolSearchResultWsDTO reviewFile(@Parameter(description = "Article number position") @RequestParam(required = false) final Integer articleNumberPosition,
                                               @Parameter(description = "Quantity position") @RequestParam(required = false) final Integer quantityPosition,
                                               @Parameter(description = "Reference position") @RequestParam(required = false) final Integer referencePosition,
                                               @Parameter(description = "Ignore first row") @RequestParam(defaultValue = "false", required = false) final boolean ignoreFirstRow,
                                               @Parameter(description = "File name", required = true) @RequestParam final String fileName,
                                               @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws BomToolFileException {

        BomToolSearchResultData bomToolResult = null;

        if (articleNumberPosition == null || quantityPosition == null) {
            throw new RequestParameterException(getErrorMessage("import-tool.file.noColumnsSelected"), RequestParameterException.MISSING);
        }

        boolean fileExists = false;

        try {
            final String savedFileName = distBomToolFacade.generateBomToolFileName(fileName);
            final MediaModel file = mediaService.getMedia(savedFileName);

            if (file != null) {
                fileExists = true;
                String extension = null;

                final int position = fileName.lastIndexOf('.');
                if (position > 0) {
                    extension = fileName.substring(position + 1);
                }

                if (isFileExtensionSupported(extension)) {
                    final Integer nullSafeReferencePosition = Optional.ofNullable(referencePosition).orElse(Integer.MIN_VALUE);
                    bomToolResult = distBomToolFacade.searchProductsFromFile(file, articleNumberPosition, quantityPosition,
                                                                             nullSafeReferencePosition, ignoreFirstRow);
                }
                modelService.remove(file);
            }
        } catch (final BomToolFacadeException e) {
            LOG.error("An error occurred during BOM Tool Import.", e);
            String messageKey = getErrorMessageKey(e);
            throw new BomToolFileException(getErrorMessage(messageKey));
        } catch (final Exception e) {
            LOG.error("An error occurred during BOM Tool Import.", e);
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                throw new BomToolFileException(getErrorMessage("import-tool.file.template.error"));
            } else {
                throw new BomToolFileException(getErrorMessage("import-tool.file.error"));
            }
        }

        if (!fileExists) {
            throw new BomToolFileException(getErrorMessage("import-tool.file.extension.notSupported"));
        }

        if (bomToolResult == null) {
            throw new BomToolFileException(getErrorMessage("import-tool.file.nothingToLoad"));
        }

        BomToolSearchResultWsDTO searchResultWsDTO = getDataMapper().map(bomToolResult, BomToolSearchResultWsDTO.class, fields);
        populateErrorMessages(searchResultWsDTO);
        searchResultWsDTO.setFileName(fileName);

        return searchResultWsDTO;
    }

    private boolean isFileExtensionSupported(String extension) {
        return extension != null && SUPPORTED_EXTENSIONS.contains(extension);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{userId}/matching", method = { RequestMethod.POST, RequestMethod.GET }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    @Operation(operationId = "Matching", summary = "Review matching page", description = "Returns lines from uploaded file.")
    @ApiBaseSiteIdAndUserIdParam
    public List<String[]> matching(@Parameter(description = "File for BOM tool upload", required = true)
                                       @RequestParam("uploadFile") final CommonsMultipartFile uploadFile) throws BomToolFileException {

        List<String[]> bomResult = null;

        boolean fileExists = false;

        long fileSize = getConfigurationService().getConfiguration().getLong("bom.file.allowed.size.limit");
        if (uploadFile != null && uploadFile.getSize() > fileSize) {
            throw new BomToolFileException(getErrorMessage("import-tool.file.sizelimit"));
        }

        try {
            int allowedFileCount = getConfigurationService().getConfiguration().getInt("bom.file.allowedlimit");
            if (distBomToolFacade.getCountOfBomFilesUploadedForCurrentCustomer() >= allowedFileCount) {
                throw new BomToolFileLimitExceededException("Customer has reached limit of number of BOM files allowed");
            }

            if (uploadFile != null && uploadFile.getOriginalFilename() != null && uploadFile.getOriginalFilename().length() > 1) {
                fileExists = true;
                final String originalFilename = uploadFile.getOriginalFilename();
                final String fileName = distBomToolFacade.generateBomToolFileName(originalFilename);

                distBomToolFacade.removeFileIfAlreadyExists(fileName);
                final MediaModel media = distBomToolFacade.saveFileAsMedia(fileName, uploadFile);

                String extension = null;
                final int position = originalFilename.lastIndexOf('.');
                if (position > 0) {
                    extension = originalFilename.substring(position + 1);
                }

                if (isFileExtensionSupported(extension)) {
                    bomResult = distBomToolFacade.getLinesFromFile(media);
                }
            }

        } catch (final BomToolFacadeException e) {
            LOG.error("An error occurred during BOM Tool Import.", e);
            String messageKey = getErrorMessageKey(e);
            throw new BomToolFileException(getErrorMessage(messageKey));

        } catch (final Exception e) {
            LOG.error("An error occurred during BOM Tool Import.", e);
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                throw new BomToolFileException(getErrorMessage("import-tool.file.template.error"));
            } else if (e instanceof BomToolFileLimitExceededException) {
                throw new BomToolFileException(getErrorMessage("import-tool.file.limitexhausted"));
            } else {
                throw new BomToolFileException(getErrorMessage("import-tool.file.error"));
            }
        }

        if (!fileExists) {
            throw new BomToolFileException(getErrorMessage("import-tool.file.extension.notSupported"));
        }

        if (bomResult == null) {
            throw new BomToolFileException(getErrorMessage("import-tool.file.nothingToLoad"));
        }

        return bomResult;
    }

}
