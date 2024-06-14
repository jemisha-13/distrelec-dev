package com.distrelec.smartedit.cmsitems.validator;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CONTAINS_INVALID_CHARS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_LENGTH_EXCEEDED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.NO_RESTRICTION_SET_FOR_VARIATION_PAGE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmsfacades.cmsitems.validator.DefaultBaseAbstractPageValidator;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;

public class DistrelecBaseAbstractPageValidator extends DefaultBaseAbstractPageValidator {

    @Override
    public void validate(final AbstractPageModel validatee) {
        if (Strings.isBlank(validatee.getUid())) {
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                                                .field(AbstractPageModel.UID) //
                                                .errorCode(FIELD_REQUIRED) //
                                                .build()
            );
        } else if (!getOnlyHasSupportedCharactersPredicate().test(validatee.getUid())) {
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                                                .field(AbstractPageModel.UID) //
                                                .errorCode(FIELD_CONTAINS_INVALID_CHARS) //
                                                .build()
            );
        }

        if (!isCategoryOrProductPage(validatee)) {
            getLanguageFacade().getLanguages().stream() //
                               .filter(LanguageData::isRequired) //
                               .forEach(languageData -> {
                                   if (isBlank(validatee.getTitle(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode())))) {
                                       getValidationErrorsProvider().getCurrentValidationErrors().add(
                                               newValidationErrorBuilder() //
                                                                           .field(AbstractPageModel.TITLE) //
                                                                           .language(languageData.getIsocode())
                                                                           .errorCode(FIELD_REQUIRED_L10N) //
                                                                           .errorArgs(new Object[] { languageData.getIsocode() }) //
                                                                           .build()
                                       );
                                   }
                               });
        }

        getLanguageFacade().getLanguages().stream() //
                           .forEach(languageData -> {
                               final String title = validatee.getTitle(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode()));
                               if (isNotBlank(title) && !getValidStringLengthPredicate().test(title)) {
                                   getValidationErrorsProvider().getCurrentValidationErrors().add(
                                           newValidationErrorBuilder() //
                                                                       .field(AbstractPageModel.TITLE) //
                                                                       .language(languageData.getIsocode())
                                                                       .errorCode(FIELD_LENGTH_EXCEEDED) //
                                                                       .rejectedValue(title)
                                                                       .build()
                                   );
                               }
                           });

        getLanguageFacade().getLanguages().stream() //
                           .forEach(languageData -> {
                               final String description = validatee.getDescription(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode()));
                               if (isNotBlank(description) && !getValidStringLengthPredicate().test(description)) {
                                   getValidationErrorsProvider().getCurrentValidationErrors().add(
                                           newValidationErrorBuilder() //
                                                                       .field(AbstractPageModel.DESCRIPTION) //
                                                                       .language(languageData.getIsocode())
                                                                       .errorCode(FIELD_LENGTH_EXCEEDED) //
                                                                       .rejectedValue(description)
                                                                       .build()
                                   );
                               }
                           });

        if (!validatee.getDefaultPage() && CollectionUtils.isEmpty(validatee.getRestrictions())) {
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                                                .field(AbstractPageModel.RESTRICTIONS) //
                                                .errorCode(NO_RESTRICTION_SET_FOR_VARIATION_PAGE) //
                                                .build()
            );
        }
    }

    private boolean isCategoryOrProductPage(final AbstractPageModel validatee) {
        return validatee instanceof CategoryPageModel || validatee instanceof ProductPageModel;
    }
    
}
