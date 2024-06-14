package com.distrelec.smartedit.converter;

import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import de.hybris.platform.cms2.common.functions.Converter;

public class DistVideoMediaToStringConverter implements Converter<DistVideoMediaModel, String> {

    @Override
    public String convert(DistVideoMediaModel videoMediaModel) {
        return videoMediaModel.getCode();
    }

}
