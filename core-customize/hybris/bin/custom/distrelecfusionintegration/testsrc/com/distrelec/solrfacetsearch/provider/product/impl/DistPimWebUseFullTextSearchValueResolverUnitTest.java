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

public class DistPimWebUseFullTextSearchValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String PIM_WEB_USE_JSON = "[{\"code\":\"dissmdledcasestyletxt\",\"attributeName\":\"SMD LED Case Style\",\"value\":\"Top Mount\",\"fieldType\":\"string\"},{\"code\":\"disledcolourtxt\",\"attributeName\":\"LED Colour\",\"value\":\"Blue\",\"fieldType\":\"string\"},{\"code\":\"dislenscolourtxt\",\"attributeName\":\"Lens Colour\",\"value\":\"Water Clear\",\"fieldType\":\"string\"},{\"code\":\"dislensshapetxt\",\"attributeName\":\"Lens Shape\",\"value\":\"Dome\",\"fieldType\":\"string\"},{\"code\":\"disbulbsizerectangulartxt\",\"attributeName\":\"Bulb Size (Rectangular)\",\"value\":\"1.6 x 3.2 mm\",\"fieldType\":\"string\"},{\"code\":\"disluminousintensitynum\",\"attributeName\":\"Luminous Intensity\",\"value\":\"150\",\"unit\":\"mcd\",\"fieldType\":\"double\"},{\"code\":\"dispeakwavelengthnum\",\"attributeName\":\"Peak Wavelength\",\"value\":\"468\",\"unit\":\"nm\",\"fieldType\":\"double\"},{\"code\":\"disbulbpackagetxt\",\"attributeName\":\"Bulb Package\",\"value\":\"1206\",\"fieldType\":\"string\"},{\"code\":\"disviewinganglenum\",\"attributeName\":\"Viewing Angle\",\"value\":\"120\",\"unit\":\"°\",\"fieldType\":\"double\"},{\"code\":\"disforwardvoltagevfnum\",\"attributeName\":\"Forward Voltage (Vf)\",\"value\":\"3.2\",\"unit\":\"V\",\"fieldType\":\"string\"},{\"code\":\"disforwardcurrentifnum\",\"attributeName\":\"Forward Current (If)\",\"value\":\"25\",\"unit\":\"mA\",\"fieldType\":\"double\"}]";

    @InjectMocks
    private DistPimWebUseFullTextSearchValueResolver distPimWebUseFullTextSearchValueResolver;

    @Mock
    private DistUnitConversionService unitConversionService;

    @Mock
    private Unit unitmcd;

    @Mock
    private Unit unitnM;

    @Mock
    private Unit unitDegree;

    @Mock
    private Unit unitmA;

    private Gson gson = new Gson();

    @Before
    public void init() {
        super.init();

        ReflectionTestUtils.setField(distPimWebUseFullTextSearchValueResolver, "gson", gson);

        cloneableIndexedProperty.setName("pimWebUseFullTextSearch");
        cloneableIndexedProperty.setExportId("pimWebUseFullTextSearch");
        cloneableIndexedProperty.setLocalized(true);

        when(product.getPimWebUseJson()).thenReturn(PIM_WEB_USE_JSON);

        when(unitConversionService.getUnitBySymbol("nm")).thenReturn(Optional.of(unitnM));
        when(unitnM.getConversionFactor()).thenReturn(1e-9);
        when(unitnM.getUnitType()).thenReturn("unece.unit.MTR");
        when(unitConversionService.getBaseUnitSymbolForUnitType("unece.unit.MTR")).thenReturn("m");

        when(unitConversionService.getUnitBySymbol("mcd")).thenReturn(Optional.of(unitmcd));
        when(unitmcd.getConversionFactor()).thenReturn(0.001);
        when(unitmcd.getUnitType()).thenReturn("unece.unit.CDL");
        when(unitConversionService.getBaseUnitSymbolForUnitType("unece.unit.CDL")).thenReturn("cd");

        when(unitConversionService.getUnitBySymbol("°")).thenReturn(Optional.of(unitDegree));
        when(unitDegree.getConversionFactor()).thenReturn(1d);
        when(unitDegree.getUnitType()).thenReturn("unece.unit.DD");
        when(unitConversionService.getBaseUnitSymbolForUnitType("unece.unit.DD")).thenReturn("°");

        when(unitConversionService.getUnitBySymbol("mA")).thenReturn(Optional.of(unitmA));
        when(unitmA.getConversionFactor()).thenReturn(0.001);
        when(unitmA.getUnitType()).thenReturn("unece.unit.AMP");
        when(unitConversionService.getBaseUnitSymbolForUnitType("unece.unit.AMP")).thenReturn("A");

        when(unitConversionService.convertToBaseUnit(unitnM, "468")).thenCallRealMethod();
        when(unitConversionService.convertToBaseUnit(unitmcd, "150")).thenCallRealMethod();
        when(unitConversionService.convertToBaseUnit(unitDegree, "120")).thenCallRealMethod();
        when(unitConversionService.convertToBaseUnit(unitmA, "25")).thenCallRealMethod();
    }

    @Test
    public void testResolve() throws FieldValueProviderException {
        List<String> expectedNumbericalValues = List.of("0.15 cd", "0.000000468 m", "120 °", "0.025 A");
        List<String> expectedTextValues = List.of("Top Mount", "Blue", "Water Clear", "Dome", "1.6 x 3.2 mm", "1206", "3.2 V");

        distPimWebUseFullTextSearchValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(document, times(1)).addField(refEq(stringIndexedProperty("textValues")), eq(expectedTextValues), any());
        verify(document, times(1)).addField(refEq(stringIndexedProperty("numericalValues")), eq(expectedNumbericalValues), any());
    }

    private IndexedProperty stringIndexedProperty(String attributeName) {
        String newAttributeName = cloneableIndexedProperty.getExportId() + "_" + attributeName;
        return distPimWebUseFullTextSearchValueResolver.createNewIndexedProperty(cloneableIndexedProperty, newAttributeName,
                                                                                 SolrPropertiesTypes.STRING.getCode());
    }
}
