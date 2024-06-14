package com.namics.distrelec.b2b.facades.common.predicate;

import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
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
public class ManufacturerPageTypeCodePredicateTest {

    ManufacturerPageTypeCodePredicate predicate = new ManufacturerPageTypeCodePredicate();

    @Parameter(0)
    public String pageType;

    @Parameter(1)
    public boolean matches;

    @Parameters
    public static Collection<Object[]> options() {
        return List.of(
                new Object[] { DistManufacturerPageModel._TYPECODE, true },
                new Object[] { ContentPageModel._TYPECODE, false });
    }

    @Test
    public void testPositive() {
        assertEquals(matches, predicate.test(pageType));
    }
}
