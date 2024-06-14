/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.validator;

import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.converters.AbstractLocalizedErrorConverter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toList;

/**
 * Validation Converter for {@link ValidationErrors} type.
 */
public class ValidationErrorConverter extends AbstractLocalizedErrorConverter {
    private static final String TYPE = "ValidationError";
    private static final String SUBJECT_TYPE = "parameter";
    private static final String REASON_INVALID = "invalid";
    private static final String REASON_MISSING = "missing";

    private I18NService i18NService;

    public boolean supports(final Class clazz) {
        return ValidationErrors.class.isAssignableFrom(clazz);
    }

    public void populate(final Object o, final List<ErrorWsDTO> webserviceErrorList) {
        final ValidationErrors errors = (ValidationErrors) o;
        final Locale currentLocale = getCurrentLocale();

        webserviceErrorList.addAll(
                errors.getValidationErrors().stream().map(validationError -> {
                    ErrorWsDTO errorDto = this.createTargetElement();
                    errorDto.setType(TYPE);
                    errorDto.setSubjectType(SUBJECT_TYPE);
                    errorDto.setMessage(this.getMessage(validationError.getErrorCode(), validationError.getErrorArgs(), currentLocale, validationError.getDefaultMessage()));
                    errorDto.setSubject(validationError.getField());
                    errorDto.setReason(validationError.getRejectedValue() == null ? REASON_MISSING : REASON_INVALID);
                    errorDto.setLanguage(validationError.getLanguage());
                    errorDto.setPosition(validationError.getPosition());
                    errorDto.setExceptionMessage(validationError.getExceptionMessage());
                    errorDto.setErrorCode(validationError.getErrorCode());
                    return errorDto;
                }).collect(toList()));
    }

    private Locale getCurrentLocale() {
        return Locale.ENGLISH;
    }

    protected I18NService getI18NService() {
        return i18NService;
    }

    @Required
    public void setI18NService(final I18NService i18NService) {
        this.i18NService = i18NService;
    }
}
