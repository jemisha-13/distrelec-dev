package com.namics.distrelec.b2b.core.service.i18n.impl;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;

public interface DistCommerceCommonI18NService extends CommerceCommonI18NService {

    LanguageModel getBaseLanguage(LanguageModel language);

}
