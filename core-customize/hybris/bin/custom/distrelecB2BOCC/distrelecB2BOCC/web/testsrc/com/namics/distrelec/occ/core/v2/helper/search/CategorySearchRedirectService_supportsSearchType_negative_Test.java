package com.namics.distrelec.occ.core.v2.helper.search;

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.namics.hybris.ffsearch.data.search.DistSearchType;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@UnitTest
@RunWith(Parameterized.class)
public class CategorySearchRedirectService_supportsSearchType_negative_Test
        extends AbstractCategorySearchRedirectServiceTest {

    @Parameter
    public DistSearchType searchType;

    @Parameters
    public static Collection<Object> data() {
        return Arrays.stream(DistSearchType.values())
                .filter(distSearchType -> !DistSearchType.CATEGORY.equals(distSearchType))
                .filter(distSearchType -> !DistSearchType.CATEGORY_AND_TEXT.equals(distSearchType))
                .collect(Collectors.toList());
    }

    @Test
    public void testSupportsSearchTypeNegative() {
        boolean supported = service.supportsSearchType(searchType);

        assertFalse(supported);
    }
}
