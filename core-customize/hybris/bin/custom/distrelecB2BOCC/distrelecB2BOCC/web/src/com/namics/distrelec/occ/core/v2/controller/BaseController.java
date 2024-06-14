/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Product.PRODUCT_CODE_REGEX;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.namics.distrelec.occ.core.swagger.CommonQueryParams;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;
import com.namics.distrelec.b2b.core.bomtool.BomToolFileLimitExceededException;
import com.namics.distrelec.b2b.core.bomtool.BomToolServiceException;
import com.namics.distrelec.b2b.core.inout.erp.exception.MoqConversionException;
import com.namics.distrelec.b2b.core.inout.erp.exception.ProductStatusMisalignmentException;
import com.namics.distrelec.b2b.core.inout.erp.exception.TemporaryQualityBlockException;
import com.namics.distrelec.b2b.core.service.order.exceptions.CustomerBlockedInErpException;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.occ.core.cart.impl.CommerceWebServicesCartFacade;
import com.namics.distrelec.occ.core.handler.ProductNotFoundException;

/**
 * Base Controller. It defines the exception handler to be used by all controllers. Extending controllers can add or
 * overwrite the exception
 * handler if needed.
 */
@Controller
@CommonQueryParams
public class BaseController {
    protected static final String DEFAULT_CURRENT_PAGE = "0";

    protected static final String BASIC_FIELD_SET = FieldSetLevelHelper.BASIC_LEVEL;

    protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;

    protected static final String FULL_FIELD_SET = FieldSetLevelHelper.FULL_LEVEL;

    protected static final String HEADER_TOTAL_COUNT = "X-Total-Count";

    protected static final String INVALID_REQUEST_BODY_ERROR_MESSAGE = "Request body is invalid or missing";

    protected static final int DEFAULT_SEARCH_MAX_SIZE = 100;

    private static final String DEFAULT_PAGE_SIZE_PROPERTY = "default.page.size";

    private static final int DEFAULT_PAGE_SIZE = 50;

    private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected DistUserFacade userFacade;

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private DistCustomerFacade b2bCustomerFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private DistUserService userService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private CommerceWebServicesCartFacade distCommerceWebServicesCartFacade;

    protected static String sanitize(final String input) {
        return YSanitizer.sanitize(input);
    }

    protected boolean isShowDoubleOptInPopup() {
        return isTrue(isConsentConfirmationRequired());
    }

    private boolean isConsentConfirmationRequired() {
        boolean isConfirmationRequired = false;
        try {
            final String[] baseSites = getConfigurationService().getConfiguration()
                                                                .getString("ymkt.customer.sevice.consent.confirmation.required.shop")
                                                                .split(",");
            final UserModel currentUser = getUserService().getCurrentUser();
            final boolean isAnonymous = getUserService().isAnonymousUser(currentUser);
            if (baseSites.length > 0) {
                if (!isAnonymous && currentUser instanceof B2BCustomerModel) {
                    final B2BCustomerModel customer = (B2BCustomerModel) currentUser;
                    final String customerBaseSite = (null != customer.getCustomersBaseSite())
                                                                                              ? customer.getCustomersBaseSite()
                                                                                                        .getUid()
                                                                                              : "";
                    isConfirmationRequired = (null != getCurrentBaseSiteUid())
                                                                               ? Arrays.asList(baseSites).contains(getCurrentBaseSiteUid())
                                                                               : Arrays.asList(baseSites).contains(customerBaseSite);
                } else {
                    isConfirmationRequired = Arrays.asList(baseSites).contains(getCurrentBaseSiteUid());
                }
            }
        } catch (final Exception ex) {
            LOG.info("Exception in checking confirmation flag", ex);
        }
        return isConfirmationRequired;
    }

    private String getCurrentBaseSiteUid() {
        if (getBaseSiteService() != null && getBaseSiteService().getCurrentBaseSite() != null) {
            return getBaseSiteService().getCurrentBaseSite().getUid();
        }
        LOG.warn("no current base site available");
        return null;
    }

    /**
     * Adds pagination field to the 'fields' parameter
     *
     * @param fields
     * @return fields with pagination
     */
    protected String addPaginationField(final String fields) {
        String fieldsWithPagination = fields;

        if (StringUtils.isNotBlank(fieldsWithPagination)) {
            fieldsWithPagination += ",";
        }
        fieldsWithPagination += "pagination";

        return fieldsWithPagination;
    }

