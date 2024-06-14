package com.namics.distrelec.b2b.facades.preview;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.previewwebservices.dto.CatalogVersionWsDTO;
import de.hybris.platform.previewwebservices.dto.PreviewTicketWsDTO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.webservicescommons.util.LocalViewExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.stream.Collectors;

public class DistPreviewTicketPopulator implements Populator<CMSPreviewTicketModel, PreviewTicketWsDTO> {
    private LocalViewExecutor localViewExecutor;

    @Override
    public void populate(CMSPreviewTicketModel cmsPreviewTicketModel, PreviewTicketWsDTO previewTicketWsDTO) throws ConversionException {
        this.localViewExecutor.executeWithAllCatalogs(() -> {
            PreviewDataModel previewData = cmsPreviewTicketModel.getPreviewData();
            if (previewData != null) {
                if (CollectionUtils.isNotEmpty(previewData.getCatalogVersions())) {
                    List<CatalogVersionWsDTO> catalogVersions = (List)previewData.getCatalogVersions()
                        .stream()
                        .map(this::buildCatalogVersionWsDTO)
                        .collect(Collectors.toList());
                    previewTicketWsDTO.setCatalogVersions(catalogVersions);
                }
            }

            return null;
        });
    }

    protected CatalogVersionWsDTO buildCatalogVersionWsDTO(CatalogVersionModel catalogVersion) {
        CatalogVersionWsDTO result = new CatalogVersionWsDTO();

        String catalogId = catalogVersion.getCatalog().getId();
        if (DistUtils.containsUnderscore(catalogId)) {
            catalogId = DistUtils.convertCatalogIdUnderscoreToMinus(catalogId);
        }
        result.setCatalog(catalogId);

        result.setCatalogVersion(catalogVersion.getVersion());
        return result;
    }

    protected LocalViewExecutor getLocalViewExecutor() {
        return localViewExecutor;
    }

    @Required
    public void setLocalViewExecutor(LocalViewExecutor localViewExecutor) {
        this.localViewExecutor = localViewExecutor;
    }
}
