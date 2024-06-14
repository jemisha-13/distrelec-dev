package com.namics.distrelec.b2b.core.internal.link.converters;

import com.namics.distrelec.b2b.core.message.queue.model.CIValue;
import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.model.internal.link.DistInternalValueModel;
import com.namics.distrelec.b2b.core.model.internal.link.DistRelatedDataModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class DistRelatedDataReverseConverter extends AbstractPopulatingConverter<CRelatedData, DistRelatedDataModel> {

    @Autowired
    private Converter<CIValue, DistInternalValueModel> distInternalValueReverseConverter;

    @Override
    public void populate(CRelatedData source, DistRelatedDataModel target) throws ConversionException {
        target.setType(source.getType() != null ? source.getType().getCode() : null);
        if (source.getValues() != null) {
            populateValues(source, target);
        }
    }

    private void populateValues(CRelatedData source, DistRelatedDataModel target) {
        Set<DistInternalValueModel> internalValues = new HashSet<>();
        for (CIValue ciValue : source.getValues()) {
            DistInternalValueModel distInternalValue = getDistInternalValueReverseConverter().convert(ciValue);
            distInternalValue.setRelatedData(target);
            internalValues.add(distInternalValue);
        }
        target.setValues(internalValues);
    }

    public Converter<CIValue, DistInternalValueModel> getDistInternalValueReverseConverter() {
        return distInternalValueReverseConverter;
    }

    public void setDistInternalValueReverseConverter(final Converter<CIValue, DistInternalValueModel> distInternalValueReverseConverter) {
        this.distInternalValueReverseConverter = distInternalValueReverseConverter;
    }
}
