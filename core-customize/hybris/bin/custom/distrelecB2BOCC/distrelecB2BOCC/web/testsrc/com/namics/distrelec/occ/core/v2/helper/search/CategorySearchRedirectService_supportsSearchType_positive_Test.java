package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.hybris.ffsearch.data.search.DistSearchType;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;

@UnitTest
@RunWith(Parameterized.class)
public class CategorySearchRedirectService_supportsSearchType_positive_Test
        extends AbstractCategorySearchRedirectServiceTest {

    @Parameter
    public DistSearchType searchType;

    @Parameters
    public static Collection<Object> data() {
        return List.of(DistSearchType.CATEGORY, DistSearchType.CATEGORY_AND_TEXT);
    }

    @Test
    public void testSupportsSearchTypePositive() {
        boolean supported = service.supportsSearchType(searchType);

        assertTrue(supported);
    }
}
