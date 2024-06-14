package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.BRAND_LOGO;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_MEDIUM;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_SMALL;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_MEDIUM;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_SMALL;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.UNDERSCORE;

import java.util.Optional;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistProductImagesValueResolver extends AbstractDistProductValueResolver {

    private static final String THUMBNAIL_IMAGE_URL = "thumbnail";

    private static final String ENERGY_EFFICIENCY_LABEL_IMAGE_URL = "energyEfficiencyLabelImageUrl";

    private static final String MANUFACTURER_IMAGE_URL = "manufacturerImageUrl";

    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    protected DistProductImagesValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                             EnumerationService enumerationService, DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
        this.distSiteBaseUrlResolutionService = distSiteBaseUrlResolutionService;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        addProductImages(document, indexerBatchContext, indexedProperty, product, resolverContext);
        addEnergyEfficiencyImages(document, indexerBatchContext, indexedProperty, product, resolverContext);
        addManufacturerImages(document, indexerBatchContext, indexedProperty, product, resolverContext);
    }

    private void addProductImages(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        MediaContainerModel mediaContainer = product.getPrimaryImage() != null ? product.getPrimaryImage() : product.getIllustrativeImage();

        if (mediaContainer == null) {
            return;
        }
        Optional<MediaModel> smallPortrait = getMediaForFormat(mediaContainer, PORTRAIT_SMALL);
        Optional<MediaModel> landscapeSmall = getMediaForFormat(mediaContainer, LANDSCAPE_SMALL);
        Optional<MediaModel> landscapeMedium = getMediaForFormat(mediaContainer, LANDSCAPE_MEDIUM);

        if (landscapeSmall.isPresent()) {
            addFieldValue(document, indexerBatchContext,
                          createNewIndexedProperty(indexedProperty, indexedProperty.getExportId() + UNDERSCORE + LANDSCAPE_SMALL),
                          landscapeSmall.get().getInternalURL(),
                          resolverContext.getFieldQualifier());
        }

        CMSSiteModel cmsSite = indexerBatchContext.getFacetSearchConfig().getIndexConfig().getCmsSite();
        if (landscapeSmall.isPresent() && cmsSite != null) {
            addFieldValue(document, indexerBatchContext,
                          createNewIndexedProperty(indexedProperty, THUMBNAIL_IMAGE_URL),
                          createFullThumbnailURL(landscapeSmall.get(), cmsSite),
                          resolverContext.getFieldQualifier());
        }

        if (landscapeMedium.isPresent()) {
            addFieldValue(document, indexerBatchContext,
                          createNewIndexedProperty(indexedProperty, indexedProperty.getExportId() + UNDERSCORE + LANDSCAPE_MEDIUM),
                          landscapeMedium.get().getInternalURL(),
                          resolverContext.getFieldQualifier());
        }
        if (smallPortrait.isPresent()) {
            document.addField(indexedProperty,
                              smallPortrait.get().getInternalURL(),
                              resolverContext.getFieldQualifier());
        }
    }

    private String createFullThumbnailURL(MediaModel media, CMSSiteModel cmsSite) {
        return distSiteBaseUrlResolutionService.getMediaUrlForSite(cmsSite, true, media.getInternalURL());
    }

    private void addEnergyEfficiencyImages(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
                                           ProductModel product, ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        MediaContainerModel mediaContainer = product.getEnergyEfficiencyLabelImage();
        if (mediaContainer == null) {
            return;
        }
        Optional<MediaModel> energyEfficiencySmallPortrait = getMediaForFormat(mediaContainer, PORTRAIT_MEDIUM);
        if (energyEfficiencySmallPortrait.isPresent()) {
            addFieldValue(document, indexerBatchContext,
                          createNewIndexedProperty(indexedProperty, ENERGY_EFFICIENCY_LABEL_IMAGE_URL),
                          energyEfficiencySmallPortrait.get().getInternalURL(),
                          resolverContext.getFieldQualifier());
        }

    }

    private void addManufacturerImages(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel product,
                                       ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        DistManufacturerModel manufacturer = product.getManufacturer();
        if (manufacturer == null) {
            return;
        }
        MediaContainerModel mediaContainer = manufacturer.getPrimaryImage();
        if (mediaContainer == null) {
            return;
        }

        Optional<MediaModel> manufacturerBrandLogo = getMediaForFormat(mediaContainer, BRAND_LOGO);
        if (manufacturerBrandLogo.isPresent()) {
            addFieldValue(document, indexerBatchContext,
                          createNewIndexedProperty(indexedProperty, MANUFACTURER_IMAGE_URL),
                          manufacturerBrandLogo.get().getDownloadURL(),
                          resolverContext.getFieldQualifier());
        }
    }

}
