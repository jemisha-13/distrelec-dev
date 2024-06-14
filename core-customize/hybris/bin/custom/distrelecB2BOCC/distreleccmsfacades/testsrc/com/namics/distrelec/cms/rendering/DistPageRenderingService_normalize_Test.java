package com.namics.distrelec.cms.rendering;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(Parameterized.class)
public class DistPageRenderingService_normalize_Test extends AbstractDistPageRenderingServiceTest {

    @Parameter(0)
    public String  pageLabelOrId;

    @Parameter(1)
    public String normalizedPageLabelOrId;

    @Parameters
    public static Collection<Object[]> data() {
        return List.of(new Object[][] {
                {"plain", "plain"},
                {"/manufacturer?no-cache=true", "/manufacturer"},
                {"/en/manufacturer", "/manufacturer"},
                {"/en/checkout/address", "/checkout/address"},
                {"/cms/manufacturer", "/manufacturer"},
                {"/cms/manufacturer?no-cache=true", "/manufacturer"},
                {"/cms/manufacturer\tffa", "/manufacturer"}});
    }

    @Test
    public void testNormalize() {
        String normalized = renderingService.normalize(pageLabelOrId);

        assertEquals(this.normalizedPageLabelOrId, normalized);
    }
}
