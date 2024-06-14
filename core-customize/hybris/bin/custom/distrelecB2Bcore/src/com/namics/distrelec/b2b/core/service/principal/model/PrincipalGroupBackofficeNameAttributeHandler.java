package com.namics.distrelec.b2b.core.service.principal.model;

import java.util.Locale;

import com.namics.distrelec.b2b.core.model.AbstractDynamicLocalizedAttributeHandler;
import de.hybris.platform.core.model.security.PrincipalGroupModel;

public class PrincipalGroupBackofficeNameAttributeHandler extends AbstractDynamicLocalizedAttributeHandler<String, PrincipalGroupModel> {

    @Override
    public String get(PrincipalGroupModel model) {
        return get(model, null);
    }

    @Override
    public String get(PrincipalGroupModel principalGroup, Locale loc) {
        String locName = principalGroup.getLocName(loc);

        if (locName != null) {
            return locName;
        } else {
            return principalGroup.getName();
        }
    }
}
