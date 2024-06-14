/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.model.DistCOPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistCUPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistProductPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@IntegrationTest
public class ProductDistCOPunchOutFilterElementConverterTest extends ServicelayerTransactionalTest {
    private static final Logger LOG = LoggerFactory.getLogger(ProductDistCOPunchOutFilterElementConverterTest.class);

    private static final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final String MIN_DATE = "19700101000001";
    private static final String MAX_DATE = "99991231235959";

    @Resource
    private ProductCOPunchOutFilterElementConverter productCOPunchOutFilterElementConverter;

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogService catalogService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private NamicsCommonI18NService namicsCommonI18NService;

    @Resource
    private DistSalesOrgService distSalesOrgService;

    @Before
    public void before() throws Exception {
        importStream(ProductDistCOPunchOutFilterElementConverterTest.class.getResourceAsStream("/distrelecB2Bcore/test/testErpCodelist.impex"), "UTF-8", null);

        final CatalogModel catalog = catalogService.getCatalogForId("testCatalog");
        final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
        catalogVersion.setCatalog(catalog);
        catalogVersion.setVersion("testCatalogVersion");
        modelService.save(catalogVersion);
    }

    @Test
    public void test_without() throws DocumentException {
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/distcopunchoutfilter_with.xml");
        final ProductModel target = createProduct("derCode1");
        modelService.save(target);

        productCOPunchOutFilterElementConverter.convert(element, target);

        final Set<DistProductPunchOutFilterModel> finalModelList = target.getPunchOutFilters();
        Assert.assertTrue(CollectionUtils.isEmpty(finalModelList));
        Assert.assertEquals(0, finalModelList.size());
    }

    @Test
    public void test_diff() throws DocumentException, ParseException {
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/distcopunchoutfilter_with.xml");
        final ProductModel target = createProduct("derCode2");

        final Set<DistProductPunchOutFilterModel> modelList = new HashSet<>();
        modelList.add(createModel("SE"));
        modelList.add(createModel("IT"));
        modelList.add(createOtherModel());

        target.setPunchOutFilters(modelList);
        modelService.saveAll(modelList);
        modelService.save(target);

        productCOPunchOutFilterElementConverter.convert(element, target);

        final Set<DistProductPunchOutFilterModel> finalModelList = target.getPunchOutFilters();
        Assert.assertTrue(CollectionUtils.isNotEmpty(finalModelList));
        Assert.assertEquals(3, finalModelList.size());
    }

    @Test
    public void test_with() throws DocumentException, ParseException {
        final Element element = getRootElement("/distrelecB2Bcore/test/pim/import/distcopunchoutfilter_without.xml");
        final ProductModel target = createProduct("derCode3");

        final Set<DistProductPunchOutFilterModel> modelList = new HashSet<>();
        modelList.add(createModel("SE"));
        modelList.add(createModel("IT"));
        modelList.add(createModel("DE"));

        target.setPunchOutFilters(modelList);
        modelService.saveAll(modelList);
        modelService.save(target);

        productCOPunchOutFilterElementConverter.convert(element, target);

        final Set<DistProductPunchOutFilterModel> finalModelList = target.getPunchOutFilters();
        Assert.assertTrue(CollectionUtils.isNotEmpty(finalModelList));
    }

    private ProductModel createProduct(final String code) {
        final ProductModel target = modelService.create(ProductModel.class);
        target.setCode(code);

        final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "testCatalogVersion");
        target.setCatalogVersion(catalogVersion);
        return target;
    }

    private DistCOPunchOutFilterModel createModel(final String country) throws ParseException {
        final DistCOPunchOutFilterModel model = modelService.create(DistCOPunchOutFilterModel.class);
        final DistSalesOrgModel salesOrg = distSalesOrgService.getSalesOrgForCountryCodeAndBrandCode(country, "distrelec");
        model.setSalesOrg(salesOrg);
        model.setValidFromDate(df.parse(MIN_DATE));
        model.setValidUntilDate(df.parse(MAX_DATE));
        model.setLastModifiedErp(new Date());
        model.setCountry(getOrCreateCountryForIsoCode(country));
        model.setProductHierarchy("asdf");
        return model;
    }

    private DistCUPunchOutFilterModel createOtherModel() throws ParseException {
        final DistCUPunchOutFilterModel model = modelService.create(DistCUPunchOutFilterModel.class);
        final DistSalesOrgModel salesOrg = distSalesOrgService.getSalesOrgForCountryCodeAndBrandCode("CH", "distrelec");
        model.setSalesOrg(salesOrg);
        model.setErpCustomerID("123456");
        model.setValidFromDate(df.parse(MIN_DATE));
        model.setValidUntilDate(df.parse(MAX_DATE));
        model.setLastModifiedErp(new Date());
        model.setProductHierarchy("asdf");
        return model;
    }

    protected CountryModel getOrCreateCountryForIsoCode(final String countryIsoCode) {
        CountryModel countryModel = null;
        try {
            countryModel = commonI18NService.getCountry(countryIsoCode.toUpperCase(Locale.ROOT));
        } catch (final UnknownIdentifierException e) {
            countryModel = new CountryModel();
            countryModel.setIsocode(countryIsoCode);
            modelService.save(countryModel);
        }

        return countryModel;
    }

    private Element getRootElement(final String resourcePath) throws DocumentException {
        final SAXReader reader = new SAXReader();
        final Document document = reader.read(getClass().getResourceAsStream(resourcePath));
        return document.getRootElement();
    }
}
