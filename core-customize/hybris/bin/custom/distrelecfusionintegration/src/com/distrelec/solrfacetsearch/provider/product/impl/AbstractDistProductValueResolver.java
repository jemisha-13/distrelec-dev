package com.distrelec.solrfacetsearch.provider.product.impl;

import org.apache.commons.lang3.SerializationUtils;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public abstract class AbstractDistProductValueResolver extends AbstractDistValueResolver<ProductModel> {

    private DistCMSSiteDao distCMSSiteDao;

    private DistProductSearchExportDAO distProductSearchExportDAO;

    private EnumerationService enumerationService;

    protected AbstractDistProductValueResolver(DistCMSSiteDao distCMSSiteDao,
                                               DistProductSearchExportDAO distProductSearchExportDAO,
                                               EnumerationService enumerationService) {
        this.distCMSSiteDao = distCMSSiteDao;
        this.distProductSearchExportDAO = distProductSearchExportDAO;
        this.enumerationService = enumerationService;
    }

    protected IndexedProperty createNewIndexedProperty(final IndexedProperty indexedProperty, String name) {
        return createNewIndexedProperty(indexedProperty, name, indexedProperty.getType());
    }

    protected IndexedProperty createNewIndexedProperty(final IndexedProperty indexedProperty, String name, String type) {
        IndexedProperty newProperty = SerializationUtils.clone(indexedProperty);
        newProperty.setName(name);
        newProperty.setType(type);
        newProperty.setExportId(name);
        return newProperty;
    }

    protected <T> T getDocumentContextAttribute(final InputDocument document, String attributeKey) {
        return (T) ((DistSolrInputDocument) document).getIndexDocumentContext().get(attributeKey);
    }

    public DistCMSSiteDao getDistCMSSiteDao() {
        return distCMSSiteDao;
    }

    public DistProductSearchExportDAO getDistProductSearchExportDAO() {
        return distProductSearchExportDAO;
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }
}
