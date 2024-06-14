package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistPimWebUseDisplayValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String PIM_WEB_USE_JSON = "[{\"code\":\"dissmdledcasestyletxt\",\"attributeName\":\"SMD LED Case Style\",\"value\":\"Top Mount\",\"fieldType\":\"string\"},{\"code\":\"disledcolourtxt\",\"attributeName\":\"LED Colour\",\"value\":\"Blue\",\"fieldType\":\"string\"},{\"code\":\"dislenscolourtxt\",\"attributeName\":\"Lens Colour\",\"value\":\"Water Clear\",\"fieldType\":\"string\"},{\"code\":\"dislensshapetxt\",\"attributeName\":\"Lens Shape\",\"value\":\"Dome\",\"fieldType\":\"string\"},{\"code\":\"disbulbsizerectangulartxt\",\"attributeName\":\"Bulb Size (Rectangular)\",\"value\":\"1.6 x 3.2 mm\",\"fieldType\":\"string\"},{\"code\":\"disluminousintensitynum\",\"attributeName\":\"Luminous Intensity\",\"value\":\"150\",\"unit\":\"mcd\",\"fieldType\":\"double\"},{\"code\":\"dispeakwavelengthnum\",\"attributeName\":\"Peak Wavelength\",\"value\":\"468\",\"unit\":\"nm\",\"fieldType\":\"double\"},{\"code\":\"disbulbpackagetxt\",\"attributeName\":\"Bulb Package\",\"value\":\"1206\",\"fieldType\":\"string\"},{\"code\":\"disviewinganglenum\",\"attributeName\":\"Viewing Angle\",\"value\":\"120\",\"unit\":\"°\",\"fieldType\":\"double\"},{\"code\":\"disforwardvoltagevfnum\",\"attributeName\":\"Forward Voltage (Vf)\",\"value\":\"3.2\",\"unit\":\"V\",\"fieldType\":\"string\"},{\"code\":\"disforwardcurrentifnum\",\"attributeName\":\"Forward Current (If)\",\"value\":\"25\",\"unit\":\"mA\",\"fieldType\":\"double\"}]";

    @InjectMocks
    private DistPimWebUseDisplayValueResolver distPimWebUseDisplayValueResolver;

    @Before
    public void init() {
        super.init();

        when(product.getPimWebUseJson()).thenReturn(PIM_WEB_USE_JSON);
    }

    @Test
    public void testResolve() throws FieldValueProviderException {
        distPimWebUseDisplayValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(document, times(1)).addField(eq(cloneableIndexedProperty), eq(PIM_WEB_USE_JSON), any());
    }
}