    protected void setTotalCountHeader(final HttpServletResponse response, final PaginationWsDTO paginationDto) {
        if (paginationDto != null && paginationDto.getTotalResults() != null) {
            response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalResults()));
        }
    }

    protected void setTotalCountHeader(final HttpServletResponse response, final PaginationData paginationDto) {
        if (paginationDto != null) {
            response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalNumberOfResults()));
        }
    }

    protected void validate(final Object object, final String objectName, final Validator validator) {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
    }

    protected Errors validateObject(final Object object, final String objectName, final Validator validator) {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
        return errors;
    }

    protected String normalizeProductCode(final String code) {
        if (isNotBlank(code) && !code.contains("it.code")) {
            final String cleanedCode = code.replaceAll("-", "");
            if (cleanedCode.matches(PRODUCT_CODE_REGEX)) {
                return cleanedCode;
            }
        }
        return null;
    }

    protected int getPageSizeOrDefault(final int pageSize) {
        if (pageSize <= 0) {
            return getConfigurationService().getConfiguration().getInt(DEFAULT_PAGE_SIZE_PROPERTY, DEFAULT_PAGE_SIZE);
        }
        return pageSize;
    }

    protected CartData getSessionCart() {
        return distCommerceWebServicesCartFacade.getSessionCart();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({ ModelNotFoundException.class })
    public ErrorListWsDTO handleModelNotFoundException(final Exception ex) {
        String warnMsg = String.format("Handling Exception for this request - {} - {}", ex.getClass().getSimpleName(), sanitize(ex.getMessage()));
        LOG.warn(warnMsg, ex);

        return handleErrorInternal(UnknownIdentifierException.class.getSimpleName(), ex.getMessage());
    }

    protected ErrorListWsDTO handleErrorInternal(final String type, final String message) {
        final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
        final ErrorWsDTO error = new ErrorWsDTO();
        error.setType(type.replace("Exception", "Error"));
        error.setMessage(sanitize(message));
        errorListDto.setErrors(Lists.newArrayList(error));
        return errorListDto;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({ DuplicateUidException.class })
    public ErrorListWsDTO handleDuplicateUidException(final DuplicateUidException ex) {
        LOG.warn("DuplicateUidException", ex);
        return handleErrorInternal("DuplicateUidException", ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({ HttpMessageNotReadableException.class })
    public ErrorListWsDTO handleHttpMessageNotReadableException(final Exception ex) {
        LOG.warn(INVALID_REQUEST_BODY_ERROR_MESSAGE, ex);
        return handleErrorInternal(HttpMessageNotReadableException.class.getSimpleName(), INVALID_REQUEST_BODY_ERROR_MESSAGE);
    }

    @ResponseBody
    @ExceptionHandler({ CalculationException.class })
    public CartWsDTO handleCalculationException(final Exception ex) {
        final CartWsDTO cartWsDTO = getDataMapper().map(getSessionCart(), CartWsDTO.class, DEFAULT_FIELD_SET);
        cartWsDTO.setCalculationFailed(Boolean.TRUE);
        return cartWsDTO;
    }

    @ResponseBody
    @ExceptionHandler({ ProductStatusMisalignmentException.class })
    public CartWsDTO handleProductStatusMisalignmentException(final ProductStatusMisalignmentException ex) {
        final CartWsDTO cartWsDTO = getDataMapper().map(getSessionCart(), CartWsDTO.class, DEFAULT_FIELD_SET);
        cartWsDTO.setProductCodeMisalignment(ex.getMessage());
        return cartWsDTO;
    }

    @ResponseBody
    @ExceptionHandler({ TemporaryQualityBlockException.class })
    public CartWsDTO handleTemporaryQualityBlockException(final TemporaryQualityBlockException ex) {
        final CartWsDTO cartWsDTO = getDataMapper().map(getSessionCart(), CartWsDTO.class, DEFAULT_FIELD_SET);
        cartWsDTO.setProductCodeMisalignment(ex.getMessage());
        return cartWsDTO;
    }

    @ResponseBody
    @ExceptionHandler({ MoqConversionException.class })
    public CartWsDTO handleMoqConversionException(final MoqConversionException ex) {
        final CartWsDTO cartWsDTO = getDataMapper().map(getSessionCart(), CartWsDTO.class, DEFAULT_FIELD_SET);
        cartWsDTO.setProductCodeMisalignment(ex.getCode());
        cartWsDTO.setMoq(ex.getMoq());
        return cartWsDTO;
    }

    @ResponseBody
    @ExceptionHandler({ CustomerBlockedInErpException.class })
    public CartWsDTO handleCustomerBlockedException(final Exception ex) {
        final CartWsDTO cartWsDTO = getDataMapper().map(getSessionCart(), CartWsDTO.class, DEFAULT_FIELD_SET);
        cartWsDTO.setCustomerBlockedInErp(Boolean.TRUE);
        return cartWsDTO;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({ VoucherOperationException.class })
    public ErrorListWsDTO handleVoucherOperationException(final Exception ex) {
        LOG.warn(INVALID_REQUEST_BODY_ERROR_MESSAGE, ex);
        return handleErrorInternal("VoucherOperationException", ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
    @ResponseBody
    @ExceptionHandler({ BomToolFileLimitExceededException.class })
    public ErrorListWsDTO handleBomToolFileLimitExceededException(final Exception ex) {
        LOG.warn("An exception occurred!", ex);
        return handleErrorInternal(BomToolFileLimitExceededException.class.getSimpleName(), ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler({ BomToolServiceException.class })
    public ErrorListWsDTO handleBomToolServiceException(final Exception ex) {
        LOG.warn("An exception occurred!", ex);
        return handleErrorInternal(BomToolServiceException.class.getSimpleName(), ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler({ ProductNotFoundException.class })
    public ErrorListWsDTO handleProductNotFoundException(final Exception ex) {
        LOG.warn("An exception occurred!", ex);
        return handleErrorInternal(ProductNotFoundException.class.getSimpleName(), ex.getMessage());
    }

    protected DataMapper getDataMapper() {
        return dataMapper;
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    protected I18NService getI18nService() {
        return i18nService;
    }

    protected DistUserFacade getUserFacade() {
        return userFacade;
    }

    protected DistCustomerFacade getB2bCustomerFacade() {
        return b2bCustomerFacade;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    protected DistUserService getUserService() {
        return userService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public CommerceWebServicesCartFacade getDistCommerceWebServicesCartFacade() {
        return distCommerceWebServicesCartFacade;
    }
}
