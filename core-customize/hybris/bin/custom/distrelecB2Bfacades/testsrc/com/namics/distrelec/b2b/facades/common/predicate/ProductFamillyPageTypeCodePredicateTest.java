package com.namics.distrelec.b2b.facades.common.predicate;

import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
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
public class ProductFamillyPageTypeCodePredicateTest {

    ProductFamilyPageTypeCodePredicate predicate = new ProductFamilyPageTypeCodePredicate();

    @Parameter(0)
    public String pageType;

    @Parameter(1)
    public boolean matches;

    @Parameters
    public static Collection<Object[]> options() {
        return List.of(
                new Object[] { ProductFamilyPageModel._TYPECODE, true },
                new Object[] {ContentPageModel._TYPECODE, false });
    }

    @Test
    public void test() {
        assertEquals(matches, predicate.test(pageType));
    }
}
