package com.namics.distrelec.b2b.core.service.preview;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.previewwebservices.dto.PreviewTicketWsDTO;
import de.hybris.platform.previewwebservices.populators.PreviewTicketReversePopulator;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DistPreviewTicketReversePopulator extends PreviewTicketReversePopulator {

    @Override
    protected Map<String, String> buildRequestedCatalogVersionMap(PreviewTicketWsDTO source) {
        Map<String, String> catalogVersionMap = super.buildRequestedCatalogVersionMap(source);

        Map<String, String> fixedCatalogVersionMap = catalogVersionMap.entrySet().stream().map(entry -> {
            String catalogId = entry.getKey();

            if (DistUtils.containsMinus(catalogId)) {
                String originalCatalogId = DistUtils.revertCatalogIdMinusToUnderscore(catalogId);
                AbstractMap.SimpleEntry newEntry = new AbstractMap.SimpleEntry(originalCatalogId, entry.getValue());
                return newEntry;
            }

            return entry;
        }).collect(Collectors.toMap(entry -> {
            return (String) entry.getKey();
        }, entry -> {
            return (String) entry.getValue();
        }));

        return fixedCatalogVersionMap;
    }

    @Override
    protected CatalogVersionModel getValidCatalogVersionModel(CatalogModel catalog, Map<String, String> catalogVersionMap) {
        String version = catalogVersionMap.get(catalog.getId());
        if (version == null && catalog instanceof ContentCatalogModel) {
            String subCatalogVersion = null;
            for (Map.Entry<String, String> catVersionEntry : catalogVersionMap.entrySet()) {
                try {
                    CatalogVersionModel subCatVersion = getCatalogVersionService().getCatalogVersion(catVersionEntry.getKey(), catVersionEntry.getValue());
                    CatalogModel subCatalog = subCatVersion.getCatalog();
                    if (subCatalog instanceof ContentCatalogModel) {
                        ContentCatalogModel subContentCatalog = (ContentCatalogModel) subCatalog;
                        CatalogModel superCat = subContentCatalog.getSuperCatalog();
                        if (superCat != null && catalog.getId().equals(superCat.getId())) {
                            subCatalogVersion = subCatVersion.getVersion();
                        }
                    }
                } catch (UnknownIdentifierException e) {
                    // igno
                }
            }

            if (subCatalogVersion != null) {
                final String subVersion = subCatalogVersion;
                Optional<CatalogVersionModel> catalogVersionOptional = catalog.getCatalogVersions().stream()
                        .filter(catVersion -> subVersion.equals(catVersion.getVersion()))
                        .findAny();

                if (catalogVersionOptional.isPresent()) {
                    return catalogVersionOptional.get();
                }
            }
        }

        return super.getValidCatalogVersionModel(catalog, catalogVersionMap);
    }



    @Override
    protected void setCatalogVersionInSession(PreviewTicketWsDTO source, PreviewDataModel target) {
        source.getCatalogVersions().forEach(catalogVersionData -> {
            String originalCatalogId = DistUtils.revertCatalogIdMinusToUnderscore(catalogVersionData.getCatalog());
            catalogVersionData.setCatalog(originalCatalogId);
        });
        target.setCatalogVersions(retrieveCatalogVersionsForPreview(source, target));
    }
}
