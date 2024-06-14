package com.distrelec.smartedit.cmsitems.validator;

import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.validator.DefaultCMSLinkComponentValidator;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import org.apache.commons.lang.StringUtils;

import java.util.stream.Stream;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static java.util.Objects.isNull;

public class DistrelecCMSLinkComponentValidator extends DefaultCMSLinkComponentValidator {

    private static final String LINK_TO = "linkTo";

    @Override
    protected void verifyOnlyOneTypeProvided(CMSLinkComponentModel target) {
        final long count = Stream
                .of(target.getCategory(), target.getContentPage(), target.getProduct(), StringUtils.isBlank(target.getLocalizedUrl()) ? null : target.getLocalizedUrl())
                .filter(item -> !isNull(item)).count();
        if (count > 1)
        {
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.CONTENTPAGE)
                            .errorCode(CmsfacadesConstants.LINK_ITEMS_EXCEEDED) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.PRODUCT)
                            .errorCode(CmsfacadesConstants.LINK_ITEMS_EXCEEDED) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.CATEGORY)
                            .errorCode(CmsfacadesConstants.LINK_ITEMS_EXCEEDED) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.LOCALIZEDURL)
                            .errorCode(CmsfacadesConstants.LINK_ITEMS_EXCEEDED) //
                            .build()
            );
        }
        else if (count < 1)
        {
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(LINK_TO)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.CONTENTPAGE)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.PRODUCT)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.CATEGORY)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
            getValidationErrorsProvider().getCurrentValidationErrors().add(
                    newValidationErrorBuilder() //
                            .field(CMSLinkComponentModel.LOCALIZEDURL)
                            .errorCode(CmsfacadesConstants.LINK_MISSING_ITEMS) //
                            .build()
            );
        }
    }
}
