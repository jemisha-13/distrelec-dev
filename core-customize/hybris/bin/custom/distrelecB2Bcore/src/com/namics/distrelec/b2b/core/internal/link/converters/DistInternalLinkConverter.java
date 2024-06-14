package com.namics.distrelec.b2b.core.internal.link.converters;

import com.namics.distrelec.b2b.core.message.queue.model.CInternalLinkData;
import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import com.namics.distrelec.b2b.core.model.internal.link.DistInternalLinkModel;
import com.namics.distrelec.b2b.core.model.internal.link.DistRelatedDataModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class DistInternalLinkConverter extends AbstractPopulatingConverter<DistInternalLinkModel, CInternalLinkData> {

    @Autowired
    private Converter<DistRelatedDataModel, CRelatedData> distRelatedDataConverter;

    @Override
    public void populate(DistInternalLinkModel source, CInternalLinkData target) throws ConversionException {
        target.setCode(source.getCode());
        target.setSite(source.getSite());
        target.setType(RowType.findByCode(source.getType()));
        target.setTimestamp(source.getTimestamp());
        populateDatas(source, target);
    }

    private void populateDatas(DistInternalLinkModel source, CInternalLinkData target) {
        String language = target.getLanguage();
        if (language != null && source.getDatas() != null) {
            List<CRelatedData> relatedDataList = new ArrayList<>();
            for (DistRelatedDataModel relatedData : source.getDatas()) {
                if (language.equals(relatedData.getLanguage())) {
                    relatedDataList.add(getDistRelatedDataConverter().convert(relatedData));
                }
            }
            target.setDatas(relatedDataList);
        }
    }

    public Converter<DistRelatedDataModel, CRelatedData> getDistRelatedDataConverter() {
        return distRelatedDataConverter;
    }

    public void setDistRelatedDataConverter(final Converter<DistRelatedDataModel, CRelatedData> distRelatedDataConverter) {
        this.distRelatedDataConverter = distRelatedDataConverter;
    }
}
