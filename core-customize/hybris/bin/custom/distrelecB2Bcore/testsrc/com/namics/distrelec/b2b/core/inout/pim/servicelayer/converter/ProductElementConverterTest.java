/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementIdNotFoundException;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.ProductReferenceCreator;
import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Locale;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils.getImportContext;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link ProductElementConverter} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductElementConverterTest extends AbstractProductElementConverterTest {

    @InjectMocks
    private final ProductElementConverter productElementConverter = new ProductElementConverter();
    @Mock
    private DistProductService mockedDistProductService;
    @Mock
    private ProductCOPunchOutFilterElementConverter mockedProductCOPunchOutFilterElementConverter;
    @Mock
    private CommonI18NService mockedCommonI18NService;
    @Mock
    private ProductReferenceCreator mockedProductReferenceCreator;
    @Mock
    private ConverterLanguageUtil mockedConverterLanguageUtil;

    private static final String PATH_TO_PRODUCT_WITH_PRODUCT_REFERENCE_XML_FILE = "/distrelecB2Bcore/test/pim/import/productWithProductReference.xml";
    private static final String PATH_TO_PRODUCT_XML_FILE = "/distrelecB2Bcore/test/pim/import/product.xml";
    private static final String PATH_TO_PRODUCT_NO_NAME_XML_FILE = "/distrelecB2Bcore/test/pim/import/productNoName.xml";
    private static final String PATH_TO_PRODUCT_CURATED_PRODUCT_SELECTION_XML_FILE = "/distrelecB2Bcore/test/pim/import/productWithCuratedProductSelection.xml";
    private static final String PATH_TO_PRODUCT_SEO_CAMPAIGN_XML_FILE = "/distrelecB2Bcore/test/pim/import/productWithSeoCampaignTag.xml";
    private static final String XP_NAME = "Values/ValueGroup[@AttributeID='shortdescription_shop_displayed']/Value']";
    private static final String SHORT_DESCRIPTION = "Diodennetzwerk 2 Dioden 3 Pins 2 Dioden 3 Pins Diodennetzwerk 2 Dioden 3 Pins 2 Dioden 3 Pins Diodennetzwerk 2 Dioden 3 Pins 2 Dioden 3 Pins Diodennetzwerk 2 Dioden 3 Pins 2 Dioden 3 Pins Diodennetzwerk 2 Dioden 3 Pins 2 Dioden 3 Pins Diodennetzwerk 2 Dioden 3 Pins 2 Dioden 3 Pins";

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Override
    @Before
    public void init() {
        super.init();
        mockConfigurationService();
        mockModelService();
        mockConverterLanguageUtil();
    }

    @Test
    public void testGetId() throws DocumentException {
        // Init
        final Element element = TestUtils.getDomElementFromResource(this.getClass(), PATH_TO_PRODUCT_XML_FILE);

        // Action
        final String id = productElementConverter.getId(element);

        // Evaluation
        assertEquals("600325", id);
    }


    @Test
    public void testGetIdWithPopulatedName() throws DocumentException {
        final Element testElement = TestUtils.getDomElementFromResource(this.getClass(), PATH_TO_PRODUCT_XML_FILE);
        final String id = productElementConverter.getId(testElement);

        assertNotNull(id);
        assertEquals("600325", id);

    }

    @Test
    public void testGetIdWithNoName() throws DocumentException {
        final Element testElement = TestUtils.getDomElementFromResource(this.getClass(), PATH_TO_PRODUCT_NO_NAME_XML_FILE);
        final String expectedErrorMessage = "No product code found for product with PIM-ID [di01600325]";

        thrown.expect(ElementIdNotFoundException.class);
        thrown.expectMessage(expectedErrorMessage);
        productElementConverter.getId(testElement);
    }

    @Test
    public void testConvert() throws DocumentException {
        // Init
        final Element element = TestUtils.getDomElementFromResource(this.getClass(), PATH_TO_PRODUCT_XML_FILE);
        final ProductModel product = new ProductModel();
        final String hash = "hash";
        final Locale locale = Locale.ENGLISH;

        final CategoryModel category = mockDistCategoryService("cat-DC-71109");
        final ClassificationClassModel classificationClass = mockClassificationSystemService();
        mockCommonI18NService(locale);

        final ImportContext importContext = getImportContext(classificationClass);
        importContext.setCurrentClassificationClass(classificationClass);

        final DistManufacturerModel manufacturer = mockDistManufacturerService("man_diotec");
        final MediaContainerModel mediaContainer = mockMediaContainerService(importContext, "600323f");
        final DistDownloadMediaModel downloadMedia = mockMediaService(importContext, "qw600325");

        // Action
        productElementConverter.convert(element, product, importContext, hash);

        // Evaluation
        assertEquals("di01600325", product.getPimId());
        assertEquals(manufacturer, product.getManufacturer());
        assertTrue(product.getSupercategories().contains(category));
        assertNull(product.getSvhcReviewDate());
        assertEquals("DAP208", product.getTypeName());
        assertEquals(0, product.getProductReferences().size());
        assertEquals(hash, product.getPimXmlHashMaster());
        assertEquals(mediaContainer, product.getPrimaryImage());
        assertEquals(1, product.getDownloadMedias().size());
        assertEquals(downloadMedia, product.getDownloadMedias().iterator().next());
    }

    @Test
    public void testConvertWithOversizedProductName() throws DocumentException {
        // Init
        final Locale locale = Locale.ENGLISH;
        final Element element = TestUtils.getDomElementFromResource(this.getClass(), "/distrelecB2Bcore/test/pim/import/productOversizedName.xml");
        final ProductModel product = new ProductModel();
        // product.setLocaleProvider(new StubLocaleProvider(locale));
        final String hash = "hash";

        mockDistCategoryService("cat-DC-71109");
        final ClassificationClassModel classificationClass = mockClassificationSystemService();
        mockCommonI18NService(locale);

        final ImportContext importContext = getImportContext(classificationClass);
        importContext.setCurrentClassificationClass(classificationClass);

        mockDistManufacturerService("man_diotec");
        mockMediaContainerService(importContext, "600323f");
        mockMediaService(importContext, "qw600325");

        // Action
        productElementConverter.convert(element, product, importContext, hash);

        // Evaluation
        assertEquals(ProductElementConverter.MAX_COLUMN_LENGTH_PRODUCT_NAME, product.getName(locale).length());
    }

    @Test
    public void testConvertWithProductReference() throws DocumentException {
        // Init
        final Element element = TestUtils.getDomElementFromResource(this.getClass(), PATH_TO_PRODUCT_WITH_PRODUCT_REFERENCE_XML_FILE);
        final ProductModel product = new ProductModel();
        final String hash = "hash";
        final Locale locale = Locale.ENGLISH;

        final CategoryModel category = mockDistCategoryService("cat-DC-22281");
        final ClassificationClassModel classificationClass = mockClassificationSystemService();
        mockCommonI18NService(locale);

        final ImportContext importContext = getImportContext(classificationClass);
        importContext.setCurrentClassificationClass(classificationClass);

        final DistManufacturerModel manufacturer = mockDistManufacturerService("man_semikron");
        final DistDownloadMediaModel downloadMedia = mockMediaService(importContext, "ub601392-601399_e");

        when(mockedConfigurationService.getConfiguration().getString("mpnalias.pimimport.regex.notalphanumeric")).thenReturn("[^A-Za-z0-9|]");
        // Action
        productElementConverter.convert(element, product, importContext, hash);

        // Evaluation
        assertEquals("di01601392", product.getPimId());
        assertEquals(manufacturer, product.getManufacturer());
        assertTrue(product.getSupercategories().contains(category));
        assertEquals(TestUtils.getDate(2012, 12, 19), product.getSvhcReviewDate());
        assertEquals("SKB25/01", product.getTypeName());
        assertEquals(1, importContext.getProductReferenceDtos().size());
        // The hash value must be set because the referenced product does not yet exist and reference creation is postponed.
        assertNull(product.getPimXmlHashMaster());
        assertEquals("di01343124", importContext.getProductReferenceDtos().get(0).getTargetPimId());
        assertEquals(ProductReferenceTypeEnum.DIST_ACCESSORY, importContext.getProductReferenceDtos().get(0).getProductReferenceType());
        assertEquals(1, product.getDownloadMedias().size());
        assertEquals(downloadMedia, product.getDownloadMedias().iterator().next());
        assertEquals("QUINT-PS", product.getAlternativesMPN());
        assertEquals("QUINTPS", product.getNormalizedAlternativesMPN());
        assertEquals("QUINT-PS/1AC/24DC/10/CO", product.getAlternativeAliasMPN());
        assertEquals("QUINTPS1AC24DC10CO", product.getNormalizedAlternativeAliasMPN());
    }

    /**
     * DISTRELEC-13364 - Update PIM Import to get the new multivalue non-localized curated_product_selection field and save it on a new
     * Hybris field, which will contain all the value with a pipe separation.
     * 
     * @throws DocumentException
     */
    @Test
    public void testCuratedProductSelection() throws DocumentException {
        // Init
        final Element element = TestUtils.getDomElementFromResource(this.getClass(), PATH_TO_PRODUCT_CURATED_PRODUCT_SELECTION_XML_FILE);
        final ProductModel product = new ProductModel();
        final ClassificationClassModel classificationClass = mockClassificationSystemService();
        final Locale locale = Locale.ENGLISH;
        final ImportContext importContext = getImportContext(classificationClass);
        importContext.setCurrentClassificationClass(classificationClass);
        final DistManufacturerModel manufacturer = mockDistManufacturerService("man_semikron");
        final DistDownloadMediaModel downloadMedia = mockMediaService(importContext, "ub601392-601399_e");
        mockCommonI18NService(locale);

        // Action
        productElementConverter.convert(element, product, importContext, "hash");

        // Evaluation
        assertEquals("cps1|cps2", product.getCuratedProductSelection());
        assertEquals(manufacturer, product.getManufacturer());
        assertEquals(downloadMedia, product.getDownloadMedias().iterator().next());
    }

    /**
     * DISTRELEC-13439 - seo_campaign_tag is now localized and multivalue
     * 
     * @throws DocumentException
     */
    @Test
    public void testSeoCampaignTag() throws DocumentException {
        // Init
        final Element element = TestUtils.getDomElementFromResource(this.getClass(), PATH_TO_PRODUCT_SEO_CAMPAIGN_XML_FILE);
        final ProductModel product = new ProductModel();
        final ClassificationClassModel classificationClass = mockClassificationSystemService();
        final Locale locale = Locale.ENGLISH;
        final ImportContext importContext = getImportContext(classificationClass);
        importContext.setCurrentClassificationClass(classificationClass);
        final DistManufacturerModel manufacturer = mockDistManufacturerService("man_semikron");
        final DistDownloadMediaModel downloadMedia = mockMediaService(importContext, "ub601392-601399_e");
        mockCommonI18NService(locale);

        // Action
        productElementConverter.convert(element, product, importContext, "hash");

        // Evaluation
        assertEquals(manufacturer, product.getManufacturer());
        assertEquals(downloadMedia, product.getDownloadMedias().iterator().next());
    }

    private void mockModelService() {
        when(mockedModelService.create(ProductReferenceModel.class)).thenReturn(new ProductReferenceModel());
        when(mockedModelService.create(ProductFeatureModel.class)).thenReturn(new ProductFeatureModel());
    }

    private ClassificationClassModel mockClassificationSystemService() {
        final ClassificationClassModel classificationClass = new ClassificationClassModel();
        classificationClass.setDeclaredClassificationAttributeAssignments(Collections.<ClassAttributeAssignmentModel> emptyList());
        when(mockedClassificationSystemService.getClassForCode(Mockito.<ClassificationSystemVersionModel> anyObject(), Mockito.<String> any()))
                .thenReturn(classificationClass);

        mockUnit(mockedClassificationSystemService, "unece.unit.4K", "4K");
        mockUnit(mockedClassificationSystemService, "unece.unit.VLT", "VLT");
        mockUnit(mockedClassificationSystemService, "unece.unit.MMT", "MMT");
        mockUnit(mockedClassificationSystemService, "unece.unit.AMP", "AMP");

        return classificationClass;
    }

    private void mockUnit(final ClassificationSystemService mockedClassificationSystemService, final String unitCode, final String unitName) {
        final ClassificationAttributeUnitModel classificationUnit = mock(ClassificationAttributeUnitModel.class);

        classificationUnit.setCode(unitCode);
        classificationUnit.setSymbol(unitName);
        when(classificationUnit.getName(Mockito.<Locale> anyObject())).thenReturn(unitName);
        when(mockedClassificationSystemService.getAttributeUnitForCode(Mockito.<ClassificationSystemVersionModel> anyObject(), Mockito.eq(unitCode)))
                .thenReturn(classificationUnit);

    }

    private void mockCommonI18NService(final Locale locale) {
        when(mockedCommonI18NService.getLanguage(locale.getLanguage())).thenReturn(new LanguageModel());
    }

    private DistDownloadMediaModel mockMediaService(final ImportContext importContext, final String distDownloadMediaCode) {
        final DistDownloadMediaModel downloadMedia = Mockito.mock(DistDownloadMediaModel.class);
        when(downloadMedia.getCode()).thenReturn(distDownloadMediaCode);
        when(mockedMediaService.getMedia(distDownloadMediaCode)).thenReturn(downloadMedia);
        when(mockedMediaService.getMedia(importContext.getProductCatalogVersion(), distDownloadMediaCode)).thenReturn(downloadMedia);
        return downloadMedia;
    }

    @Override
    protected void mockConfigurationService() {
        super.mockConfigurationService();
        when(mockedConfigurationService.getConfiguration().getBoolean("import.pim.importFactFinderDQServerInformations", Boolean.FALSE))
                .thenReturn(Boolean.FALSE);
    }

    private void mockConverterLanguageUtil() {
        when(mockedConverterLanguageUtil.getLocalizedValues(Mockito.any(), Mockito.eq(XP_NAME))).thenReturn(singletonMap(Locale.ENGLISH, SHORT_DESCRIPTION));
        when(mockedConverterLanguageUtil.getLocaleForElement(Mockito.any())).thenReturn(Locale.ENGLISH);
    }

}
