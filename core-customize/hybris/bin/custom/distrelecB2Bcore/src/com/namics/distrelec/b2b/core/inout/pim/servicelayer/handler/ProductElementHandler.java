/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimXmlHashDto;
import de.hybris.platform.core.model.ItemModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ServiceLayerPimExportParser;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementIdNotFoundException;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Element handler for "Product" XML elements containing a real product. Such elements will result in models of type {@link ProductModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ProductElementHandler extends AbstractHashAwarePimImportElementHandler {

    private static final Logger LOG = LogManager.getLogger(ProductElementHandler.class);

    private static final String XP_FEATURES = "Values/*";
    private static final String XP_FEATURES_ATTRIBUTEID = "AttributeID";

    @Autowired
    private ProductService productService;

    private PimImportElementConverter pimImportProductFeatureElementConverter;

    public ProductElementHandler() {
        super(ProductModel._TYPECODE);
    }

    @Override
    protected void doUpdate(final Element element, final String id, final String newHash, final ItemModel item) {
        if (item instanceof ProductModel) {
            final ProductModel model = (ProductModel) item;
            try {
                getPimImportElementConverter().convert(element, model, getImportContext(), newHash);

                // update product features
                getPimImportProductFeatureElementConverter().convert(element, model, getImportContext(), newHash);

                // DISTRELEC-12071 make the product dirty if it has no manufacturer set.
                if (ServiceLayerPimExportParser.makeDirty(model)) {
                    model.setPimXmlHashMaster(null);
                    model.setPimHashTimestamp(null);
                }
                getModelService().save(model);
            } catch (final ElementConverterException e) {
                LOG.error("Could not import product with code [" + id + "]", e);
            } catch (final ModelSavingException e) {
                LOG.error("Could not save Product with code [" + model.getCode() + "]", e);
            } catch (Exception e) {
                LOG.error("ProductElementHandler exception for product with code [" + model.getCode() + "]", e);
            } finally {
                getModelService().detach(model);
            }
        }
    }

    @Override
    protected boolean isWhitelisted() {
        return getImportContext().isImportProductsOfCurrentProductLine();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.AbstractHashAwarePimImportElementHandler#doUpdateIfRequired(org.dom4j
     * .Element)
     */
    @Override
    protected void doUpdateIfRequired(final Element element) {
        // We need this to ensure that all class attribute assignment are marked as non empty
        markClassAttributeAssignements(element);

        String id;
        try {
            id = getPimImportElementConverter().getId(element);
        } catch (final ElementIdNotFoundException e) {
            LOG.error("No ID found on XML element: {}", e.getMessage());
            return;
        }

        final PimXmlHashDto pimXmlHashDto = getImportContext().getHashValue(getTypeCode(), id);
        final String oldHash = pimXmlHashDto != null ? pimXmlHashDto.getPimXmlHashMaster() : null;
        final Date pimHashTimestamp = pimXmlHashDto != null ? pimXmlHashDto.getPimHashTimestamp() : null;
        final Date globalHashTimestamp = getImportContext().getGlobalHashTimestamp();
        final String newHash = calculateHashV2(element.asXML());
        final ProductModel model = getModel(id, element);
        boolean updated = false;

        boolean updateRequired = updateRequired(element, id, oldHash, newHash, model, pimHashTimestamp, globalHashTimestamp);
        // Oracle update
        if (updateRequired) {
            doUpdate(element, id, newHash, model);
            updated = true;
        }

        if (!updated) {
            getImportContext().incrementSkippedCounter(getTypeCode());
        } else {
            model.setLatestPimFilename(getImportContext().getFilename());
            getModelService().save(model);
        }
        doAfterProcess(id);
    }

    /**
     * Mark all Class Attribute Assignment of the current classification wrapper as non empty. This resolves the problem where all products
     * from a certain categories are not updated.
     * 
     * @param source
     *            The source element.
     */
    protected void markClassAttributeAssignements(final Element source) {
        final List<Element> featureElements = source.selectNodes(XP_FEATURES);
        for (final Element element : featureElements) {
            final String attributeID = element.attributeValue(XP_FEATURES_ATTRIBUTEID);
            if (getImportContext().getBlacklistedProductFeatures().contains(attributeID)) {
                // Skip blacklisted feature
                continue;
            }
            // For to mark the Class Attribute Assignement as non empty
            if(getImportContext().getCurrentClassificationClassWrapper()!=null) {
            	getImportContext().getCurrentClassificationClassWrapper().getClassAttributeAssignment(attributeID);
            }
        }
    }

    @Override
    protected ProductModel getModel(final String code, final Element element) {
        try {
            return productService.getProductForCode(code);
        } catch (final UnknownIdentifierException e) {
            LOG.info("Could not find Product with code [" + code + "]");
        } catch (final AmbiguousIdentifierException e) {
            LOG.error("Ambiguous Product code [" + code + "]");
        } catch (final Exception e) {
            LOG.error("An error occur while fetching Product with code [" + code + "]", e);
        }
        return null;
    }

    public PimImportElementConverter getPimImportProductFeatureElementConverter() {
        return pimImportProductFeatureElementConverter;
    }

    public void setPimImportProductFeatureElementConverter(final PimImportElementConverter pimImportProductFeatureElementConverter) {
        this.pimImportProductFeatureElementConverter = pimImportProductFeatureElementConverter;
    }
}
