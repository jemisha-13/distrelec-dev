package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.ACTIVE_LABELS_CONTEXT_FIELD;
import static java.util.Comparator.comparing;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistPromotionLabelValueResolver extends AbstractDistProductValueResolver {

    public DistPromotionLabelValueResolver(DistProductSearchExportDAO distProductSearchExportDAO, DistCMSSiteDao distCMSSiteDao,
                                           EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> valueResolverContext) throws FieldValueProviderException {
        List<DistPromotionLabelModel> activeLabelsForProduct = getDocumentContextAttribute(document, ACTIVE_LABELS_CONTEXT_FIELD);

        if (isNotEmpty(activeLabelsForProduct)) {
            DistPromotionLabelModel highestPrioLabel = activeLabelsForProduct.stream()
                                                                             .sorted(comparing(DistPromotionLabelModel::getPriority))
                                                                             .toList()
                                                                             .get(0);
            String labelValue = getLabelValue(highestPrioLabel);
            addFieldValue(document, batchContext, indexedProperty, labelValue, valueResolverContext.getFieldQualifier());
        }
    }

    protected String getLabelValue(DistPromotionLabelModel label) {
        return label.getCode() + "|" + label.getName();
    }
}
