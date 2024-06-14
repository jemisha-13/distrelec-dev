/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.catplus.impex.decorator;

import java.util.Map;

import org.apache.cxf.common.util.StringUtils;

import com.namics.distrelec.b2b.core.inout.impex.AbstractDistCellDecorator;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.HeaderDescriptor;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * Checks if a Catalog+ product is existing for the given Catalog+ Supplier AID.
 * 
 * If not, a new product code is generated, because a Catalog+ product has no Distrelec article number.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DistGenerateCodeCellDecorator extends AbstractDistCellDecorator {

    private FlexibleSearchService flexibleSearchService = SpringUtil.getBean("flexibleSearchService", FlexibleSearchService.class);
    private PersistentKeyGenerator catplusCodeGenerator = SpringUtil.getBean("catplusCodeGenerator", PersistentKeyGenerator.class);
    private CatalogVersionService catalogVersionService = SpringUtil.getBean("catalogVersionService", CatalogVersionService.class);

    @Override
    public String decorate(final int position, final Map<Integer, String> line) {
        final HeaderDescriptor impexHeader = getColumnDescriptor().getHeader();
        final String code = line.get(Integer.valueOf(position));
        if (StringUtils.isEmpty(code)) {
            final String catalogPlusSupplierAID = getColumnValueByName(impexHeader, line, ProductModel.CATPLUSSUPPLIERAID);
            final String catalogPlusSupplierShortName = getColumnValueByName(impexHeader, line, ProductModel.CATPLUSSUPPLIERSHORTNAME);

            if (StringUtils.isEmpty(catalogPlusSupplierAID)) {
                throw new JaloSystemException(ProductModel.CATPLUSSUPPLIERAID + " is empty");
            } else if (StringUtils.isEmpty(catalogPlusSupplierShortName)) {
                throw new JaloSystemException(ProductModel.CATPLUSSUPPLIERSHORTNAME + " is empty");
            } else {
                try {
                    // get the catalogmodel from impex definition
                    final AbstractColumnDescriptor catalogVersionColumn = getColumnDescriptorByName(impexHeader, line, ProductModel.CATALOGVERSION);
                    final String catalogVersion = parseCatalogVersionFromColumn(catalogVersionColumn);
                    catalogVersionService.setSessionCatalogVersion(catalogVersion.split(",")[0], catalogVersion.split(",")[1]);
                    if (StringUtils.isEmpty(catalogVersion)) {
                        throw new JaloSystemException(ProductModel.CATALOGVERSION + " is empty");
                    } else {

                        final ProductModel exampleProduct = new ProductModel();
                        exampleProduct.setCatPlusSupplierAID(catalogPlusSupplierAID);
                        exampleProduct.setCatPlusSupplierShortName(catalogPlusSupplierShortName);
                        exampleProduct.setCatalogVersion(catalogVersionService.getCatalogVersion(catalogVersion.split(",")[0], catalogVersion.split(",")[1]));
                        // Get existing product code on the product
                        return getFlexibleSearchService().getModelByExample(exampleProduct).getCode();
                    }

                } catch (ModelNotFoundException e) {
                    // Generate new product code
                    return catplusCodeGenerator.generate().toString();
                }
            }

        } else {
            // Get the code from impex line value
            return code;
        }
    }

    private String parseCatalogVersionFromColumn(final AbstractColumnDescriptor catalogVersionColumn) {

        // String test = "catalogversion(catalog(id[default=distrelecCatalogPlusProductCatalog]),version[default='Online'])";
        // System.out.println(test.substring(test.indexOf("catalog(")+19, test.indexOf("])",test.indexOf("catalog(")+19)));
        //
        // System.out.println(test.substring(test.indexOf("version[")+17, test.indexOf("'])",test.indexOf("version[")+17)));
        final String definition = catalogVersionColumn.getDefinitionSrc();
        try {

            return definition.substring(definition.indexOf("catalog(") + 19, definition.indexOf("])", definition.indexOf("catalog(") + 19)) + ","
                    + definition.substring(definition.indexOf("version[") + 17, definition.indexOf("'])", definition.indexOf("version[") + 17));
        } catch (final Exception ex) {
            throw new JaloSystemException("Unable to parse " + ProductModel.CATALOGVERSION + " From header columndefinition: " + definition);
        }
    }

    // Spring beans
    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public PersistentKeyGenerator getCatplusCodeGenerator() {
        return catplusCodeGenerator;
    }

    public void setCatplusCodeGenerator(final PersistentKeyGenerator catplusCodeGenerator) {
        this.catplusCodeGenerator = catplusCodeGenerator;
    }
}
