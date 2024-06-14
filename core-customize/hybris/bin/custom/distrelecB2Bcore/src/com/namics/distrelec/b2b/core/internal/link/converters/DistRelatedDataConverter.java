package com.namics.distrelec.b2b.core.internal.link.converters;

import com.namics.distrelec.b2b.core.message.queue.model.CIValue;
import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.message.queue.model.RelatedDataType;
import com.namics.distrelec.b2b.core.model.internal.link.DistInternalValueModel;
import com.namics.distrelec.b2b.core.model.internal.link.DistRelatedDataModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;

public class DistRelatedDataConverter extends AbstractPopulatingConverter<DistRelatedDataModel, CRelatedData> {

    @Autowired
    private Converter<DistInternalValueModel, CIValue> distInternalValueConverter;

    @Override
    public void populate(DistRelatedDataModel source, CRelatedData target) throws ConversionException {
        target.setType(RelatedDataType.findByCode(source.getType()));
        target.setValues(Converters.convertAll(source.getValues(), getDistInternalValueConverter()));
    }

    public Converter<DistInternalValueModel, CIValue> getDistInternalValueConverter() {
        return distInternalValueConverter;
    }

    public void setDistInternalValueConverter(final Converter<DistInternalValueModel, CIValue> distInternalValueConverter) {
        this.distInternalValueConverter = distInternalValueConverter;
    }
}
