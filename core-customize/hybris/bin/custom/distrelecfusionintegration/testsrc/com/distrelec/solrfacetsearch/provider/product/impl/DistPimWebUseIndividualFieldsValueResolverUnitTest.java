package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.distrelec.b2b.core.search.data.Unit;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.service.unit.impl.DistUnitConversionService;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;

public class DistPimWebUseIndividualFieldsValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String PIM_WEB_USE_JSON = "[{\"code\":\"dissmdledcasestyletxt\",\"attributeName\":\"SMD LED Case Style\",\"value\":\"Top Mount\",\"fieldType\":\"string\"},{\"code\":\"disledcolourtxt\",\"attributeName\":\"LED Colour\",\"value\":\"Blue\",\"fieldType\":\"string\"},{\"code\":\"dislenscolourtxt\",\"attributeName\":\"Lens Colour\",\"value\":\"Water Clear\",\"fieldType\":\"string\"},{\"code\":\"dislensshapetxt\",\"attributeName\":\"Lens Shape\",\"value\":\"Dome\",\"fieldType\":\"string\"},{\"code\":\"disbulbsizerectangulartxt\",\"attributeName\":\"Bulb Size (Rectangular)\",\"value\":\"1.6 x 3.2 mm\",\"fieldType\":\"string\"},{\"code\":\"disluminousintensitynum\",\"attributeName\":\"Luminous Intensity\",\"value\":\"150\",\"unit\":\"mcd\",\"fieldType\":\"double\"},{\"code\":\"dispeakwavelengthnum\",\"attributeName\":\"Peak Wavelength\",\"value\":\"468\",\"unit\":\"nm\",\"fieldType\":\"double\"},{\"code\":\"disbulbpackagetxt\",\"attributeName\":\"Bulb Package\",\"value\":\"1206\",\"fieldType\":\"string\"},{\"code\":\"disviewinganglenum\",\"attributeName\":\"Viewing Angle\",\"value\":\"120\",\"unit\":\"°\",\"fieldType\":\"double\"},{\"code\":\"disforwardvoltagevfnum\",\"attributeName\":\"Forward Voltage (Vf)\",\"value\":\"3.2\",\"unit\":\"V\",\"fieldType\":\"string\"},{\"code\":\"disforwardcurrentifnum\",\"attributeName\":\"Forward Current (If)\",\"value\":\"25\",\"unit\":\"mA\",\"fieldType\":\"double\"}]";

    @InjectMocks
    private DistPimWebUseIndividualFieldsValueResolver distPimWebUseIndividualFieldsValueResolver;

    @Mock
    private DistUnitConversionService unitConversionService;

    @Mock
    private Unit unitmcd;

    @Mock
    private Unit unitnM;

    @Mock
    private Unit unitDegree;

    @Mock
    private Unit unitV;

    @Mock
    private Unit unitmA;

    private Gson gson = new Gson();

    @Before
    public void init() {
        super.init();

        ReflectionTestUtils.setField(distPimWebUseIndividualFieldsValueResolver, "gson", gson);

        cloneableIndexedProperty.setName("pimWebUse");
        cloneableIndexedProperty.setExportId("pimWebUse");
        cloneableIndexedProperty.setLocalized(true);

        when(product.getPimWebUseJson()).thenReturn(PIM_WEB_USE_JSON);

        when(unitConversionService.getUnitBySymbol("nm")).thenReturn(Optional.of(unitnM));
        when(unitnM.getConversionFactor()).thenReturn(1e-9);
        when(unitConversionService.getUnitBySymbol("mcd")).thenReturn(Optional.of(unitmcd));
        when(unitmcd.getConversionFactor()).thenReturn(0.001);
        when(unitConversionService.getUnitBySymbol("°")).thenReturn(Optional.of(unitDegree));
        when(unitDegree.getConversionFactor()).thenReturn(1d);
        when(unitConversionService.getUnitBySymbol("mA")).thenReturn(Optional.of(unitmA));
        when(unitmA.getConversionFactor()).thenReturn(0.001);

        when(unitConversionService.convertToBaseUnit(unitnM, "468")).thenCallRealMethod();
        when(unitConversionService.convertToBaseUnit(unitmcd, "150")).thenCallRealMethod();
        when(unitConversionService.convertToBaseUnit(unitDegree, "120")).thenCallRealMethod();
        when(unitConversionService.convertToBaseUnit(unitmA, "25")).thenCallRealMethod();
    }

    @Test
    public void testResolve() throws FieldValueProviderException {
        distPimWebUseIndividualFieldsValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(document, times(1)).addField(refEq(doubleIndexedProperty("disluminousintensitynum")), eq(0.15), any());
        verify(document, times(1)).addField(refEq(doubleIndexedProperty("dispeakwavelengthnum")), eq(4.68E-7), any());
        verify(document, times(1)).addField(refEq(doubleIndexedProperty("disviewinganglenum")), eq(120d), any());
        verify(document, times(1)).addField(refEq(doubleIndexedProperty("disforwardcurrentifnum")), eq(0.025), any());

        verify(document, times(1)).addField(refEq(stringIndexedProperty("dissmdledcasestyletxt")), eq("Top Mount"), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty("disledcolourtxt")), eq("Blue"), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty("dislenscolourtxt")), eq("Water Clear"), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty("dislensshapetxt")), eq("Dome"), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty("disbulbsizerectangulartxt")), eq("1.6 x 3.2 mm"), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty("disbulbpackagetxt")), eq("1206"), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty("disforwardvoltagevfnum")), eq("3.2"), any());
    }

    private IndexedProperty doubleIndexedProperty(String attributeName) {
        String newAttributeName = cloneableIndexedProperty.getExportId() + "_" + attributeName;
        return distPimWebUseIndividualFieldsValueResolver.createNewIndexedProperty(cloneableIndexedProperty, newAttributeName,
                                                                                   SolrPropertiesTypes.DOUBLE.getCode());
    }

    private IndexedProperty stringIndexedProperty(String attributeName) {
        String newAttributeName = cloneableIndexedProperty.getExportId() + "_" + attributeName;
        return distPimWebUseIndividualFieldsValueResolver.createNewIndexedProperty(cloneableIndexedProperty, newAttributeName,
                                                                                   SolrPropertiesTypes.STRING.getCode());
    }
}
