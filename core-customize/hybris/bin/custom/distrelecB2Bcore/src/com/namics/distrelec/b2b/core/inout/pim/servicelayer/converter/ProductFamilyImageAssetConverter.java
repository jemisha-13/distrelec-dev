package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimMediaDto;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.impl.MediaDao;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.ProductFamilyElementConverter.XP_VALUE;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.ProductFamilyElementConverter.getNodesForXPath;

public class ProductFamilyImageAssetConverter extends ImageAssetElementConverter {

    private static final String XP_PUSH_DOWNLOADED = String.format("%s[@Status='OK_DOWNLOADED']",XP_ASSET_PUSH_LOCATION);

    private static final String XP_DS_LANG = "Values/MultiValue[@AttributeID='40_datasheetlanguages_lov']";

    @Autowired
    private MediaDao mediaDao;

    @Autowired
    private ConverterLanguageUtil converterLanguageUtil;

    @Override
    protected List<Element> getPushLocations(Element source) {
        return source.selectNodes(XP_PUSH_DOWNLOADED);
    }

    @Override
    protected void cleanUpMedias(Map<String, MediaModel> mediaMap) {}

    @Override
    protected MediaModel getOrCreateMedia(ImportContext importContext, Collection<MediaModel> medias, PimMediaDto mediaDto) {
        Optional<MediaModel> existing = Optional.ofNullable(medias)
                                                .map(Collection::stream)
                                                .orElse(Stream.empty())
                                                .filter(m -> m.getCode().equals(mediaDto.getCode()))
                                                .map(m -> populateMediaFromDTO(importContext, mediaDto, m))
                                                .findFirst();
        return existing.orElseGet(() -> existing.orElse(mediaDao.findMediaByCode(importContext.getProductCatalogVersion(), mediaDto.getCode())
                .stream()
                .findAny()
                .orElse(createMedia(importContext, mediaDto))));
    }

    @Override
    public void convert(Element source, MediaContainerModel target, ImportContext importContext, String hash) {
        super.convert(source, target, importContext, hash);
        getNodesForXPath(source, XP_DS_LANG).findFirst().filter(Element.class::isInstance).map(Element.class::cast).ifPresent(el -> {
            getNodesForXPath(el, XP_VALUE).forEach(n -> importContext.addLocaleForMedia(target.getQualifier(), converterLanguageUtil.getLocaleForLanguage(n.getText().toLowerCase())));
        });
    }
}
