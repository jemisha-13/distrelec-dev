package com.distrelec.smartedit.cmsfacades.users.services.impl;

import de.hybris.platform.cmsfacades.users.services.impl.DefaultCMSUserService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class DefaultDistCMSUserService extends DefaultCMSUserService {

    @Override
    protected Set<String> getLanguagesForUser(final UserModel userModel, final Function<UserGroupModel, Collection<LanguageModel>> languagesRetrievalFn) {
        return getAllSupportedLanguages();
    }
}
