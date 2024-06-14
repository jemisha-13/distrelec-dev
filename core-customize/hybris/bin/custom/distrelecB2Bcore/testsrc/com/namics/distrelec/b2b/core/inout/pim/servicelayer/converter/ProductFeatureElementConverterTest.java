/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils.getImportContextMocking;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.CProductFeatureKey;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.PimImportInitializer;
import com.namics.distrelec.b2b.core.service.classification.impl.DefaultDistClassificationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeature;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeatures;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.product.ProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductFeatureElementConverterTest extends AbstractProductElementConverterTest {

    private static final Logger LOG = LogManager.getLogger(ProductFeatureElementConverterTest.class);

    private static final String PATH_TO_PRODUCT_NO_FEATURE = "/distrelecB2Bcore/test/pim/import/productWithBlankFeature.xml";

    @InjectMocks
    private final ProductFeatureElementConverter productFeatureElementConverter = new ProductFeatureElementConverter();

    @Mock
    private DefaultDistClassificationService defaultDistClassificationService;

    @Mock
    private FFAdditionalDataGenerator ffAdditionalDataGenerator;

    @Mock
    private PimImportInitializer<ClassAttributeAssignmentModel> classAttributeAssignmentInitializer;

    @Mock
    private ConverterLanguageUtil converterLanguageUtil;

    @Captor
    private ArgumentCaptor<List<CProductFeature>> featuresCaptor;

    @Test
    public void convertTestBlankFeature() throws DocumentException {
        final Element element = TestUtils.getDomElementFromResource(this.getClass(), PATH_TO_PRODUCT_NO_FEATURE);
        final String hash = "hash";
        final ProductModel product = new ProductModel();
        final ClassificationClassModel rootClassificationClass = Mockito.mock(ClassificationClassModel.class);
        mockDistManufacturerService("man_lea");
        mockDistCategoryService("cat-DC-684564");
        mockDistCategoryService("cat-DNAV_PL_15030903");
        final ImportContext importContext = getImportContextMocking(rootClassificationClass);
        mockMediaContainerService(importContext, "30013092-01");

        final ClassificationAttributeUnitModel unit = Mockito.mock(ClassificationAttributeUnitModel.class);
        Mockito.when(mockedClassificationSystemService.getAttributeUnitForCode(Mockito.any(), Mockito.any())).thenReturn(unit);
        Mockito.when(unit.getName(Mockito.any())).thenReturn("mockUnitName");

        Mockito.when(converterLanguageUtil.getActivePimLocales()).thenReturn(Collections.singleton(Locale.ENGLISH));

        productFeatureElementConverter.convert(element, product, importContext, hash);

        Mockito.verify(defaultDistClassificationService).saveLangIndependentProductFeatures(Mockito.any(), featuresCaptor.capture());
        final List<CProductFeature> cProductFeatures = featuresCaptor.getValue();
        for (final CProductFeature feature : cProductFeatures) {
            LOG.debug("feature with Qualifier: {}  and value: {}", feature.getQualifier(), feature.getValue());
            if (StringUtils.isEmpty(feature.getValue())) {
                fail();
            }
        }
    }

    @Test
    public void addProductFeatureTestNotEmptyValue() {
        final String value = "testValue";
        final int wantedNumberOfInvocations = 1;
        addProductFeatureTestInternal(value, wantedNumberOfInvocations);
    }

    @Test
    public void addProductFeatureTestEmptyValue() {
        final String value = StringUtils.EMPTY;
        final int wantedNumberOfInvocations = 0;
        addProductFeatureTestInternal(value, wantedNumberOfInvocations);
    }

    private void addProductFeatureTestInternal(final String value, final int wantedNumberOfInvocations) {
        final ProductModel target = Mockito.mock(ProductModel.class);
        final CProductFeatures cProductFeatures = Mockito.mock(CProductFeatures.class);
        final List<CProductFeature> featuresList = Mockito.mock(List.class);
        Mockito.when(cProductFeatures.getFeatures()).thenReturn(featuresList);
        Mockito.when(cProductFeatures.getLanguage()).thenReturn("en");
        final String attributeCode = "testAttributeCode";
        final String unitCode = "testUnitCode";
        final int featurePosition = 44;
        final Integer valuePosition = 45;
        final ImportContext importContext = Mockito.mock(ImportContext.class);
        final ClassAttributeAssignmentModel assignment = TestUtils.createMockClassAttributeAssignmentModel();
        final Map<CProductFeatureKey, CProductFeature> indexedFeatures = new HashMap<>();
        productFeatureElementConverter.addProductFeature(target, cProductFeatures, indexedFeatures, attributeCode,
                unitCode, value, featurePosition, valuePosition, importContext, assignment);
        Mockito.verify(featuresList, Mockito.times(wantedNumberOfInvocations)).add(Mockito.any());
    }

    @Override
    protected void mockConfigurationService() {
        super.mockConfigurationService();
        when(mockedConfigurationService.getConfiguration().getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(Boolean.TRUE);
    }

}
