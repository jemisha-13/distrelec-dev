package com.distrelec.solrfacetsearch.provider.punchout.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;
import com.namics.distrelec.b2b.core.model.DistCUPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistPunchOutFilterModel;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistPunchOutAllAttributesValueResolver extends AbstractDistValueResolver<DistPunchOutFilterModel> {

    private static final Logger LOG = LogManager.getLogger(DistPunchOutAllAttributesValueResolver.class);

    private static final String SINGLE_PRODUCT_FILTER = "singleProductFilter";

    private static final String PRODUCT_HIERARCHY_FILTER = "productHierarchyFilter";

    private static final String MANUFACTURER_FILTER = "manufacturerFilter";

    private static final String TYPE_FIELD = "type";

    private static final String PRODUCT_HIERARCHY_CODE_FIELD = "productHierarchyCode";

    private static final String PRODUCT_CODE_FIELD = "productCode";

    private static final String MANUFACTURER_CODE_FIELD = "manufacturerCode";

    private static final String CUSTOMER_ERP_ID_FIELD = "customerId";

    private static final String SALES_ORG_CODE_FIELD = "salesOrgCode";

    @Override
    protected void addFieldValues(InputDocument document,
                                  IndexerBatchContext indexerBatchContext,
                                  IndexedProperty indexedProperty,
                                  DistPunchOutFilterModel punchOutFilter,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        if (isSingleProductPunchOutFilter(punchOutFilter)) {
            assignSingleProductPunchOutFilterFields(document, indexedProperty, punchOutFilter, resolverContext);
        } else if (isProductHierarchyPunchOutFilter(punchOutFilter)) {
            assignProductHierarchyPunchOutFilterFields(document, indexedProperty, punchOutFilter, resolverContext);
        } else if (isManufacturerPunchOutFilter(punchOutFilter)) {
            assignManufacturerPunchOutFilterFields(document, indexedProperty, punchOutFilter, resolverContext);
        } else {
            LOG.error("Unknown PunchOutFilter, skipping PK:{} ", punchOutFilter.getPk());
        }
    }

    private boolean isSingleProductPunchOutFilter(DistPunchOutFilterModel punchOutFilter) {
        return punchOutFilter instanceof DistCUPunchOutFilterModel distCUPunchOutFilter
                && distCUPunchOutFilter.getProduct() != null;
    }

    private void assignSingleProductPunchOutFilterFields(InputDocument document,
                                                         IndexedProperty indexedProperty,
                                                         DistPunchOutFilterModel punchOutFilter,
                                                         ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        if (punchOutFilter instanceof DistCUPunchOutFilterModel distCUPunchOutFilter) {
            document.addField(createNewIndexedProperty(indexedProperty, TYPE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              SINGLE_PRODUCT_FILTER, resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, PRODUCT_CODE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distCUPunchOutFilter.getProduct().getCode(), resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, CUSTOMER_ERP_ID_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distCUPunchOutFilter.getErpCustomerID(), resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, SALES_ORG_CODE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distCUPunchOutFilter.getSalesOrg().getCode(), resolverContext.getFieldQualifier());
        }
    }

    private boolean isProductHierarchyPunchOutFilter(DistPunchOutFilterModel punchOutFilter) {
        return punchOutFilter instanceof DistCUPunchOutFilterModel distCUPunchOutFilter
                && isNotBlank(distCUPunchOutFilter.getProductHierarchy());
    }

    private void assignProductHierarchyPunchOutFilterFields(InputDocument document,
                                                            IndexedProperty indexedProperty,
                                                            DistPunchOutFilterModel punchOutFilter,
                                                            ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        if (punchOutFilter instanceof DistCUPunchOutFilterModel distCUPunchOutFilter) {
            document.addField(createNewIndexedProperty(indexedProperty, TYPE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              PRODUCT_HIERARCHY_FILTER, resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, PRODUCT_HIERARCHY_CODE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distCUPunchOutFilter.getProductHierarchy(), resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, CUSTOMER_ERP_ID_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distCUPunchOutFilter.getErpCustomerID(), resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, SALES_ORG_CODE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distCUPunchOutFilter.getSalesOrg().getCode(), resolverContext.getFieldQualifier());
        }
    }

    private boolean isManufacturerPunchOutFilter(DistPunchOutFilterModel punchOutFilter) {
        return punchOutFilter instanceof DistManufacturerPunchOutFilterModel distManufacturerPunchOutFilter
                && distManufacturerPunchOutFilter.getManufacturer() != null;
    }

    private void assignManufacturerPunchOutFilterFields(InputDocument document,
                                                        IndexedProperty indexedProperty,
                                                        DistPunchOutFilterModel punchOutFilter,
                                                        ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        if (punchOutFilter instanceof DistManufacturerPunchOutFilterModel distManufacturerPunchOutFilter) {
            document.addField(createNewIndexedProperty(indexedProperty, TYPE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              MANUFACTURER_FILTER, resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, MANUFACTURER_CODE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distManufacturerPunchOutFilter.getManufacturer().getCode(), resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, CUSTOMER_ERP_ID_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distManufacturerPunchOutFilter.getErpCustomerID(), resolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, SALES_ORG_CODE_FIELD, SolrPropertiesTypes.STRING.getCode()),
                              distManufacturerPunchOutFilter.getSalesOrg().getCode(), resolverContext.getFieldQualifier());
        }
    }

    protected IndexedProperty createNewIndexedProperty(final IndexedProperty indexedProperty, String name, String type) {
        IndexedProperty newProperty = SerializationUtils.clone(indexedProperty);
        newProperty.setName(name);
        newProperty.setType(type);
        newProperty.setExportId(name);
        return newProperty;
    }

}
