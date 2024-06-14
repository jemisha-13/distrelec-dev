/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistDocumentTypeModel;
import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;
import com.namics.distrelec.b2b.facades.product.data.DistDownloadData;
import com.namics.distrelec.b2b.facades.product.data.DistDownloadSectionData;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

public class DistDownloadMediaPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {
    private static final String LANG_EN = "en";

    private static final Logger LOG = LogManager.getLogger(DistDownloadMediaPopulator.class);

    @Autowired
    @Qualifier("distDownloadMediaConverter")
    private Converter<DistDownloadMediaModel, DistDownloadData> distDownloadMediaConverter;

    @Autowired
    @Qualifier("distDocumentTypeConverter")
    private Converter<DistDocumentTypeModel, DistDownloadSectionData> distDocumentTypeConverter;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Override
    public void populate(final SOURCE source, final TARGET target) {
        if (CollectionUtils.isNotEmpty(source.getDownloadMedias())) {
            final Map<Integer, DistDownloadSectionData> downloads = new TreeMap<>();
            final Collection<DistDownloadMediaModel> downloadMedias = source.getDownloadMedias();
            for (final DistDownloadMediaModel downloadMedia : downloadMedias) {
                final Integer rank = downloadMedia.getDocumentType().getRank();
                DistDownloadSectionData downloadSection = downloads.get(rank);
                if (downloadSection == null) {
                    downloadSection = distDocumentTypeConverter.convert(downloadMedia.getDocumentType());
                    assert downloadSection != null;
                    downloadSection.setDownloads(new ArrayList<>());
                }

                final DistDownloadData downloadData = distDownloadMediaConverter.convert(downloadMedia);
                if ((downloadMedia.getLanguages() != null && downloadMedia.getLanguages().contains(commonI18NService.getCurrentLanguage()))
                        || downloadMedia.getLanguages() == null || downloadMedia.getLanguages().isEmpty()) {
                    // Download media is of current language OR language independent (zxx)
                    downloadSection.getDownloads().add(downloadData);
                } else {
                    List<DistDownloadData> downloadDatas = downloadSection.getAlternativeDownloads();
                    if (downloadDatas == null) {
                        downloadDatas = new ArrayList<>();
                    }
                    downloadDatas.add(downloadData);
                    downloadSection.setAlternativeDownloads(downloadDatas);
                }

                downloads.put(downloadSection.getRank(), downloadSection);
            }

            // Sort downloads
            final List<DistDownloadSectionData> downloadsList = new ArrayList<>(downloads.values());
            for (final DistDownloadSectionData downloadSection : downloadsList) {
                sortDownloadsByLanguage(downloadSection.getAlternativeDownloads());
                orderDownloadsByLang(downloadSection.getDownloads(), downloadSection.getAlternativeDownloads(), LANG_EN);
                sortDownloadsByLanguage(downloadSection.getDownloads());
            }

            target.setDownloads(downloadsList);
        }
    }

    protected void orderDownloadsByLang(final List<DistDownloadData> downloads, final List<DistDownloadData> alternativeDownloads, final String lang) {
        if (CollectionUtils.isEmpty(downloads) && CollectionUtils.isNotEmpty(alternativeDownloads)) {
            final Iterator<DistDownloadData> iterator = alternativeDownloads.iterator();
            while (iterator.hasNext()) {
                final DistDownloadData download = iterator.next();
                if (languageDataListContainsLanguageIsoCode(download.getLanguages(), lang)) {
                    downloads.add(download);
                } else if (download.getLanguages() == null || download.getLanguages().isEmpty()) {
                    iterator.remove();
                } else if (CollectionUtils.isNotEmpty(downloads)) {
                    break;
                }
            }
            alternativeDownloads.removeAll(downloads);

            if (CollectionUtils.isEmpty(downloads) && CollectionUtils.isNotEmpty(alternativeDownloads)) {
                orderDownloadsByLang(downloads, alternativeDownloads, getFirstLanguageIsoCodeByFirstDownloadData(alternativeDownloads));
            }
        }
    }

    private String getFirstLanguageIsoCodeByFirstDownloadData(final List<DistDownloadData> downloads) {
        if (downloads != null && downloads.iterator().hasNext()) {
            final DistDownloadData download = downloads.iterator().next();
            if (download.getLanguages() != null && download.getLanguages().iterator().hasNext()) {
                return download.getLanguages().iterator().next().getIsocode();
            }
        }
        return LANG_EN;
    }

    private boolean languageDataListContainsLanguageIsoCode(final List<LanguageData> languageDataList, final String languageIsoCode) {
        if (languageDataList != null && languageIsoCode != null) {
            for (final LanguageData language : languageDataList) {
                if (languageIsoCode.equalsIgnoreCase(language.getIsocode())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void sortDownloadsByLanguage(final List<DistDownloadData> downloads) {
        if (CollectionUtils.isNotEmpty(downloads)) {
            downloads.sort(new Comparator<>() {
                @Override
                public int compare(final DistDownloadData o1, final DistDownloadData o2) {
                    if (hasLanguage(o1) && hasLanguage(o2)) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    if (hasLanguage(o1)) {
                        return 1;
                    }
                    if (hasLanguage(o2)) {
                        return -1;
                    }
                    try {
                        return o1.getLanguages().iterator().next().getIsocode().compareTo(o2.getLanguages().iterator().next().getIsocode());
                    } catch (final Exception e) {
                        LOG.error(e);
                        return 0;
                    }
                }

                private boolean hasLanguage(final DistDownloadData downloadData) {
                    return downloadData.getLanguages() == null || downloadData.getLanguages().isEmpty();
                }

            });
        }
    }

    public Converter<DistDownloadMediaModel, DistDownloadData> getDistDownloadMediaConverter() {
        return distDownloadMediaConverter;
    }

    public void setDistDownloadMediaConverter(final Converter<DistDownloadMediaModel, DistDownloadData> distDownloadMediaConverter) {
        this.distDownloadMediaConverter = distDownloadMediaConverter;
    }

    public Converter<DistDocumentTypeModel, DistDownloadSectionData> getDistDocumentTypeConverter() {
        return distDocumentTypeConverter;
    }

    public void setDistDocumentTypeConverter(final Converter<DistDocumentTypeModel, DistDownloadSectionData> distDocumentTypeConverter) {
        this.distDocumentTypeConverter = distDocumentTypeConverter;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

}
