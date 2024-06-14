package com.namics.distrelec.b2b.core.internal.link.service.impl;

import com.namics.distrelec.b2b.core.internal.link.dao.DistInternalLinkDao;
import com.namics.distrelec.b2b.core.internal.link.service.DistInternalLinkService;
import com.namics.distrelec.b2b.core.message.queue.model.CInternalLinkData;
import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.model.internal.link.DistInternalLinkModel;
import com.namics.distrelec.b2b.core.model.internal.link.DistRelatedDataModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.converters.Converters;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DefaultDistInternalLinkService implements DistInternalLinkService {

    @Autowired
    private DistInternalLinkDao distInternalLinkDao;

    @Autowired
    private Converter<DistInternalLinkModel, CInternalLinkData> distInternalLinkConverter;

    @Autowired
    private Converter<CInternalLinkData, DistInternalLinkModel> distInternalLinkReverseConverter;

    @Autowired
    private Converter<CRelatedData, DistRelatedDataModel> distRelatedDataReverseConverter;

    @Override
    public CInternalLinkData findInternalLink(String code, String site, String type, String language) {
        DistInternalLinkModel internalLink = getDistInternalLinkDao().findInternalLinkByLanguage(code, site, type, language);
        if (internalLink != null) {
            CInternalLinkData internalLinkData = new CInternalLinkData();
            internalLinkData.setLanguage(language);
            return getDistInternalLinkConverter().convert(internalLink, internalLinkData);
        }
        return null;
    }

    @Override
    public List<CInternalLinkData> findInternalLinks(String code, String site, String type) {
        List<CInternalLinkData> internalLinks = new ArrayList<>();
        DistInternalLinkModel internalLink = getDistInternalLinkDao().findInternalLink(code, site, type);
        if (internalLink != null && internalLink.getDatas() != null) {
            for (DistRelatedDataModel relatedData : internalLink.getDatas()) {
                CInternalLinkData internalLinkData = new CInternalLinkData();
                internalLinkData.setLanguage(relatedData.getLanguage());
                internalLinks.add(getDistInternalLinkConverter().convert(internalLink, internalLinkData));
            }
        }
        return internalLinks;
    }

    @Override
    public boolean createInternalLink(CInternalLinkData iLinkData) {
        DistInternalLinkModel storedLink = findInternalLink(iLinkData);
        if (storedLink != null) {
            return true;
        }
        DistInternalLinkModel internalLink = getDistInternalLinkReverseConverter().convert(iLinkData);
        return getDistInternalLinkDao().createInternalLink(internalLink);
    }

    @Override
    public void updateInternalLink(CInternalLinkData iLinkData) {
        DistInternalLinkModel storedLink = findInternalLink(iLinkData);

        String language = iLinkData.getLanguage();
        List<DistRelatedDataModel> relatedDataList = Converters.convertAll(iLinkData.getDatas(), getDistRelatedDataReverseConverter());
        for (DistRelatedDataModel relatedData : relatedDataList) {
            relatedData.setLanguage(language);
            relatedData.setInternalLink(storedLink);
        }
        getDistInternalLinkDao().updateInternalLink(storedLink, new HashSet<>(relatedDataList), language);
    }

    @Override
    public boolean lockInternalLink(CInternalLinkData iLinkData) {
        DistInternalLinkModel storedLink = findInternalLink(iLinkData);
        return getDistInternalLinkDao().lockInternalLink(storedLink);
    }

    @Override
    public boolean unlockInternalLink(CInternalLinkData iLinkData) {
        DistInternalLinkModel storedLink = findInternalLink(iLinkData);
        return getDistInternalLinkDao().unlockInternalLink(storedLink);
    }

    protected DistInternalLinkModel findInternalLink(CInternalLinkData iLinkData) {
        String type = iLinkData.getType() != null ? iLinkData.getType().getCode() : null;
        return getDistInternalLinkDao().findInternalLink(iLinkData.getCode(), iLinkData.getSite(), type);
    }

    public DistInternalLinkDao getDistInternalLinkDao() {
        return distInternalLinkDao;
    }

    public void setDistInternalLinkDao(final DistInternalLinkDao distInternalLinkDao) {
        this.distInternalLinkDao = distInternalLinkDao;
    }

    public Converter<DistInternalLinkModel, CInternalLinkData> getDistInternalLinkConverter() {
        return distInternalLinkConverter;
    }

    public void setDistInternalLinkConverter(final Converter<DistInternalLinkModel, CInternalLinkData> distInternalLinkConverter) {
        this.distInternalLinkConverter = distInternalLinkConverter;
    }

    public Converter<CInternalLinkData, DistInternalLinkModel> getDistInternalLinkReverseConverter() {
        return distInternalLinkReverseConverter;
    }

    public void setDistInternalLinkReverseConverter(final Converter<CInternalLinkData, DistInternalLinkModel> distInternalLinkReverseConverter) {
        this.distInternalLinkReverseConverter = distInternalLinkReverseConverter;
    }

    public Converter<CRelatedData, DistRelatedDataModel> getDistRelatedDataReverseConverter() {
        return distRelatedDataReverseConverter;
    }

    public void setDistRelatedDataReverseConverter(final Converter<CRelatedData, DistRelatedDataModel> distRelatedDataReverseConverter) {
        this.distRelatedDataReverseConverter = distRelatedDataReverseConverter;
    }
}
