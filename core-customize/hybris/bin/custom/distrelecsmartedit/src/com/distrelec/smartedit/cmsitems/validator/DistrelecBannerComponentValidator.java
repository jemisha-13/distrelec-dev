package com.distrelec.smartedit.cmsitems.validator;

import com.namics.distrelec.b2b.core.model.cms2.components.DistBannerComponentModel;
import de.hybris.platform.cms2lib.model.components.BannerComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.validator.DefaultBannerComponentValidator;

public class DistrelecBannerComponentValidator extends DefaultBannerComponentValidator {

    @Override
    public void validate(BannerComponentModel validatee) {
        if (validatee instanceof DistBannerComponentModel) {
            validateField((languageData) -> validatee.getMedia(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode())),
                    BannerComponentModel.MEDIA);
        } else {
            super.validate(validatee);
        }
    }
}