package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Brand new unit test class to test {@link DistProductFeatureListPopulator}
 * 
 * @Author N D Clarke, June 2018
 */
@UnitTest
public class DistProductFeatureListPopulatorTest {

    @Mock
    private Converter<ClassificationClassModel, ClassificationData> classificationConverter;

    @Mock
    private Converter<Feature, FeatureData> featureConverter;

    @InjectMocks
    private DistProductFeatureListPopulator distProductFeatureListPopulator;

    private static final String ENERGY_TOP_CODE = "DistrelecClassification/1.0/class-DNAV_PL_110608.calc_energylabel_top_text";

    private static final String ENERGY_LABEL_TOP = "EnergyLabel top";

    private static final String A_VISIBILITY = "a_visibility";

    private FeatureList featureList;

    private FeatureData mockFeatureData;

    private ProductData productData;

    /**
     * Sets up objects and injects Mock Objects.
     */
    @Before
    public void setUp() {
        // buildFixtures();
        productData = new ProductData();
        Feature feature = createFeature();
        featureList = new FeatureList(feature);

        mockFeatureData = new FeatureData();
        mockFeatureData.setName(ENERGY_LABEL_TOP);
        mockFeatureData.setCode(ENERGY_TOP_CODE);
        mockFeatureData.setVisibility(A_VISIBILITY);

        // productData.setClassifications();

        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests that Energy Labels are not present with the new changes.
     */
    @Test
    public void testEnergyLabelsAreNotPresent() {
        Mockito.when(classificationConverter.convert(Mockito.any(ClassificationClassModel.class))).thenReturn(createClassificationData());
        Mockito.when(featureConverter.convert(Mockito.any(Feature.class))).thenReturn(mockFeatureData);

        distProductFeatureListPopulator.populate(featureList, productData);

        // Energy Labels have been removed
        Assert.assertNotNull(productData);
        Assert.assertEquals(null, productData.getEnergyBottomText());
        Assert.assertEquals(null, productData.getEnergyTopText());
    }

    /**
     * Creates a Feature Object used in the {@link DistProductFeatureListPopulator}
     * 
     * @return a {@link Feature} used in
     */
    private Feature createFeature() {
        // ClassificationClassModel classificationClassModel = createClassificationModel();

        final ClassAttributeAssignmentModel classAttributeAssignmentModel = createClassificationAttributeModel();
        final FeatureValue featureValue = new FeatureValue("code");
        final UnlocalizedFeature feature = new UnlocalizedFeature(classAttributeAssignmentModel, featureValue);
        return feature;
    }

    private ClassAttributeAssignmentModel createClassificationAttributeModel() {
        final ClassificationClassModel classificationClassModel = new ClassificationClassModel();

        classificationClassModel.setCode("code");

        final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
        classAttributeAssignmentModel.setClassificationClass(classificationClassModel);

        final ClassificationAttributeModel classificationAttributeModel = new ClassificationAttributeModel();
        classAttributeAssignmentModel.setClassificationAttribute(classificationAttributeModel);
        classAttributeAssignmentModel.setLocalized(true);
        return classAttributeAssignmentModel;
    }

    /**
     * Constructs a Collection of Classification Data
     * 
     * @return A Collection of ClassificationData full of Feature Data
     */
    private ClassificationData createClassificationData() {
        final ClassificationData classificationData = new ClassificationData();
        classificationData.setCode("12345678");
        classificationData.setFeatures(createFeatureListData());
        classificationData.setName("Name");
        return classificationData;
    }

    /**
     * Create a collection of FeatureData for our test case.
     * 
     * @return a List of Feature data for our test case.
     */
    private Collection<FeatureData> createFeatureListData() {
        final FeatureData featureData = new FeatureData();
        featureData.setCode(ENERGY_TOP_CODE);
        featureData.setVisibility(A_VISIBILITY);

        final Collection<FeatureData> features = new ArrayList<>();
        features.add(featureData);
        return features;
    }

}
