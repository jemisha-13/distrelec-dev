package com.namics.distrelec.b2b.core.internal.link.converters;

import com.namics.distrelec.b2b.core.message.queue.model.CInternalLinkData;
import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.model.internal.link.DistInternalLinkModel;
import com.namics.distrelec.b2b.core.model.internal.link.DistRelatedDataModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DistInternalLinkReverseConverter extends AbstractPopulatingConverter<CInternalLinkData, DistInternalLinkModel> {

    private static final String CLUSTER_NODE = "cluster.id";

    @Autowired
    private Converter<CRelatedData, DistRelatedDataModel> distRelatedDataReverseConverter;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void populate(CInternalLinkData source, DistInternalLinkModel target) throws ConversionException {
        target.setCode(source.getCode());
        target.setSite(source.getSite());
        target.setType(source.getType() != null ? source.getType().getCode() : null);
        target.setTimestamp(source.getTimestamp());
        target.setWorker(getClusterId());
        target.setWorkerTimestamp(new Date());
        if (source.getDatas() != null) {
            populateRelatedData(source, target);
        }
    }

    private void populateRelatedData(CInternalLinkData source, DistInternalLinkModel target) {
        Set<DistRelatedDataModel> relatedDataList = new HashSet<>();
        for (CRelatedData relatedData : source.getDatas()) {
            DistRelatedDataModel relatedDataModel = getDistRelatedDataReverseConverter().convert(relatedData);
            relatedDataModel.setLanguage(source.getLanguage());
            relatedDataModel.setInternalLink(target);
            relatedDataList.add(relatedDataModel);
        }
        target.setDatas(relatedDataList);
    }

    public int getClusterId() {
        return configurationService.getConfiguration().getInt(CLUSTER_NODE);
    }

    public Converter<CRelatedData, DistRelatedDataModel> getDistRelatedDataReverseConverter() {
        return distRelatedDataReverseConverter;
    }

    public void setDistRelatedDataReverseConverter(final Converter<CRelatedData, DistRelatedDataModel> distRelatedDataReverseConverter) {
        this.distRelatedDataReverseConverter = distRelatedDataReverseConverter;
    }
}
