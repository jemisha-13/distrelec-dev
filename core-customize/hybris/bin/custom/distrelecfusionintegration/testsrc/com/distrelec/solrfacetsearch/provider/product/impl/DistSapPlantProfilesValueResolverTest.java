package com.distrelec.solrfacetsearch.provider.product.impl;

import static java.lang.Boolean.FALSE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistSapPlantProfilesValueResolverTest extends DistAbstractValueResolverTest {

    private static final String CH_SUFFIX = "_CH";

    private static final String SAP_PLANT_PROFILE_FORMAT = "7371&%s|7374&%s";

    private static final String SIMPLIFIED_PROFILE_1 = "1";

    private static final String SIMPLIFIED_PROFILE_2 = "2";

    private static final String SIMPLIFIED_PROFILE_4 = "4";

    private static final String PROFILE_NEW = "NEW";

    private static final String PROFILE_MAN = "MAN";

    private static final String PROFILE_COMP = "COMP";

    private static final String PROFILE_DAVE = "DAVE";

    private static final String PROFILE_RSP = "RSP";

    @InjectMocks
    private DistSapPlantProfilesValueResolver plantProfileValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("sapPlantProfile");
        cloneableIndexedProperty.setExportId("sapPlantProfile");
        cloneableIndexedProperty.setLocalized(false);

        when(qualifierProvider.canApply(cloneableIndexedProperty)).thenReturn(FALSE);
    }

    @Test
    public void testTplusM1() throws FieldValueProviderException {
        String internationalPlantProfile = "T+M1";
        String swissPlantProfile = "T+M1";
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_1), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_1), any());
    }

    // applicable to all base plant profiles in format FPX, FB0X, FA0X, FPXY
    @Test
    public void testFP23() throws FieldValueProviderException {
        String internationalPlantProfile = "FP23";
        String swissPlantProfile = "FP23";
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_2), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_2), any());
    }

    @Test
    public void testFP2() throws FieldValueProviderException {
        String internationalPlantProfile = "FP2";
        String swissPlantProfile = "FP2";
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_2), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_2), any());
    }

    @Test
    public void testFA02() throws FieldValueProviderException {
        String internationalPlantProfile = "FA02";
        String swissPlantProfile = "FA02";
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_2), any());
        verify(distSolrInputDocument, times(0)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_2), any());
    }

    @Test
    public void testFB02() throws FieldValueProviderException {
        String internationalPlantProfile = "FB02";
        String swissPlantProfile = "FB02";
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_2), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_2), any());
    }

    // special cases for plant profile 4
    @Test
    public void testNEW() throws FieldValueProviderException {
        String internationalPlantProfile = PROFILE_NEW;
        String swissPlantProfile = PROFILE_NEW;
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_4), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_4), any());
    }

    @Test
    public void testMAN() throws FieldValueProviderException {
        String internationalPlantProfile = PROFILE_MAN;
        String swissPlantProfile = PROFILE_MAN;
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_4), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_4), any());
    }

    @Test
    public void testCOMP() throws FieldValueProviderException {
        String internationalPlantProfile = PROFILE_COMP;
        String swissPlantProfile = PROFILE_COMP;
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_4), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_4), any());
    }

    @Test
    public void testDAVE() throws FieldValueProviderException {
        String internationalPlantProfile = PROFILE_DAVE;
        String swissPlantProfile = PROFILE_DAVE;
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_4), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_4), any());
    }

    @Test
    public void testRSP() throws FieldValueProviderException {
        String internationalPlantProfile = PROFILE_RSP;
        String swissPlantProfile = PROFILE_RSP;
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(SIMPLIFIED_PROFILE_4), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(SIMPLIFIED_PROFILE_4), any());
    }

    @Test
    public void testTwoPlantProfileAssignmentsNoSimplification() throws FieldValueProviderException {
        testTwoPlantProfileAssignment("F001", "F001");
        testTwoPlantProfileAssignment("BANS", "BANS");
        testTwoPlantProfileAssignment("FM00", "FM00");
        testTwoPlantProfileAssignment("FN01", "FN01");
        testTwoPlantProfileAssignment("CAT+", "CAT+");
        testTwoPlantProfileAssignment("XYZ", "XYZ");
    }

    private void testTwoPlantProfileAssignment(String internationalPlantProfile, String swissPlantProfile) throws FieldValueProviderException {
        createAndSetSapPlantProfile(internationalPlantProfile, swissPlantProfile);

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(internationalPlantProfile), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq(swissPlantProfile), any());
    }

    @Test
    public void testNoInternationalProfile() throws FieldValueProviderException {
        when(product.getSapPlantProfiles()).thenReturn("7374&BANS");

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(eq(cloneableIndexedProperty), any(), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), eq("BANS"), any());
    }

    @Test
    public void testNoSwissProfile() throws FieldValueProviderException {
        when(product.getSapPlantProfiles()).thenReturn("7371&BANS");

        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq("BANS"), any());
        verify(distSolrInputDocument, times(0)).addField(refEq(createNewIndexedProperty_sapPlantProfileCH()), any(), any());
    }

    @Test
    public void testPlantProfileNull() throws FieldValueProviderException {
        plantProfileValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

    private void createAndSetSapPlantProfile(String internationalPlantProfile, String swissPlantProfile) {
        String sapPlantProfiles = String.format(SAP_PLANT_PROFILE_FORMAT, internationalPlantProfile, swissPlantProfile);
        when(product.getSapPlantProfiles()).thenReturn(sapPlantProfiles);
    }

    private IndexedProperty createNewIndexedProperty_sapPlantProfileCH() {
        return plantProfileValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                  cloneableIndexedProperty.getExportId() + CH_SUFFIX);
    }

}
